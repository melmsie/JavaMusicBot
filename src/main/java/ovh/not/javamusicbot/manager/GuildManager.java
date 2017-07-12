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

    Guild getGuild() {
        return guild;
    }

    VoiceManager getVoiceManager() {
        return voiceManager;
    }

    AnnouncementsManager getAnnouncementsManager() {
        return announcementsManager;
    }

    QueueManager getQueueManager() {
        return queueManager;
    }
}
