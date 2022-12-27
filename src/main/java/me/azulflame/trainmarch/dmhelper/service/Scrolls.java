package me.azulflame.trainmarch.dmhelper.service;


import java.util.Random;

public class Scrolls {

    public static String getMinimumItems(int players, int tier, Difficulty difficulty) {


        Random r = new Random();

        int[] odds = {
                80,     // TPK likely
                60,     // Deadly
                40,     // Hard
                20,     // Medium
                0};     // Easy

        if (tier < 1 || tier > 4 || players < 0) {
            return "Unable to find needed information, please provide the number of wizards, tier, and difficulty";
        }
        int scrolls = 0;

        String response = "Number of scrolls for your tier " + tier + ", " + difficulty.getName()
                + " quest:\n";

        for (int i = 0; i < players; i++) {
            if (r.nextInt(100) < odds[difficulty.getValue()]) {
                scrolls++;
            }
        }

        response += scrolls + " spell scrolls";

        return response;
    }

}
