/**
 * 성경 구절 미리보기를 위한 서비스 구현체입니다.
 * 이 클래스는 성경 구절의 미리보기 정보를 제공합니다.
 */
package com.ej.bibletoppt.service;

import com.ej.bibletoppt.service.query.ISearchBible;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PreviewService implements IPreviewService {
    private static final Logger LOGGER = Logger.getLogger(PreviewService.class.getName());
    
    private final ISearchBible searchBible;
    
    public PreviewService(ISearchBible searchBible) {
        this.searchBible = searchBible;
    }
    
    @Override
    public PreviewInfo getPreviewInfo(String verseReference) {
        if (verseReference == null || verseReference.trim().isEmpty()) {
            return PreviewInfo.empty();
        }
        
        try {
            // 첫 번째 구절의 실제 내용을 가져오기
            List<String> verseTexts = searchBible.searchVerses(verseReference);
            
            if (verseTexts.isEmpty()) {
                LOGGER.warning("구절을 찾을 수 없습니다: " + verseReference);
                return PreviewInfo.empty();
            }
            
            // 첫 번째 구절의 내용 추출 (& 이후의 텍스트)
            String fullVerse = verseTexts.get(0);
            String[] verseParts = fullVerse.split("&", 2);
            
            if (verseParts.length > 1) {
                String verseContent = verseParts[1].trim();
                return PreviewInfo.of(verseReference, verseContent);
            } else {
                LOGGER.warning("구절 내용을 추출할 수 없습니다: " + fullVerse);
                return PreviewInfo.of(verseReference, "");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "미리보기 정보를 가져오는 중 오류 발생", e);
            return PreviewInfo.empty();
        }
    }
    
    @Override
    public boolean isPreviewEmpty(String verseReference) {
        return getPreviewInfo(verseReference).isEmpty();
    }
}