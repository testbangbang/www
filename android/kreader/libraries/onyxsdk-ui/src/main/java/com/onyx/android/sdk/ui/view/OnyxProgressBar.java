package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.onyx.android.sdk.ui.R;

public class OnyxProgressBar extends ImageView {
    private PaintFlagsDrawFilter mPaintFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private boolean mIsInit = true;
    private ProgressBarTask mTask = new ProgressBarTask();
    private AnimationDrawable mAnimationDrawable = null;

    public OnyxProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OnyxProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OnyxProgressBar(Context context) {
        super(context);
        init();
    }

    private void init() {
        setImageResource(R.anim.reader_loading_animation);
        mAnimationDrawable = (AnimationDrawable) this.getDrawable();
    }

    public void setAnimResource(int id) {
        setImageResource(id);
        mAnimationDrawable = (AnimationDrawable) this.getDrawable();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mAnimationDrawable != null) {
            mAnimationDrawable.start();
        }
        canvas.setDrawFilter(mPaintFilter);
        super.onDraw(canvas);

        if (mIsInit) {
            mTask.execute();
            mIsInit = false;
        }
    }

    class ProgressBarTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            publishProgress(params);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            OnyxProgressBar.this.invalidate();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mAnimationDrawable != null) {
//        		mAnimationDrawable.stop();
            }
        }
    }
}
