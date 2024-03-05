package com.ej.bibletoppt.service.command;

import com.ej.bibletoppt.controller.dto.PresentationRequest;
import com.ej.bibletoppt.domain.Presentation;
import com.ej.bibletoppt.domain.PresentationStyle;
import com.ej.bibletoppt.infrastructure.database.SQLiteConnector;
import com.ej.bibletoppt.domain.SlideSizeType;
import com.ej.bibletoppt.domain.TextBoxType;
import com.ej.bibletoppt.service.query.SearchBible;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PPTGenerator {

    public void createPresentation(PresentationRequest request) {
        // SearchBible 클래스를 생성하고 SQLiteConnector를 전달
        SearchBible searchBible = new SearchBible(new SQLiteConnector());

        // 검색 결과를 받아옴
        List<String> verses = searchBible.searchVerses(request.bibleVerseInput());

        PresentationStyle style = new PresentationStyle(request.selectedSize(), request.selectedFont(), request.titleSlideOption());

        // Presentation 객체 생성 및 슬라이드 크기 설정
        Presentation presentation = new Presentation(style);

        // PPT 생성 및 저장
        presentation.createPresentationFromVerses(verses, request.mainTitle(), request.outputPath().toString());
    }
}