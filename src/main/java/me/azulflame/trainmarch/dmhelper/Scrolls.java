package me.azulflame.trainmarch.dmhelper;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scrolls {

    public static String getMinimumItems(String text) {

        int players = -1;
        int tier = -1;
        int difficulty = Quest.MEDIUM;

        Random r = new Random();

        int[][] odds = { { 0, 0, 0, 0 }, { 50, 40, 30, 20 }, { 70, 60, 50, 40 }, { 90, 90, 80, 70 },
                { 100, 100, 100, 90 } };

        Matcher matcher = Pattern.compile("(^|[^tT\\.])\\d+").matcher(text);
        if (matcher.find()) {
            players = Integer.parseInt(matcher.group(0).trim());
        }
        matcher = Pattern.compile("[Tt]\\d+(\\.\\d+)?").matcher(text);
        if (matcher.find()) {
            tier = Integer.parseInt(matcher.group(0).substring(1).trim());
        }
        if (text.contains("hard")) {
            difficulty = Quest.HARD;
        }
        if (text.contains("deadly") && difficulty != Quest.VERY_DEADLY) {
            difficulty = Quest.DEADLY;
        }
        if (text.contains("very")) {
            difficulty = Quest.VERY_DEADLY;
        }

        if (tier < 1 || tier > 4 || players < 0 || difficulty == -1) {
            return "Unable to find needed information, please provide the number of wizards, tier, and difficulty";
        }
        int scrolls = 0;

        String response = "Number of scrolls for your tier " + tier + ", " + Quest.decodeDifficulty(difficulty)
                + " quest:\n";

        for (int i = 0; i < players; i++) {
            if (r.nextInt(100) < odds[tier][difficulty]) {
                scrolls++;
            }
        }

        response += scrolls + " spell scrolls";

        return response;
    }

}
