package com.onyx.kreader.ui.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.onyx.kreader.R;
import com.onyx.kreader.device.ReaderDeviceManager;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by joy on 2/13/17.
 */

public class DialogImageView extends DialogBase {

    private float scaleFactor = 1.0f;

    public DialogImageView(Context context, Bitmap bitmap) {
        super(context, android.R.style.Theme_NoTitleBar_Fullscreen);

        setContentView(R.layout.dialog_image_view);

        findViewById(R.id.image_view_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        final AtomicBoolean scaling = new AtomicBoolean(false);
        final ImageView imageView = ((ImageView)findViewById(R.id.image_view));

        final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor = Math.min(2.0f, Math.max(0.5f, scaleFactor * detector.getScaleFactor()));
                imageView.setScaleX(scaleFactor);
                imageView.setScaleY(scaleFactor);
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                scaling.set(true);
                ReaderDeviceManager.enterAnimationUpdate(true);
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                scaling.set(false);
                ReaderDeviceManager.exitAnimationUpdate(false);
                ReaderDeviceManager.applyWithGcUpdate(imageView);
            }
        });
        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                if (scaling.get()) {
                    return false;
                }
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (scaling.get()) {
                    return false;
                }
                imageView.scrollBy((int)distanceX, (int)distanceY);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleDetector.onTouchEvent(event);
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        imageView.setImageBitmap(bitmap);
    }

}
