package me.azulflame.trainmarch.dmhelper;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinimumItems {
    static Random rand = new Random();

    private static String noMin = "No minimum";
    private static String nonMagic = "Non-magical item";
    private static String common = "Common magic item";
    private static String uncommon = "Uncommon magic item";
    private static String rare = "Rare magic item";
    private static String veryRare = "Very rare magic item";

    private static String[] minItem = { noMin, nonMagic, common, uncommon, rare, veryRare };

    private static Integer[][] t1 = {
            {
                    0, 1, 1, 1, 1, 1, 2, 2, 2, 2
            },
            {
                    0, 0, 1, 1, 1, 1, 1, 2, 2, 2
            },
            {
                    0, 0, 0, 1, 1, 1, 1, 1, 2, 2
            },
            {
                    0, 0, 0, 0, 1, 1, 1, 1, 1, 2
            }
    };
    private static Integer[][] t2 = {
            {
                    1, 2, 2, 2, 3, 3, 3, 3, 3, 3
            },
            {
                    1, 1, 2, 2, 2, 3, 3, 3, 3, 3
            },
            {
                    1, 1, 1, 2, 2, 2, 3, 3, 3, 3
            },
            {
                    1, 1, 1, 1, 2, 2, 2, 3, 3, 3
            }
    };
    private static Integer[][] t3 = {
            {
                    2, 3, 3, 3, 3, 4, 4, 4, 4, 4
            },
            {
                    2, 3, 3, 3, 3, 3, 4, 4, 4, 4
            },
            {
                    2, 2, 3, 3, 3, 3, 3, 4, 4, 4
            },
            {
                    2, 2, 2, 3, 3, 3, 3, 3, 4, 4
            }
    };

    private static Integer[][] t4 = {
            {
                    3, 4, 4, 4, 4, 5, 5, 5, 5, 5
            },
            {
                    3, 3, 4, 4, 4, 4, 5, 5, 5, 5
            },
            {
                    3, 3, 3, 4, 4, 4, 4, 5, 5, 5
            },
            {
                    3, 3, 3, 3, 4, 4, 4, 4, 5, 5
            }
    };

    public static String getMinimumItems(String text) {

        int players = -1;
        int tier = -1;
        int difficulty = Quest.MEDIUM;

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
            return "Unable to find minimum items, please provide players, tier, and difficulty";
        }
        int[] minimums = new int[6];
        for (int i = 0; i < players; i++) {
            minimums[minimumForPlayer(tier, difficulty)]++;
        }

        String response = "Minimim items for your tier " + tier + ", " + Quest.decodeDifficulty(difficulty)
                + " quest:\n";

        for (int i = 0; i < 6; i++) {
            if (minimums[i] > 0) {
                response += minimums[i] + "x " + minItem[i] + "\n";
            }
        }

        return response;
    }

    private static int minimumForPlayer(int tier, int difficulty) {

        int odds = rand.nextInt(10);

        if (tier == 1) {
            return t1[difficulty][odds];
        }
        if (tier == 2) {
            return t2[difficulty][odds];
        }
        if (tier == 3) {
            return t3[difficulty][odds];
        }
        if (tier == 4) {
            return t4[difficulty][odds];
        }
        return 0;
    }
}
