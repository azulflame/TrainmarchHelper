package me.azulflame.trainmarch.dmhelper.listeners;

import lombok.extern.slf4j.Slf4j;
import me.azulflame.trainmarch.dmhelper.repository.DatabaseManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class AdminCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("admin"))
        {
            if (event.getSubcommandName().equals("check-downtime"))
            {
                User user = event.getOption("player").getAsUser();
                String character = event.getOption("character").getAsString();

                int dt = DatabaseManager.getDowntime(user.getId(), character);
                if (dt == -999)
                {
                    event.reply(user.getAsTag() + "'s " + character + " is not registered in the downtime system.").setEphemeral(true).queue();
                    return;
                }
                event.reply(user.getAsTag() + "'s " + character + " has " + dt + " downtime.").setEphemeral(true).queue();
                return;
            }
            else if(event.getSubcommandName().equals("check-all-downtime"))
            {
                User user = event.getOption("player").getAsUser();

                Map<String, Integer> dts = DatabaseManager.getAllDowntime(user.getId());
                if (dts.isEmpty())
                {
                    event.reply("Unable to find any characters for " + user.getAsTag()).setEphemeral(true).queue();
                    return;
                }
                String output = "Downtime for " + user.getAsTag() + "'s characters:\n" +
                        dts.entrySet().stream().map(x -> x.getKey() + ": " + x.getValue()).collect(Collectors.joining("\n"));
                event.reply(output).setEphemeral(true).queue();
                return;
            }
            else if(event.getSubcommandName().equals("enforce-command-only"))
            {
                String id = event.getChannel().getId();
                if (CommandModeEnforcer.lockChannel(id))
                {
                    DatabaseManager.lockChannel(id);
                    event.reply("This channel has been forced into command-only mode by " + event.getMember().getAsMention()).queue();
                }
                else
                {
                    event.reply("This channel is already in command-only mode.").queue();
                }
                return;
            }
            else if(event.getSubcommandName().equals("unlock-command-only"))
            {
                String id = event.getChannel().getId();
                if (CommandModeEnforcer.unlockChannel(id))
                {
                    DatabaseManager.unlockChannel(id);
                    event.reply("This channel has been unlocked from command-only mode by " + event.getMember().getAsMention()).queue();
                }
                else
                {
                    event.reply("This channel is not in command-only mode.").queue();
                }
                return;
            }
        }
    }

    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("admin") && event.getSubcommandName().equals("check-downtime") && event.getFocusedOption().getName().equals("character")) {
            List<Command.Choice> options = DatabaseManager.getAllDowntime(event.getOption("player").getAsUser().getId()).keySet().stream()
                    .filter(name -> name.toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(name -> new Command.Choice(name, name))
                    .limit(25)
                    .toList();
            event.replyChoices(options).queue();
        }
    }
}
