/**
 * 성경 구절 형식의 유효성을 검사하고 정규화하는 인터페이스입니다.
 * 이 인터페이스는 입력된 성경 구절 문자열이 올바른 형식인지 확인하고,
 * 약어를 전체 이름으로 변환하는 등의 정규화 작업을 수행합니다.
 */
package com.ej.bibletoppt.service;

import java.util.List;

public interface IBibleVerseValidator {
    /**
     * 입력된 성경 구절 문자열이 올바른 형식인지 검사합니다.
     * 
     * @param input 검사할 성경 구절 문자열 (예: "창세기 1:1", "창 1:1-20", "창 1:1-20, 마 28:19-20")
     * @return 입력된 문자열이 올바른 형식이면 true, 그렇지 않으면 false
     */
    boolean validate(String input);

    /**
     * 입력된 성경 구절 문자열을 정규화합니다.
     * 이 메서드는 약어를 전체 이름으로 변환하고, 형식을 일관되게 만듭니다.
     * 
     * @param input 정규화할 성경 구절 문자열 (예: "창 1:1", "마 28:19-20")
     * @return 정규화된 성경 구절 문자열 (예: "창세기 1:1", "마태복음 28:19-20")
     */
    String normalize(String input);

    /**
     * 입력된 부분적인 성경 구절 문자열을 자동완성합니다.
     * 
     * @param partialInput 자동완성할 부분적인 성경 구절 문자열 (예: "창 1", "마 28")
     * @return 자동완성된 성경 구절 문자열 목록 (예: ["창세기 1:1", "창세기 1:2", ...])
     */
    List<String> autocomplete(String partialInput);

    /**
     * 입력된 성경 구절이 실제로 존재하는지 확인합니다.
     * 
     * @param input 확인할 성경 구절 문자열 (예: "창세기 1:1", "창 1:1")
     * @return 성경 구절이 존재하면 true, 그렇지 않으면 false
     */
    boolean verseExists(String input);

    /**
     * 입력된 성경 구절을 기존 입력에 추가합니다.
     * 
     * @param currentInput 현재 입력된 성경 구절 문자열 (예: "창세기 1:1")
     * @param newVerse 추가할 성경 구절 문자열 (예: "마태복음 28:19")
     * @return 두 구절이 합쳐진 문자열 (예: "창세기 1:1, 마태복음 28:19")
     */
    String appendVerse(String currentInput, String newVerse);
}
