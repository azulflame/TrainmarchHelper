package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.repository.DatabaseManager;
import me.azulflame.trainmarch.dmhelper.service.Difficulty;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.Arrays;
import java.util.List;

public class CommonAutofill extends ListenerAdapter {

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getFocusedOption().getName().equals("difficulty")) {
            List<Command.Choice> options = Arrays.stream(Difficulty.values())
                    .filter(word -> word.getName().contains(event.getFocusedOption().getValue()))
                    .map(word -> new Command.Choice(word.getName(), word.getName()))
                    .limit(25)
                    .toList();
            event.replyChoices(options).queue();
//        } else if (event.getFocusedOption().getName().equals("character")) {
//            List<String> names = DatabaseManager.getCharacters(event.getUser().getId());
//            List<Command.Choice> options = names.stream()
//                    .filter(name -> name.toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
//                    .map(name -> new Command.Choice(name, name))
//                    .limit(25)
//                    .toList();
//            event.replyChoices(options).queue();
//        }
//        else if(event.getFocusedOption().getName().equals("workplace")) {
//            List<String> names = DowntimeCommand.workplaces;
//            List<Command.Choice> options = names.stream()
//                    .filter(name -> name.toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
//                    .map(name -> new Command.Choice(name, name))
//                    .limit(25)
//                    .toList();
//            event.replyChoices(options).queue();
        }
    }
}
