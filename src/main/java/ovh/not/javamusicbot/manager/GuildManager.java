package ovh.not.javamusicbot.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.core.entities.Guild;

public class GuildManager {
    private final Guild guild;
    private final VoiceManager voiceManager;
    private final AnnouncementsManager announcementsManager;
    private final QueueManager queueManager;

    public GuildManager(Guild guild, AudioPlayerManager playerManager) {
        this.guild = guild;
        voiceManager = new VoiceManager(this, playerManager);
        announcementsManager = new AnnouncementsManager();
        queueManager = new QueueManager(this);
    }

    public Guild getGuild() {
        return guild;
    }

    public VoiceManager getVoiceManager() {
        return voiceManager;
    }

    public AnnouncementsManager getAnnouncementsManager() {
        return announcementsManager;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }
}
