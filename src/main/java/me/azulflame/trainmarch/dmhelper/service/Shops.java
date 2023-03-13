package me.azulflame.trainmarch.dmhelper.service;

import me.azulflame.trainmarch.dmhelper.repository.DatabaseManager;
import me.azulflame.trainmarch.dmhelper.repository.Market;

import java.util.*;

public class Shops {


    public static final HashMap<Market, Shops> shopList = new HashMap<>();
    public Shops(Market m)
    {
        this.market = m;
    }

    public static Shops getShop(Market market)
    {
        if (!shopList.containsKey(market))
        {
            Shops s = new Shops(market);
            shopList.put(market, s);
        }
        return shopList.get(market);
    }

    private HashMap<String, List<String>> shops = new HashMap<>();
    private final Market market;

    public void add(String shop, List<String> items, boolean saveToDb) {
        if (!shops.containsKey(shop)) {
            shops.put(shop, new LinkedList<>());
        }
        for (String item : items)
            shops.get(shop).add(item);
        if (saveToDb)
            DatabaseManager.insertItem(market, shop, items);
    }

    public Set<String> getShops() {
        return shops.keySet();
    }

    public List<String> getItems(String shop) {
        if (shops.containsKey(shop)) {
            HashSet<String> items = new HashSet<>(shops.get(shop));
            return new ArrayList<>(items);
        }
        return null;
    }

    public boolean sell(String shop, String item) {
        DatabaseManager.deleteItem(market, shop, item);
        if (shops.containsKey(shop))
            return shops.get(shop).remove(item);
        return false;
    }

    public List<String> reset(String shop) {
        DatabaseManager.wipeShop(market, shop);
        return shops.remove(shop);
    }

    public void resetAll() {
        DatabaseManager.wipeMarket(market);
        shops = new HashMap<>();
    }
}
