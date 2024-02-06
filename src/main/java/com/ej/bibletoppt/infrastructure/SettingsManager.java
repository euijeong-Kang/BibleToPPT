package com.ej.bibletoppt.infrastructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SettingsManager {

    private SQLiteConnector connector;
    private Map<String, String> settingsCache;

    public SettingsManager(SQLiteConnector connector) {
        this.connector = connector;
        this.settingsCache = new HashMap<>();
    }

    public void saveSetting(Settings setting) {
        String sql = "INSERT OR REPLACE INTO Settings (Key, Value) VALUES(?, ?)";
        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, setting.getKey());
            pstmt.setString(2, setting.getValue());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

//    public Settings getSetting(String key) {
//        String sql = "SELECT Key, Value FROM Settings WHERE Key = ?";
//
//        try (Connection conn = connector.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, key);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                return new Settings(rs.getString("Key"), rs.getString("Value"));
//            }
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        return null;
//    }

    public void loadAllSettings() {
        String sql = "SELECT Key, Value FROM Settings";

        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String key = rs.getString("Key");
                String value = rs.getString("Value");
                settingsCache.put(key, value);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getSetting(String key) {
        return settingsCache.getOrDefault(key, null);
    }
}
