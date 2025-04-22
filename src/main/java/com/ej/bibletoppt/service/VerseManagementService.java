/**
 * 성경 구절 관리를 위한 서비스 구현체입니다.
 * 이 클래스는 성경 구절의 추가, 제거, 이동 등의 기능을 제공합니다.
 */
package com.ej.bibletoppt.service;

import com.ej.bibletoppt.controller.VerseItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.logging.Logger;

public class VerseManagementService implements IVerseManagementService {
    private static final Logger LOGGER = Logger.getLogger(VerseManagementService.class.getName());
    
    private final ObservableList<VerseItem> verseItems = FXCollections.observableArrayList();
    private final IBibleVerseValidator bibleVerseValidator;
    private String currentVerses = "";
    
    public VerseManagementService(IBibleVerseValidator bibleVerseValidator) {
        this.bibleVerseValidator = bibleVerseValidator;
    }
    
    @Override
    public VerseItem addVerse(String verse) {
        // 정규화된 구절로 변환
        String normalizedVerse = bibleVerseValidator.normalize(verse);
        
        // 중복 검사
        for (VerseItem item : verseItems) {
            if (item.getText().equals(normalizedVerse)) {
                return null; // 이미 존재하는 구절이면 null 반환
            }
        }
        
        // 새 구절 항목 생성 및 추가
        int position = verseItems.size();
        VerseItem newItem = new VerseItem(normalizedVerse, position);
        verseItems.add(newItem);
        
        // 현재 구절 문자열 업데이트
        updateCurrentVerses();
        
        return newItem;
    }
    
    @Override
    public void removeVerse(VerseItem item) {
        // 목록에서 항목 제거
        verseItems.remove(item);
        
        // 위치 정보 업데이트
        for (int i = 0; i < verseItems.size(); i++) {
            verseItems.get(i).setPosition(i);
        }
        
        // 현재 구절 문자열 업데이트
        updateCurrentVerses();
    }
    
    @Override
    public boolean moveVerseUp(VerseItem item) {
        int position = item.getPosition();
        if (position > 0) {
            // 위치 교환
            VerseItem upperItem = verseItems.get(position - 1);
            item.setPosition(position - 1);
            upperItem.setPosition(position);
            
            // 목록 재정렬
            FXCollections.sort(verseItems, (a, b) -> Integer.compare(a.getPosition(), b.getPosition()));
            
            // 현재 구절 문자열 업데이트
            updateCurrentVerses();
            
            return true;
        }
        return false;
    }
    
    @Override
    public boolean moveVerseDown(VerseItem item) {
        int position = item.getPosition();
        if (position < verseItems.size() - 1) {
            // 위치 교환
            VerseItem lowerItem = verseItems.get(position + 1);
            item.setPosition(position + 1);
            lowerItem.setPosition(position);
            
            // 목록 재정렬
            FXCollections.sort(verseItems, (a, b) -> Integer.compare(a.getPosition(), b.getPosition()));
            
            // 현재 구절 문자열 업데이트
            updateCurrentVerses();
            
            return true;
        }
        return false;
    }
    
    @Override
    public void clearVerses() {
        // 구절 목록 초기화
        verseItems.clear();
        
        // 현재 구절 문자열 초기화
        currentVerses = "";
    }
    
    @Override
    public List<VerseItem> getVerseItems() {
        return verseItems;
    }
    
    @Override
    public String getCurrentVerses() {
        return currentVerses;
    }
    
    /**
     * 현재 구절 문자열을 업데이트합니다.
     */
    private void updateCurrentVerses() {
        if (verseItems.isEmpty()) {
            currentVerses = "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < verseItems.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(verseItems.get(i).getText());
            }
            currentVerses = sb.toString();
        }
    }
}