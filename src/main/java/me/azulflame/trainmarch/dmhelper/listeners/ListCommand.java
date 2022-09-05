package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.backend.Lists;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.io.IOException;
import java.util.List;

public class ListCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("random"))
        {
            String list = event.getOption("list").getAsString();
            int count = event.getOption("count").getAsInt();

            if (!Lists.exists(list))
            {
                event.reply("Unable to find a list named " + list + ".").setEphemeral(true).queue();
                return;
            }
            event.reply(count + " results from the " + list + " list:\n" + String.join("\n",Lists.get(list, count))).setEphemeral(true).queue();
        }
        if (event.getName().equals("reload"))
        {
            event.deferReply().setEphemeral(true).queue();
            try {
                Lists.load();
            } catch (IOException e) {
                event.getHook().sendMessage("Unable to load lists. An error reading the lists occured.").setEphemeral(true).queue();
                return;
            }
            event.getHook().sendMessage("Lists reloaded. Commands loaded:\n" + String.join("\n", Lists.getCommands())).setEphemeral(true).queue();
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("random") && event.getFocusedOption().getName().equals("list"))
        {
            List<Command.Choice> options = Lists.getCommands().stream()
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue()))
                    .map(word -> new Command.Choice(word, word))
                    .toList();
            event.replyChoices(options).queue();
        }
    }
}
