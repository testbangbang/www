package com.onyx.kreader.host.options;


import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.LocaleUtils;

/**
 * Created by zhuzeng on 11/6/15.
 */
public class ReaderStyle {

    public static enum Alignment {
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

    static public class SubStyle {
        public Alignment alignment = Alignment.ALIGNMENT_JUSTIFY;
        public int lineSpacing = DEFAULT_LINE_SPACING;
        public float topMargin = 1;
        public float leftMargin = 1;
        public float rightMargin = 1;
        public float bottomMargin = 1;
        public float topPadding = 1;
        public float leftPadding = 1;
        public float rightPadding = 1;
        public float bottomPadding = 1;
        public float indent = defaultParagraphIndent();

        public String getIndent() {
            return String.valueOf(indent);
        }

        public String getAlignment() {
            return getAlignmentString(alignment);
        }

        public String getTopPadding() {
            return String.valueOf(topPadding);
        }

        public String getLeftPadding() {
            return String.valueOf(leftPadding);
        }

        public String getRightPadding() {
            return String.valueOf(rightPadding);
        }

        public String getBottomPadding() {
            return String.valueOf(bottomPadding);
        }

        public String getTopMargin() {
            return String.valueOf(topMargin);
        }

        public String getLeftMargin() {
            return String.valueOf(leftMargin);
        }

        public String getRightMargin() {
            return String.valueOf(rightMargin);
        }

        public String getBottomMargin() {
            return String.valueOf(bottomMargin);
        }

        public String getLineSpacing() {
            return String.valueOf(lineSpacing);
        }

        public static SubStyle bodyStyle() {
            SubStyle subStyle = new SubStyle();
            subStyle.topMargin = 0f;
            subStyle.leftMargin = 0;
            subStyle.rightMargin = 0;
            subStyle.bottomMargin = 0;
            subStyle.topPadding = 0f;
            subStyle.leftPadding = 0;
            subStyle.rightPadding = 0;
            subStyle.bottomPadding = 0;
            return subStyle;
        }

        public static SubStyle paragraphStyle() {
            SubStyle subStyle = new SubStyle();
            subStyle.topMargin = 0.8f;
            subStyle.leftMargin = 0.5f;
            subStyle.rightMargin = 0.5f;
            subStyle.bottomMargin = 0f;
            subStyle.topPadding = 0f;
            subStyle.leftPadding = 0;
            subStyle.rightPadding = 0;
            subStyle.bottomPadding = 0;
            return subStyle;
        }

    }

    private static final String DEFAULT_REGULAR_FONT = "OnyxCustomFont-Regular.ttf";
    private static final String DEFAULT_BOLD_FONT = "OnyxCustomFont-Bold.ttf";
    private static String currentGlobalFont;

    private String fontFace = null;
    private SubStyle bodyStyle = SubStyle.bodyStyle();
    private SubStyle paragraphStyle = SubStyle.paragraphStyle();

    private static String template;
    private static String dummyStyle;
    private String style;

    public static final String FONT_TAG = "font-holder";
    public static final String BODY_PADDING_LEFT_TAG = "body-padding-left-holder";
    public static final String BODY_PADDING_TOP_TAG = "body-padding-top-holder";
    public static final String BODY_PADDING_RIGHT_TAG = "body-padding-right-holder";
    public static final String BODY_PADDING_BOTTOM_TAG = "body-padding-bottom-holder";
    public static final String BODY_MARGIN_TOP_TAG = "body-margin-top-holder";
    public static final String BODY_MARGIN_LEFT_TAG = "body-margin-left-holder";
    public static final String BODY_MARGIN_RIGHT_TAG = "body-margin-right-holder";
    public static final String BODY_MARGIN_BOTTOM_TAG = "body-margin-bottom-holder";
    public static final String BODY_LINE_HEIGHT_TAG = "body-line-height-holder";
    public static final String BODY_TEXT_ALIGN_TAG = "body-text-align-holder";


    public static final String PARA_TEXT_ALIGN_TAG = "para-text-align-holder";
    public static final String PARA_TEXT_INDENT_TAG = "para-text-indent-holder";
    public static final String PARA_LINE_HEIGHT_TAG = "para-line-height-holder";
    public static final String PARA_PADDING_LEFT_TAG = "para-padding-left-holder";
    public static final String PARA_PADDING_TOP_TAG = "para-padding-top-holder";
    public static final String PARA_PADDING_RIGHT_TAG = "para-padding-right-holder";
    public static final String PARA_PADDING_BOTTOM_TAG = "para-padding-bottom-holder";
    public static final String PARA_MARGIN_TOP_TAG = "para-margin-top-holder";
    public static final String PARA_MARGIN_BOTTOM_TAG = "para-margin-bottom-holder";
    public static final String PARA_MARGIN_LEFT_TAG = "para-margin-left-holder";
    public static final String PARA_MARGIN_RIGHT_TAG = "para-margin-right-holder";

    public static void setTemplate(final String t, final String dummy) {
        template = t;
        dummyStyle = dummy;
        if (StringUtils.isNullOrEmpty(dummyStyle)) {
            dummyStyle = "";
        }
    }

    public ReaderStyle() {
        initCheck();
        updateStlye();
    }

    private void initCheck() {
        if (LocaleUtils.isChinese()) {
            fontFace = getDefaultRegularFontByLocale();
        }
    }

    public void setBodyAlignment(final Alignment a) {
        bodyStyle.alignment = a;
        updateStlye();
    }

    public Alignment getBodyAlignment() {
        return bodyStyle.alignment;
    }

    public void setBodyLineSpacing(final int spacing) {
        bodyStyle.lineSpacing = spacing;
        updateStlye();
    }

    public int getBodyLineSpacing() {
        return bodyStyle.lineSpacing;
    }

    public String getFontFace() {
        if (StringUtils.isNullOrEmpty(fontFace)) {
            return "";
        }
        return fontFace;
    }

    private void verifyFontFace() {
        if (fontFace == null) {
            fontFace = getDefaultRegularFontByLocale();
        }
    }

    public void setFontFace(final String face) {
        fontFace = face;
        verifyFontFace();
        currentGlobalFont = fontFace;
        updateStlye();
    }

    public void reset() {
        resetFontFace();
    }

    public void resetFontFace() {
        fontFace = "";
        verifyFontFace();
        currentGlobalFont = "";
    }

    public static String getCurrentGlobalFont() {
        if (StringUtils.isNotBlank(currentGlobalFont)) {
            return currentGlobalFont;
        }
        return "";
    }

    public static String getDefaultRegularFontByLocale() {
        if (LocaleUtils.isChinese()) {
            return DEFAULT_REGULAR_FONT;
        }
        return null;
    }

    public static String getDefaultBoldFontByLocale() {
        if (LocaleUtils.isChinese()) {
            return DEFAULT_BOLD_FONT;
        }
        return null;
    }

    public String getStyle() {
        return style;
    }

    public void setParagraphLineSpacing(final int spacing) {
        paragraphStyle.lineSpacing = spacing;
        updateStlye();
    }

    public void increaseParagraphLineSpacing() {
        if (paragraphStyle.lineSpacing + LINE_SPACEING_STEP > MAX_LINE_SPACING) {
            return;
        }
        paragraphStyle.lineSpacing += LINE_SPACEING_STEP;
        updateStlye();
    }

    public void decreaseParagraphLineSpacing() {
        if (paragraphStyle.lineSpacing - LINE_SPACEING_STEP < MIN_LINE_SPACING) {
            return;
        }
        paragraphStyle.lineSpacing -= LINE_SPACEING_STEP;
        updateStlye();
    }

    public float setParagraphIndent(final float indent) {
        paragraphStyle.indent = indent;
        updateStlye();
        return paragraphStyle.indent;
    }

    public float getParagraphIndent() {
        return paragraphStyle.indent;
    }

    public int getParagraphLineSpacing() {
        return paragraphStyle.lineSpacing;
    }

    private String updateStlye() {
        if (StringUtils.isNullOrEmpty(template)) {
            style = dummyStyle;
            return style;
        }

        style = new String(template);
        style = style.replaceAll(FONT_TAG, getFontFace());
        style = style.replaceAll(BODY_PADDING_TOP_TAG, bodyStyle.getTopPadding());
        style = style.replaceAll(BODY_PADDING_LEFT_TAG, bodyStyle.getLeftPadding());
        style = style.replaceAll(BODY_PADDING_RIGHT_TAG, bodyStyle.getRightPadding());
        style = style.replaceAll(BODY_PADDING_BOTTOM_TAG, bodyStyle.getBottomPadding());
        style = style.replaceAll(BODY_MARGIN_TOP_TAG, bodyStyle.getTopMargin());
        style = style.replaceAll(BODY_MARGIN_BOTTOM_TAG, bodyStyle.getBottomMargin());
        style = style.replaceAll(BODY_MARGIN_RIGHT_TAG, bodyStyle.getRightMargin());
        style = style.replaceAll(BODY_MARGIN_LEFT_TAG, bodyStyle.getLeftMargin());
        style = style.replaceAll(BODY_LINE_HEIGHT_TAG, bodyStyle.getLineSpacing());
        style = style.replaceAll(BODY_TEXT_ALIGN_TAG, bodyStyle.getAlignment());


        style = style.replaceAll(PARA_TEXT_ALIGN_TAG, paragraphStyle.getAlignment());
        style = style.replaceAll(PARA_TEXT_INDENT_TAG, paragraphStyle.getIndent());
        style = style.replaceAll(PARA_LINE_HEIGHT_TAG, paragraphStyle.getLineSpacing());
        style = style.replaceAll(PARA_PADDING_TOP_TAG, paragraphStyle.getTopPadding());
        style = style.replaceAll(PARA_PADDING_BOTTOM_TAG, paragraphStyle.getBottomPadding());
        style = style.replaceAll(PARA_PADDING_LEFT_TAG, paragraphStyle.getLeftPadding());
        style = style.replaceAll(PARA_PADDING_RIGHT_TAG, paragraphStyle.getRightPadding());
        style = style.replaceAll(PARA_MARGIN_TOP_TAG, paragraphStyle.getTopMargin());
        style = style.replaceAll(PARA_MARGIN_BOTTOM_TAG, paragraphStyle.getBottomMargin());
        style = style.replaceAll(PARA_MARGIN_LEFT_TAG, paragraphStyle.getLeftMargin());
        style = style.replaceAll(PARA_MARGIN_RIGHT_TAG, paragraphStyle.getRightMargin());
        return style;
    }


    static private String getAlignmentString(final Alignment alignment) {
        if (alignment == Alignment.ALIGNMENT_JUSTIFY) {
            return alignmentJustify();
        } else if (alignment == Alignment.ALIGNMENT_LEFT) {
            return alignmentLeft();
        } else if (alignment == Alignment.ALIGNMENT_RIGHT) {
            return alignmentRight();
        } else {
            return alignmentDefault();
        }
    }

    public static final String alignmentDefault() {
        return " justify ";
    }

    public static final String alignmentJustify() {
        return "justify";
    }

    public static final String alignmentLeft() {
        return "left";
    }

    public static final String alignmentRight() {
        return "right";
    }

    public static float defaultParagraphIndent() {
        return 2;
    }


}
