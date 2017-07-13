package ovh.not.javamusicbot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import ovh.not.javamusicbot.manager.AnnouncementsManager;
import ovh.not.javamusicbot.manager.GuildManager;
import ovh.not.javamusicbot.manager.QueueManager;
import ovh.not.javamusicbot.manager.VoiceManager;

public class TrackScheduler extends AudioEventAdapter {
    private final VoiceManager voiceManager;
    private final AnnouncementsManager announcementsManager;
    private final QueueManager queueManager;

    public TrackScheduler(GuildManager guildManager) {
        this.voiceManager = guildManager.getVoiceManager();
        this.announcementsManager = guildManager.getAnnouncementsManager();
        this.queueManager = guildManager.getQueueManager();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // todo logging of pauses
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // todo logging of resumes
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // todo log track started
        announcementsManager.announceTrackStart(track);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) { // todo logging of end reasons
            AudioTrack newTrack = queueManager.nextTrack(track);

            // if the track is null it will return false
            if (!player.startTrack(newTrack, false)) {
                // todo log that tracks have finished & vc closed
                voiceManager.close();
            }
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // todo logging
        System.out.println("An error occurred with track " + track.getIdentifier() + "!");
        exception.printStackTrace();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        System.out.println("Track " + track.getIdentifier() + " stuck with threshold: " + thresholdMs); // todo logging
    }
}
