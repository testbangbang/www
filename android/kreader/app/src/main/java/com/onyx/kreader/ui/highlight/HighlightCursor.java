package com.onyx.kreader.ui.highlight;

import android.content.Context;
import android.graphics.*;

import java.util.List;

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

    private Bitmap startCursorBitmap;
    private Bitmap endCursorBitmap;
    private RectF originRect = new RectF();
    private RectF hitTestRect = new RectF();
    private RectF displayRect = new RectF();
    private RectF hotPointRect = new RectF();
    private Type cursorType = Type.BEGIN_CURSOR;
    private int hotPointOffset = 0;
    static private boolean debugHitTest = false;
    static private boolean debugHotRect = false;


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

    public Type getCursorType() {
        return cursorType;
    }

    public void setOriginPosition(final float x, final float y) {
        float left = x - startCursorBitmap.getWidth() / 2;
        if (cursorType == Type.BEGIN_CURSOR) {
            originRect.set(left, y - startCursorBitmap.getHeight(), left + startCursorBitmap.getWidth() - 1, y);
            float width = startCursorBitmap.getWidth();
            float height = startCursorBitmap.getHeight();
            hitTestRect.set(originRect.left - width*3, originRect.top - height*3, originRect.right + width*3, originRect.bottom + height);
        } else {
            originRect.set(left, y, left + endCursorBitmap.getWidth() - 1, y + endCursorBitmap.getHeight() - 1);
            float width = endCursorBitmap.getWidth();
            float height = endCursorBitmap.getHeight();
            hitTestRect.set(originRect.left - width*3, originRect.top, originRect.right + width*2, originRect.bottom + height);
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

}
