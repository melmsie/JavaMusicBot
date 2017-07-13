package ovh.not.javamusicbot.manager;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.entities.TextChannel;
import ovh.not.javamusicbot.Utils;

public class AnnouncementsManager {
    private static final String FORMAT_TRACK_START = "Now playing **%s** by **%s** `[%s]`";

    private TextChannel textChannel = null; // todo instantiate

    AnnouncementsManager() {
    }

    public void announceTrackStart(AudioTrack audioTrack) {
        AudioTrackInfo info = audioTrack.getInfo();
        String duration = Utils.formatDuration(audioTrack.getDuration());
        String message = String.format(FORMAT_TRACK_START, info.title, info.author, duration);

        textChannel.sendMessage(message).queue();
    }

    public void setTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }
}
