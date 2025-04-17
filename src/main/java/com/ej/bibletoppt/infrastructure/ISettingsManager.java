/**
 * 애플리케이션 설정을 관리하는 인터페이스입니다.
 * 이 인터페이스는 설정 값을 저장하고 로드하는 기능을 제공합니다.
 */
package com.ej.bibletoppt.infrastructure;

public interface ISettingsManager {
    /**
     * 모든 설정을 로드합니다.
     */
    void loadAllSettings();
    
    /**
     * 설정을 저장합니다.
     * 
     * @param setting 저장할 설정
     */
    void saveSetting(Settings setting);
    
    /**
     * 설정 값을 가져옵니다.
     * 
     * @param key 설정 키
     * @return 설정 값, 설정이 없는 경우 null
     */
    String getSetting(String key);
}