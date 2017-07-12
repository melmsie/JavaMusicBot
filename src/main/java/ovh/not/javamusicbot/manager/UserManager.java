package ovh.not.javamusicbot.manager;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import ovh.not.javamusicbot.Config;

import java.util.List;

public class UserManager {
    private final Guild guild;
    private final User self;
    private final Role supporter;
    private final Role superSupporter;
    private final Role superDuperSupporter;

    UserManager(Config config, ShardManager shardManager) {
        guild = shardManager.findGuild(config.discordServer);
        self = guild.getSelfMember().getUser();
        supporter = guild.getRoleById(config.supporterRole);
        superSupporter = guild.getRoleById(config.superSupporterRole);
        superDuperSupporter = guild.getRoleById(config.superDuperSupporterRole);
    }

    private boolean hasRole(User user, Role target) {
        if (user == self) {
            return false;
        }
        Member member = guild.getMember(user);
        if (member == null) {
            return false;
        }
        List<Role> roles = member.getRoles();
        if (roles == null || roles.size() == 0) {
            return false;
        }
        for (Role role : roles) {
            if (role == target) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSupporter(User user) {
        return hasRole(user, supporter);
    }

    public boolean hasSuperSupporter(User user) {
        return hasRole(user, superSupporter);
    }

    public boolean hasSuperDuperSupporter(User user) {
        return hasRole(user, superDuperSupporter);
    }
}
