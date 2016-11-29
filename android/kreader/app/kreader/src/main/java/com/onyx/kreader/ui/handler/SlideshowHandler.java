package com.onyx.kreader.ui.handler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.ui.actions.GotoPageAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogSlideshowStatistic;
import com.onyx.kreader.utils.DeviceUtils;

import java.util.Calendar;

/**
 * Created by joy on 7/29/16.
 */
public class SlideshowHandler extends BaseHandler {

    private static Intent intent = new Intent(SlideshowHandler.class.getCanonicalName());

    private ReaderDataHolder readerDataHolder;
    private int maxPageCount;
    private int pageCount;
    private int startBatteryPercent;
    private Calendar startTime;
    private int interval = 3 * 1000; // in milliseconds

    BaseCallback pageLimitCallback = new BaseCallback() {
        @Override
        public void done(BaseRequest request, Throwable e) {
            if (pageCount >= maxPageCount) {
                quit();
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Debug.d(getClass(), "onReceive: " + intent.getAction());
            loopNextScreen();
        }
    };
    private PendingIntent pendingIntent;

    public SlideshowHandler(HandlerManager parent) {
        super(parent);
        readerDataHolder = getParent().getReaderDataHolder();
        pendingIntent = PendingIntent.getBroadcast(readerDataHolder.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onActivate(ReaderDataHolder readerDataHolder) {
        readerDataHolder.registerReceiver(broadcastReceiver, new IntentFilter(intent.getAction()));
    }

    @Override
    public void onDeactivate(ReaderDataHolder readerDataHolder) {
        readerDataHolder.unregisterReceiver(broadcastReceiver);
        readerDataHolder.cancelAlarm(pendingIntent);
    }

    @Override
    public boolean onKeyDown(ReaderDataHolder readerDataHolder, int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public boolean onKeyUp(ReaderDataHolder readerDataHolder, int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                quit();
                return true;
            default:
                Toast.makeText(readerDataHolder.getContext(), "Press back to quit slideshow mode!", Toast.LENGTH_SHORT).show();
                return true;
        }
    }

    @Override
    public boolean onTouchEvent(ReaderDataHolder readerDataHolder, MotionEvent e) {
        Toast.makeText(readerDataHolder.getContext(), "Press back to quit slideshow mode!", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onDown(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return true;
    }

    @Override
    public boolean onActionUp(ReaderDataHolder readerDataHolder, float startX, float startY, float endX, float endY) {
        return true;
    }

    @Override
    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return true;
    }

    @Override
    public void onLongPress(ReaderDataHolder readerDataHolder, float x1, float y1, float x2, float y2) {
        return;
    }

    @Override
    public boolean onScaleBegin(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public boolean onScale(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public boolean onScaleEnd(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public boolean onFling(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    @Override
    public boolean onScroll(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public boolean onScrollAfterLongPress(ReaderDataHolder readerDataHolder, float x1, float y1, float x2, float y2) {
        return true;
    }

    public void setInterval(int interval) {
        this.interval = interval * 1000;
    }

    public void start(int maxPageCount) {
        this.maxPageCount = maxPageCount;
        pageCount = 0;
        startBatteryPercent = DeviceUtils.getBatteryPecentLevel(readerDataHolder.getContext());
        startTime = Calendar.getInstance();
        readerDataHolder.registerRepeatingAlarm(AlarmManager.RTC_WAKEUP,
                startTime.getTimeInMillis() + interval, interval, pendingIntent);
    }

    private void loopNextScreen() {
        pageCount++;
        if (readerDataHolder.getReaderViewInfo().canNextScreen) {
            nextScreen(readerDataHolder, pageLimitCallback);
        } else {
            new GotoPageAction(0).execute(readerDataHolder, pageLimitCallback);
        }
    }

    private void quit() {
        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.READING_PROVIDER);
        showStatisticDialog();
    }

    private void showStatisticDialog() {
        Calendar endTime = Calendar.getInstance();
        int endBatteryPercent = DeviceUtils.getBatteryPecentLevel(readerDataHolder.getContext());
        new DialogSlideshowStatistic(readerDataHolder.getContext(), startTime, endTime,
                pageCount, startBatteryPercent, endBatteryPercent).show();
    }
}
