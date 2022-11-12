package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.dto.PlayerCharacter;
import me.azulflame.trainmarch.dmhelper.repository.DatabaseManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class CoinCommand extends ListenerAdapter {
    private static final DecimalFormat dfSharp = new DecimalFormat("#.##");

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("coins")) {
            if (event.getSubcommandName().equals("add")) {
                String userId = event.getUser().getId();
                String name = event.getOption("character").getAsString();
                int platinum = 0, gold = 0, silver = 0, copper = 0;
                if (event.getOption("platinum") != null) {
                    platinum = event.getOption("platinum").getAsInt();
                }
                if (event.getOption("gold") != null) {
                    gold = event.getOption("gold").getAsInt();
                }
                if (event.getOption("silver") != null) {
                    silver = event.getOption("silver").getAsInt();
                }
                if (event.getOption("copper") != null) {
                    copper = event.getOption("copper").getAsInt();
                }
                if (platinum < 0 || gold < 0 || silver < 0 || copper < 0) {
                    event.reply("Cannot add a negative amount of currency").setEphemeral(true).queue();
                    return;
                }
                PlayerCharacter pc = DatabaseManager.getCharacter(userId, name);
                if (pc == null) {
                    event.reply("Unable to find a PC by that name.").setEphemeral(true).queue();
                    return;
                }
                pc.setPlatinum(pc.getPlatinum() + platinum);
                pc.setGold(pc.getGold() + gold);
                pc.setSilver(pc.getSilver() + silver);
                pc.setCopper(pc.getCopper() + copper);
                if (DatabaseManager.saveCharacter(pc)) {
                    event.reply("Coins added:\n" + "<:Platinum:1018311845431484547> " + pc.getPlatinum() + " " + addLeadingSign(platinum) + "\n<:Gold:1018311896337756221> " + pc.getGold() + " " + addLeadingSign(gold) + "\n<:Silver:1018311956496650302> " + pc.getSilver() + " " + addLeadingSign(silver) + "\n<:Copper:1018311994513830019> " + pc.getCopper() + " " + addLeadingSign(copper) + "\nTotal value:\n<:Gold:1018311896337756221> " +
                            dfSharp.format(totalValue(new int[]{pc.getPlatinum(), pc.getGold(), pc.getSilver(), pc.getCopper()})) + " (+" + dfSharp.format(totalValue(new int[]{platinum, gold, silver, copper})) + ")"
                    ).queue();
                }
            }
            if (event.getSubcommandName().equals("remove")) {
                String userId = event.getUser().getId();
                String name = event.getOption("character").getAsString();
                int platinum = 0, gold = 0, silver = 0, copper = 0;
                if (event.getOption("platinum") != null) {
                    platinum = event.getOption("platinum").getAsInt();
                }
                if (event.getOption("gold") != null) {
                    gold = event.getOption("gold").getAsInt();
                }
                if (event.getOption("silver") != null) {
                    silver = event.getOption("silver").getAsInt();
                }
                if (event.getOption("copper") != null) {
                    copper = event.getOption("copper").getAsInt();
                }
                if (platinum < 0 || gold < 0 || silver < 0 || copper < 0) {
                    event.reply("Cannot remove a negative amount of gold").setEphemeral(true).queue();
                    return;
                }
                PlayerCharacter pc = DatabaseManager.getCharacter(userId, name);
                if (pc == null) {
                    event.reply("Unable to find a PC by that name.").setEphemeral(true).queue();
                    return;
                }
                pc.setPlatinum(pc.getPlatinum() + platinum);
                pc.setGold(pc.getGold() + gold);
                pc.setSilver(pc.getSilver() + silver);
                pc.setCopper(pc.getCopper() + copper);

                if (pc.getPlatinum() < 0 || pc.getGold() < 0 || pc.getSilver() < 0 || pc.getCopper() < 0) {
                    event.reply("Unable to remove more coins than you have.").setEphemeral(true).queue();
                    return;
                }
                if (DatabaseManager.saveCharacter(pc)) {
                    event.reply("Coins removed:\n" + "<:Platinum:1018311845431484547> " + pc.getPlatinum() + " " + addLeadingSign(platinum) + "\n<:Gold:1018311896337756221> " + pc.getGold() + " " + addLeadingSign(gold) + "\n<:Silver:1018311956496650302> " + pc.getSilver() + " " + addLeadingSign(silver) + "\n<:Copper:1018311994513830019> " + pc.getCopper() + " " + addLeadingSign(copper) + "\nTotal value:\n<:Gold:1018311896337756221> " +
                            dfSharp.format(totalValue(pc)) + " (+" + dfSharp.format(totalValue(new int[]{platinum, gold, silver, copper})) + ")"
                    ).queue();
                }
            }
            if (event.getSubcommandName().equals("check")) {
                String name = event.getOption("character").getAsString();
                PlayerCharacter pc = DatabaseManager.getCharacter(event.getUser().getId(), name);
                if (pc == null) {
                    event.reply("Unable to find a PC by that name.").setEphemeral(true).queue();
                    return;
                }
                event.reply(name + "'s coins:\n" + "<:Platinum:1018311845431484547> " + pc.getPlatinum() + "\n<:Gold:1018311896337756221> " + pc.getGold() + "\n<:Silver:1018311956496650302> " + pc.getSilver() + "\n<:Copper:1018311994513830019> " + pc.getCopper() + "\nTotal value:\n<:Gold:1018311896337756221> " + dfSharp.format(totalValue(pc))
                ).queue();
            }
            if (event.getSubcommandName().equals("convert")) {
                int copperValue = 0;
                String name = event.getOption("character").getAsString();
                String from = event.getOption("from").getAsString();
                String to = event.getOption("to").getAsString();

                PlayerCharacter pc = DatabaseManager.getCharacter(event.getUser().getId(), name);
                if (pc == null) {
                    event.reply("Unable to find a PC by that name.").setEphemeral(true).queue();
                    return;
                }

                int[] originalCoins = new int[]{pc.getPlatinum(), pc.getGold(), pc.getSilver(), pc.getCopper()};

                int toValue = 1;
                if (to.equalsIgnoreCase("platinum")) {
                    toValue = 1000;
                }
                if (to.equalsIgnoreCase("gold")) {
                    toValue = 100;
                }
                if (to.equalsIgnoreCase("silver")) {
                    toValue = 10;
                }

                if (from.equalsIgnoreCase("platinum") || from.equalsIgnoreCase("all")) {
                    copperValue += 1000 * pc.getPlatinum();
                    pc.setPlatinum(0);
                }
                if (from.equalsIgnoreCase("gold") || from.equalsIgnoreCase("all")) {
                    copperValue += 100 * (pc.getGold() - pc.getGold() % toValue);
                    pc.setGold(pc.getGold() % toValue);
                }
                if (from.equalsIgnoreCase("silver") || from.equalsIgnoreCase("all")) {
                    copperValue += 10 * (pc.getSilver() - pc.getSilver() % toValue);
                    pc.setSilver(0);
                }
                if (from.equalsIgnoreCase("copper") || from.equalsIgnoreCase("all")) {
                    copperValue += (pc.getCopper() - pc.getCopper() % toValue);
                    pc.setCopper(0);
                }

                if (to.equalsIgnoreCase("copper")) {
                    pc.setCopper(pc.getCopper() + copperValue);
                    copperValue = 0;
                }
                pc.setSilver(pc.getSilver() + copperValue % 10);
                copperValue = copperValue / 10;
                if (to.equalsIgnoreCase("silver")) {
                    pc.setSilver(pc.getSilver() + copperValue);
                    copperValue = 0;
                }
                copperValue = copperValue / 10;
                if (to.equalsIgnoreCase("gold")) {
                    pc.setGold(pc.getGold() + copperValue);
                    copperValue = 0;
                }
                copperValue = copperValue / 10;
                if (to.equalsIgnoreCase("platinum")) {
                    pc.setPlatinum(pc.getPlatinum() + copperValue);
                }
                DatabaseManager.saveCharacter(pc);
                event.reply(name + "'s coins:\n" + "<:Platinum:1018311845431484547> " + pc.getPlatinum() + addLeadingSign(pc.getPlatinum() - originalCoins[0]) + "\n<:Gold:1018311896337756221> " + pc.getGold() + addLeadingSign(pc.getGold() - originalCoins[1]) + "\n<:Silver:1018311956496650302> " + pc.getSilver() + addLeadingSign(pc.getSilver() - originalCoins[2]) + "\n<:Copper:1018311994513830019> " + pc.getCopper() + addLeadingSign(pc.getCopper() - originalCoins[3]) + "\nTotal value:\n<:Gold:1018311896337756221> " + dfSharp.format(totalValue(pc))).queue();
            }
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("coins") && event.getFocusedOption().getName().equals("to")) {
            List<Command.Choice> choices = Arrays.stream(new String[]{"platinum", "gold", "silver", "copper"}).map(x -> new Command.Choice(x, x)).toList();
            event.replyChoices(choices).queue();
        } else if (event.getName().equals("coins") && event.getFocusedOption().getName().equals("from")) {
            List<Command.Choice> choices = Arrays.stream(new String[]{"platinum", "gold", "silver", "copper", "all"}).map(x -> new Command.Choice(x, x)).toList();
            event.replyChoices(choices).queue();
        }
    }

    private double totalValue(PlayerCharacter pc) {
        return totalValue(new int[]{pc.getPlatinum(), pc.getGold(), pc.getSilver(), pc.getCopper()});
    }

    private double totalValue(int[] coins) {
        return (double) coins[0] * 10 + (double) coins[1] + (double) coins[2] / 10 + (double) coins[3] / 100;
    }

    private String addLeadingSign(int num) {
        if (num < 0) {
            return " (" + num + ")";
        }
        if (num > 0) {
            return " (+" + num + ")";
        }
        return "";
    }
}
