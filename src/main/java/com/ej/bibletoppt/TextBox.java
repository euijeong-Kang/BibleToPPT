package com.ej.bibletoppt;

import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

import java.awt.geom.Rectangle2D;

public class TextBox {
    private XSLFTextShape textShape;
    private TextBoxType type;
    private double slideWidth;

    protected TextBox(XSLFTextShape textShape, TextBoxType type) {
        this.textShape = textShape;
        this.type = type;
    }

    public TextBox createContentBox(XSLFSlide slide) {
        return new TextBox(slide.createTextBox(), TextBoxType.CONTENT);
    }

    public TextBox createScriptureBox(XSLFTextShape textShape) {
        return new TextBox(textShape, TextBoxType.SCRIPTURE_VERSE);
    }

    public void setSlideWidth(double slideWidth) {
        this.slideWidth = slideWidth;
    }
}
