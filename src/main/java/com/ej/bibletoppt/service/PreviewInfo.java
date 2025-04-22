/**
 * 성경 구절 미리보기 정보를 담는 클래스입니다.
 * 이 클래스는 미리보기에 필요한 구절 참조와 내용을 포함합니다.
 */
package com.ej.bibletoppt.service;

/**
 * 성경 구절 미리보기 정보를 담는 레코드입니다.
 * 
 * @param reference 구절 참조 (예: "창세기 1:1")
 * @param content 구절 내용
 * @param isEmpty 미리보기가 비어있는지 여부
 */
public record PreviewInfo(String reference, String content, boolean isEmpty) {
    /**
     * 비어있는 미리보기 정보를 생성합니다.
     * 
     * @return 비어있는 미리보기 정보
     */
    public static PreviewInfo empty() {
        return new PreviewInfo("", "", true);
    }
    
    /**
     * 구절 참조와 내용으로 미리보기 정보를 생성합니다.
     * 
     * @param reference 구절 참조
     * @param content 구절 내용
     * @return 미리보기 정보
     */
    public static PreviewInfo of(String reference, String content) {
        return new PreviewInfo(reference, content, false);
    }
}