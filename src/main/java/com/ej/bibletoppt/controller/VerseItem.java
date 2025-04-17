package com.ej.bibletoppt.controller;

/**
 * 성경 구절 항목을 나타내는 클래스입니다.
 * 이 클래스는 성경 구절 텍스트와 위치 정보를 저장합니다.
 */
public class VerseItem {
    private final String text;
    private int position;

    /**
     * 성경 구절 항목을 생성합니다.
     * 
     * @param text 성경 구절 텍스트
     * @param position 목록 내 위치
     */
    public VerseItem(String text, int position) {
        this.text = text;
        this.position = position;
    }

    /**
     * 성경 구절 텍스트를 반환합니다.
     * 
     * @return 성경 구절 텍스트
     */
    public String getText() {
        return text;
    }

    /**
     * 목록 내 위치를 반환합니다.
     * 
     * @return 목록 내 위치
     */
    public int getPosition() {
        return position;
    }

    /**
     * 목록 내 위치를 설정합니다.
     * 
     * @param position 새로운 위치
     */
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return text;
    }
}