/**
 * 성경 구절 미리보기를 위한 인터페이스입니다.
 * 이 인터페이스는 성경 구절의 미리보기 정보를 제공합니다.
 */
package com.ej.bibletoppt.service;

public interface IPreviewService {
    /**
     * 현재 선택된 구절의 미리보기 정보를 가져옵니다.
     * 
     * @param verseReference 미리보기할 구절 참조 (예: "창세기 1:1")
     * @return 미리보기 정보 객체
     */
    PreviewInfo getPreviewInfo(String verseReference);
    
    /**
     * 미리보기 정보가 비어있는지 확인합니다.
     * 
     * @param verseReference 확인할 구절 참조
     * @return 미리보기 정보가 비어있으면 true, 그렇지 않으면 false
     */
    boolean isPreviewEmpty(String verseReference);
}