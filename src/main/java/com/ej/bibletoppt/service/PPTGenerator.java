package com.ej.bibletoppt.service;

import com.ej.bibletoppt.controller.dto.PresentationRequest;
import com.ej.bibletoppt.infrastructure.SQLiteConnector;
import com.ej.bibletoppt.SlideSizeType;
import com.ej.bibletoppt.TextBoxType;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PPTGenerator {

    private static final String BACKGROUND_COLOR = "0x000000"; // 검은색 배경
    private static final String TEXT_COLOR = "0xFFFFFF"; // 흰색 텍스트
    private static final double TITLE_FONT_SIZE = 54;
    private static final double BODY_FONT_SIZE = 65;
    private static double SLIDE_WIDTH;
    private static double SLIDE_HEIGHT;
    private static String FONT = "나눔스퀘어 Bold";

    public void createPresentation(PresentationRequest request) {
        // SearchBible 클래스를 생성하고 SQLiteConnector를 전달
        SearchBible searchBible = new SearchBible(new SQLiteConnector());

        // 검색 결과를 받아옴
        List<String> verses = searchBible.searchVerses(request.bibleVerseInput());
        try {
            // 빈 PPTX 생성
            XMLSlideShow pptx = new XMLSlideShow();

            // 16:9 크기로 슬라이드 설정
            setSlideSize(pptx, request.selectedSize());
            FONT = request.selectedFont();


            // 검색 결과를 이용하여 각 절별로 슬라이드 추가
            for (String verse : verses) {
                addVerseSlide(pptx, verse);
            }

            // PPTX 파일 저장
            try (FileOutputStream out = new FileOutputStream(request.outputPath().toFile())) {
                pptx.write(out);
                System.out.println(request.outputPath() + " 파일이 생성되었습니다.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSlideSize(XMLSlideShow pptx, SlideSizeType type) {
        // 슬라이드 크기를 설정
        Dimension pageSize = new Dimension((int) (type.getWidth() * 72), (int) (type.getHeight() * 72));
        pptx.setPageSize(pageSize);
    }

    private void addVerseSlide(XMLSlideShow pptx, String verse) {
        String[] parts = verse.split("&", 2);
        if (parts.length == 2) {
            String title = parts[0];
            String verseContent = parts[1];
            createAndConfigureSlide(pptx, title, verseContent);
        } else {
            // parts의 길이가 2가 아닌 경우 처리 (예외 상황에 따라 추가 수정 필요)
            System.err.println("Invalid format: " + verse);
        }
    }

    private void createAndConfigureSlide(XMLSlideShow pptx, String title, String verseContent) {
        XSLFSlide slide = pptx.createSlide();

        // 슬라이드 너비 계산
        SLIDE_WIDTH = pptx.getPageSize().getWidth();
        SLIDE_HEIGHT = pptx.getPageSize().getHeight();

        // 슬라이드 배경색 설정
        slide.getBackground().setFillColor(Color.decode(BACKGROUND_COLOR));

        // 제목과 내용 추가
        XSLFTextShape titleShape = createTextBox(slide, title, TextBoxType.TITLE);
        XSLFTextShape contentShape = createTextBox(slide, verseContent, TextBoxType.CONTENT);

        // 글자 색상 및 크기 설정
        setTextColorAndSize(titleShape, Color.decode(TEXT_COLOR), TITLE_FONT_SIZE, FONT);
        setTextColorAndSize(contentShape, Color.decode(TEXT_COLOR), BODY_FONT_SIZE, FONT);
    }

    private XSLFTextShape createTextBox(XSLFSlide slide, String text, TextBoxType type) {
        // 텍스트 상자를 생성하고 텍스트를 설정
        XSLFTextShape textShape = slide.createTextBox();
        textShape.setText(text);

        // 앵커 설정
        initSizeAndPosition(textShape, type);

        return textShape;
    }

    private void setTextColorAndSize(XSLFTextShape shape, Color color, Double fontSize, String selectedFont) {
        shape.getTextParagraphs().forEach(paragraph -> {
            paragraph.getTextRuns().forEach(textRun -> {
                textRun.setFontColor(color);
                textRun.setFontSize(fontSize);
                textRun.setFontFamily(selectedFont);
            });
        });
    }

    private void initSizeAndPosition(XSLFTextShape textShape, TextBoxType type) {
        // 슬라이드의 중앙 계산
        double slideCenterX = SLIDE_WIDTH / 2.0;

        // 텍스트 박스의 중앙 위치 계산
        double textBoxWidth = SLIDE_WIDTH - 80;
        double textBoxCenterX = slideCenterX - textBoxWidth / 2.0;

        double textBoxHeight;

        // 텍스트 박스의 y축 위치 계산
        double textBoxY;
        if (type.equals(TextBoxType.TITLE)) {
            textBoxY = 85;
            textBoxHeight = TITLE_FONT_SIZE * 1.5;
        } else {
            double titleHeight = 40;
            textBoxY = 2 * titleHeight + 100;
            textBoxHeight = SLIDE_HEIGHT - 120 - TITLE_FONT_SIZE * 2;
        }
        // 텍스트 박스의 앵커 설정
        Rectangle2D anchor = new Rectangle2D.Double(textBoxCenterX, textBoxY, textBoxWidth, textBoxHeight);
        textShape.setAnchor(anchor);
    }
}