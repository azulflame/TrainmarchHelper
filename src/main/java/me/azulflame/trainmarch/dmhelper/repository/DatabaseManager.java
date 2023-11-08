package me.azulflame.trainmarch.dmhelper.repository;

import lombok.extern.slf4j.Slf4j;
import me.azulflame.trainmarch.dmhelper.dto.PlayerCharacter;
import me.azulflame.trainmarch.dmhelper.service.Shops;

import java.sql.*;
import java.util.*;

@Slf4j
public class DatabaseManager {
    private static String url;

    public static final String BLACK_MARKET = "market";
    public static final String BAZAAR = "bazaar";
    public static final String TRADE_MARKET = "trade_market";

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public static void loadItems(Market market) {
        try (Connection conn = connect()) {
            String getQuery = "SELECT * FROM " + market.getValue() + ";";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(getQuery);
            while (rs.next()) {
                Shops.getShop(market).add(rs.getString("shop"), List.of(rs.getString("item")), false);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void wipeShop(Market market, String shop) {
        try (Connection conn = connect()) {
            String query = "DELETE FROM " + market.getValue() + " WHERE shop = ?;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, shop);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void wipeMarket(Market market) {
        try (Connection conn = connect()) {
            String query = "DELETE FROM " + market.getValue() + ";";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteItem(Market market, String shop, String item) {
        try (Connection conn = connect()) {
            String query = "DELETE FROM " + market.getValue() + " WHERE shop = ? AND item = ? LIMIT 1;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, shop);
            ps.setString(2, item);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertItem(Market market, String shop, List<String> items) {
        try (Connection conn = connect()) {
            for (String item : items) {
                String query = "INSERT INTO " + market.getValue() + " (shop, item) VALUES (?, ?);";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, shop);
                ps.setString(2, item);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Integer getDmxp(String userId) {
        Integer result = null;
        try (Connection conn = connect()) {
            String query = "SELECT * FROM dmxp WHERE user_id = ?;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())// first result
                result = rs.getInt("dmxp");
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static void setDmxp(String userId, int dmxp) {
        try (Connection conn = connect()) {
            String query = "INSERT INTO dmxp (user_id, dmxp) VALUES (? , ?) ON DUPLICATE KEY UPDATE dmxp = ?;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ps.setInt(2, dmxp);
            ps.setInt(3, dmxp);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getDowntime(String userId, String character) {
        String query = "SELECT downtime FROM downtime WHERE user_id = ? AND `character` = ?;";
        try (Connection conn = connect()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ps.setString(2, character.toLowerCase());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("downtime");
            }
        } catch (SQLException e) {
            log.error("Error finding DMXP for a character named " + character);
        }
        return -999;
    }

    public static Map<String, Integer> getAllDowntime(String userId) {
        Map<String, Integer> downtimes = new HashMap<>();
        String query = "SELECT `character`, downtime FROM downtime WHERE user_id = ?;";
        try (Connection conn = connect()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                downtimes.put(rs.getString("character"), rs.getInt("downtime"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return downtimes;
    }

    public static void setDowntime(String userId, String character, int downtime)
    {
        String query = "INSERT INTO downtime (user_id, `character`, downtime) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE downtime = ?;";
        try (Connection conn = connect())
        {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ps.setString(2, character.toLowerCase());
            ps.setInt(3, downtime);
            ps.setInt(4, downtime);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void lockChannel(String id)
    {
        String query = "INSERT INTO command_mode (id) VALUES (?) ON DUPLICATE KEY UPDATE id=id;";
        try (Connection conn = connect())
        {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void unlockChannel(String id)
    {
        String query = "DELETE FROM command_mode WHERE id = ?;";
        try (Connection conn = connect())
        {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<String> getLockedChannels()
    {
        Set<String> output = new HashSet<>();
        String query = "SELECT id FROM command_mode;";
        try (Connection conn = connect())
        {
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                output.add(rs.getString("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    public static Set<Progress> getCurrentProgress(String userId, String character)
    {
        Set<Progress> progress = new HashSet<>();
        String query = "SELECT element, current, target FROM progress WHERE user_id = ? AND char_name = ? AND current <> target;";
        try (Connection conn = connect())
        {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ps.setString(2, character.toLowerCase());
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                progress.add(new Progress(userId.toLowerCase(), character.toLowerCase(), rs.getString("element"), rs.getInt("current"), rs.getInt("target")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return progress;
    }

    public static Set<Progress> getCurrentProgress(String userId)
    {
        Set<Progress> progress = new HashSet<>();
        String query = "SELECT char_name, element, current, target FROM progress WHERE user_id = ? AND current <> target;";
        try (Connection conn = connect())
        {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                progress.add(new Progress(userId.toLowerCase(), rs.getString("char_name"), rs.getString("element"), rs.getInt("current"), rs.getInt("target")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return progress;
    }

    public static void setProgress(String userId, String character, String element, int current, int total)
    {
        String query = "INSERT INTO progress (user_id, char_name, element, current, target) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE current = ?;";
        try (Connection conn = connect())
        {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId.toLowerCase());
            ps.setString(2, character.toLowerCase());
            ps.setString(3, element);
            ps.setInt(4, current);
            ps.setInt(5, total);
            ps.setInt(6, current);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void setConnectionString(String connectionString) {
        url = connectionString;
    }

    public static void setOwner(String ownerId, String channelId) {
        String query = "INSERT INTO housing (channel, owner) VALUES (?, ?) ON DUPLICATE KEY UPDATE owner = ?;";
        try (Connection conn = connect())
        {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, channelId);
            ps.setString(2, ownerId);
            ps.setString(3, ownerId);

            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String getOwner(String id) {
        String query = "SELECT owner FROM housing WHERE channel = ?;";
        try (Connection conn = connect())
        {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                return rs.getString("owner");
            }
            return "no match";
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void registerCharacter(String playerId, String characterName, String sheetLink)
    {
        String query = "INSERT INTO characters(owner, character_name, sheet_link) VALUES (?, ?, ?);";
        try (Connection conn = connect()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, playerId);
            ps.setString(2, characterName);
            ps.setString(3, sheetLink);

            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static List getCharacters(String character) {
        List<PlayerCharacter> list = new ArrayList<>();
        String query = "SELECT owner, character_name, sheet_link FROM characters WHERE character_name LIKE ?;";
        try (Connection conn = connect())
        {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%"+character+"%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new PlayerCharacter(rs.getString("owner"), rs.getString("character_name"), rs.getString("sheet_link")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
