package com.onyx.kreader.ui.highlight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelXorXfermode;
import android.graphics.RectF;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 6/2/14
 * Time: 9:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class HighlightCursor {
    public enum Type {
        BEGIN_CURSOR,
        END_CURSOR,
    }

    public static final int BEGIN_CURSOR_INDEX = 0;
    public static final int END_CURSOR_INDEX   = 1;
    public static final int HIT_TEST_SCALE_VALUE = 1;

    private Bitmap startCursorBitmap;
    private Bitmap endCursorBitmap;
    private RectF originRect = new RectF();
    private RectF hitTestRect = new RectF();
    private RectF displayRect = new RectF();
    private RectF hotPointRect = new RectF();
    private Type cursorType = Type.BEGIN_CURSOR;
    private int hotPointOffset = 0;
    private float fontHeight = 0;
    static private boolean debugHitTest = false;
    static private boolean debugHotRect = false;
    private boolean enable = false;

    public HighlightCursor(final Context context, int startResId, int endResId, Type t) {
        super();
        startCursorBitmap =  BitmapFactory.decodeResource(context.getResources(), startResId);
        endCursorBitmap =  BitmapFactory.decodeResource(context.getResources(), endResId);
        cursorType = t;
    }

    public boolean hitTest(final float x, final float y) {
        return hitTestRect.contains(x, y);
    }

    public void setCursorType(Type t) {
        cursorType = t;
    }

    public void setFontHeight(float fontHeight) {
        if (fontHeight > this.fontHeight){
            this.fontHeight = fontHeight;
        }
    }

    public Type getCursorType() {
        return cursorType;
    }

    public void setOriginPosition(final float x, final float y) {
        if (cursorType == Type.BEGIN_CURSOR) {
            float width = startCursorBitmap.getWidth();
            float height = startCursorBitmap.getHeight();
            originRect.set(x - width, y, x, y + height );
            hitTestRect.set(originRect.left - width * HIT_TEST_SCALE_VALUE, originRect.top, originRect.right + width * HIT_TEST_SCALE_VALUE, originRect.bottom + height * HIT_TEST_SCALE_VALUE);
        } else {
            float width = endCursorBitmap.getWidth();
            float height = endCursorBitmap.getHeight();
            originRect.set(x, y, x + width, y + height);
            hitTestRect.set(originRect.left - width * HIT_TEST_SCALE_VALUE, originRect.top, originRect.right + width * HIT_TEST_SCALE_VALUE, originRect.bottom + height * HIT_TEST_SCALE_VALUE);
        }
    }

    public void updateDisplayPosition() {
        displayRect.set(originRect);
    }

    public void setHotPointOffset(int offset) {
        hotPointOffset = offset;
    }

    public boolean tracking(final float sx, final float sy, final float ex, final float ey) {
        if (!hitTest(sx, sy)) {
            return false;
        }
        if (cursorType == Type.BEGIN_CURSOR) {
            float left = ex + startCursorBitmap.getWidth() / 4;
            float top = ey - startCursorBitmap.getWidth() / 4;
            displayRect.set(left, top, left + startCursorBitmap.getWidth() - 1, top  + startCursorBitmap.getHeight()  - 1);
        } else {
            displayRect.set(ex, ey, ex + endCursorBitmap.getWidth() - 1, ey + endCursorBitmap.getHeight() - 1);
        }
        return true;
    }

    // change from display rect to selection rectangle
    public final RectF getHotPoint() {
        hotPointRect.set(displayRect);
        if (cursorType == Type.BEGIN_CURSOR) {
            hotPointRect.offset(0, hotPointOffset * startCursorBitmap.getHeight());
        } else {
            hotPointRect.offset(0, - hotPointOffset * endCursorBitmap.getHeight());
        }
        return hotPointRect;
    }

    public final RectF getDisplayRect() {
        return displayRect;
    }

    public final RectF getOriginRect() {
        return originRect;
    }

    public void draw(Canvas canvas, Paint paint, PixelXorXfermode xor) {
        if (!enable){
            return;
        }
        if (!displayRect.isEmpty()) {
            if (cursorType == Type.BEGIN_CURSOR) {
                canvas.drawBitmap(startCursorBitmap, displayRect.left, displayRect.top, null);
            } else {
                canvas.drawBitmap(endCursorBitmap, displayRect.left, displayRect.top, null);
            }
        }
        if (debugHitTest && !hitTestRect.isEmpty())  {
            canvas.drawRect(hitTestRect, paint);
        }
        if (debugHotRect && hotPointRect != null) {
            canvas.drawRect(hotPointRect, paint);
        }
    }

    public void clear() {
        originRect.setEmpty();
        hitTestRect.setEmpty();
        displayRect.setEmpty();
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

//    public RectF getCursorTopPoint(){
//        if (cursorType == Type.BEGIN_CURSOR) {
//            return new RectF(originRect.right, originRect.top);
//        }else {
//
//        }
//    }
}
