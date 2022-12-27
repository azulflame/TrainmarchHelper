package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.repository.Market;
import me.azulflame.trainmarch.dmhelper.service.Shops;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class TradeCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("trade-market")) {
            String store = event.getOption("section").getAsString();

            Set<String> shops = Shops.getShop(Market.TRADE_MARKET).getShops();
            if (!shops.contains(store)) {
                event.reply("Unable to find that store name. Check the autofill for the store names.").setEphemeral(true).queue();
                return;
            }
            String items = String.join("\n", Shops.getShop(Market.TRADE_MARKET).getItems(store));
            event.reply("The items in the Trade Market's " + store + " section:\n" + items).setEphemeral(true).queue();
        } else if (event.getName().equals("trade-market-admin")) {
            if (!event.getChannel().getId().equals("1037673806690533436") && !event.getChannel().getId().equals("1015883484545433685")) {
                event.reply("This is the wrong channel to use this command in. Try again in <#1037673806690533436> .").setEphemeral(true).queue();
                return;
            } else if (event.getSubcommandName().equals("add")) {
                String store = event.getOption("section").getAsString();
                String items = event.getOption("items").getAsString();
                List<String> itemList = Arrays.stream(items.split("\\s\\s+")).map(String::trim).toList();
                Shops.getShop(Market.TRADE_MARKET).add(store, itemList, true);
                event.reply("These items were added to the " + store + " section:\n" + String.join("\n", itemList)).queue();
            } else if (event.getSubcommandName().equals("delete")) {
                String section = event.getOption("section").getAsString();
                if (!Shops.getShop(Market.TRADE_MARKET).getShops().contains(section)) {
                    event.reply("Unable to find the " + section + " section.").setEphemeral(true).queue();
                    return;
                }
                Shops.getShop(Market.TRADE_MARKET).reset(section);
                event.reply("Section " + section + " reset.").queue();
            } else if (event.getSubcommandName().equals("delete-all")) {
                Set<String> shops = new HashSet<String>(Shops.getShop(Market.TRADE_MARKET).getShops());
                for (String s : shops) {
                    if (!s.equals("Player Items")) {
                        Shops.getShop(Market.TRADE_MARKET).reset(s);
                    }
                }
                event.reply("All sections reset.").queue();
            } else if (event.getSubcommandName().equals("sell")) {
                String newSection = "Player Items";
                String player = event.getOption("player").getAsMember().getUser().getName();
                String soldItem = event.getOption("sold-item").getAsString();
                String section = event.getOption("section").getAsString();
                String item1 = null != event.getOption("item1") ? event.getOption("item1").getAsString() + " - " + player : "";
                String item2 = null != event.getOption("item2") ? event.getOption("item2").getAsString() + " - " + player : "";
                String item3 = null != event.getOption("item3") ? event.getOption("item3").getAsString() + " - " + player : "";
                if (!Shops.getShop(Market.TRADE_MARKET).getShops().contains(section)) {
                    event.reply("The section " + section + " does not exist.").setEphemeral(true).queue();
                    return;
                }
                if (!Shops.getShop(Market.TRADE_MARKET).getItems(section).contains(soldItem)) {
                    event.reply("The section " + section + " does not contain the item " + soldItem + ".").setEphemeral(true).queue();
                    return;
                }
                if (soldItem.endsWith(player))
                {
                    event.reply("Sale rejected: Moving items between PCs of the same player.").queue();
                    return;
                }

                List<String> items = Stream.of(item1, item2, item3).filter(x -> !x.equals("")).toList();
                Shops.getShop(Market.TRADE_MARKET).sell(section, soldItem);
                Shops.getShop(Market.TRADE_MARKET).add(newSection, items, true);
                event.reply("Sold from " + section + ":\n" + soldItem + "\n\nAdded to stock:\n" + String.join("\n", items)).queue();
            }
            else if(event.getSubcommandName().equals("remove")) {
                if (!event.getMember().hasPermission(Permission.ADMINISTRATOR))
                {
                    event.reply("Unable to complete this command. Please contact <@511302118963937280> or <@102187100476014592> .").setEphemeral(true).queue();
                    return;
                }
                else
                {
                    String section = event.getOption("section").getAsString();
                    String item = event.getOption("item").getAsString();
                    if (!Shops.getShop(Market.TRADE_MARKET).getShops().contains(section))
                    {
                        event.reply("Unable to find the " + section + " section.").setEphemeral(true).queue();
                        return;
                    }
                    if (!Shops.getShop(Market.TRADE_MARKET).getItems(section).contains(item))
                    {
                        event.reply("Unable to find the " + item + " in the " + section + " section.").setEphemeral(true).queue();
                        return;
                    }
                    Shops.getShop(Market.TRADE_MARKET).sell(section, item);
                    event.reply("Removed " + item + " from the " + section + " section.").queue();
                }

            }
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if ((event.getName().equals("trade-market") || event.getName().equals("trade-market-admin")) && event.getFocusedOption().getName().equals("section")) {
            List<Command.Choice> options = Shops.getShop(Market.TRADE_MARKET).getShops().stream()
                    .filter(word -> word.toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(word -> new Command.Choice(word, word))
                    .limit(25)
                    .toList();
            event.replyChoices(options).queue();
            return;
        } else if (event.getName().equals("trade-market-admin")) {
            if (event.getFocusedOption().getName().equals("sold-item") || event.getFocusedOption().getName().equals("item")) {
                if (null != event.getOption("section")) {
                    String section = event.getOption("section").getAsString();
                    List<Command.Choice> choices = Shops.getShop(Market.TRADE_MARKET).getItems(section).stream()
                            .filter(word -> word.toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                            .map(word -> new Command.Choice(word, word))
                            .limit(25)
                            .toList();
                    event.replyChoices(choices).queue();
                    return;
                }
            }
        }
    }
}
