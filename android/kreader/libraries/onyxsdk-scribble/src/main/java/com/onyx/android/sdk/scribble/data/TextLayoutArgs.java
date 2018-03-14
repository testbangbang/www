package com.onyx.android.sdk.scribble.data;

import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by lxm on 2018/1/12.
 */

public class TextLayoutArgs {

    public static final int DRAW_DEFAULT_TEXT_SIZE = 30;
    public static final int DRAW_DEFAULT_TEXT_PADDING = 10;
    public static final float DRAW_DEFAULT_TEXT_SPACING_ADD = 15f;
    public static final float DRAW_DEFAULT_TEXT_SPACING_MULT = 1f;

    public static final float DRAW_FILL_TEXT_SPACING_ADD = 15f;

    public static final int BACKGROUND_LINE_SPACING = 76;

    public String text;
    public int textSize = DRAW_DEFAULT_TEXT_SIZE;
    public int textPadding = DRAW_DEFAULT_TEXT_PADDING;
    public float textSpacingAdd = DRAW_DEFAULT_TEXT_SPACING_ADD;
    public float textSpacingMult = DRAW_DEFAULT_TEXT_SPACING_MULT;

    public TextLayoutArgs(String text) {
        this.text = text;
    }

    public TextLayoutArgs(String text, float textSpacingAdd) {
        this.text = text;
        this.textSpacingAdd = textSpacingAdd;
    }

    public boolean isNull() {
        return text == null;
    }

    public static TextLayoutArgs create(String text, float textSpacingAdd) {
        return new TextLayoutArgs(text, textSpacingAdd);
    }
}
