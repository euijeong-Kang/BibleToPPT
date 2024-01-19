package com.ej.bibletoppt;

public enum SlideSizeType {
    WIDESCREEN_16_9(16, 9),
    STANDARD_4_3(4, 3),
    SCREEN_SLIDESHOW_16_10(16, 10),
    A4_SIZE(21, 29.7);

    private final double width;
    private final double height;

    SlideSizeType(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public static SlideSizeType fromString(String sizeString) {
        switch (sizeString) {
            case "16:9":
                return WIDESCREEN_16_9;
            case "4:3":
                return STANDARD_4_3;
            case "16:10":
                return SCREEN_SLIDESHOW_16_10;
            case "A4":
                return A4_SIZE;
            default:
                // 기본값으로 16:9를 반환
                return WIDESCREEN_16_9;
        }
    }
}