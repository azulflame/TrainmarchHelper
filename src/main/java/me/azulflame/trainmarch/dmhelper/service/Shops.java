package me.azulflame.trainmarch.dmhelper.service;

import me.azulflame.trainmarch.dmhelper.repository.DatabaseManager;
import me.azulflame.trainmarch.dmhelper.repository.Market;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Shops {
    private static final Random rand = new Random();
    private static final HashMap<String, List<String>> shopItems = new HashMap<>();
    public static final HashMap<Market, Shops> shopList = new HashMap<>();

    public Shops(Market m) {
        this.market = m;
    }

    public static Shops getShop(Market market) {
        if (!shopList.containsKey(market)) {
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

    // reset and generate items
    public void generate() {
        for (String shop : shopItems.keySet()) {
            List<String> items = shopItems.get(shop);
            Collections.shuffle(items);
            List<String> finalItems = items.stream().limit(20).map(this::formatItem).toList();
            add(shop, finalItems, true);
        }
    }

    // from the list of items, get just the item name
    private String formatItem(String item) {
        // format: rarity;item (data)
        String[] split = item.split(";");
        String price = "";
        if (market.getMultiplier() > 0) {
            price = ": " + (int)(getPrice(split[0]) * market.getMultiplier()) + "gp";
        }
        return split[1] + price;
    }

    public static void loadItems(String filename) throws IOException {
        File f = new File(filename);
        Scanner s = new Scanner(f);
        while (s.hasNextLine())
        {
            String line = s.nextLine();
            String[] split = line.split(";");
            if (!shopItems.containsKey(split[0]))
            {
                shopItems.put(split[0], new ArrayList<>());
            }
            String item = "";
            try {
                item = split[2] + ";" + split[1] + ((split.length > 3) ? " (" + split[3] + ")" : "");
            }
            catch (Exception e)
            {
                System.out.println(".");
            }
            shopItems.get(split[0]).add(item);
            // format: rarity;item name (data): cost

        }
    }

    private static int getPrice(String rarity)
    {
        return switch (rarity)
        {
            case "Very Rare" -> randomInt(5001, 50000);
            case "Rare" -> randomInt(501, 5000);
            case "Uncommon" -> randomInt(101, 500);
            case "Common" -> randomInt(50, 100);
            case "1st Level Spell", "Cantrip" -> randomInt(50, 75);
            case "2nd Level Spell" -> randomInt(101, 250);
            case "3rd Level Spell" -> randomInt(250, 500);
            case "4th Level Spell" -> randomInt(500, 2750);
            case "5th Level Spell" -> randomInt(2750, 5000);
            case "6th Level Spell" -> randomInt(5000, 20000);
            case "7th Level Spell" -> randomInt(20000, 35000);
            case "8th Level Spell" -> randomInt(35000, 50000);
            case "9th Level Spell" -> randomInt(50000, 100000);
            default -> 100000;
        };
    }

    private static int randomInt(int min, int max)
    {
        return rand.nextInt(max - min) + min; 
    }
}
