package com.ej.bibletoppt.domain;

import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.xslf.usermodel.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Presentation {
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
        createAndConfigureSlide(" ", mainTitle);
    }

    private void addVerseSlide(String verse) {
        String[] parts = verse.split("&", 2);
        if (parts.length == 2) {
            createAndConfigureSlide(parts[0], parts[1]);
        } else {
            System.err.println("Invalid format: " + verse);
        }
    }

    private void createAndConfigureSlide(String title, String content) {
        XSLFSlide slide = pptx.createSlide();
        slide.getBackground().setFillColor(style.getBackgroundColor()); // 배경색 설정

        if (!title.isEmpty()) {
            // 제목 텍스트 박스 설정
            configureTextBox(slide, title, 0.04, 0.065, 0.92, 0.125, style.getTitleFontSize(), style.getFontFamily(), style.getTextColor());
        }

        if (!content.isEmpty()) {
            // 내용 텍스트 박스 설정
            configureTextBox(slide, content, 0.04, 0.235, 0.92, 0.75, style.getBodyFontSize(), style.getFontFamily(), style.getTextColor());
        }
    }

    private void configureTextBox(XSLFSlide slide, String text, double xPercent, double yPercent, double widthPercent, double heightPercent, double fontSize, String fontFamily, Color fontColor) {
        XSLFTextShape shape = slide.createTextBox();
        Rectangle2D anchor = new Rectangle2D.Double(slideSize.getWidth() * xPercent, slideSize.getHeight() * yPercent, slideSize.getWidth() * widthPercent, slideSize.getHeight() * heightPercent);
        shape.setAnchor(anchor);
        shape.setTextAutofit(TextShape.TextAutofit.NORMAL);
        shape.setText(text);
        shape.setFillColor(new Color(0,0,0,0)); // 텍스트 박스 배경 투명 설정


        shape.getTextParagraphs().forEach(paragraph -> paragraph.getTextRuns().forEach(textRun -> {
            textRun.setFontColor(fontColor);
            textRun.setFontSize(fontSize);
            textRun.setFontFamily(fontFamily);
        }));
    }

    public void save(String filePath) throws IOException {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            pptx.write(out);
            System.out.println(filePath + " 파일이 생성되었습니다.");
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
            e.printStackTrace();
        }
    }
}