package me.azulflame.trainmarch.dmhelper.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    public static List<ListenerAdapter> getListeners()
    {
        return List.of(
                new CommandManager()
                , new ItemCommand()
                , new RewardCommand()
                , new ScrollsCommand()
                , new ShopCommand()
                , new ListCommand()
        );
    }

    // Guild commands -- instant update, max 100

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("bazaar", "View the available items from the weekly shops"));
        commandData.add(Commands.slash("shop", "Add items to a shop")
                .addSubcommands(
                        new SubcommandData("add", "Add items to a shop")
                            .addOption(OptionType.STRING, "shop", "The shop to add items to", true)
                            .addOption(OptionType.STRING, "items", "The items to add to the shop", true),
                        new SubcommandData("delete", "Delete a shop"),
                        new SubcommandData("sell", "Sell an item from a shop"))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_TTS)));
        commandData.add(Commands.slash("rewards", "Calculate rewards for a quest")
                .addOption(OptionType.NUMBER, "time", "The length of the quest, in hours", true)
                .addOption(OptionType.NUMBER, "tier", "The tier of the quest", true)
                .addOption(OptionType.STRING, "difficulty", "The difficulty of the quest", true)
                .addOption(OptionType.BOOLEAN, "vc", "Was this a VC quest?", true));
        commandData.add(Commands.slash("items", "Get the minimum items for your quest")
                .addOption(OptionType.INTEGER, "tier", "The tier of the players", true)
                .addOption(OptionType.INTEGER, "players", "The number of the players", true)
                .addOption(OptionType.STRING, "difficulty", "The difficulty of the quest", true));
        commandData.add(Commands.slash("scrolls", "Identify the number of scrolls needed to be available for a wizard in a quest")
                .addOption(OptionType.INTEGER, "players", "The number of players", true)
                .addOption(OptionType.INTEGER, "tier", "The tier of the player(s)", true)
                .addOption(OptionType.STRING, "difficulty", "The difficulty of the quest", true));
        commandData.add(Commands.slash("random", "Select a random item from a list")
                .addOption(OptionType.STRING, "list", "The name of a list", true, true)
                .addOption(OptionType.INTEGER, "count", "The number of items to pull", true));
        commandData.add(Commands.slash("reload", "Reload the custom lists")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)));
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
