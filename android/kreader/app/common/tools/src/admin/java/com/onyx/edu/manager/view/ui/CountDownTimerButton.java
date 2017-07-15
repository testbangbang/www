package com.onyx.edu.manager.view.ui;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by suicheng on 2017/7/12.
 */
public class CountDownTimerButton extends Button {

    private String originString;
    private String countDownStringFormat;
    private CountDownTimer countDownTimer;

    private int normalBackgroundRes;
    private int timerBackgroundRes;

    private CountDownStateChangeListener listener;

    public interface CountDownStateChangeListener {
        void onStartCount(long millsUtilFinished);

        void onTickCount(long millisUntilFinished);

        void onFinishCount();
    }

    public void setCountDownStateChangeListener(CountDownStateChangeListener listener) {
        this.listener = listener;
    }

    public void setNormalBackgroundRes(int res) {
        this.normalBackgroundRes = res;
    }

    public void setTimerBackgroundRes(int res) {
        this.timerBackgroundRes = res;
    }

    public void setCountDownStringFormat(String format) {
        this.countDownStringFormat = format;
    }

    public CountDownTimerButton(Context context) {
        this(context, null);
    }

    public CountDownTimerButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownTimerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        originString = String.valueOf(getText());
    }

    public void start(final long millisInFuture, final long countDownInterval) {
        stopCountDownTimer();
        changeToCountStatus(millisInFuture);
        invokeStartListener(millisInFuture);
        countDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                setText(String.format(countDownStringFormat, millisUntilFinished / 1000));
                invokeOnTickListener(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                finishCountDown();
            }
        };
        countDownTimer.start();
    }

    private void invokeStartListener(long millisInFuture) {
        if (listener != null) {
            listener.onStartCount(millisInFuture);
        }
    }

    private void invokeFinishedListener() {
        if (listener != null) {
            listener.onFinishCount();
        }
    }

    private void invokeOnTickListener(long millisInFuture) {
        if (listener != null) {
            listener.onTickCount(millisInFuture);
        }
    }

    public void stopCountDownTimer() {
        resetButtonStatus();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void finishCountDown() {
        invokeFinishedListener();
        resetButtonStatus();
    }

    private void changeToCountStatus(final long millisInFuture) {
        setClickable(false);
        setBackgroundResource(timerBackgroundRes);
        originString = String.valueOf(getText());
        setText(String.format(countDownStringFormat, (millisInFuture / 1000)));
    }

    private void resetButtonStatus() {
        setClickable(true);
        setText(originString);
        setBackgroundResource(normalBackgroundRes);
    }
}
