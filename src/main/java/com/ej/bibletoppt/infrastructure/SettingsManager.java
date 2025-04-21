package com.ej.bibletoppt.infrastructure;

import com.ej.bibletoppt.infrastructure.database.ISQLiteConnector;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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
        // 데이터베이스에서 설정 로드
        loadSettingsFromDatabase();

        // 프로퍼티 파일에서 설정 로드
        loadSettingsFromProperties();
    }

    /**
     * 데이터베이스에서 설정을 로드합니다.
     */
    private void loadSettingsFromDatabase() {
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
            LOGGER.log(Level.SEVERE, "데이터베이스에서 설정 로드 중 오류 발생", e);
        }
    }

    /**
     * 프로퍼티 파일에서 설정을 로드합니다.
     */
    private void loadSettingsFromProperties() {
        Properties properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);

                // 프로퍼티를 설정 캐시에 추가
                for (String key : properties.stringPropertyNames()) {
                    String value = properties.getProperty(key);
                    settingsCache.put(key, value);
                }

                LOGGER.info("프로퍼티 파일에서 설정을 로드했습니다.");
            } else {
                LOGGER.warning("application.properties 파일을 찾을 수 없습니다.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "프로퍼티 파일에서 설정 로드 중 오류 발생", e);
        }
    }

    public String getSetting(String key) {
        return settingsCache.getOrDefault(key, null);
    }
}
