package ovh.not.javamusicbot;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;

class WhitelistedHttpAudioSourceManager extends HttpAudioSourceManager {
    private final Constants constants;

    WhitelistedHttpAudioSourceManager(Constants constants) {
        super();
        this.constants = constants;
    }

    @Override
    public AudioItem loadItem(DefaultAudioPlayerManager manager, AudioReference reference) {
        if (!constants.radioStations.containsValue(reference.identifier)) {
            return null;
        }
        return super.loadItem(manager, reference);
    }
}
