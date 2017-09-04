package ovh.not.javamusicbot;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Cluster implements Runnable {
    // this is the same buffer sized used in bando
    private static final int INPUT_BUFFER_SIZE = 1024;

    private final Gson gson = new Gson();
    private final Config config;

    private boolean running = true;
    private OutputStream out = null;

    // reconnection states
    private boolean reconnecting = false;
    private final long initialReconnectPause = 2000;
    private long reconnectPause = initialReconnectPause;

    Cluster(Config config) {
        this.config = config;
    }

    @Override
    public void run() {
        Socket socket = null;

        while (running) {
            try {
                socket = new Socket(config.bandoAddress, config.bandoPort);

                System.out.printf("Connection to bando on %s:%d established!\n", config.bandoAddress, config.bandoPort);
                if (reconnecting) {
                    reconnecting = false;
                    reconnectPause = initialReconnectPause;
                }

                out = socket.getOutputStream();

                // send identify packet
                send(out, new Message(Opcode.IDENTIFY, new IdentifyMessage(config.bandoKey, 0, 3)));

                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()), INPUT_BUFFER_SIZE)) {
                    String content;
                    while ((content = in.readLine()) != null) {
                        Message message = gson.fromJson(content, Message.class);

                        // todo logging
                        switch (Opcode.fromId(message.op)) {
                            case AUTHENTICATED:
                                System.out.println("Connected to bando!");


                                // todo remove this test summons message
                                send(out, new Message(Opcode.SUMMONS, new SummonsMessage(Opcode.STATUS_REQUEST, null)));


                                break;

                            case AUTHENTICATION_REJECTED:
                                System.out.println("Invalid RPC key! Running cluster without RPC...");
                                return; // exit the method

                            case STATUS_REQUEST: {
                                String json = gson.toJson(message.data);
                                StatusRequestMessage request = gson.fromJson(json, StatusRequestMessage.class);
                                System.out.println("Received status request with id " + request.id);

                                StatusResponseMessage response = new StatusResponseMessage(request.id);
                                /* todo this
                                shards.forEach(shard -> {
                                    int id = shard.getId();
                                    JDA jda = shard.getJDA();
                                    List<Guild> guilds = jda.getGuilds();

                                    response.guilds.put(id, guilds.size());
                                    response.voice.put(id, (int) guilds.stream()
                                        .filter(guild -> guild.getAudioManager().isConnected())
                                        .count());
                                    response.states.put(id, jda.getStatus().ordinal());
                                });
                                 */

                                send(out, new Message(Opcode.STATUS_RESPONSE, response));
                                break;
                            }

                            case STATUS_ANSWER: {
                                String json = gson.toJson(message.data);
                                System.out.println("Received status answer " + json);

                                StatusAnswer answer = gson.fromJson(json, StatusAnswer.class);

                                // todo pass answer to whatever requested it lol
                                break;
                            }

                            default:
                                System.out.printf("Received message with unhandled opcode %d\n", message.op);
                        }
                    }
                } finally {
                    // close the output stream. the input stream should be auto closed
                    if (out != null) out.close();
                }
            } catch (IOException e) {
                if ((e instanceof ConnectException && e.getMessage().equals("Connection refused: connect"))
                        || (e instanceof SocketException && e.getMessage().equals("Connection reset"))) {
                    // ignored
                }

                // todo logging and more checks

                else {
                    e.printStackTrace();
                }
            } finally {
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // todo error handling
                        e.printStackTrace();

                        // break out of the reconnect loop
                        running = false;
                    }
                }
            }

            // if still running wait before reconnecting
            if (running) {
                reconnecting = true;

                // todo logging
                System.out.println(String.format("Attempting to reconnect in %dms...", reconnectPause));

                try {
                    Thread.sleep(reconnectPause);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // set the next pause to be twice as long as the last
                reconnectPause += reconnectPause;
            }
        }
    }

    private void send(OutputStream out, Message message) throws IOException {
        out.write(message.toJson());
        out.flush();
    }

    enum Opcode {
        IDENTIFY(0),
        AUTHENTICATED(1),
        AUTHENTICATION_REJECTED(2),
        SUMMONS(3),
        STATUS_REQUEST(6),
        STATUS_RESPONSE(7),
        STATUS_ANSWER(8),
        RESTART_ALL_SHARDS(50),
        RESTART_SUCCESS(51),
        RESTART_FAILURE(52),
        ROLLING_RESTART(53),
        INVALID_REQUEST(99),
        UNKNOWN_OPCODE(-1);

        int id;

        Opcode(int id) {
            this.id = id;
        }

        static Opcode fromId(int id) {
            return Arrays.stream(Opcode.values())
                    .filter(opcode -> opcode.id == id)
                    .findFirst()
                    .orElse(Opcode.UNKNOWN_OPCODE);
        }
    }

    class Message {
        int op;
        Object data;

        Message(int op, Object data) {
            this.op = op;
            this.data = data;
        }

        Message(Opcode op, Object data) {
            this.op = op.id;
            this.data = data;
        }

        byte[] toJson() {
            return gson.toJson(this).getBytes();
        }
    }

    class IdentifyMessage {
        String key;
        int min, max;

        IdentifyMessage(String key, int min, int max) {
            this.key = key;
            this.min = min;
            this.max = max;
        }
    }

    class SummonsMessage {
        int op;
        Object data;

        SummonsMessage(int op, Object data) {
            this.op = op;
            this.data = data;
        }

        SummonsMessage(Opcode op, Object data) {
            this.op = op.id;
            this.data = data;
        }
    }

    class StatusRequestMessage {
        String id;

        StatusRequestMessage(String id) {
            this.id = id;
        }
    }

    class StatusResponseMessage {
        String id;
        Map<Integer, Integer> guilds;
        Map<Integer, Integer> voice;
        Map<Integer, Integer> states;

        StatusResponseMessage(String id, Map<Integer, Integer> guilds, Map<Integer, Integer> voice,
                              Map<Integer, Integer> states) {
            this.id = id;
            this.guilds = guilds;
            this.voice = voice;
            this.states = states;
        }

        StatusResponseMessage(String id) {
            this.id = id;
            this.guilds = new HashMap<>();
            this.voice = new HashMap<>();
            this.states = new HashMap<>();
        }
    }

    class StatusAnswer {
        Map<Integer, Integer> guilds;
        Map<Integer, Integer> voice;
        Map<Integer, Integer> states;

        public StatusAnswer(Map<Integer, Integer> guilds, Map<Integer, Integer> voice, Map<Integer, Integer> states) {
            this.guilds = guilds;
            this.voice = voice;
            this.states = states;
        }
    }
}
