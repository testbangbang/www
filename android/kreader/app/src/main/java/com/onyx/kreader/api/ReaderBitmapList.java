package com.onyx.kreader.api;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 5/20/14
 * Time: 5:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderBitmapList {
    private int current;
    private int count;
    transient private List<Bitmap> bitmapList;
    static transient private boolean keepMemoryCache = false;

    public ReaderBitmapList() {
        super();
        current = 0;
        count = 0;
    }

    public int getCurrent() {
        return current;
    }

    public Bitmap getCurrentBitmap() {
        if (bitmapList == null) {
            return null;
        }
        if (current < bitmapList.size()) {
            return bitmapList.get(current);
        }
        return null;
    }

    public void setCurrent(int c) {
        current  = c;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int c) {
        count = c;
    }

    public boolean atBegin() {
        return current == 0;
    }

    public boolean atEnd() {
        return (current >= count - 1);
    }

    public void moveToEnd() {
        if (count > 0) {
            current = count - 1;
        }
    }

    public void moveToBegin() {
        current = 0;
    }

    public void moveToScreen(final int screenIndex) {
        current = screenIndex;
    }

    public boolean next() {
        if (++current >= count) {
            return false;
        }
        return true;
    }

    public boolean prev() {
        if (current <= 0) {
            return false;
        }
        --current;
        return true;
    }

    public boolean isEmpty() {
        return (count <= 0);
    }

    public void clear() {
        current = 0;
        count = 0;
        if (bitmapList != null) {
            bitmapList.clear();
        }
    }

    public void addBitmap(Bitmap bitmap) {
        if (bitmapList == null) {
            bitmapList = new ArrayList<Bitmap>();
        }
        count++;
        if (keepMemoryCache) {
            bitmapList.add(bitmap);
        }
    }

    public void clearAllBitmap() {
        if (bitmapList == null) {
            return;
        }
        for(Bitmap bitmap : bitmapList) {
            bitmap.recycle();
        }
        bitmapList.clear();
    }
}
