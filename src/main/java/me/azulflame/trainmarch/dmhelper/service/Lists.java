package me.azulflame.trainmarch.dmhelper.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Lists {
    Random r = new Random();
    private static Map<String, RandomSelector> lists;

    public static void load() throws IOException {
        lists = new HashMap<>();
        List<String> files = Files.list(Paths.get("lists")).filter(file -> !Files.isDirectory(file)).map(Path::getFileName).map(Path::toString).toList();

        for (String file : files) {
            RandomSelector r = new RandomSelector();
            r.load("lists/" + file);
            lists.put(file, r);
        }
    }

    public static boolean exists(String command)
    {
        return lists.containsKey(command);
    }

    public static List<String> get(String command, int count)
    {
        List<String> output = new ArrayList<>(count);
        for(int i = 0; i < count; i++)
        {
            output.add(getItem(command));
        }
        return output;
    }

    public static String getItem(String command)
    {
        return lists.get(command).get();
    }

    public static List<String> getCommands()
    {
        return lists.keySet().stream().toList();
    }
}
