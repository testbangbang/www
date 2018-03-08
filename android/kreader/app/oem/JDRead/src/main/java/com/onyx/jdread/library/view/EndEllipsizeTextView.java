package com.onyx.jdread.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Created by hehai on 18-3-6.
 */

public class EndEllipsizeTextView extends AppCompatTextView {
    public EndEllipsizeTextView(Context context) {
        super(context);
    }

    public EndEllipsizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Layout layout = getLayout();
        if (layout == null) {
            return;
        }
        Paint.FontMetrics fm = getPaint().getFontMetrics();
        int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
        textHeight = (int) (textHeight * layout.getSpacingMultiplier() + layout.getSpacingAdd());
        measureText(getMeasuredWidth(), getText(), textHeight, canvas);
    }

    /**
     * @param width
     * @param text
     * @param textHeight
     */
    public void measureText(int width, CharSequence text, int textHeight, Canvas canvas) {
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
        int textLength = text.length();
        float textSize = paint.getTextSize();
        int lineCount = getLineCount();
        int lineStartIndex = 0;
        int lineEndIndex = 0;
        for (int i = 0; i < lineCount; i++) {
            if (i == lineCount - 1 && getEllipsize() == TextUtils.TruncateAt.END) {
                CharSequence lastLine = text.subSequence(lineStartIndex, textLength);
                lastLine = getEllipsizeLine(width, lastLine, paint);
                canvas.drawText(lastLine, 0, lastLine.length(), 0, textSize + textHeight * i, paint);
            } else {
                int length = getNoEllipsizeLineEndIndex(width, text.subSequence(lineStartIndex, textLength), paint);
                lineEndIndex += length;
                canvas.drawText(text, lineStartIndex, lineEndIndex, 0, textSize + textHeight * i, paint);
                lineStartIndex += length;
                if (lineEndIndex == textLength) {
                    return;
                }
            }
        }
    }

    public CharSequence getEllipsizeLine(int width, CharSequence lineText, TextPaint paint) {
        int length = lineText.length();
        String ellipsis = "...";
        float ellipsisWidth = StaticLayout.getDesiredWidth(ellipsis, paint);
        for (int i = 0; i < length; i++) {
            CharSequence cha = lineText.subSequence(0, i);
            float textWidth = StaticLayout.getDesiredWidth(cha, paint);
            if (textWidth + ellipsisWidth > width) {
                lineText = lineText.subSequence(0, i - 1) + ellipsis;
                return lineText;
            }
        }
        return lineText;
    }

    public int getNoEllipsizeLineEndIndex(int width, CharSequence text, TextPaint paint) {
        CharSequence lineOne = null;
        int length = text.length();
        for (int i = 0; i <= length; i++) {
            lineOne = text.subSequence(0, i);
            float textWidth = StaticLayout.getDesiredWidth(lineOne, paint);
            if (textWidth >= width) {
                CharSequence lastWorld = text.subSequence(i - 1, i);
                float lastWidth = StaticLayout.getDesiredWidth(lastWorld, paint);
                if (textWidth - width < lastWidth) {
                    return i - 1;
                }
            }
        }
        return length;
    }
}