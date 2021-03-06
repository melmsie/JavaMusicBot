package ovh.not.javamusicbot.command;

import ovh.not.javamusicbot.Command;
import ovh.not.javamusicbot.GuildMusicManager;

public class LoopCommand extends Command {
    public LoopCommand() {
        super("loop");
    }

    @Override
    public void on(Context context) {
        GuildMusicManager musicManager = GuildMusicManager.get(context.getEvent().getGuild());
        if (musicManager == null || musicManager.getPlayer().getPlayingTrack() == null) {
            context.reply("No music is playing on this guild! To play a song use `{{prefix}}play`");
            return;
        }
        boolean loop = !musicManager.getScheduler().isLoop();
        musicManager.getScheduler().setLoop(loop);
        context.reply("**%s** queue looping!", loop ? "Enabled" : "Disabled");
    }
}
