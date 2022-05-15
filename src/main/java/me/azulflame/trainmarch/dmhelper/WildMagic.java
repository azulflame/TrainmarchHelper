package me.azulflame.trainmarch.dmhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class WildMagic {
    private static ArrayList<String> effects;

    public static void load() {
        try {
            effects = new ArrayList<String>(100);
            File f = new File("wildmagic.txt");
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                effects.add(s.nextLine());
            }
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get() {
        Random r = new Random();
        int rand = r.nextInt(effects.size());
        return effects.get(rand);
    }
}
