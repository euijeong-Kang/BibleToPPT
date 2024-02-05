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
            // 데이터베이스 파일 경로를 'C:\Program Files\BibleToPPT\app'로 설정
            File dbFile = extractDatabaseFile(DB_RESOURCE_PATH);
            String jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
//            String dbPath = getClass().getResource("/com/ej/bibletoppt/db/identifier.sqlite").getPath();
////            String jdbcUrl = "jdbc:sqlite:" + dbPath;
            // 객체 생성 시에 연결을 수립
            connection = DriverManager.getConnection(jdbcUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private File extractDatabaseFile(String resourcePath) throws Exception {
        URL inputUrl = getClass().getResource(resourcePath);
        if (inputUrl == null) {
            throw new Exception("Resource not found: " + resourcePath);
        }
        InputStream inputStream = inputUrl.openStream();
        // 임시 파일 생성
        File tempFile = File.createTempFile("db-", ".sqlite");
        tempFile.deleteOnExit();
        try (OutputStream outStream = new FileOutputStream(tempFile)) {
            // 파일 내용 복사
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }
        }
        return tempFile;
    }

    public Connection getConnection() {
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