package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.repository.DatabaseManager;
import me.azulflame.trainmarch.dmhelper.repository.Progress;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.*;
import java.util.stream.Collectors;

public class DowntimeCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("downtime")) {
            if (event.getSubcommandName().equals("check")) {
                String userID = event.getUser().getId();
                String character = event.getOption("character").getAsString();

                int dt = DatabaseManager.getDowntime(userID, character);

                if (dt == -999) {
                    event.reply(character + " is not registered in the DT system. Please add some downtime to use them.").setEphemeral(true).queue();
                    return;
                }

                event.reply(character + " has " + dt + " downtime.").setEphemeral(true).queue();
                return;
            } else if (event.getSubcommandName().equals("check-all")) {
                String userId = event.getUser().getId();

                Map<String, Integer> downtimes = DatabaseManager.getAllDowntime(userId);
                if (downtimes.isEmpty()) {
                    event.reply("Unable to find any characters for you. Please add downtime for a character and try again.").setEphemeral(true).queue();
                    return;
                }
                String output = "Downtime for your characters: \n" +
                        downtimes.entrySet().stream().map(x -> x.getKey() + ": " + x.getValue()).collect(Collectors.joining("\n"));
                event.reply(output).setEphemeral(true).queue();
                return;
            } else if (event.getSubcommandName().equals("add")) {
                if (!event.getChannel().getId().equals("930144523001151518") && !event.getChannel().getId().equals("1015883484545433685")) {
                    event.reply("Please use this in the downtime-commands channel").setEphemeral(true).queue();
                    return;
                }
                String userId = event.getUser().getId();
                String character = event.getOption("character").getAsString();
                int amount = event.getOption("amount").getAsInt();
                String reason = event.getOption("reason").getAsString();

                if (amount < 0) {
                    event.reply("Amount must not be negative").setEphemeral(true).queue();
                    return;
                }
                int current = DatabaseManager.getDowntime(userId, character);
                if (current == -999) {
                    current = 0;
                }
                DatabaseManager.setDowntime(userId, character, current + amount);
                event.reply("Downtime added to " + character + "\nOld: " + current + "\nChange: " + amount + "\nNew: " + (current + amount) + "\nReason: " + reason).queue();
                return;
            } else if (event.getSubcommandName().equals("start")) {
                if (!event.getChannel().getId().equals("930144523001151518") && !event.getChannel().getId().equals("1015883484545433685")) {
                    event.reply("Please use this in the downtime-commands channel").setEphemeral(true).queue();
                    return;
                }
                String userId = event.getUser().getId();
                String character = event.getOption("character").getAsString();
                String element = event.getOption("action").getAsString();
                int amount = event.getOption("amount").getAsInt();
                int total = event.getOption("total-to-reach").getAsInt();

                if (total <= 0) {
                    event.reply("Total must be greater than 0").setEphemeral(true).queue();
                    return;
                }
                if (amount < 0) {
                    event.reply("Amount must not be negative").setEphemeral(true).queue();
                    return;
                }
                int current = DatabaseManager.getDowntime(userId, character);
                if (current == -999) {
                    event.reply("Add downtime to your character before you can use it.").setEphemeral(true).queue();
                    return;
                }
                if (current - amount < 0) {
                    event.reply("You cannot use more downtime than you have").setEphemeral(true).queue();
                    return;
                }

                Set<Progress> progress = DatabaseManager.getCurrentProgress(userId, character);
                for (Progress p : progress) {
                    if (p.userId().equals(userId) && p.character().equalsIgnoreCase(character) && p.element().equalsIgnoreCase(element) && p.total() == total) {
                        event.reply("You cannot start something already started. Please use **progress** instead.").setEphemeral(true).queue();
                        return;
                    }
                }
                DatabaseManager.setProgress(userId, character, element, amount, total);
                DatabaseManager.setDowntime(userId, character, current - amount);
                event.reply(character + " started working on " + element + ":\nDowntime " + current + " -> " + (current - amount) + "\nProgress: 0/" + total + " -> " + amount + "/" + total + "\nAction:" + element).queue();
                return;
            } else if (event.getSubcommandName().equals("check-progress")) {

                Set<Progress> output = DatabaseManager.getCurrentProgress(event.getUser().getId());
                String outString = "Your downtime progresses:";
                for (Progress p : output) {
                    outString = outString + "\nCharacter: " + p.character() + " Action: " + p.element() + " Current Progress: " + p.current() + "/" + p.total();
                }
                event.reply(outString).setEphemeral(true).queue();
                return;
            } else if (event.getSubcommandName().equals("progress")) {
                if (!event.getChannel().getId().equals("930144523001151518") && !event.getChannel().getId().equals("1015883484545433685")) {
                    event.reply("Please use this in the downtime-commands channel").setEphemeral(true).queue();
                    return;
                }
                String userId = event.getUser().getId();
                String character = event.getOption("character").getAsString();
                String element = event.getOption("action").getAsString();
                int amount = event.getOption("amount").getAsInt();

                if (amount < 0) {
                    event.reply("Amount must not be negative").setEphemeral(true).queue();
                    return;
                }
                int current = DatabaseManager.getDowntime(userId, character);
                if (current == -999) {
                    event.reply("Add downtime to your character before you can use it.").setEphemeral(true).queue();
                    return;
                }
                if (current - amount < 0) {
                    event.reply("You cannot use more downtime than you have").setEphemeral(true).queue();
                    return;
                }

                Set<Progress> progress = DatabaseManager.getCurrentProgress(userId, character);
                Progress action = null;
                for (Progress p : progress) {
                    if (p.userId().equals(userId) && p.character().equalsIgnoreCase(character) && p.element().equalsIgnoreCase(element)) {
                        action = p;
                    }
                }
                if (action == null) {
                    event.reply("Unable to progress an action that hasn't been started, please use **start** to start the action.").setEphemeral(true).queue();
                    return;
                }
                if (action.current() + amount > action.total()) {
                    amount = action.total() - action.current();
                    DatabaseManager.setProgress(userId, action.character(), action.element(), action.total(), action.total());
                    DatabaseManager.setDowntime(userId, character, current - amount);
                    event.reply(character + " finished working on " + element + ":\nProgress: " + action.current() + "/" + action.total() + " -> " + action.total() + "/" + action.total() + "\nDowntime: " + current + " -> " + (current - amount) + "\nAction completed: " + element).queue();
                } else {
                    DatabaseManager.setProgress(userId, action.character(), action.element(), action.current() + amount, action.total());
                    DatabaseManager.setDowntime(userId, character, current - amount);
                    event.reply(character + " progressed working on " + element + ":\nDowntime: " + current + " -> " + (current - amount) + "\nProgress: " + action.current() + "/" + action.total() + " -> " + (action.current() + amount) + "/" + action.total() + "\nAction: " + element).queue();
                }
                return;
            } else if (event.getSubcommandName().equals("use")) {
                if (!event.getChannel().getId().equals("930144523001151518") && !event.getChannel().getId().equals("1015883484545433685")) {
                    event.reply("Please use this in the downtime-commands channel").setEphemeral(true).queue();
                    return;
                }
                String userId = event.getUser().getId();
                String character = event.getOption("character").getAsString();
                int amount = event.getOption("amount").getAsInt();
                String reason = event.getOption("reason").getAsString();

                if (amount < 0) {
                    event.reply("Amount must not be negative").setEphemeral(true).queue();
                    return;
                }
                int current = DatabaseManager.getDowntime(userId, character);
                if (current == -999) {
                    event.reply("Add downtime to your character before you can use it.").setEphemeral(true).queue();
                    return;
                }
                if (current - amount < 0) {
                    event.reply("You cannot use more downtime than you have").setEphemeral(true).queue();
                    return;
                }
                DatabaseManager.setDowntime(userId, character, current - amount);
                event.reply("Downtime used by " + character + "\nOld: " + current + "\nChange: " + amount + "\nNew: " + (current - amount) + "\nReason: " + reason).queue();
                return;
            }
        }
    }

    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("downtime") && event.getFocusedOption().getName().equals("character")) {
            List<Command.Choice> options = DatabaseManager.getAllDowntime(event.getUser().getId()).keySet().stream()
                    .filter(name -> name.toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(name -> new Command.Choice(name, name))
                    .limit(25)
                    .toList();
            event.replyChoices(options).queue();
        }
        else if(event.getName().equals("downtime") && event.getSubcommandName().equals("progress") && event.getFocusedOption().getName().equals("action"))
        {
            List<Command.Choice> options = DatabaseManager.getCurrentProgress(event.getUser().getId(), event.getOption("character").getAsString()).stream()
                    .filter(p -> p.element().toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(x -> new Command.Choice(x.element(), x.element()))
                    .limit(25)
                    .toList();
            event.replyChoices(options).queue();
        }
    }

}
