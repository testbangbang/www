package com.onyx.jdread.reader.handler;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.onyx.android.sdk.data.TouchAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.data.RegionFunctionManager;

/**
 * Created by huxiaomao on 17/5/8.
 */

public class ReadingHandler extends BaseHandler {
    private static final String TAG = ReadingHandler.class.getSimpleName();

    public ReadingHandler(ReaderDataHolder readerDataHolder) {
        super(readerDataHolder);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_PAGE_DOWN:
                onPageDown();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_PAGE_UP:
                onPageUp();
                return true;
            case KeyEvent.KEYCODE_MENU:
                processSingleTapUp(getReaderDataHolder(), TouchAction.SHOW_MENU, null);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onPageUp() {

    }

    private void onPageDown() {

    }

    @Override
    public boolean onActionCancel(MotionEvent event) {
        return super.onActionCancel(event);
    }

}