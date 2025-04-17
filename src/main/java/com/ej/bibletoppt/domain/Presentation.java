package com.ej.bibletoppt.domain;

import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.xslf.usermodel.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Presentation {
    private static final Logger LOGGER = Logger.getLogger(Presentation.class.getName());

    // 상수 정의
    private static final String EMPTY_TITLE = " ";
    private static final String VERSE_DELIMITER = "&";
    private static final int VERSE_PARTS_COUNT = 2;

    // 제목 텍스트 박스 위치 및 크기 상수
    private static final double TITLE_X_PERCENT = 0.04;
    private static final double TITLE_Y_PERCENT = 0.065;
    private static final double TITLE_WIDTH_PERCENT = 0.92;
    private static final double TITLE_HEIGHT_PERCENT = 0.125;

    // 내용 텍스트 박스 위치 및 크기 상수
    private static final double CONTENT_X_PERCENT = 0.04;
    private static final double CONTENT_Y_PERCENT = 0.235;
    private static final double CONTENT_WIDTH_PERCENT = 0.92;
    private static final double CONTENT_HEIGHT_PERCENT = 0.75;

    // 투명 색상 상수
    private static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);

    private XMLSlideShow pptx;
    private Dimension slideSize;
    private PresentationStyle style;


    public Presentation(PresentationStyle style) {
        this.pptx = new XMLSlideShow();
        this.style = style;
        setSlideSize();
    }

    private void setSlideSize() {
        this.slideSize = new Dimension((int) (style.getSlideSizeType().getWidth() * 72), (int) (style.getSlideSizeType().getHeight() * 72));
        pptx.setPageSize(this.slideSize);
    }

    private void addTitleSlide(String mainTitle) {
        createAndConfigureSlide(EMPTY_TITLE, mainTitle);
    }

    private void addVerseSlide(String verse) {
        if (verse == null || verse.trim().isEmpty()) {
            LOGGER.warning("빈 구절이 전달되었습니다.");
            return;
        }

        String[] parts = verse.split(VERSE_DELIMITER, VERSE_PARTS_COUNT);
        if (parts.length == VERSE_PARTS_COUNT) {
            createAndConfigureSlide(parts[0], parts[1]);
        } else {
            LOGGER.warning("유효하지 않은 형식: " + verse);
            // 오류 처리 또는 기본값 사용
            createAndConfigureSlide("오류", "구절 형식이 올바르지 않습니다: " + verse);
        }
    }

    private void createAndConfigureSlide(String title, String content) {
        XSLFSlide slide = pptx.createSlide();
        slide.getBackground().setFillColor(style.getBackgroundColor()); // 배경색 설정

        if (!title.isEmpty()) {
            // 제목 텍스트 박스 설정
            configureTextBox(slide, title, TITLE_X_PERCENT, TITLE_Y_PERCENT, TITLE_WIDTH_PERCENT, TITLE_HEIGHT_PERCENT, 
                    style.getTitleFontSize(), style.getFontFamily(), style.getTextColor());
        }

        if (!content.isEmpty()) {
            // 내용 텍스트 박스 설정
            configureTextBox(slide, content, CONTENT_X_PERCENT, CONTENT_Y_PERCENT, CONTENT_WIDTH_PERCENT, CONTENT_HEIGHT_PERCENT, 
                    style.getBodyFontSize(), style.getFontFamily(), style.getTextColor());
        }
    }

    private void configureTextBox(XSLFSlide slide, String text, double xPercent, double yPercent, double widthPercent, double heightPercent, double fontSize, String fontFamily, Color fontColor) {
        XSLFTextShape shape = slide.createTextBox();
        Rectangle2D anchor = new Rectangle2D.Double(slideSize.getWidth() * xPercent, slideSize.getHeight() * yPercent, slideSize.getWidth() * widthPercent, slideSize.getHeight() * heightPercent);
        shape.setAnchor(anchor);
        shape.setTextAutofit(TextShape.TextAutofit.NORMAL);
        shape.setText(text);
        shape.setFillColor(TRANSPARENT_COLOR); // 텍스트 박스 배경 투명 설정


        shape.getTextParagraphs().forEach(paragraph -> paragraph.getTextRuns().forEach(textRun -> {
            textRun.setFontColor(fontColor);
            textRun.setFontSize(fontSize);
            textRun.setFontFamily(fontFamily);
        }));
    }

    public void save(String filePath) throws IOException {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            pptx.write(out);
            LOGGER.info(filePath + " 파일이 생성되었습니다.");
        }
    }

    // 새로운 public 함수: 구절 리스트를 바탕으로 PPT 생성
    public void createPresentationFromVerses(List<String> verses, String mainTitle, String filePath) {
        if (!mainTitle.isEmpty() && this.style.isTitleOptionCheck()) {
            addTitleSlide(mainTitle);
        }

        for (String verse : verses) {
            addVerseSlide(verse);
        }

        try {
            save(filePath);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "프레젠테이션 저장 중 오류 발생", e);
        }
    }
}
