package com.onyx.android.sun.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by hehai on 2016/11/23.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private Drawable mDivider;

    private int mOrientation;
    private Paint paint;
    private int size = 0;
    private int color = 0xFF000000;
    private boolean drawLine;
    private final Paint weakPaint;
    private boolean drawWeakLine;
    private int space = 0;
    private boolean isVerticalSpace;

    public DividerItemDecoration(Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
        paint = new Paint();
        weakPaint = new Paint();
        paint.setColor(color);
        weakPaint.setStyle(Paint.Style.STROKE);
        weakPaint.setColor(color);
        weakPaint.setStrokeWidth(1);
        weakPaint.setPathEffect(new DashPathEffect(new float[]{3, 2}, 0));
    }

    public DividerItemDecoration setLineType() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{3, 2}, 0));
        return this;
    }

    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
        weakPaint.setColor(color);
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }

    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView v = new RecyclerView(parent.getContext());
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
//            mDivider.setBounds(left, top, right, bottom);
//            mDivider.draw(c);
            if (drawLine) {
                c.drawRect(left, top, right, bottom, paint);
            }
            if (drawWeakLine) {
                c.drawRect(left, top, right, bottom, weakPaint);
            }
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
//            mDivider.setBounds(left, top, right, bottom);
//            mDivider.draw(c);
            if (drawLine) {
                c.drawRect(left, top, right, bottom, paint);
            }
            if (drawWeakLine) {
                c.drawRect(left, top, right, bottom, weakPaint);
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public void setVerticalSpace(boolean b) {
        this.isVerticalSpace = b;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (!isVerticalSpace) {
            outRect.right = space;
            outRect.left = space;
        } else {
            outRect.bottom = space;
            outRect.top = space;
        }
    }

    public void setDrawLine(boolean drawLine) {
        this.drawLine = drawLine;
    }

    public void setDrawWeakLine(boolean drawWeakLine) {
        this.drawWeakLine = drawWeakLine;
    }
}
