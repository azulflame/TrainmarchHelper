package me.azulflame.trainmarch.dmhelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Lists {
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

    public static String get(String command)
    {
        System.out.println("Getting a line from " + command);
        return lists.get(command).get();
    }

    public static List<String> getCommands()
    {
        return lists.keySet().stream().toList();
    }
}
