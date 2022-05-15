package me.azulflame.trainmarch.dmhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class DeathEffects {
    private static List<String> deathEffects;

    public static void load() {
        try {
            deathEffects = new ArrayList<String>();
            File f = new File("death_effects.txt");
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                deathEffects.add(s.nextLine());
            }
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get() {
        Random r = new Random();
        int rand = r.nextInt(deathEffects.size());
        return deathEffects.get(rand);
    }
}