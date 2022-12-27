package me.azulflame.trainmarch.dmhelper.listeners;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class CommandModeEnforcer extends ListenerAdapter {

    private static Set<String> ids = new HashSet<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (ids.contains(event.getChannel().getId())) {
            if (!event.getMember().getUser().isBot()) {
                if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    event.getMessage().delete().queue();
                }
            }
        }
    }

    public static boolean lockChannel(String id)
    {
        return ids.add(id);
    }

    public static boolean unlockChannel(String id)
    {
        return ids.remove(id);
    }

}
