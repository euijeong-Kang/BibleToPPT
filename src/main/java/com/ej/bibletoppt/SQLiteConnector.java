package com.ej.bibletoppt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnector {

    private static final String DB_NAME = "identifier.sqlite";
    private Connection connection;

    public SQLiteConnector() {
        try {
            // SQLite JDBC 드라이버 로드
            Class.forName("org.sqlite.JDBC");
            // 데이터베이스 파일 경로를 'C:\Program Files\BibleToPPT\app'로 설정
            String dbPath = "C:/Program Files/BibleToPPT/app/" + DB_NAME;
            String jdbcUrl = "jdbc:sqlite:" + dbPath;
            // 객체 생성 시에 연결을 수립
            connection = DriverManager.getConnection(jdbcUrl);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
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