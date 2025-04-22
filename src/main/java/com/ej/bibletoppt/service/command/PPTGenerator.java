package com.ej.bibletoppt.service.command;

import com.ej.bibletoppt.controller.dto.PresentationRequest;
import com.ej.bibletoppt.domain.Presentation;
import com.ej.bibletoppt.domain.PresentationStyle;
import com.ej.bibletoppt.service.query.ISearchBible;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PPTGenerator implements IPPTGenerator {
    private static final Logger LOGGER = Logger.getLogger(PPTGenerator.class.getName());

    private final ISearchBible searchBible;

    public PPTGenerator(ISearchBible searchBible) {
        this.searchBible = searchBible;
    }

    @Override
    public void createPresentation(PresentationRequest request) {
        try {
            // 검색 결과를 받아옴
            List<String> verses = searchBible.searchVerses(request.bibleVerseInput());

            if (verses.isEmpty()) {
                throw new IllegalArgumentException("검색된 성경 구절이 없습니다.");
            }

            PresentationStyle style = new PresentationStyle(request.selectedSize(), request.selectedFont(), request.titleSlideOption(), request.bodyFontSize());

            // Presentation 객체 생성 및 슬라이드 크기 설정
            Presentation presentation = new Presentation(style);

            // PPT 생성 및 저장
            presentation.createPresentationFromVerses(verses, request.mainTitle(), request.outputPath().toString());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "프레젠테이션 생성 중 오류 발생", e);
            throw new RuntimeException("프레젠테이션 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
