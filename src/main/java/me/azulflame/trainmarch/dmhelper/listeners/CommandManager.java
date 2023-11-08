package me.azulflame.trainmarch.dmhelper.listeners;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CommandManager extends ListenerAdapter {

    // Log all command interactions
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String args = event.getOptions().stream().map(x -> x.getName() + ": " + x.getAsString()).collect(Collectors.joining(", "));
        log.info("Command " + event.getName() + " invoked by " + event.getUser().getName() + " (ID " + event.getUser().getId() + "). Subcommand: " + event.getSubcommandName() + " args: " + args);
    }

    // Guild commands -- instant update, max 100

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("bazaar", "View the available items from the weekly shops"));
        commandData.add(Commands.slash("bazaar-admin", "Manage the Bazaar")
                .addSubcommands(
                        new SubcommandData("add", "Add items to a shop")
                                .addOption(OptionType.STRING, "shop", "The shop to add items to", true)
                                .addOption(OptionType.STRING, "items", "The items to add to the shop", true),
                        new SubcommandData("delete", "Delete a shop"),
                        new SubcommandData("sell", "Sell an item from a shop"),
                        new SubcommandData("wipe", "Wipe all the shops"),
                        new SubcommandData("generate", "Generate all shop data"))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_TTS)));
        commandData.add(Commands.slash("rewards", "Calculate rewards for a quest")
                .addOption(OptionType.NUMBER, "time", "The length of the quest, in hours", true)
                .addOption(OptionType.NUMBER, "t1", "The number of tier 1 players", true)
                .addOption(OptionType.NUMBER, "t2", "The number of tier 2 players", true)
                .addOption(OptionType.NUMBER, "t3", "The number of tier 3 players", true)
                .addOption(OptionType.NUMBER, "t4", "The number of tier 4 players", true)
                .addOption(OptionType.STRING, "difficulty", "The difficulty of the quest", true, true)
                .addOption(OptionType.STRING, "quest-type", "Was this a VC quest?", true, true)
        );
        commandData.add(Commands.slash("items", "Generate the minimum items for your quest")
                .addOption(OptionType.INTEGER, "tier", "The tier of the players", true)
                .addOption(OptionType.INTEGER, "players", "The number of the players", true)
                .addOption(OptionType.STRING, "difficulty", "The difficulty of the quest", true, true));
        commandData.add(Commands.slash("scrolls", "Generate the number of scrolls needed to be available for a wizard in a quest")
                .addOption(OptionType.INTEGER, "players", "The number of players", true)
                .addOption(OptionType.INTEGER, "tier", "The tier of the player(s)", true)
                .addOption(OptionType.STRING, "difficulty", "The difficulty of the quest", true, true));
        commandData.add(Commands.slash("random", "Select a random item from a list")
                .addOption(OptionType.STRING, "list", "The name of a list", true, true)
                .addOption(OptionType.INTEGER, "count", "The number of items to pull", true));
        commandData.add(Commands.slash("reload", "Reload the custom lists")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)));
        commandData.add(Commands.slash("dmxp", "Adjust or check DMXP").addSubcommands(
                        new SubcommandData("check", "Check a DM's dmxp")
                                .addOption(OptionType.USER, "target", "Thee user to check the DMXP of", true),
                        new SubcommandData("update", "Update your DMXP")
                                .addOption(OptionType.INTEGER, "change", "The change in DMXP", true)
                                .addOption(OptionType.STRING, "reason", "The reason for the change", true),
                        new SubcommandData("give", "Give another user some of your DMXP")
                                .addOption(OptionType.USER, "recipient", "The DM to give your DMXP to", true)
                                .addOption(OptionType.INTEGER, "amount", "The amount of DMXP to give", true)
                                .addOption(OptionType.STRING, "reason", "The reason to give the DMXP", true))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.VOICE_MUTE_OTHERS)));
        commandData.add(Commands.slash("blackmarket", "View the available items from the black market"));
        commandData.add(Commands.slash("black-market-admin", "Manage the Black Market")
                .addSubcommands(
                        new SubcommandData("add", "Add items to a shop")
                                .addOption(OptionType.STRING, "shop", "The shop to add items to", true)
                                .addOption(OptionType.STRING, "items", "The items to add to the shop", true),
                        new SubcommandData("delete", "Delete a shop"),
                        new SubcommandData("sell", "Sell an item from a shop"),
                        new SubcommandData("wipe", "Delete all shops"),
                        new SubcommandData("generate", "Generate the shops for the week"))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_TTS)));
        commandData.add(Commands.slash("timestamp", "Generate a formatted timestamp")
                .addOption(OptionType.STRING, "timezone", "The timezone the time is in", true, true)
                .addOption(OptionType.INTEGER, "year", "The year of the target time.")
                .addOption(OptionType.INTEGER, "month", "The month of the target time.")
                .addOption(OptionType.INTEGER, "day", "The day of the month for the target time")
                .addOption(OptionType.INTEGER, "hour", "The hour of the target time.")
                .addOption(OptionType.INTEGER, "minute", "The minute of the target time.")
                .addOption(OptionType.INTEGER, "second", "The second of the target time.")
                .addOption(OptionType.BOOLEAN, "is-pm", "12 hour PM indicator"));
        commandData.add(Commands.slash("create-housing", "Create housing for a user")
                .addOption(OptionType.USER, "player", "The player the house is for", true)
                .addOption(OptionType.STRING, "character", "The character who paid for the house", true)
                .addOption(OptionType.STRING, "housing-type", "The type of the house", true, true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)));
        commandData.add(Commands.slash("register-housing", "Register the owner of a house")
                .addOption(OptionType.USER, "new-owner", "The new owner of the housing channel", true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)));
        commandData.add(Commands.slash("trade-market", "View the items in a trade market section")
                .addOption(OptionType.STRING, "section", "The section to view the items in", true, true));
        commandData.add(Commands.slash("trade-market-admin", "A collection of admin commands for the trade market")
                .addSubcommands(
                        new SubcommandData("add", "Add one or more items to a section in the trade market")
                                .addOption(OptionType.STRING, "section", "The section to add the items to", true, true)
                                .addOption(OptionType.STRING, "items", "The items to add to the section", true),
                        new SubcommandData("delete", "Delete a section and all of its items")
                                .addOption(OptionType.STRING, "section", "The section to delete all items from", true, true),
                        new SubcommandData("delete-all", "Delete all sections and all their items"),
                        new SubcommandData("sell", "Sell an item to a player")
                                .addOption(OptionType.USER, "player", "The player the transaction was with", true)
                                .addOption(OptionType.STRING, "section", "The section to sell the item from", true, true)
                                .addOption(OptionType.STRING, "sold-item", "The item that was sold", true, true)
                                .addOption(OptionType.STRING, "item1", "The first item that was traded in", true)
                                .addOption(OptionType.STRING, "item2", "The second item that was traded", true)
                                .addOption(OptionType.STRING, "item3", "The third item that was traded"),
                        new SubcommandData("remove", "Remove a specific item. Admin-only.")
                                .addOption(OptionType.STRING, "section", "The section to remove the item from", true, true)
                                .addOption(OptionType.STRING, "item", "The item to remove", true, true),
                        new SubcommandData("generate", "Generate the shops for the week"))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_TTS)));
        commandData.add(Commands.slash("downtime", "Manage your character's downtime")
                .addSubcommands(
                        new SubcommandData("check-all", "Check the downtime for all of your characters"),
                        new SubcommandData("check", "Check the downtime for a character")
                                .addOption(OptionType.STRING, "character", "The character to check the downtime of", true, true),
                        new SubcommandData("add", "Add downtime to your character")
                                .addOption(OptionType.STRING, "character", "The character to add downtime to", true, true)
                                .addOption(OptionType.INTEGER, "amount", "The amount of downtime to add", true)
                                .addOption(OptionType.STRING, "reason", "The reason to add downtime", true),
                        new SubcommandData("use", "Use downtime from your character")
                                .addOption(OptionType.STRING, "character", "The character to remove downtime from", true, true)
                                .addOption(OptionType.INTEGER, "amount", "The amount of downtime to remove", true)
                                .addOption(OptionType.STRING, "reason", "The reason to remove downtime", true),
                        new SubcommandData("start", "Start a progress-based downtime event")
                                .addOption(OptionType.STRING, "character", "The character that is doing the action", true, true)
                                .addOption(OptionType.STRING, "action", "The action you are undertaking", true)
                                .addOption(OptionType.INTEGER, "amount", "The initial amount of progress you are using", true)
                                .addOption(OptionType.INTEGER, "total-to-reach", "The completion progress for what you are working on", true),
                        new SubcommandData("progress", "Put downtime towards a progress-based downtime event")
                                .addOption(OptionType.STRING, "character", "The character that is doing the action", true, true)
                                .addOption(OptionType.STRING, "action", "The action you are working on", true, true)
                                .addOption(OptionType.INTEGER, "amount", "The amount of downtime you are putting towards the action", true),
                        new SubcommandData("check-progress", "Check the progress of your downtime progress actions")
//                        new SubcommandData("craft", "Craft an item using downtime")
//                                .addOption(OptionType.STRING, "character", "The character to craft on", true, true)
//                                .addOption(OptionType.STRING, "item", "The item you will be crafting", true, true)
//                                .addOption(OptionType.STRING, "downtime-amount", "The amount of downtime to use. The excess used will be refunded.", true)
//                                .addOption(OptionType.BOOLEAN, "level-10-artificer", "Whether or not your character has the level 10 artificer ability"),
//                        new SubcommandData("remove-character", "Remove a character from the downtime tracking")
//                                .addOption(OptionType.STRING, "character", "The character to remove", true, true)
//                        new SubcommandData("daily", "Add daily downtime to all characters")
//                                .addOption(OptionType.STRING, "character", "The character to remove downtime from", true, true)
//                                .addOption(OptionType.STRING, "reason", "The reason to remove downtime", true)
                ));
        commandData.add(Commands.slash("admin", "A suite of administrator commands to check what state other characters are in")
                .addSubcommands(
                        new SubcommandData("check-downtime", "Check a player's Downtime")
                                .addOption(OptionType.USER, "player", "The player to check the downtime of", true),
                        new SubcommandData("check-all-downtime", "Check all the downtime of a certain player")
                                .addOption(OptionType.USER, "player", "The player to check the downtime of", true),
                        new SubcommandData("enforce-command-only", "Locks the channel to where it can only have bot command outputs saved."),
                        new SubcommandData("unlock-command-only", "Unlocks a channel from command-only mode"))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
        );
        commandData.add(Commands.slash("housing", "A collection of housing management related commands")
                .addSubcommands(
                        new SubcommandData("allow", "Allow a user to see the channel")
                                .addOption(OptionType.USER, "player", "The player to add to the channel", true),
                        new SubcommandData("restrict", "Restrict a user from seeeing this channel")
                                .addOption(OptionType.USER, "player", "The player to restrict from the channel", true)
                ));
        commandData.add(Commands.slash("compute-dmxp", "Compute the DMXP you get from a quest, given the time and rating")
                .addOption(OptionType.NUMBER, "time", "The time the quest ran for", true)
                .addOption(OptionType.NUMBER, "average-rating", "The average rating out of 10", true));
        commandData.add(Commands.slash("sheet", "Utilities for Sheet Checkers").addSubcommands(
                        new SubcommandData("approve", "Approve a character's sheet")
                                .addOption(OptionType.USER, "player", "The player who is being approved", true)
                                .addOption(OptionType.STRING, "character", "The character name being approved", true)
                                .addOption(OptionType.STRING, "sheet-link", "The link of the sheet being approved", true),
                        new SubcommandData("find", "See who has a character. Also does partial matches.")
                                .addOption(OptionType.STRING, "character", "The character name to look up.", true))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES)));
        commandData.add(Commands.message("Inspect").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)));
        commandData.add(Commands.message("Inspect (Bytes)").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)));
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
