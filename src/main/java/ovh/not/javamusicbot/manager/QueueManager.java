package ovh.not.javamusicbot.manager;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class QueueManager {
    private final AnnouncementsManager announcementsManager;
    private final Queue<AudioTrack> queue = new LinkedList<>();

    private boolean repeatSong = false;
    private boolean loopQueue = false;

    QueueManager(GuildManager guildManager) {
        announcementsManager = guildManager.getAnnouncementsManager();
    }

    @SuppressWarnings("unchecked")
    public void addTrack(AudioTrack audioTrack, int position) {
        ((List<AudioTrack>) queue).add(position, audioTrack);
    }

    public void addTrack(AudioTrack audioTrack) {
        queue.offer(audioTrack);
    }

    @SuppressWarnings("unchecked")
    public AudioTrack removeTrack(int position) {
        return ((List<AudioTrack>) queue).remove(position);
    }

    public AudioTrack nextTrack(AudioTrack lastTrack) {
        AudioTrack track;
        if (repeatSong && lastTrack != null) {
            track = lastTrack.makeClone();
        } else {
            if (loopQueue && lastTrack != null) {
                queue.add(lastTrack.makeClone());
            }
            track = queue.poll();
        }
        return track;
    }

    public void clear() {
        queue.clear();
    }
}
