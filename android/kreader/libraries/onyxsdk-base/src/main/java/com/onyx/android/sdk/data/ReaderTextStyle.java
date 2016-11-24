package com.onyx.android.sdk.data;

/**
 * Created by zhuzeng on 11/6/15.
 */
public class ReaderTextStyle {

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

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof Percentage) {
                return this.getPercent() == ((Percentage) o).getPercent();
            }
            return false;

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

        public static DPUnit create(int value) {
            return new DPUnit(value);
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof DPUnit) {
                return this.getValue() == ((DPUnit) o).getValue();
            }
            return false;
        }
    }

    public static class SPUnit {
        private float value;

        public SPUnit(float value) {
            this.value = value;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public static SPUnit create(float value) {
            return new SPUnit(value);
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof SPUnit) {
                return this.getValue() == ((SPUnit) o).getValue();
            }
            return false;
        }
    }

    public static class PageMargin {
        private DPUnit topMargin;
        private DPUnit leftMargin;
        private DPUnit rightMargin;
        private DPUnit bottomMargin;

        public PageMargin(DPUnit leftMargin, DPUnit bottomMargin, DPUnit rightMargin, DPUnit topMargin) {
            this.bottomMargin = bottomMargin;
            this.leftMargin = leftMargin;
            this.rightMargin = rightMargin;
            this.topMargin = topMargin;
        }

        public static PageMargin copy(PageMargin pageMargin) {
            return new PageMargin(new DPUnit(pageMargin.getLeftMargin().getValue()),
                    new DPUnit(pageMargin.getBottomMargin().getValue()),
                    new DPUnit(pageMargin.getRightMargin().getValue()),
                    new DPUnit(pageMargin.getTopMargin().getValue()));
        }

        public void increasePageMargin(PageMargin pageMargin) {
            topMargin.setValue(topMargin.getValue() + pageMargin.getTopMargin().getValue());
            leftMargin.setValue(leftMargin.getValue() + pageMargin.getLeftMargin().getValue());
            rightMargin.setValue(rightMargin.getValue() + pageMargin.getRightMargin().getValue());
            bottomMargin.setValue(bottomMargin.getValue() + pageMargin.getBottomMargin().getValue());
        }

        public void decreasePageMargin(PageMargin pageMargin) {
            topMargin.setValue(topMargin.getValue() - pageMargin.getTopMargin().getValue());
            leftMargin.setValue(leftMargin.getValue() - pageMargin.getLeftMargin().getValue());
            rightMargin.setValue(rightMargin.getValue() - pageMargin.getRightMargin().getValue());
            bottomMargin.setValue(bottomMargin.getValue() - pageMargin.getBottomMargin().getValue());
        }

        public DPUnit getBottomMargin() {
            return bottomMargin;
        }

        public void setBottomMargin(DPUnit bottomMargin) {
            this.bottomMargin = bottomMargin;
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

        public DPUnit getTopMargin() {
            return topMargin;
        }

        public void setTopMargin(DPUnit topMargin) {
            this.topMargin = topMargin;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof PageMargin) {
                PageMargin value = (PageMargin) o;
                return this.getBottomMargin().equals(value.getBottomMargin()) &&
                        this.getLeftMargin().equals(value.getLeftMargin()) &&
                        this.getTopMargin().equals(value.getTopMargin()) &&
                        this.getRightMargin().equals(value.getRightMargin());
            }
            return false;
        }
    }

    static public Percentage DEFAULT_LINE_SPACING = new Percentage(150);
    static public Percentage LINE_SPACING_STEP = new Percentage(10);
    static public Percentage LARGE_LINE_SPACING = new Percentage(180);
    static public Percentage NORMAL_LINE_SPACING = new Percentage(150);
    static public Percentage SMALL_LINE_SPACING = new Percentage(120);
    static public Percentage MIN_LINE_SPACING = new Percentage(110);
    static public Percentage MAX_LINE_SPACING = new Percentage(200);

    static public PageMargin DEFAULT_PAGE_MARGIN = new PageMargin(DPUnit.create(20), DPUnit.create(20), DPUnit.create(20), DPUnit.create(20));
    static public PageMargin PAGE_MARGIN_STEP = new PageMargin(DPUnit.create(5), DPUnit.create(5), DPUnit.create(5), DPUnit.create(5));
    static public PageMargin LARGE_PAGE_MARGIN = new PageMargin(DPUnit.create(30), DPUnit.create(30), DPUnit.create(30), DPUnit.create(30));
    static public PageMargin NORMAL_PAGE_MARGIN = new PageMargin(DPUnit.create(20), DPUnit.create(20), DPUnit.create(20), DPUnit.create(20));
    static public PageMargin SMALL_PAGE_MARGIN = new PageMargin(DPUnit.create(10), DPUnit.create(10), DPUnit.create(10), DPUnit.create(10));

    static public SPUnit[] FONT_SIZE_LIST = {SPUnit.create(20.0f), SPUnit.create(24.0f), SPUnit.create(28.0f),
            SPUnit.create(32.0f), SPUnit.create(36.0f), SPUnit.create(40.0f), SPUnit.create(44.0f), SPUnit.create(48.0f)};

    private String fontFace = null;
    private SPUnit fontSize = SPUnit.create(32.0f);
    private Alignment alignment = Alignment.ALIGNMENT_JUSTIFY;
    private CharacterIndent indent = new CharacterIndent(2);
    private Percentage lineSpacing = new Percentage(150);
    private PageMargin pageMargin = DEFAULT_PAGE_MARGIN;

    public static ReaderTextStyle copy(ReaderTextStyle style) {
        ReaderTextStyle copy = new ReaderTextStyle();
        copy.fontFace = style.fontFace;
        copy.fontSize = style.fontSize;
        copy.alignment = style.alignment;
        copy.indent = style.indent;
        copy.lineSpacing = style.lineSpacing;
        copy.pageMargin = style.pageMargin;
        return copy;
    }

    public String getFontFace() {
        return fontFace;
    }

    public void setFontFace(String fontFace) {
        this.fontFace = fontFace;
    }

    public SPUnit getFontSize() {
        return fontSize;
    }

    public void setFontSize(SPUnit fontSize) {
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

    public PageMargin getPageMargin() {
        return pageMargin;
    }

    public void setPageMargin(PageMargin pageMargin) {
        this.pageMargin = pageMargin;
    }
}
