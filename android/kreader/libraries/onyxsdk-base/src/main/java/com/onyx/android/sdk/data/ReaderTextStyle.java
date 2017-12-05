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

        public static Percentage create(int percent) {
            return new Percentage(percent);
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

        public static CharacterIndent create(int indent) {
            return new CharacterIndent(indent);
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

        public SPUnit increaseSPUnit(SPUnit step) {
            value  = Math.min(value + step.getValue(), MAX_FONT_SIZE.getValue());
            return this;
        }

        public SPUnit decreaseSPUnit(SPUnit step) {
            value  = Math.max(value - step.getValue(), MIN_FONT_SIZE.getValue());
            return this;
        }
    }

    public static class PageMargin {
        private Percentage topMargin;
        private Percentage leftMargin;
        private Percentage rightMargin;
        private Percentage bottomMargin;

        public PageMargin(Percentage leftMargin, Percentage bottomMargin, Percentage rightMargin, Percentage topMargin) {
            this.bottomMargin = bottomMargin;
            this.leftMargin = leftMargin;
            this.rightMargin = rightMargin;
            this.topMargin = topMargin;
        }

        public static PageMargin copy(PageMargin pageMargin) {
            return new PageMargin(Percentage.create(pageMargin.getLeftMargin().getPercent()),
                    Percentage.create(pageMargin.getBottomMargin().getPercent()),
                    Percentage.create(pageMargin.getRightMargin().getPercent()),
                    Percentage.create(pageMargin.getTopMargin().getPercent()));
        }

        public void increasePageMargin(PageMargin pageMargin) {
            topMargin.setPercent(topMargin.getPercent() + pageMargin.getTopMargin().getPercent());
            leftMargin.setPercent(leftMargin.getPercent() + pageMargin.getLeftMargin().getPercent());
            rightMargin.setPercent(rightMargin.getPercent() + pageMargin.getRightMargin().getPercent());
            bottomMargin.setPercent(bottomMargin.getPercent() + pageMargin.getBottomMargin().getPercent());
        }

        public void decreasePageMargin(PageMargin pageMargin) {
            topMargin.setPercent(topMargin.getPercent() - pageMargin.getTopMargin().getPercent());
            leftMargin.setPercent(leftMargin.getPercent() - pageMargin.getLeftMargin().getPercent());
            rightMargin.setPercent(rightMargin.getPercent() - pageMargin.getRightMargin().getPercent());
            bottomMargin.setPercent(bottomMargin.getPercent() - pageMargin.getBottomMargin().getPercent());
        }

        public Percentage getBottomMargin() {
            return bottomMargin;
        }

        public void setBottomMargin(Percentage bottomMargin) {
            this.bottomMargin = bottomMargin;
        }

        public Percentage getLeftMargin() {
            return leftMargin;
        }

        public void setLeftMargin(Percentage leftMargin) {
            this.leftMargin = leftMargin;
        }

        public Percentage getRightMargin() {
            return rightMargin;
        }

        public void setRightMargin(Percentage rightMargin) {
            this.rightMargin = rightMargin;
        }

        public Percentage getTopMargin() {
            return topMargin;
        }

        public void setTopMargin(Percentage topMargin) {
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

    static public Alignment DEFAULT_ALIGNMENT = Alignment.ALIGNMENT_JUSTIFY;
    static public CharacterIndent DEFAULT_CHARACTER_INDENT = new CharacterIndent(2);

    static public Percentage LINE_SPACING_STEP = new Percentage(10);
    static public Percentage SMALL_LINE_SPACING = new Percentage(70);
    static public Percentage NORMAL_LINE_SPACING = new Percentage(120);
    static public Percentage LARGE_LINE_SPACING = new Percentage(150);
    static public Percentage DEFAULT_LINE_SPACING = NORMAL_LINE_SPACING;

    static private int MARGIN_STEP = 1;
    static private int SMALL_MARGIN = 1;
    static private int NORMAL_MARGIN = 10;
    static private int LARGE_MARGIN = 20;

    static public PageMargin PAGE_MARGIN_STEP = new PageMargin(Percentage.create(MARGIN_STEP), Percentage.create(MARGIN_STEP), Percentage.create(MARGIN_STEP), Percentage.create(MARGIN_STEP));
    static public PageMargin SMALL_PAGE_MARGIN = new PageMargin(Percentage.create(SMALL_MARGIN), Percentage.create(SMALL_MARGIN), Percentage.create(SMALL_MARGIN), Percentage.create(SMALL_MARGIN));
    static public PageMargin NORMAL_PAGE_MARGIN = new PageMargin(Percentage.create(NORMAL_MARGIN), Percentage.create(NORMAL_MARGIN), Percentage.create(NORMAL_MARGIN), Percentage.create(NORMAL_MARGIN));
    static public PageMargin LARGE_PAGE_MARGIN = new PageMargin(Percentage.create(LARGE_MARGIN), Percentage.create(LARGE_MARGIN), Percentage.create(LARGE_MARGIN), Percentage.create(LARGE_MARGIN));
    static public PageMargin DEFAULT_PAGE_MARGIN = new PageMargin(Percentage.create(NORMAL_MARGIN), Percentage.create(NORMAL_MARGIN), Percentage.create(NORMAL_MARGIN), Percentage.create(NORMAL_MARGIN));;

    static public SPUnit[] DEFAULT_FONT_SIZE_LIST = {SPUnit.create(20.0f), SPUnit.create(24.0f), SPUnit.create(28.0f),
            SPUnit.create(32.0f), SPUnit.create(36.0f), SPUnit.create(40.0f), SPUnit.create(44.0f), SPUnit.create(48.0f)};
    static public SPUnit DEFAULT_FONT_SIZE = SPUnit.create(40.0f);
    static public SPUnit MAX_FONT_SIZE = SPUnit.create(96.0f);
    static public SPUnit MIN_FONT_SIZE = SPUnit.create(10.0f);
    static public SPUnit FONT_SIZE_STEP = SPUnit.create(4.0f);

    private String fontFace = null;
    private SPUnit fontSize = DEFAULT_FONT_SIZE;
    private Alignment alignment = DEFAULT_ALIGNMENT;
    private CharacterIndent indent = DEFAULT_CHARACTER_INDENT;
    private Percentage lineSpacing = DEFAULT_LINE_SPACING;
    private PageMargin pageMargin = DEFAULT_PAGE_MARGIN;

    public static float limitFontSize(float newSize) {
        final float minSize = MIN_FONT_SIZE.getValue();
        final float maxSize = MAX_FONT_SIZE.getValue();
        if (newSize < minSize) {
            newSize = minSize;
        } else if (newSize > maxSize) {
            newSize = maxSize;
        }
        return newSize;
    }

    public static void setDefaultFontSizes(Float[] fontSizes) {
        if (fontSizes == null || fontSizes.length <=0) {
            return;
        }
        DEFAULT_FONT_SIZE_LIST = new SPUnit[fontSizes.length];
        for (int i = 0; i < fontSizes.length; i++) {
            DEFAULT_FONT_SIZE_LIST[i] = SPUnit.create(fontSizes[i]);
        }
    }

    public static SPUnit getFontSizeByIndex(int index) {
        if (DEFAULT_FONT_SIZE_LIST.length > index) {
            return DEFAULT_FONT_SIZE_LIST[index];
        }
        return DEFAULT_FONT_SIZE;
    }

    public static Percentage getLineSpacingByIndex(int index) {
        switch (index) {
            case 0:
                return SMALL_LINE_SPACING;
            case 1:
                return NORMAL_LINE_SPACING;
            case 2:
                return LARGE_LINE_SPACING;
            default:
                return NORMAL_LINE_SPACING;
        }
    }

    public static PageMargin getPageMarginByIndex(int index) {
        switch (index) {
            case 0:
                return SMALL_PAGE_MARGIN;
            case 1:
                return NORMAL_PAGE_MARGIN;
            case 2:
                return LARGE_PAGE_MARGIN;
            default:
                return NORMAL_PAGE_MARGIN;
        }
    }

    private ReaderTextStyle() {
    }

    public static ReaderTextStyle defaultStyle() {
        return new ReaderTextStyle();
    }

    public static ReaderTextStyle create(String fontface, SPUnit fontSize, Percentage lineSpacing,
                                         Percentage leftMargin, Percentage topMargin,
                                         Percentage rightMargin, Percentage bottomMargin) {
        ReaderTextStyle style = new ReaderTextStyle();
        style.fontFace = fontface;
        style.fontSize = fontSize;
        style.lineSpacing = lineSpacing;
        style.pageMargin.setLeftMargin(leftMargin);
        style.pageMargin.setTopMargin(topMargin);
        style.pageMargin.setRightMargin(rightMargin);
        style.pageMargin.setBottomMargin(bottomMargin);
        return style;
    }

    public static ReaderTextStyle copy(ReaderTextStyle style) {
        if (style == null) {
            return null;
        }
        ReaderTextStyle copy = new ReaderTextStyle();
        copy.fontFace = style.fontFace;
        copy.fontSize = SPUnit.create(style.fontSize.getValue());
        copy.alignment = style.alignment;
        copy.indent = CharacterIndent.create(style.indent.getIndent());
        copy.lineSpacing = Percentage.create(style.lineSpacing.getPercent());
        copy.pageMargin = PageMargin.copy(style.pageMargin);
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

    public void increaseFontSize() {
        int i = 0;
        for (; i < DEFAULT_FONT_SIZE_LIST.length - 1; i++) {
            if (DEFAULT_FONT_SIZE_LIST[i].getValue() > fontSize.getValue()) {
                break;
            }
        }
        fontSize = DEFAULT_FONT_SIZE_LIST[i];
    }

    public void decreaseFontSize() {
        int i = DEFAULT_FONT_SIZE_LIST.length - 1;
        for (; i > 0; i--) {
            if (DEFAULT_FONT_SIZE_LIST[i].getValue() < fontSize.getValue()) {
                break;
            }
        }
        fontSize = DEFAULT_FONT_SIZE_LIST[i];
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
