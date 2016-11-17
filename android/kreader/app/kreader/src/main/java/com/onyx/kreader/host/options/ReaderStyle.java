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

    public static class Percentage {
        private int percent;

        public Percentage(int percent) {
            this.percent = percent;
        }

        public int getPercent() {
            return percent;
        }

        public void setPercent(int percent) {
            this.percent = percent;
        }
    }

    public static class CharacterIndent {
        private int indent;

        public CharacterIndent(int indent) {
            this.indent = indent;
        }

        public int getIndent() {
            return indent;
        }

        public void setIndent(int indent) {
            this.indent = indent;
        }
    }

    /**
     * Density-independent Pixels
     */
    public static class DPUnit {
        private int value;

        public DPUnit(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    static public Percentage DEFAULT_LINE_SPACING = new Percentage(150);
    static public Percentage LINE_SPACEING_STEP = new Percentage(10);
    static public Percentage LARGE_LINE_SPACING = new Percentage(180);
    static public Percentage NORMAL_LINE_SPACING = new Percentage(150);
    static public Percentage SMALL_LINE_SPACING = new Percentage(120);
    static public Percentage MIN_LINE_SPACING = new Percentage(110);
    static public Percentage MAX_LINE_SPACING = new Percentage(200);

    private String fontFace = null;
    private float fontSize = 12.0f;
    private Alignment alignment = Alignment.ALIGNMENT_JUSTIFY;
    private CharacterIndent indent = new CharacterIndent(2);
    private Percentage lineSpacing = new Percentage(150);
    private DPUnit topMargin = new DPUnit(1);
    private DPUnit leftMargin = new DPUnit(1);
    private DPUnit rightMargin = new DPUnit(1);
    private DPUnit bottomMargin = new DPUnit(1);

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

    public CharacterIndent getIndent() {
        return indent;
    }

    public void setIndent(CharacterIndent indent) {
        this.indent = indent;
    }

    public Percentage getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(Percentage lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public DPUnit getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(DPUnit topMargin) {
        this.topMargin = topMargin;
    }

    public DPUnit getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(DPUnit leftMargin) {
        this.leftMargin = leftMargin;
    }

    public DPUnit getRightMargin() {
        return rightMargin;
    }

    public void setRightMargin(DPUnit rightMargin) {
        this.rightMargin = rightMargin;
    }

    public DPUnit getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(DPUnit bottomMargin) {
        this.bottomMargin = bottomMargin;
    }


}
