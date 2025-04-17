/**
 * SQLite 데이터베이스 연결을 관리하는 인터페이스입니다.
 * 이 인터페이스는 데이터베이스 연결 생성, 관리 및 종료 기능을 제공합니다.
 */
package com.ej.bibletoppt.infrastructure.database;

import java.sql.Connection;

public interface ISQLiteConnector {
    /**
     * 데이터베이스 연결을 가져옵니다.
     * 연결이 존재하지 않거나 닫혀 있으면 새로운 연결을 생성합니다.
     * 
     * @return 데이터베이스 연결 객체
     */
    Connection getConnection();
    
    /**
     * 데이터베이스 연결을 종료합니다.
     */
    void closeConnection();
}