package com.ej.bibletoppt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnector {

//    private static final String JDBC_URL = "jdbc:sqlite:identifier.sqlite";
private static final String JDBC_URL = "jdbc:sqlite:C:/workspace/toys/BibleToPPT/src/main/resources/com/ej/bibletoppt/db/identifier.sqlite";
    private Connection connection;

    public SQLiteConnector() {
        try {
            // SQLite JDBC 드라이버 로드
            Class.forName("org.sqlite.JDBC");
            // 객체 생성 시에 연결을 수립
            connection = DriverManager.getConnection(JDBC_URL);
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