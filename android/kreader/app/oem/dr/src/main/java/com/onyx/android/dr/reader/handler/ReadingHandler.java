package com.onyx.android.dr.reader.handler;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.onyx.android.dr.reader.common.ReadPhysicalKeyConfig;
import com.onyx.android.dr.reader.data.PageTurningDetector;
import com.onyx.android.dr.reader.data.PageTurningDirection;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.data.ControlType;
import com.onyx.android.sdk.data.CustomBindKeyBean;

/**
 * Created by huxiaomao on 17/5/8.
 */

public class ReadingHandler extends BaseHandler {
    private static final String TAG = ReadingHandler.class.getSimpleName();

    public ReadingHandler(ReaderPresenter readerPresenter) {
        super(readerPresenter);
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
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onPageUp() {
        int value = ReadPhysicalKeyConfig.getReadSettingFontFace(getReaderPresenter().getReaderView().getViewContext());
        switch (value) {
            case ReadPhysicalKeyConfig.READ_Physical_Key_ONE:
                getReaderPresenter().prevScreen();
                break;
            case ReadPhysicalKeyConfig.READ_Physical_Key_TWO:
                getReaderPresenter().nextScreen();
                break;
            case ReadPhysicalKeyConfig.READ_Physical_Key_THREE:
                getReaderPresenter().nextScreen();
                break;
            default:
                getReaderPresenter().prevScreen();
        }
    }

    private void onPageDown() {
        int value = ReadPhysicalKeyConfig.getReadSettingFontFace(getReaderPresenter().getReaderView().getViewContext());
        switch (value) {
            case ReadPhysicalKeyConfig.READ_Physical_Key_ONE:
                getReaderPresenter().nextScreen();
                break;
            case ReadPhysicalKeyConfig.READ_Physical_Key_TWO:
                getReaderPresenter().nextScreen();
                break;
            case ReadPhysicalKeyConfig.READ_Physical_Key_THREE:
                getReaderPresenter().prevScreen();
                break;
            default:
                getReaderPresenter().nextScreen();
        }
    }

    @Override
    public boolean onActionUp(MotionEvent event) {
        panFinished((int) (getStartPoint().x - event.getX()), (int) (getStartPoint().y - event.getY()));
        return true;
    }

    @Override
    public boolean onActionCancel(MotionEvent event) {
        return super.onActionCancel(event);
    }

    public void panFinished(int offsetX, int offsetY) {
        if (!getReaderPresenter().getReaderViewInfo().canPan()) {
            PageTurningDirection direction = PageTurningDetector.detectHorizontalTuring(getReaderPresenter().getReaderView().getViewContext(), -offsetX);
            if (direction == PageTurningDirection.Left) {
                getReaderPresenter().prevScreen();
            } else if (direction == PageTurningDirection.Right) {
                getReaderPresenter().nextScreen();
            }
            return;
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        if (super.onSingleTapUp(event)) {
            return true;
        }
        final String touchArea = getTouchAreaCode(getReaderPresenter(), event);
        CustomBindKeyBean object = getControlBinding(ControlType.TOUCH, touchArea);
        if (object == null) {
            return false;
        }
        String action = object.getAction();
        String args = object.getArgs();

        return processSingleTapUp(getReaderPresenter(), action, args);
    }


}
