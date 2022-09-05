package me.azulflame.trainmarch.dmhelper.backend;


import java.util.Random;

public class Scrolls {

    public static String getMinimumItems(int players, int tier, Difficulty difficulty) {


        Random r = new Random();

        int[][] odds = { { 0, 0, 0, 0 }, { 50, 40, 30, 20 }, { 70, 60, 50, 40 }, { 90, 90, 80, 70 },
                { 100, 100, 100, 90 } };

        if (tier < 1 || tier > 4 || players < 0) {
            return "Unable to find needed information, please provide the number of wizards, tier, and difficulty";
        }
        int scrolls = 0;

        String response = "Number of scrolls for your tier " + tier + ", " + difficulty.getName()
                + " quest:\n";

        for (int i = 0; i < players; i++) {
            if (r.nextInt(100) < odds[tier][difficulty.getValue()]) {
                scrolls++;
            }
        }

        response += scrolls + " spell scrolls";

        return response;
    }

}
