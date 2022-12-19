package me.azulflame.trainmarch.dmhelper.service;

import java.util.Random;

public class Items {
    static Random rand = new Random();

    private static String noMin = "No minimum";
    private static String nonMagic = "Non-magical item";
    private static String common = "Common magic item";
    private static String uncommon = "Uncommon magic item";
    private static String rare = "Rare magic item";
    private static String veryRare = "Very rare magic item";

    private static String[] minItem = {noMin, nonMagic, common, uncommon, rare, veryRare};

    private static Integer[][] t1 = {
            {
                    0, 1, 1, 1, 1, 1, 2, 2, 2, 2    // Very Deadly
            },
            {
                    0, 0, 1, 1, 1, 1, 1, 2, 2, 2    // Deadly
            },
            {
                    0, 0, 0, 1, 1, 1, 1, 1, 2, 2    // Hard
            },
            {
                    0, 0, 0, 0, 1, 1, 1, 1, 1, 2    // Medium
            },
            {
                    0, 0, 0, 0, 0, 0, 1, 1, 1, 1    // Easy
            }
    };
    private static Integer[][] t2 = {
            {
                    1, 2, 2, 2, 3, 3, 3, 3, 3, 3    // Very Deadly
            },
            {
                    1, 1, 2, 2, 2, 3, 3, 3, 3, 3    // Deadly
            },
            {
                    1, 1, 1, 2, 2, 2, 3, 3, 3, 3    // Hard
            },
            {
                    1, 1, 1, 1, 2, 2, 2, 3, 3, 3    // Medium
            },
            {
                    0, 1, 1, 1, 1, 1, 2, 2, 2, 2    // Easy
            }
    };
    private static Integer[][] t3 = {
            {
                    2, 3, 3, 3, 3, 4, 4, 4, 4, 4    // Very Deadly
            },
            {
                    2, 3, 3, 3, 3, 3, 4, 4, 4, 4    // Deadly
            },
            {
                    2, 2, 3, 3, 3, 3, 3, 4, 4, 4    // Hard
            },
            {
                    2, 2, 2, 3, 3, 3, 3, 3, 4, 4    // Medium
            },
            {
                    1, 2, 2, 2, 2, 2, 3, 3, 3, 3    // Easy
            }
    };

    private static Integer[][] t4 = {
            {
                    3, 4, 4, 4, 4, 5, 5, 5, 5, 5    // Very Deadly
            },
            {
                    3, 3, 4, 4, 4, 4, 5, 5, 5, 5    // Deadly
            },
            {
                    3, 3, 3, 4, 4, 4, 4, 5, 5, 5    // Hard
            },
            {
                    3, 3, 3, 3, 4, 4, 4, 4, 5, 5    // Medium
            },
            {
                    2, 3, 3, 3, 3, 3, 4, 4, 4, 4    // Easy
            }
    };

    public static String getMinimumItems(int tier, int players, Difficulty difficulty) {

        if (tier < 1 || tier > 4 || players < 0) {
            return "Unable to find minimum items, please provide players, tier, and difficulty";
        }
        int[] minimums = new int[6];
        for (int i = 0; i < players; i++) {
            minimums[minimumForPlayer(tier, difficulty.getValue())]++;
        }

        String response = "Minimum items for your tier " + tier + ", " + difficulty.getName()
                + " quest:\n";

        for (int i = 0; i < 6; i++) {
            if (minimums[i] > 0) {
                response += minimums[i] + "x " + minItem[i] + "\n";
            }
        }

        return response;
    }

    public static String getBasicItems(int tier, Difficulty difficulty, int players) {
        int[] minimums = new int[6];
        for (int i = 0; i < players; i++) {
            minimums[minimumForPlayer(tier, difficulty.getValue())]++;
        }

        String response = "";

        for (int i = 0; i < 6; i++) {
            if (minimums[i] > 0) {
                response += minimums[i] + "x " + minItem[i] + ". Random generation: " + getRandomItem(i, minimums[i]) + "\n";
            }
        }

        return response;
    }

    private static String getRandomItem(int rarity, int count)
    {
        if (rarity == 2)
        {
            return String.join(", ", Lists.get("Common Items", count));
        }
        if (rarity == 3)
        {
            return String.join(", ", Lists.get("Uncommon Items", count));
        }
        if (rarity == 4)
        {
            return String.join(", ", Lists.get("Rare Items", count));
        }
        if (rarity == 5)
        {
            return String.join(", ", Lists.get("Very Rare Items", count));
        }
        return "not supported";
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
