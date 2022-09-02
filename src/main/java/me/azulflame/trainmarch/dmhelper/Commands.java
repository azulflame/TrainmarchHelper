package me.azulflame.trainmarch.dmhelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Commands {
    public static final int NO_COMMAND = 0;
    public static final int QUEST_CALCULATOR = 2;
    public static final int TIME_CALCULATOR = 5;
    public static final int MINIMUM_ITEMS = 6;
    public static final int SCROLLS = 7;
    public static final int SHOP = 8;

    private static final Map<String, Integer> key = new HashMap<String, Integer>();

    public static int resolve(String text) {
        if (text.length()>0 && text.split(" ")[0].length() > 0) {
            String command = text.split(" ")[0].toLowerCase().substring(1);
            return key.getOrDefault(command, NO_COMMAND);
        }
        return NO_COMMAND;
    }

    public static void load() {
        try {
            File f = new File("commands.txt");
            Scanner s = new Scanner(f);
            while (s.hasNext()) {
                String line = s.nextLine();
                String[] arr = line.split(" ");
                if (arr.length == 2) {
                    if (arr[0].equals("rewards")) {
                        key.put(arr[1], QUEST_CALCULATOR);
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
                    if (arr[0].equals("shop")) {
                        key.put(arr[1], SHOP);
                    }
                }
            }
            s.close();
        } catch (Exception e) {

        }
    }
}
