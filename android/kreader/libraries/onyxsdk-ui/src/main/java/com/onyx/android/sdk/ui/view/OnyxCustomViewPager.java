package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.onyx.android.sdk.data.KeyAction;
import com.onyx.android.sdk.ui.utils.PageTurningDetector;
import com.onyx.android.sdk.ui.utils.PageTurningDirection;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by ming on 16/6/24.
 */
public class OnyxCustomViewPager extends ViewPager {

    private boolean isPagingEnabled = true;
    private boolean useGesturesPage = false;
    private boolean useKeyPage = false;
    private float lastX, lastY;
    private Map<Integer, String> keyBindingMap = new Hashtable<>();

    public OnyxCustomViewPager(Context context) {
        super(context);
        init();
    }

    public OnyxCustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        keyBindingMap.put(KeyEvent.KEYCODE_PAGE_DOWN, KeyAction.NEXT_PAGE);
        keyBindingMap.put(KeyEvent.KEYCODE_VOLUME_DOWN, KeyAction.NEXT_PAGE);
        keyBindingMap.put(KeyEvent.KEYCODE_PAGE_UP, KeyAction.PREV_PAGE);
        keyBindingMap.put(KeyEvent.KEYCODE_VOLUME_UP, KeyAction.PREV_PAGE);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (useGesturesPage) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = ev.getX();
                    lastY = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    return (detectDirection(ev) != PageTurningDirection.NONE);
                default:
                    break;
            }
        }
        return this.isPagingEnabled && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (useGesturesPage) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = ev.getX();
                    lastY = ev.getY();
                    return true;
                case MotionEvent.ACTION_UP:
                    int direction = detectDirection(ev);
                    if (direction == PageTurningDirection.NEXT) {
                        nextPage();
                        return true;
                    } else if (direction == PageTurningDirection.PREV) {
                        prevPage();
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }
        return this.isPagingEnabled && super.onTouchEvent(ev);
    }

    private void nextPage() {
        setCurrentItem(getCurrentItem() + 1, false);
    }

    private void prevPage() {
        setCurrentItem(getCurrentItem() - 1, false);
    }

    private int detectDirection(MotionEvent currentEvent) {
        return PageTurningDetector.detectBothAxisTuring(getContext(), (int) (currentEvent.getX() - lastX), (int) (currentEvent.getY() - lastY));
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }

    public void setUseGesturesPage(boolean useGesturesPage) {
        this.useGesturesPage = useGesturesPage;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return processKeyAction(event) || super.dispatchKeyEvent(event);
    }

    private boolean processKeyAction(KeyEvent event){
        if (!isUseKeyPage()) {
            return false;
        }
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return false;
        }
        final String args = keyBindingMap.get(event.getKeyCode());
        if (args == null){
            return false;
        }
        switch (args){
            case KeyAction.NEXT_PAGE:
                nextPage();
                break;
            case KeyAction.PREV_PAGE:
                prevPage();
                break;
            default:
                nextPage();
        }
        return true;
    }

    @Override
    public boolean executeKeyEvent(KeyEvent event) {
        return false;
    }

    public boolean isUseKeyPage() {
        return useKeyPage;
    }

    public void setUseKeyPage(boolean useKeyPage) {
        this.useKeyPage = useKeyPage;
    }
}
