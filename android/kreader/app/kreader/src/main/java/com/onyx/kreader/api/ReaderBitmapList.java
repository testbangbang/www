package com.onyx.kreader.api;

import android.graphics.Bitmap;
import com.onyx.kreader.cache.BitmapHolder;

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
    transient private List<BitmapHolder> bitmapList;
    static transient private boolean keepMemoryCache = false;

    public ReaderBitmapList() {
        super();
        current = 0;
        count = 0;
    }

    public int getCurrent() {
        return current;
    }

    public BitmapHolder getCurrentBitmap() {
        if (bitmapList == null) {
            return null;
        }
        if (current < bitmapList.size()) {
            return bitmapList.get(current).attach();
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
            // TODO why not recycle bitmaps in the list?
            bitmapList.clear();
        }
    }

    public void addBitmap(BitmapHolder bitmap) {
        if (bitmapList == null) {
            bitmapList = new ArrayList<BitmapHolder>();
        }
        count++;
        if (keepMemoryCache) {
            bitmapList.add(bitmap.attach());
        }
    }

    public void clearAllBitmap() {
        if (bitmapList == null) {
            return;
        }
        for(BitmapHolder bitmap : bitmapList) {
            bitmap.detach();
        }
        bitmapList.clear();
    }
}
