package com.ej.bibletoppt.infrastructure;

import com.ej.bibletoppt.infrastructure.database.ISQLiteConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SettingsManager implements ISettingsManager {
    private static final Logger LOGGER = Logger.getLogger(SettingsManager.class.getName());

    private ISQLiteConnector connector;
    private Map<String, String> settingsCache;

    public SettingsManager(ISQLiteConnector connector) {
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
            LOGGER.log(Level.SEVERE, "설정 저장 중 오류 발생", e);
        }
    }

    public void loadAllSettings() {
        String sql = "SELECT Key, Value FROM Settings";

        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String key = rs.getString("Key");
                String value = rs.getString("Value");
                settingsCache.put(key, value);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "설정 로드 중 오류 발생", e);
        }
    }

    public String getSetting(String key) {
        return settingsCache.getOrDefault(key, null);
    }
}
