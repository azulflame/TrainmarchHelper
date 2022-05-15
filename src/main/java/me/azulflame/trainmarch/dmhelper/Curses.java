package me.azulflame.trainmarch.dmhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Curses {
    private static List<String> curses;

    public static void load() {
        try {
            curses = new ArrayList<String>();
            File f = new File("curses.txt");
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                curses.add(s.nextLine());
            }
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get() {
        Random r = new Random();
        int rand = r.nextInt(curses.size());
        return curses.get(rand);
    }
}
