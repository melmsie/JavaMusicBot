package ovh.not.javamusicbot.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.managers.AudioManager;
import ovh.not.javamusicbot.AudioPlayerSendHandler;
import ovh.not.javamusicbot.exception.VoiceChannelAlreadyOpenException;

public class VoiceManager {
    private final Guild guild;
    private final AudioPlayer audioPlayer;

    private VoiceChannel voiceChannel = null;

    VoiceManager(GuildManager guildManager, AudioPlayerManager playerManager) {
        guild = guildManager.getGuild();
        audioPlayer = playerManager.createPlayer();
        AudioSendHandler sendHandler = new AudioPlayerSendHandler(audioPlayer);
        guild.getAudioManager().setSendingHandler(sendHandler);
    }

    public boolean isOpen() {
        return voiceChannel != null;
    }

    public void open(VoiceChannel voiceChannel) throws VoiceChannelAlreadyOpenException, PermissionException {
        if (isOpen()) {
            throw new VoiceChannelAlreadyOpenException();
        }
        AudioManager audioManager = guild.getAudioManager();
        this.voiceChannel = voiceChannel;
        audioManager.openAudioConnection(this.voiceChannel);
        audioManager.setSelfDeafened(true);
    }

    public void close() {
        guild.getAudioManager().closeAudioConnection();
        if (voiceChannel != null) {
            voiceChannel = null;
        }
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }
}
