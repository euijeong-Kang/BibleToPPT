package com.ej.bibletoppt.domain;

import com.ej.bibletoppt.infrastructure.ISettingsManager;
import com.ej.bibletoppt.infrastructure.di.DependencyContainer;
import java.awt.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PresentationStyle {
    private static final Logger LOGGER = Logger.getLogger(PresentationStyle.class.getName());

    private Color backgroundColor;
    private Color textColor;
    private double titleFontSize;
    private double bodyFontSize;
    private SlideSizeType slideSizeType;
    private String fontFamily;
    private boolean titleOption;

    public PresentationStyle(SlideSizeType slideSizeType, String fontFamily, boolean titleOption, 
                           String bgColor, String textColor, double titleFontSize, double bodyFontSize) {
        this.slideSizeType = slideSizeType;
        this.fontFamily = fontFamily;
        this.titleOption = titleOption;
        this.backgroundColor = Color.decode(bgColor);
        this.textColor = Color.decode(textColor);
        this.titleFontSize = titleFontSize;
        this.bodyFontSize = bodyFontSize;
    }

    public PresentationStyle(SlideSizeType slideSizeType, String fontFamily, boolean titleOption) {
        this(slideSizeType, fontFamily, titleOption, 65.0); // 기본 글자 크기 65로 설정
    }

    public PresentationStyle(SlideSizeType slideSizeType, String fontFamily, boolean titleOption, double bodyFontSize) {
        this.slideSizeType = slideSizeType;
        this.fontFamily = fontFamily;
        this.titleOption = titleOption;

        // 기본값 설정
        this.backgroundColor = Color.decode("#000000"); // 검은색 배경
        this.textColor = Color.decode("#FFFFFF"); // 흰색 텍스트
        this.titleFontSize = 54;
        this.bodyFontSize = bodyFontSize;

        // 설정 파일에서 값 로드 시도
        try {
            ISettingsManager settingsManager = DependencyContainer.getInstance().getSettingsManager();
            String bgColorStr = settingsManager.getSetting("backgroundColor");
            String textColorStr = settingsManager.getSetting("textColor");
            String titleFontSizeStr = settingsManager.getSetting("titleFontSize");

            // null이 아닌 경우에만 설정값 적용
            if (bgColorStr != null) this.backgroundColor = Color.decode(bgColorStr);
            if (textColorStr != null) this.textColor = Color.decode(textColorStr);
            if (titleFontSizeStr != null) this.titleFontSize = Double.parseDouble(titleFontSizeStr);
            // bodyFontSize는 매개변수로 받은 값을 사용하므로 설정에서 로드하지 않음
        } catch (Exception e) {
            // 설정 로드 실패 시 기본값 유지
            LOGGER.log(Level.WARNING, "스타일 설정 로드 중 오류 발생", e);
        }
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public double getTitleFontSize() {
        return titleFontSize;
    }

    public double getBodyFontSize() {
        return bodyFontSize;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public SlideSizeType getSlideSizeType() {
        return slideSizeType;
    }

    public boolean isTitleOptionCheck() {
        return this.titleOption;
    }
}
