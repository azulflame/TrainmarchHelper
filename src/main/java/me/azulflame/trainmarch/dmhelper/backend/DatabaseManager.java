package me.azulflame.trainmarch.dmhelper.backend;

import java.sql.*;
import java.util.List;

public class DatabaseManager {
    private static final String url = "jdbc:sqlite:db.sqlite";

    private static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(url);
        return conn;
    }

    public static void loadItems() {
        // if the table doesn't exist, remove it
        try (Connection conn = connect()) {
            String tableQuery = "CREATE TABLE IF NOT EXISTS items (item text NOT NULL, shop text NOT NULL);";
            Statement stmt = conn.createStatement();
            stmt.execute(tableQuery);

            String getQuery = "SELECT * FROM items;";
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(getQuery);
            while (rs.next()) {
                Shops.add(rs.getString("shop"), List.of(rs.getString("item")));
            }
        } catch (SQLException e) {

        }
    }

    public static void deleteItem(String shop, String item) {
        try (Connection conn = connect()) {
            String query = "DELETE FROM items WHERE shop = ? AND item = ?;";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, shop);
            ps.setString(2, item);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {

        }
    }

    public static void insertItem(String shop, List<String> items) {
        try (Connection conn = connect()) {
            for (String item : items) {
                String query = "INSERT INTO items(shop, item) VALUES (?, ?);";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, shop);
                ps.setString(2, item);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException e) {

        }
    }
}
