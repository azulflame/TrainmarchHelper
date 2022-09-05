package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.backend.Shops;
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

public class ShopCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        String subcommand = event.getSubcommandName();
        if (command.equals("bazaar")) {
            if (Shops.getShops().size() == 0) {
                event.reply("Unable to load shops. Try again later.").setEphemeral(true).queue();
            } else {
                SelectMenu menu = SelectMenu.create("choose-shop").addOptions(Shops.getShops().stream().map(x -> {
                    return SelectOption.of(x, x);
                }).collect(Collectors.toList())).build();
                event.reply("Please select a shop").setEphemeral(true).addActionRow(menu).queue();
            }
        }
        if (command.equals("shop") && subcommand != null) {
            if (!event.getChannel().getId().equals("945066980577267752") && !event.getChannel().getId().equals("1015883484545433685")) {
                event.reply("This must be run in the shop-keepers channel").setEphemeral(true).queue();
            } else {
                if (subcommand.equals("add")) {
                    String shop = event.getOption("shop").getAsString();
                    Shops.add(shop, Arrays.stream(event.getOption("items").getAsString().trim().split("\\s\\s+")).toList());
                    event.reply("Inventory for the " + shop + " shop:\n" + String.join("\n", Shops.getItems(shop))).setEphemeral(false).queue();
                }
                if (subcommand.equals("delete")) {
                    if (Shops.getShops().size() == 0) {
                        event.reply("Unable to load shops. Try again later.").setEphemeral(true).queue();
                    } else {
                        SelectMenu menu = SelectMenu.create("reset-shop").addOptions(Shops.getShops().stream().map(x -> {
                            return SelectOption.of(x, x);
                        }).collect(Collectors.toList())).build();
                        event.reply("Please select a shop").setEphemeral(false).addActionRow(menu).queue();
                    }
                }
                if (subcommand.equals("sell")) {
                    if (Shops.getShops().size() == 0) {
                        event.reply("Unable to load shops. Try again later.").setEphemeral(true).queue();
                    } else {
                        SelectMenu menu = SelectMenu.create("sell-shop").addOptions(Shops.getShops().stream().map(x -> {
                            return SelectOption.of(x, x);
                        }).collect(Collectors.toList())).build();
                        event.reply("Please select a shop").setEphemeral(false).addActionRow(menu).queue();
                    }
                }
            }
        }
    }

    @Override
    public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
        if (event.getComponentId().equals("choose-shop")) {
            String items = String.join("\n", Shops.getItems(event.getValues().get(0)));
            if (items.equals("")) {
                items = "Unable to load shop, please try again.";
            }
            String text = "The items in the " + event.getValues().get(0) + " shop:\n" + items;
            MessageEditData meda = new MessageEditBuilder().setComponents(new LinkedList<>()).setContent(text).build();
            event.editMessage(meda).queue();
        }
        if (event.getComponentId().equals("reset-shop")) {
            String shop = event.getValues().get(0);
            String message = "Shop " + shop + " not found";
            if (Shops.reset(shop) != null) {
                message = "Shop " + shop + " reset.";
            }
            MessageEditData meda = new MessageEditBuilder().setComponents(new LinkedList<>()).setContent(message).build();
            event.editMessage(meda).queue();
        }
        if (event.getComponentId().equals("sell-shop")) {
            String shop = event.getValues().get(0);
            SelectMenu itemMenu = SelectMenu.create("sell-items").addOptions(Shops.getItems(shop).stream().map(x -> {
                return SelectOption.of(x, shop + ":" + x);
            }).collect(Collectors.toList())).build();

            MessageEditData meda = new MessageEditBuilder().setContent("Which item are you selling?").build();
            event.editMessage(meda).setActionRow(itemMenu).queue();
        }
        if (event.getComponentId().equals("sell-items")) {
            String shop = event.getValues().get(0).split(":")[0];
            String item = event.getValues().get(0).split(":")[1];
            String message = "Unable to sell item " + item + " from the " + shop + " shop.";
            if (Shops.sell(shop, item)) {
                message = "Sold " + item + " from the " + shop + " shop.";
            }
            MessageEditData meda = new MessageEditBuilder().setComponents(new LinkedList<>()).setContent(message).build();
            event.editMessage(meda).queue();
        }
    }
}
