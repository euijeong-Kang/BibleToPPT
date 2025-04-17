/**
 * 성경 구절을 검색하는 인터페이스입니다.
 * 이 인터페이스는 입력된 성경 구절 문자열을 기반으로 데이터베이스에서 해당 구절을 검색합니다.
 */
package com.ej.bibletoppt.service.query;

import java.util.List;

public interface ISearchBible {
    /**
     * 입력된 성경 구절 문자열을 기반으로 데이터베이스에서 해당 구절을 검색합니다.
     * 
     * @param input 검색할 성경 구절 문자열 (예: "창세기 1:1", "창 1:1-20", "창 1:1-20, 마 28:19-20")
     * @return 검색된 성경 구절 목록
     */
    List<String> searchVerses(String input);
}