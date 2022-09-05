package me.azulflame.trainmarch.dmhelper.backend;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Shops {

    public static HashMap<String, List<String>> shops = new HashMap<>();

    public static void add(String shop, List<String> items)
    {
        if (!shops.containsKey(shop))
        {
            shops.put(shop, new LinkedList<String>());
        }
        for(String item : items)
            shops.get(shop).add(item);
    }

    public static Set<String> getShops() {
        return shops.keySet();
    }

    public static List<String> getItems(String shop) {
        if (shops.containsKey(shop))
            return shops.get(shop);
        return null;
    }

    public static boolean sell(String shop, String item)
    {
        if (shops.containsKey(shop))
            return shops.get(shop).remove(item);
        return false;
    }

    public static List<String> reset(String shop)
    {
        return shops.remove(shop);
    }

    public static void resetAll()
    {
        shops = new HashMap<>();
    }
}
