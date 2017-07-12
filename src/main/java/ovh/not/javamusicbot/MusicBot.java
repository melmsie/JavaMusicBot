package ovh.not.javamusicbot;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.moandjiezana.toml.Toml;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import ovh.not.javamusicbot.manager.ShardManager;

import java.io.File;

public final class MusicBot {
    private static final String CONFIG_PATH = "config.toml";
    private static final String CONSTANTS_PATH = "constants.toml";
    public static final String USER_AGENT = "JavaMusicBot (https://github.com/sponges/JavaMusicBot)";
    public static final Gson GSON = new Gson();

    public static void main(String[] args) {
        Config config = new Toml().read(new File(CONFIG_PATH)).to(Config.class);
        Constants constants = new Toml().read(new File(CONSTANTS_PATH)).to(Constants.class);
        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
        HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        Unirest.setHttpClient(httpClient);

        ShardManager.Builder builder = new ShardManager.Builder(config);
        if (args.length > 0) {
            builder.useSharding(true)
                    .withShardCount(Integer.parseInt(args[0]))
                    .withShardRange(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        }
        ShardManager shardManager = builder.build();
    }
}
