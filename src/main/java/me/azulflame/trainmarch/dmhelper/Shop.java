package me.azulflame.trainmarch.dmhelper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Shop {
    private static HashMap<String, List<String>> shops = new HashMap<>();

    public static String getShops()
    {
        return "The available shops:\n" + String.join("\n", shops.keySet());
    }

    // returns the items, newline delimited
    public static String getShop(String shop)
    {
        if (shop.isBlank())
        {
            return "The available shops to browse are:\n" + String.join("\n", shops.keySet());
        }
        if (shops.containsKey(shop))
        {
            return shops.get(shop).stream().collect(Collectors.joining("\n", "The items in the " + shop + " shop:\n", ""));
        }
        return "Invalid shop name. The available shops to browse are:\n" + String.join("\n", shops.keySet());
    }

    // returns if the shop existed
    public static boolean reset(String shop)
    {
        if (shops.containsKey(shop))
        {
            shops.remove(shop);
            return true;
        }
        return false;
    }
    // returns true if the shop was created
    public static boolean add(String shop, String[] items)
    {
        boolean shopExisted = shops.containsKey(shop);
        if (!shops.containsKey(shop))
        {
            shops.put(shop, new LinkedList<String>());
        }
        for(String item : items)
        {
            shops.get(shop).add(item);
        }
        return shopExisted;
    }

    public static void resetAll()
    {
        shops = new HashMap();
    }

    // returns true on a successful remove
    public static boolean sell(String shop, String item)
    {
        if (shops.containsKey(shop) && shops.get(shop).contains(item))
        {
            shops.get(shop).remove(item);
            return true;
        }
        return false;
    }
}
