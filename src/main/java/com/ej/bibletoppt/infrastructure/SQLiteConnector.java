package com.ej.bibletoppt.infrastructure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnector {

    private static final String DB_NAME = "identifier.sqlite";
    private static final String DB_RESOURCE_PATH = "/com/ej/bibletoppt/db/identifier.sqlite";
    private Connection connection;

    public SQLiteConnector() {
        try {
            // SQLite JDBC 드라이버 로드
            Class.forName("org.sqlite.JDBC");
            // 데이터베이스 파일을 사용자 홈 디렉토리 내의 고정된 위치로 설정
            File dbFile = getDatabaseFile();
            if (!dbFile.exists()) {
                extractDatabaseFile(DB_RESOURCE_PATH, dbFile);
            }
            String jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            // 객체 생성 시에 연결을 수립
            connection = DriverManager.getConnection(jdbcUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getDatabaseFile() throws Exception {
        // 'user.home' 대신 'user.dir'을 사용하여 애플리케이션의 작업 디렉토리를 기반으로 경로를 설정할 수 있습니다.
        String appDataPath = System.getenv("LOCALAPPDATA");
        if (appDataPath == null) {
            // 환경 변수에서 LOCALAPPDATA 경로를 찾을 수 없는 경우, 사용자 홈 디렉토리의 대체 경로를 사용합니다.
            appDataPath = System.getProperty("user.home") + "\\AppData\\Local";
        }
        File dbDirectory = new File(appDataPath, "BibleToPPT");
        if (!dbDirectory.exists()) {
            if (!dbDirectory.mkdirs()) {
                throw new Exception("Failed to create directory: " + dbDirectory.getAbsolutePath());
            }
        }
        return new File(dbDirectory, DB_NAME);
    }

    private void extractDatabaseFile(String resourcePath, File dbFile) throws Exception {
        URL inputUrl = getClass().getResource(resourcePath);
        if (inputUrl == null) {
            throw new Exception("Resource not found: " + resourcePath);
        }
        try (InputStream inputStream = inputUrl.openStream();
             OutputStream outStream = new FileOutputStream(dbFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }
        }
    }

    public Connection getConnection() {
        try {
            // 연결이 존재하지 않거나 닫혀 있으면 새로운 연결을 생성
            if (connection == null || connection.isClosed()) {
                String dbPath = getDatabaseFile().getAbsolutePath();
                String url = "jdbc:sqlite:" + dbPath;
                connection = DriverManager.getConnection(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    // 데이터베이스 연결 종료
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
