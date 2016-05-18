package com.onyx.android.sdk.ui;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;

/**
 * Created by solskjaer49 on 14/9/24 19:12.
 */
public class OnyxBaseGridLayout extends GridLayout {

    public static abstract class CustomGridLayoutCallBack{
        public void onSizeChange(int height,int width) {
        }
    }

    public void setCallBack(CustomGridLayoutCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    CustomGridLayoutCallBack mCallBack;


    public OnyxBaseGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public OnyxBaseGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnyxBaseGridLayout(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Handler updateHandler=new Handler();
        updateHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallBack.onSizeChange(h,w);
            }
        });
    }
}
