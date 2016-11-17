package com.onyx.kreader.host.options;

/**
 * Created by zhuzeng on 11/6/15.
 */
public class ReaderStyle {

    public enum Alignment {
        ALIGNMENT_NONE,
        ALIGNMENT_LEFT,
        ALIGNMENT_RIGHT,
        ALIGNMENT_JUSTIFY
    }

    static public int DEFAULT_LINE_SPACING = 150;
    static public int LINE_SPACEING_STEP = 10;
    static public int LARGE_LINE_SPACING = 180;
    static public int NORMAL_LINE_SPACING = 150;
    static public int SMALL_LINE_SPACING = 120;
    static public int MIN_LINE_SPACING = 110;
    static public int MAX_LINE_SPACING = 200;

    private String fontFace = null;
    private float fontSize = 12.0f;
    private Alignment alignment = Alignment.ALIGNMENT_JUSTIFY;
    private float indent = 2.0f;
    private int lineSpacing = 150;
    private float topMargin = 1;
    private float leftMargin = 1;
    private float rightMargin = 1;
    private float bottomMargin = 1;

    public String getFontFace() {
        return fontFace;
    }

    public void setFontFace(String fontFace) {
        this.fontFace = fontFace;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public float getIndent() {
        return indent;
    }

    public void setIndent(float indent) {
        this.indent = indent;
    }

    public int getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public float getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(float topMargin) {
        this.topMargin = topMargin;
    }

    public float getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(float leftMargin) {
        this.leftMargin = leftMargin;
    }

    public float getRightMargin() {
        return rightMargin;
    }

    public void setRightMargin(float rightMargin) {
        this.rightMargin = rightMargin;
    }

    public float getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(float bottomMargin) {
        this.bottomMargin = bottomMargin;
    }


}
