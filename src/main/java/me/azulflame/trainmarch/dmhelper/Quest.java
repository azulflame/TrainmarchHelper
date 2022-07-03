package me.azulflame.trainmarch.dmhelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Quest {
    // Difficulty constants, indexes for lookup arrays
    public static final int VERY_DEADLY = 0;
    public static final int DEADLY = 1;
    public static final int HARD = 2;
    public static final int MEDIUM = 3;

    private int difficulty;
    private double length;
    private double tier;
    private boolean isVc;

    public Quest(String command) {
        difficulty = MEDIUM;
        isVc = false;
        length = 1.0;
        tier = 1.0;
        parseCommand(command);
    }

    private void parseCommand(String command) {
        String[] split = command.toLowerCase().split(" ");
        for (String text : split) {
            if (text.contains("vc") || text.contains("voice")) {
                isVc = true;
            }
            Matcher matcher = Pattern.compile("(^|[^tT\\.])\\d+(\\.\\d+)?").matcher(text);
            if (matcher.find()) {
                length = Double.parseDouble(matcher.group(0));
            }
            matcher = Pattern.compile("[Tt]\\d+(\\.\\d+)?").matcher(text);
            if (matcher.find()) {
                tier = Double.parseDouble(matcher.group(0).substring(1));
            }
            if (text.contains("hard")) {
                difficulty = HARD;
            }
            if (text.contains("deadly") && difficulty != VERY_DEADLY) {
                difficulty = DEADLY;
            }
            if (text.contains("very")) {
                difficulty = VERY_DEADLY;
            }
        }
    }

    public String getRewards() {
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
        goldMax *= length * tier;
        goldMin *= length * tier;

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
        return "Your quest rewards for the tier " + tier + ", " + length + " hour, " + decodeDifficulty(difficulty)
                + " " + type + " "
                + side + "quest:\nStamps: " + stamps + "\nGold: " + goldMin + " or " + goldMax + "\nDT: "
                + Math.round(dt);
    }

    public static String decodeDifficulty(int difficulty) {
        String[] x = { "very deadly", "deadly", "hard", "medium" };
        return x[difficulty];
    }
}
