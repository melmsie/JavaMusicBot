package ovh.not.javamusicbot.manager;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.entities.TextChannel;

public class AnnouncementsManager {
    private TextChannel textChannel = null;

    AnnouncementsManager() {
    }

    private void announceTrackStart(AudioTrack audioTrack) {
        // todo announcement
    }

    public void setTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }
}
