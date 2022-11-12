package me.azulflame.trainmarch.dmhelper.repository;

import lombok.extern.slf4j.Slf4j;
import me.azulflame.trainmarch.dmhelper.dto.PlayerCharacter;
import me.azulflame.trainmarch.dmhelper.service.Shops;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.*;

@Slf4j
public class DatabaseManager {
    private static String url;

    public static final String BLACK_MARKET = "market";
    public static final String BAZAAR = "bazaar";
    public static final String TRADE_MARKET = "trade_market";

    private static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(url);
        return conn;
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

    public static boolean registerCharacter(PlayerCharacter pc) {
        try (Connection conn = connect()) {
            String checkQuery = "SELECT * FROM characters WHERE user_id = ? AND name = ?;";
            PreparedStatement ps = conn.prepareStatement(checkQuery);
            ps.setString(1, pc.getUserId());
            ps.setString(2, pc.getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return false;
            }
            ps.close();
            String query = "INSERT INTO characters (user_id, name, strength, dexterity, constitution, intelligence, wisdom, charisma, strSave, dexSave, conSave, intSave, wisSave, chaSave, artificer, barbarian, bard, cleric, druid, fighter, monk, paladin, ranger, rogue, sorcerer, warlock, wizard, bloodhunter, platinum, gold, silver, copper, downtime, stamps, link) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
                    " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
                    " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
                    " ?, ?, ?, ?, ?);";

            ps = conn.prepareStatement(query);
            ps.setString(1, pc.getUserId());
            ps.setString(2, pc.getName());
            ps.setInt(3, pc.getStrength());
            ps.setInt(4, pc.getDexterity());
            ps.setInt(5, pc.getConstitution());
            ps.setInt(6, pc.getIntelligence());
            ps.setInt(7, pc.getWisdom());
            ps.setInt(8, pc.getCharisma());
            ps.setInt(9, pc.getStrengthSave());
            ps.setInt(10, pc.getDexteritySave());
            ps.setInt(11, pc.getConstitutionSave());
            ps.setInt(12, pc.getIntelligenceSave());
            ps.setInt(13, pc.getWisdomSave());
            ps.setInt(14, pc.getCharismaSave());
            ps.setInt(15, pc.getArtificer());
            ps.setInt(16, pc.getBarbarian());
            ps.setInt(17, pc.getBard());
            ps.setInt(18, pc.getCleric());
            ps.setInt(19, pc.getDruid());
            ps.setInt(20, pc.getFighter());
            ps.setInt(21, pc.getMonk());
            ps.setInt(22, pc.getPaladin());
            ps.setInt(23, pc.getRanger());
            ps.setInt(24, pc.getRogue());
            ps.setInt(25, pc.getSorcerer());
            ps.setInt(26, pc.getWarlock());
            ps.setInt(27, pc.getWizard());
            ps.setInt(28, pc.getBloodHunter());
            ps.setInt(29, pc.getPlatinum());
            ps.setInt(30, pc.getGold());
            ps.setInt(31, pc.getSilver());
            ps.setInt(32, pc.getCopper());
            ps.setInt(33, pc.getDt());
            ps.setInt(34, pc.getStamps());
            ps.setString(35, pc.getLink());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public static List<String> getCharacters(String userId) {
        List<String> names = new ArrayList<String>();
        try (Connection conn = connect()) {
            String query = "SELECT name FROM characters WHERE user_id = ?;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return names;
    }

    public static PlayerCharacter getCharacter(String userId, String charName) {
        String query = "SELECT * FROM characters WHERE user_id = ? AND name = ?;";
        try (Connection conn = connect()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, userId);
            ps.setString(2, charName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PlayerCharacter pc = new PlayerCharacter();
                pc.setUserId(rs.getString("user_id"));
                pc.setName(rs.getString("name"));
                pc.setStrength(rs.getInt("strength"));
                pc.setDexterity(rs.getInt("dexterity"));
                pc.setConstitution(rs.getInt("constitution"));
                pc.setIntelligence(rs.getInt("intelligence"));
                pc.setWisdom(rs.getInt("wisdom"));
                pc.setCharisma(rs.getInt("charisma"));
                pc.setStrengthSave(rs.getInt("strSave"));
                pc.setDexteritySave(rs.getInt("dexSave"));
                pc.setConstitutionSave(rs.getInt("conSave"));
                pc.setIntelligenceSave(rs.getInt("intSave"));
                pc.setWisdomSave(rs.getInt("wisSave"));
                pc.setCharismaSave(rs.getInt("chaSave"));
                pc.setArtificer(rs.getInt("artificer"));
                pc.setBarbarian(rs.getInt("barbarian"));
                pc.setBard(rs.getInt("bard"));
                pc.setCleric(rs.getInt("cleric"));
                pc.setDruid(rs.getInt("druid"));
                pc.setFighter(rs.getInt("fighter"));
                pc.setMonk(rs.getInt("monk"));
                pc.setPaladin(rs.getInt("paladin"));
                pc.setRanger(rs.getInt("ranger"));
                pc.setRanger(rs.getInt("rogue"));
                pc.setSorcerer(rs.getInt("sorcerer"));
                pc.setWarlock(rs.getInt("warlock"));
                pc.setWizard(rs.getInt("wizard"));
                pc.setBloodHunter(rs.getInt("bloodhunter"));
                pc.setPlatinum(rs.getInt("platinum"));
                pc.setGold(rs.getInt("gold"));
                pc.setSilver(rs.getInt("silver"));
                pc.setCopper(rs.getInt("copper"));
                pc.setDt(rs.getInt("downtime"));
                pc.setStamps(rs.getInt("stamps"));
                pc.setLink(rs.getString("link"));
                return pc;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    public static boolean saveCharacter(PlayerCharacter pc) {
        String query = "INSERT INTO characters (user_id, name, strength, dexterity, constitution, intelligence, wisdom, charisma, strSave, dexSave, conSave, intSave, wisSave, chaSave, artificer, barbarian, bard, cleric, druid, fighter, monk, paladin, ranger, rogue, sorcerer, warlock, wizard, bloodhunter, platinum, gold, silver, copper, downtime, stamps, link)"
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                + " ON DUPLICATE KEY UPDATE"
                + " user_id = VALUES(user_id), name = VALUES(name), strength = VALUES(strength), dexterity = VALUES(dexterity), constitution = VALUES(constitution), intelligence = VALUES(intelligence), wisdom = VALUES(wisdom), charisma = VALUES(charisma), strSave = VALUES(strSave), dexSave = VALUES(dexSave), conSave = VALUES(conSave), intSave = VALUES(intSave), wisSave = VALUES(wisSave), chaSave = VALUES(chaSave), artificer = VALUES(artificer), barbarian = VALUES(barbarian), bard = VALUES(bard), cleric = VALUES(cleric), druid = VALUES(druid), fighter = VALUES(fighter), monk = VALUES(monk), paladin = VALUES(paladin), ranger = VALUES(ranger), rogue = VALUES(rogue), sorcerer = VALUES(sorcerer), warlock = VALUES(warlock), wizard = VALUES(wizard), bloodhunter = VALUES(bloodhunter), platinum = VALUES(platinum), gold = VALUES(gold), silver = VALUES(silver), copper = VALUES(copper), downtime = VALUES(downtime), stamps = VALUES(stamps), link = VALUES(link);";

        try (Connection conn = connect()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, pc.getUserId());
            ps.setString(2, pc.getName());
            ps.setInt(3, pc.getStrength());
            ps.setInt(4, pc.getDexterity());
            ps.setInt(5, pc.getConstitution());
            ps.setInt(6, pc.getIntelligence());
            ps.setInt(7, pc.getWisdom());
            ps.setInt(8, pc.getCharisma());
            ps.setInt(9, pc.getStrengthSave());
            ps.setInt(10, pc.getDexteritySave());
            ps.setInt(11, pc.getConstitutionSave());
            ps.setInt(12, pc.getIntelligenceSave());
            ps.setInt(13, pc.getWisdomSave());
            ps.setInt(14, pc.getCharismaSave());
            ps.setInt(15, pc.getArtificer());
            ps.setInt(16, pc.getBarbarian());
            ps.setInt(17, pc.getBard());
            ps.setInt(18, pc.getCleric());
            ps.setInt(19, pc.getDruid());
            ps.setInt(20, pc.getFighter());
            ps.setInt(21, pc.getMonk());
            ps.setInt(22, pc.getPaladin());
            ps.setInt(23, pc.getRanger());
            ps.setInt(24, pc.getRogue());
            ps.setInt(25, pc.getSorcerer());
            ps.setInt(26, pc.getWarlock());
            ps.setInt(27, pc.getWizard());
            ps.setInt(28, pc.getBloodHunter());
            ps.setInt(29, pc.getPlatinum());
            ps.setInt(30, pc.getGold());
            ps.setInt(31, pc.getSilver());
            ps.setInt(32, pc.getCopper());
            ps.setInt(33, pc.getDt());
            ps.setInt(34, pc.getStamps());
            ps.setString(35, pc.getLink());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
}
