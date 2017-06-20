package com.onyx.sdk.ebookservice.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.onyx.sdk.ebookservice.CoverWrapper;

/**
 * Created by suicheng on 2017/2/13.
 */
public class CoverImageUtils {

    public static Bitmap drawTextToCenter(CoverWrapper coverWrapper) {
        TextPaint paint = getTextPaint(coverWrapper.textSize, coverWrapper.textColor);
        Bitmap bitmap = coverWrapper.bitmap;
        Canvas canvas = new Canvas(bitmap);
        drawText(canvas, paint, coverWrapper, bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() - coverWrapper.textPaddingLeftRight);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }

    private static TextPaint getTextPaint(int textSize, int textColor) {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        return textPaint;
    }

    private static void drawText(Canvas canvas, TextPaint Paint, CoverWrapper coverWrapper, int x, int y, int width) {
        StaticLayout staticLayout = new StaticLayout(coverWrapper.text, Paint, width,
                Layout.Alignment.ALIGN_NORMAL, 1.15f, 0.0f, false);
        canvas.translate(x + coverWrapper.textPosXOffset, y - staticLayout.getHeight() / 2 + coverWrapper.textPosYOffset);
        staticLayout.draw(canvas);
    }
}
