package com.onyx.android.dr.reader.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.onyx.android.dr.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2017/6/23.
 */

public class BookProgressbar extends FrameLayout {
    private ImageView load1;
    private ImageView load2;
    private ImageView load3;
    private ImageView load4;
    private ImageView load5;
    private ImageView load6;
    private ImageView load7;
    private ImageView load8;
    private ImageView load9;
    private boolean isRunning;
    private final int RUN = 1000;
    private List<ImageView> loadView;
    private ImageView currentView = null;
    private int count = -1;

    public BookProgressbar(Context context) {
        this(context, null);
    }

    public BookProgressbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BookProgressbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.control);
        isRunning = typedArray.getBoolean(R.styleable.control_run, false);
        init(context);
        typedArray.recycle();
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.open_book_progress, this);
        loadView = new ArrayList<>();
        load1 = (ImageView) view.findViewById(R.id.load1);
        load2 = (ImageView) view.findViewById(R.id.load2);
        load3 = (ImageView) view.findViewById(R.id.load3);
        load4 = (ImageView) view.findViewById(R.id.load4);
        load5 = (ImageView) view.findViewById(R.id.load5);
        load6 = (ImageView) view.findViewById(R.id.load6);
        load7 = (ImageView) view.findViewById(R.id.load7);
        load8 = (ImageView) view.findViewById(R.id.load8);
        load9 = (ImageView) view.findViewById(R.id.load9);
        loadView.add(load9);
        loadView.add(load8);
        loadView.add(load7);
        loadView.add(load6);
        loadView.add(load5);
        loadView.add(load4);
        loadView.add(load3);
        loadView.add(load2);
        loadView.add(load1);

        anim();
    }

    public void setRun(boolean b) {
        isRunning = b;
    }

    private void anim() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    handler.sendEmptyMessage(RUN);
                    SystemClock.sleep(80);
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == RUN) {
                count++;
                for (int i = 0; i < loadView.size(); i++) {
                    if (count >= loadView.size()) {
                        count = -1;
                        break;
                    }

                    if (i == count) {
                        if (currentView != null) {
                            currentView.setVisibility(GONE);
                        }
                        loadView.get(i).setVisibility(VISIBLE);
                        currentView = loadView.get(i);
                    }
                }
            }
        }
    };
}
