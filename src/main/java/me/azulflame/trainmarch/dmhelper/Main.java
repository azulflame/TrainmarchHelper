package me.azulflame.trainmarch.dmhelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main extends ListenerAdapter {

    private static ArrayList<String> powerUsers = new ArrayList<>();

    private Logger log = LoggerFactory.getLogger(Main.class);

    // Loads the bot token, then starts the bot
    public static void main(String[] args) {
        String token = "";
        Scanner s;
        try {
            File f = new File("token");
            if (f.exists()) {
                s = new Scanner(f);
                token = s.nextLine();
                s.close();
            }
        } catch (IOException e) {
            System.out.println("Token file not found, attempting to use command line inputs");
        }

        try {
            Lists.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Commands.load();

        try {
            Scanner powerUserScanner = new Scanner(new File("power_users.txt"));
            while (powerUserScanner.hasNext()) {
                powerUsers.add(powerUserScanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Unable to load power user list, admin commands not enabled");
        }
        if (token.equals("")) {
            if (args.length < 1) {
                System.out.println("Token not found and is required");
                return;
            }
            token = args[0];
        }

        try {
            JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.DIRECT_MESSAGES)
                    .addEventListeners(new Main())
                    .setActivity(Activity.playing("Type !ping"))
                    .build();
        } catch (LoginException exception) {
            System.out.print("Unable to log in, check your token or try again later");
        }
    }

    // This is the method called when a message is read in any channel
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String text = event.getMessage().getContentRaw().toLowerCase();

        // Staff-only commands
        if (powerUsers.contains(event.getAuthor().getId().toString())) {
            if (text.startsWith("!reload")) {
                try {
                    Lists.load();
                } catch (IOException e) {
                    event.getMessage().reply("Unable to load custom lists").queue();
                    System.out.println(e.getMessage());
                }
                Commands.load();
                event.getMessage().reply("Reloaded lists.\nCommands loaded:" + String.join("\n", Lists.getCommands())).queue();

            }
        }

        // General user commands
        int command = Commands.resolve(text);

        if (command == Commands.QUEST_CALCULATOR) {
            Quest quest = new Quest(text);
            String message = quest.getRewards();
            event.getMessage().reply(message).queue();
            log.info(Instant.now().atZone(ZoneOffset.UTC).toString()
                    + " Quest rewards generated by " + event.getAuthor().getId() + " ("
                    + event.getAuthor().getAsMention()
                    + " )");
        }
        if (command == Commands.TIME_CALCULATOR) {
            event.getMessage().reply(TimeConverter.getTime(text.substring(text.indexOf(" "))));
            log.info(Instant.now().atZone(ZoneOffset.UTC).toString() +
                    " Time calculated by " + event.getAuthor().getId() + " (" + event.getAuthor().getAsMention()
                    + " )");
        }
        if (command == Commands.MINIMUM_ITEMS) {
            event.getMessage().reply(MinimumItems.getMinimumItems(text)).queue();
            log.info(Instant.now().atZone(ZoneOffset.UTC).toString() + " Minimum rewards calculated by "
                    + event.getAuthor().getId() + " ("
                    + event.getAuthor().getAsMention() + " )");
        }
        if (command == Commands.SCROLLS) {
            event.getMessage().reply(Scrolls.getMinimumItems(text)).queue();
            log.info(Instant.now().atZone(ZoneOffset.UTC).toString() + "Scroll requirements calculated by "
                    + event.getAuthor().getId() + " ("
                    + event.getAuthor().getAsMention() + " )");
        }
        if (command == Commands.SHOP) {
            String toReply = "";
            if (text.equals("!shop"))
            {
                toReply = Shop.getShops();
            }
            else {
                String[] messageText = text.split(" ");
                if (messageText.length == 2) {
                    if (messageText[1].equals("fullreset")) {
                        Shop.resetAll();
                    }
                    if (messageText[1].equals("help")) {
                        toReply = "Possible subcommands:\nhelp: shows this dialog\nanything else: shows the inventory for that shop (if it exists)";
                    } else {
                        toReply = Shop.getShop(messageText[1]);
                    }
                }
                if (messageText.length > 2) {
                    List<String> secondary = new ArrayList<>(Arrays.stream(messageText).toList());
                    secondary.remove(0);
                    secondary.remove(0);
                    String secondaryText = String.join(" ", secondary);
                    String secondaryExact = secondary.remove(0);
                    String tertiary = String.join(" ", secondary);
                    if (messageText[1].equals("reset")) {
                        if (Shop.reset(secondaryText)) {
                            toReply = "Shop reset: " + secondaryText;
                        } else {
                            toReply = "Shop \"" + secondaryText + "\" not found";
                        }
                    }
                    if (messageText[1].equals("add")) {
                        Shop.add(secondaryExact, tertiary.split("\n"));
                        toReply = Shop.getShop(secondaryExact);
                    }
                    if (messageText[1].equals("sell")) {
                        if (Shop.sell(secondaryExact, tertiary)) {
                            toReply = tertiary + " sold from the " + secondaryExact;
                        } else {
                            toReply = "Unable to sell \"" + tertiary + "\" from shop \"" + secondaryExact + "\". Please check spelling and item availability";
                        }
                    }
                }
            }
            event.getMessage().reply(toReply).queue();
        }
        // check if there's a list that matches the command
        if (text.length() > 1) {
            if (text.equals("!curse 5"))
            {
                System.out.println("Debugging started");
            }
            String listName = text.substring(1);
            int times = 1;
            // catch repeats
            if (listName.matches(".* \\d+$")) {
                List<String> temp = Arrays.stream(listName.split(" ")).toList();
                int last = temp.size() - 1;
                times = Integer.parseInt(temp.get(last));
                String possibleListName = String.join(" ", temp.subList(0, last));
                if (Lists.exists(possibleListName) && !Lists.exists(listName))
                {
                    listName = possibleListName;
                }
            }
            if (Lists.exists(listName)) {
                String message = "Result:\n";
                for(int i = 0; i < times; i++)
                {
                    message = message + Lists.get(listName) + "\n";
                }
                event.getMessage().reply(message.trim()).queue();
            }
        }
    }
}