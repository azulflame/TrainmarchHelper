package me.azulflame.trainmarch.dmhelper;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main extends ListenerAdapter {

    // Difficulty constants, indexes for lookup arrays
    private static final int VERY_DEADLY = 0;
    private static final int DEADLY = 1;
    private static final int HARD = 2;
    private static final int MEDIUM = 3;

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

        if (token.equals("")) {
            if (args.length < 1) {
                System.out.println("Token not found and is required");
                return;
            }
            token = args[0];
        }

        try {
            JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
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
        Message msg = event.getMessage();
        String text = msg.getContentRaw().toLowerCase();
        // use the prefix
        if (msg.getContentRaw().startsWith("!rewards")) {
            String message = "Unable to parse input, please rephrase and try again";
            try {
                double length = 0.0;
                boolean isVc = text.contains("vc")
                        || text.contains("voice");
                Matcher matcher = Pattern.compile("\\d+(\\.\\d+)?").matcher(text);
                if (matcher.find()) {
                    length = Double.parseDouble(matcher.group(0));
                } else {
                    msg.reply("Missing length of quest");
                }
                int difficulty = MEDIUM;
                if (text.contains("hard")) {
                    difficulty = HARD;
                }
                if (text.contains("deadly")) {
                    difficulty = DEADLY;
                }
                if (text.contains("very")) {
                    difficulty = VERY_DEADLY;
                }
                message = getRewards(isVc, length, difficulty);
            } finally {
                event.getChannel().sendMessage(message).queue();
            }
        } else if (msg.getContentRaw().startsWith("!reward")) {
            event.getChannel().sendMessage("Did you mean `!rewards`").queue();
        }
    }

    private String getRewards(boolean isVc, double length, int difficulty) {
        int goldMin = 10;
        int stamps = (int) Math.round(length);
        int goldMax = 10;
        double dt = Math.round(length / 2);

        // Lookup tables
        int[] vcGoldMax = { 28, 25, 20, 12 };
        int[] vcGoldMin = { 26, 21, 15, 12 };
        int[] txtGoldMax = { 28, 25, 15, 0 };
        int[] txtGoldMin = { 26, 20, 15, 0 };
        int[] vcStamps = { 5, 4, 2, 1 };
        int[] txtStamps = { 4, 3, 1, 0 };
        double[] dtMult = { 2.5, 2, 1.5, 1 };

        // actual calculations
        if (isVc) {
            stamps += vcStamps[difficulty];
            goldMin += vcGoldMin[difficulty];
            goldMax += vcGoldMax[difficulty];
        } else {
            stamps += txtStamps[difficulty];
            goldMax += txtGoldMax[difficulty];
            goldMin += txtGoldMin[difficulty];
        }

        // Side quest check
        if (length < 2.0) {
            dt = 0;
            goldMax /= 2;
            goldMin /= 2;
            stamps /= 2;
        }

        // apply calculations
        dt *= dtMult[difficulty];
        goldMax *= length;
        goldMin *= length;

        // begin formatting

        // format response
        String type = "vc";
        if (!isVc) {
            type = "pbp";
        }

        String side = "side ";
        if (length >= 2.0) {
            side = "";
        }
        return "Your quest rewards for the " + length + " hour, " + decodeDifficulty(difficulty) + " " + type + " "
                + side + "quest:\nStamps: " + stamps + "\nGold: " + goldMin + " or " + goldMax + "\nDT: "
                + Math.round(dt);
    }

    private String decodeDifficulty(int difficulty) {
        String[] x = { "very deadly", "deadly", "hard", "medium" };
        return x[difficulty];
    }
}