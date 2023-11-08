package me.azulflame.trainmarch.dmhelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import me.azulflame.trainmarch.dmhelper.repository.DatabaseManager;
import me.azulflame.trainmarch.dmhelper.repository.Market;
import me.azulflame.trainmarch.dmhelper.service.Lists;
import me.azulflame.trainmarch.dmhelper.listeners.*;
import me.azulflame.trainmarch.dmhelper.service.Shops;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

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
            log.error("Token file not found, attempting to use command line inputs");
        }

        try {
            Lists.load();
            log.info("Finished loading random lists");
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        try {
            File f = new File("connection_string");
            if (f.exists()) {
                s = new Scanner(f);
                DatabaseManager.setConnectionString(s.nextLine());
                log.info("Loaded connection string");
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return;
        }

        if (token.equals("")) {
            if (args.length < 1) {
                log.error("Token not found and is required");
                return;
            }
            token = args[0];
        }
        try {
            for (Market m : Market.values()) {
                DatabaseManager.loadItems(m);
            }
            log.info("Loaded items from database");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Unable to load shop data. Proceeding without shop data.");
        }
        try {
            Shops.loadItems("Shop Items.txt");
        } catch (IOException e) {
            log.error("Unable to load shop item data");
        }
        try {
            DatabaseManager.getLockedChannels().forEach(CommandModeEnforcer::lockChannel);
            log.info("Loaded locked channels");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Unable to load locked channels. Proceeding without locks.");
        }
        try {
            JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(
                            new CommandManager(),
                            new ItemCommand(),
                            new ListCommand(),
                            new RewardCommand(),
                            new ScrollsCommand(),
                            new ShopCommand(),
                            new DmxpCommand(),
                            new MarketCommand(),
                            new TimestampCommand(),
                            new CommonAutofill(),
                            new DowntimeCommand(),
                            new HousingCommand(),
                            new TradeCommand(),
                            new AdminCommand(),
                            new CommandModeEnforcer(),
                            new NewRewardsCommand(),
                            new ComputeDmxpCommand(),
                            new SheetcheckCommand(),
                            new InspectCommand())
                    .setActivity(Activity.watching("for commands"))
                    .build();
        } catch (LoginException exception) {
            log.error("Unable to log in, check your token or try again later");
        }
    }
}