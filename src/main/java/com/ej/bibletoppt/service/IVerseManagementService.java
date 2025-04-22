/**
 * 성경 구절 관리를 위한 인터페이스입니다.
 * 이 인터페이스는 성경 구절의 추가, 제거, 이동 등의 기능을 제공합니다.
 */
package com.ej.bibletoppt.service;

import com.ej.bibletoppt.controller.VerseItem;
import java.util.List;

public interface IVerseManagementService {
    /**
     * 구절을 추가합니다.
     * 
     * @param verse 추가할 구절 문자열
     * @return 추가된 구절 항목, 이미 존재하는 구절이면 null
     */
    VerseItem addVerse(String verse);
    
    /**
     * 구절을 제거합니다.
     * 
     * @param item 제거할 구절 항목
     */
    void removeVerse(VerseItem item);
    
    /**
     * 구절을 위로 이동합니다.
     * 
     * @param item 이동할 구절 항목
     * @return 이동 성공 여부
     */
    boolean moveVerseUp(VerseItem item);
    
    /**
     * 구절을 아래로 이동합니다.
     * 
     * @param item 이동할 구절 항목
     * @return 이동 성공 여부
     */
    boolean moveVerseDown(VerseItem item);
    
    /**
     * 모든 구절을 제거합니다.
     */
    void clearVerses();
    
    /**
     * 현재 구절 목록을 가져옵니다.
     * 
     * @return 구절 항목 목록
     */
    List<VerseItem> getVerseItems();
    
    /**
     * 현재 구절 문자열을 가져옵니다.
     * 
     * @return 쉼표로 구분된 구절 문자열
     */
    String getCurrentVerses();
}