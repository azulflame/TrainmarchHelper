package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.repository.Market;
import me.azulflame.trainmarch.dmhelper.service.Shops;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class MarketCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        String subcommand = event.getSubcommandName();
        if (command.equals("blackmarket")) {
            if (Shops.getShop(Market.BLACK_MARKET).getShops().size() == 0) {
                event.reply("Unable to load shops. Try again later.").setEphemeral(true).queue();
            } else {
                SelectMenu menu = SelectMenu.create("choose-market").addOptions(Shops.getShop(Market.BLACK_MARKET).getShops().stream().map(x -> SelectOption.of(x, x)).collect(Collectors.toList())).build();
                event.reply("Please select a shop").setEphemeral(true).addActionRow(menu).queue();
            }
        }
        if (command.equals("black-market-admin") && subcommand != null) {
            if (!event.getChannel().getId().equals("845160869772132402") && !event.getChannel().getId().equals("1015883484545433685")) {
                event.reply("This must be run in the <#845160869772132402> channel").setEphemeral(true).queue();
            } else {
                if (subcommand.equals("add")) {
                    event.deferReply(false).queue();
                    String shop = event.getOption("shop").getAsString();
                    Shops.getShop(Market.BLACK_MARKET).add(shop, Arrays.stream(event.getOption("items").getAsString().trim().split("\\s\\s+")).toList(), true);
                    event.getHook().sendMessage("Inventory for the " + shop + " shop:\n" + String.join("\n", Shops.getShop(Market.BLACK_MARKET).getItems(shop))).setEphemeral(false).queue();
                }
                if (subcommand.equals("delete")) {
                    if (Shops.getShop(Market.BLACK_MARKET).getShops().size() == 0) {
                        event.reply("Unable to load shops. Try again later.").setEphemeral(true).queue();
                    } else {
                        event.deferReply(false).queue();
                        SelectMenu menu = SelectMenu.create("reset-markets").addOptions(Shops.getShop(Market.BLACK_MARKET).getShops().stream().map(x -> SelectOption.of(x, x)).collect(Collectors.toList())).build();
                        event.getHook().sendMessage("Please select a shop").setEphemeral(false).addActionRow(menu).queue();
                    }
                }
                if (subcommand.equals("sell")) {
                    if (Shops.getShop(Market.BLACK_MARKET).getShops().size() == 0) {
                        event.reply("Unable to load shops. Try again later.").setEphemeral(true).queue();
                    } else {
                        event.deferReply(false).queue();
                        SelectMenu menu = SelectMenu.create("sell-market").addOptions(Shops.getShop(Market.BLACK_MARKET).getShops().stream().map(x -> SelectOption.of(x, x)).collect(Collectors.toList())).build();
                        event.getHook().sendMessage("Please select a shop").setEphemeral(false).addActionRow(menu).queue();
                    }
                }
                if (subcommand.equals("wipe")) {
                    event.deferReply().queue();
                    Shops.getShop(Market.BLACK_MARKET).resetAll();
                    event.getHook().sendMessage("All shops have been reset").queue();
                }
                if (subcommand.equals("generate")) {
                    event.deferReply().queue();
                    Shops.getShop(Market.BLACK_MARKET).resetAll();
                    Shops.getShop(Market.BLACK_MARKET).generate();
                    event.getHook().sendMessage("Shops have been reset and generated").queue();
                }
            }
        }
    }

    @Override
    public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
        if (event.getComponentId().equals("choose-market")) {
            String items = String.join("\n", Shops.getShop(Market.BLACK_MARKET).getItems(event.getValues().get(0)));
            if (items.equals("")) {
                items = "Unable to load shop, please try again.";
            }
            String text = "The items in the " + event.getValues().get(0) + " shop:\n" + items;
            MessageEditData meda = new MessageEditBuilder().setComponents(new LinkedList<>()).setContent(text).build();
            event.editMessage(meda).queue();
        }
        else if (event.getComponentId().equals("reset-markets")) {
            String shop = event.getValues().get(0);
            String message = "Shop " + shop + " not found";
            if (Shops.getShop(Market.BLACK_MARKET).reset(shop) != null) {
                message = "Shop " + shop + " reset.";
            }
            MessageEditData meda = new MessageEditBuilder().setComponents(new LinkedList<>()).setContent(message).build();
            event.editMessage(meda).queue();
        }
        else if (event.getComponentId().equals("sell-market")) {
            String shop = event.getValues().get(0);
            SelectMenu itemMenu = SelectMenu.create("sell-market-items").addOptions(Shops.getShop(Market.BLACK_MARKET).getItems(shop).stream().map(x -> SelectOption.of(x, shop + ":" + x)).collect(Collectors.toSet())).build();

            MessageEditData meda = new MessageEditBuilder().setContent("Which item are you selling?").build();
            event.editMessage(meda).setActionRow(itemMenu).queue();
        }
        else if (event.getComponentId().equals("sell-market-items")) {
            String shop = event.getValues().get(0).split(":")[0];
            String item = event.getValues().get(0).split(":")[1] + ":" + event.getValues().get(0).split(":")[2];
            String message = "Unable to sell item " + item + " from the " + shop + " shop.";
            if (Shops.getShop(Market.BLACK_MARKET).sell(shop, item)) {
                message = "Sold " + item + " from the " + shop + " shop.";
            }
            MessageEditData meda = new MessageEditBuilder().setComponents(new LinkedList<>()).setContent(message).build();
            event.editMessage(meda).queue();
        }
    }
}
