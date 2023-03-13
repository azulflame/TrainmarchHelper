package me.azulflame.trainmarch.dmhelper.service;

import me.azulflame.trainmarch.dmhelper.Tuple;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Crafting {

    private static final Map<String, Tuple> costs = loadCraftingCosts();


    private static Map<String, Tuple> loadCraftingCosts() {
        Map<String, Tuple> map = new HashMap<>();
        try {
            File f = new File("items.txt");
            Scanner s = new Scanner(f);
            while(s.hasNextLine())
            {
                String line = s.nextLine();
                String[] split = line.split(",");
                int gold = Integer.parseInt(split[0].trim());
                int dt = Integer.parseInt(split[1].trim());
                String name = "";
                for (int i = 2; i < split.length; i++)
                {
                    name += split[i];
                }
                map.put(name, new Tuple(gold, dt));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return map;
        }
        return map;
    }

}
