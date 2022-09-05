package me.azulflame.trainmarch.dmhelper;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import me.azulflame.trainmarch.dmhelper.backend.DatabaseManager;
import me.azulflame.trainmarch.dmhelper.backend.Lists;
import me.azulflame.trainmarch.dmhelper.listeners.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Main {
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
        if (token.equals("")) {
            if (args.length < 1) {
                System.out.println("Token not found and is required");
                return;
            }
            token = args[0];
        }
        DatabaseManager.loadItems();
        try {
            JDABuilder.createLight(token)
                    .addEventListeners(new CommandManager(), new ItemCommand(), new ListCommand(), new RewardCommand(), new ScrollsCommand(), new ShopCommand())
                    .setActivity(Activity.playing("Type !ping"))
                    .build();
        } catch (LoginException exception) {
            System.out.print("Unable to log in, check your token or try again later");
        }
    }
}