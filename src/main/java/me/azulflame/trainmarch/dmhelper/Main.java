package me.azulflame.trainmarch.dmhelper;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main extends ListenerAdapter {

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

        Curses.load();
        WildMagic.load();
        DeathEffects.load();

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
        // use the prefix
        if (text.startsWith("!randcurse") || text.startsWith("!curse")) {
            event.getMessage().reply(Curses.get()).queue();
        } else if (text.startsWith("!wild")) {
            event.getMessage().reply(WildMagic.get()).queue();
        } else if (text.startsWith("!youdied")) {
            event.getMessage().reply(DeathEffects.get()).queue();
        } else if (text.startsWith("!rewards")) {
            Quest quest = new Quest(text);
            String message = quest.getRewards();
            event.getMessage().reply(message).queue();
        } else if (text.startsWith("!reward")) {
            event.getMessage().reply("Did you mean `!rewards`").queue();
        } else if (text.startsWith("!reload")) {
            Curses.load();
            WildMagic.load();
            DeathEffects.load();
            event.getMessage().reply("Reloaded lists").queue();
        }
    }
}