package me.azulflame.trainmarch.dmhelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Commands {
    public static final int NO_COMMAND = 0;
    public static final int CURSES = 1;
    public static final int QUEST_CALCULATOR = 2;
    public static final int WILD_MAGIC = 3;
    public static final int DEATH_EFFECTS = 4;
    public static final int TIME_CALCULATOR = 5;
    public static final int MINIMUM_ITEMS = 6;
    public static final int SCROLLS = 7;

    private static final Map<String, Integer> key = new HashMap<String, Integer>();

    public static int resolve(String text) {
        String command = text.split(" ")[0].toLowerCase().substring(1);
        return key.getOrDefault(command, NO_COMMAND);
    }

    public static void load() {
        try {
            File f = new File("commands.txt");
            Scanner s = new Scanner(f);
            while (s.hasNext()) {
                String line = s.nextLine();
                String[] arr = line.split(" ");
                if (arr.length == 2) {
                    if (arr[0].equals("curse")) {
                        key.put(arr[1], CURSES);
                    }
                    if (arr[0].equals("rewards")) {
                        key.put(arr[1], QUEST_CALCULATOR);
                    }
                    if (arr[0].equals("wild")) {
                        key.put(arr[1], WILD_MAGIC);
                    }
                    if (arr[0].equals("youdied")) {
                        key.put(arr[1], DEATH_EFFECTS);
                    }
                    if (arr[0].equals("time")) {
                        key.put(arr[1], TIME_CALCULATOR);
                    }
                    if (arr[0].equals("items")) {
                        key.put(arr[1], MINIMUM_ITEMS);
                    }
                    if (arr[0].equals("scrolls")) {
                        key.put(arr[1], SCROLLS);
                    }
                }
            }
            s.close();
        } catch (Exception e) {

        }
    }
}
