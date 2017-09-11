package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.onyx.android.sdk.ui.R;


/**
 * Created by solskjaer49 on 2016/11/8 11:41.
 * CustomIconView for zoom src as target drawing size,without zoom in background.
 */

public class CustomIconView extends android.support.v7.widget.AppCompatImageView {
    float drawingSize = -1;
    boolean enableZoomDrawable = true;

    public CustomIconView(Context context) {
        this(context, null);
    }

    public CustomIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CustomIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomIconView);
        drawingSize = a.getDimension(R.styleable.CustomIconView_iconDrawingSize, -1);
        enableZoomDrawable = a.getBoolean(R.styleable.CustomIconView_enableZoomDrawable, true);

        a.recycle();
        Drawable d = getDrawable();
        if (d != null && drawingSize > 0) {
            setImageDrawable(zoomDrawable(d, drawingSize));
        }
        setScaleType(ScaleType.CENTER);
        setBackgroundResource(R.drawable.imagebtn_bg);
        setFocusable(true);
        setClickable(true);
    }

    private Drawable zoomDrawable(Drawable drawable, float size) {
        if (!enableZoomDrawable) {
            return drawable;
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldBmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float sx = (size / width);
        float sy = (size / height);
        matrix.postScale(sx, sy);
        Bitmap newBmp = Bitmap.createBitmap(oldBmp, 0, 0, width, height,
                matrix, true);
        oldBmp.recycle();
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), newBmp);
        bitmapDrawable.setAntiAlias(false);
        return bitmapDrawable;
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        if (resId <= 0) {
            return;
        }
        Drawable d;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            d = getResources().getDrawable(resId);
        } else {
            d = getResources().getDrawable(resId, getContext().getTheme());
        }

        if (d != null) {
            if (drawingSize > 0) {
                super.setImageDrawable(zoomDrawable(d, drawingSize));
            } else {
                super.setImageResource(resId);
            }
        }
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable((drawingSize > 0 && drawable != null) ?
                zoomDrawable(drawable, drawingSize) : drawable);
    }
}
