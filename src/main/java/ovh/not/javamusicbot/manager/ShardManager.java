package ovh.not.javamusicbot.manager;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import ovh.not.javamusicbot.Config;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

public class ShardManager {
    public static class Builder {
        private final Config config;
        private boolean useSharding;
        private int shardCount;
        private int shardRangeMin;
        private int shardRangeMax;

        public Builder(Config config) {
            this.config = config;
        }

        public Builder useSharding(boolean useSharding) {
            this.useSharding = useSharding;
            return this;
        }

        public Builder withShardCount(int shardCount) {
            this.shardCount = shardCount;
            return this;
        }

        public Builder withShardRange(int shardRangeMin, int shardRangeMax) {
            this.shardRangeMin = shardRangeMin;
            this.shardRangeMax = shardRangeMax;
            return this;
        }

        public ShardManager build() {
            if (useSharding) {
                return new ShardManager(config, shardCount, shardRangeMin, shardRangeMax);
            } else {
                return new ShardManager(config);
            }
        }
    }

    public class Shard {
        private final int id;
        private final ShardManager shardManager;
        private final Config config;
        private final int shardCount;
        private JDA jda = null;

        private Shard(int id, ShardManager shardManager, Config config) {
            this.id = id;
            this.shardManager = shardManager;
            this.config = config;
            this.shardCount = 0;
        }

        private Shard(int id, ShardManager shardManager, Config config, int shardCount) {
            this.id = id;
            this.shardManager = shardManager;
            this.config = config;
            this.shardCount = shardCount;
        }

        private void create() {
            JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT)
                    .setToken(config.token)
                    .setGame(Game.of(String.format("%shelp | [%d/%d]", config.prefix, id, shardCount)))
                    .addEventListener(null); // todo add listener

            if (shardCount != 0) {
                jdaBuilder.useSharding(id, shardCount);
            }

            try {
                jda = jdaBuilder.buildBlocking(); // todo switch to async?
            } catch (LoginException | RateLimitedException | InterruptedException e) {
                e.printStackTrace(); // todo logging
            }
        }

        public void restart() {
            System.out.println("Shutting down shard " + id + "..."); // todo logging
            jda.shutdown(false);

            System.out.println("Restarting shard " + id + "...");
            create();

            System.out.println("Shard " + id + " restarted!");
        }
    }

    private final Config config;
    private final Shard[] shards;

    private ShardManager(Config config) {
        this.config = config;
        this.shards = new Shard[1];
    }

    private ShardManager(Config config, int shardCount, int shardRangeMin, int shardRangeMax) {
        this.config = config;
        this.shards = new Shard[(shardRangeMax - shardRangeMin) + 1];
        int index = 0;
        for (int shardId = shardRangeMin; shardId < shardRangeMax + 1;) {
            System.out.println("Starting shard " + shardId + "..."); // todo logging
            Shard shard = new Shard(shardId, this, config, shardCount);
            shards[index] = shard;
            shardId++;
            index++;
        }
    }

    public void restart(int shardId) {
        for (Shard shard : shards) {
            if (shard.id == shardId) {
                shard.restart();
                return;
            }
        }
    }

    public void setGame(String gameName) {
        Game game = Game.of(gameName);
        for (Shard shard : shards) {
            shard.jda.getPresence().setGame(game);
        }
    }

    public Map<Shard, JDA.Status> getStatuses() {
        Map<Shard, JDA.Status> statuses = new HashMap<>();
        for (Shard shard : shards) {
            statuses.put(shard, shard.jda.getStatus());
        }
        return statuses;
    }

    // yuck sharing guild instances across threads :eyes:
    public Guild findGuild(String id) {
        for (Shard shard : shards) {
            Guild guild = shard.jda.getGuildById(id);
            if (guild != null) {
                return guild;
            }
        }
        return null;
    }
}
