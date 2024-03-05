package com.ej.bibletoppt.domain;

import java.awt.*;

public class PresentationStyle {
    private static final Color BACKGROUND_COLOR = Color.decode("#000000"); // 검은색 배경
    private static final Color TEXT_COLOR = Color.decode("#FFFFFF"); // 흰색 텍스트
    private static final double TITLE_FONT_SIZE = 54;
    private static final double BODY_FONT_SIZE = 65;
    private SlideSizeType slideSizeType;
    private String fontFamily;
    private boolean titleOption;

    public PresentationStyle(SlideSizeType slideSizeType, String fontFamily, boolean titleOption) {
        this.slideSizeType = slideSizeType;
        this.fontFamily = fontFamily;
        this.titleOption = titleOption;
    }

    public Color getBackgroundColor() {
        return BACKGROUND_COLOR;
    }

    public Color getTextColor() {
        return TEXT_COLOR;
    }

    public double getTitleFontSize() {
        return TITLE_FONT_SIZE;
    }

    public double getBodyFontSize() {
        return BODY_FONT_SIZE;
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