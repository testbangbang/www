package com.onyx.jdread.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.jdread.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 18-1-17.
 */

public class FlowLayout extends ViewGroup {

    private List<Line> mLines = new ArrayList<>();

    private float vertical_space;
    private float horizontal_space;

    private Line mCurrentLine;

    private int mMaxWidth;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        horizontal_space = array.getDimension(R.styleable.FlowLayout_width_space,0);
        vertical_space =  array.getDimension(R.styleable.FlowLayout_height_space,0);
        array.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mLines.clear();
        mCurrentLine = null;

        int width = MeasureSpec.getSize(widthMeasureSpec);
        mMaxWidth = width - getPaddingLeft() - getPaddingRight();

        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);

            if (mCurrentLine == null) {
                mCurrentLine = new Line(mMaxWidth, horizontal_space);

                mCurrentLine.addView(childView);
                mLines.add(mCurrentLine);

            } else {
                if (mCurrentLine.canAddView(childView)) {
                    mCurrentLine.addView(childView);
                } else {
                    mCurrentLine = new Line(mMaxWidth, horizontal_space);
                    mCurrentLine.addView(childView);
                    mLines.add(mCurrentLine);
                }
            }
        }

        int height = getPaddingTop() + getPaddingBottom();
        for (int i = 0; i < mLines.size(); i++) {
            height += mLines.get(i).height;
        }
        height += (mLines.size() - 1) * vertical_space;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        l = getPaddingLeft();
        t = getPaddingTop();
        for (int i = 0; i < mLines.size(); i++) {
            Line line = mLines.get(i);
            line.layout(t, l);

            t += line.height;
            if (i != mLines.size() - 1) {
                t += vertical_space;
            }
        }
    }

    public class Line {
        private List<View> views = new ArrayList<>();
        private int maxWidth;
        private int usedWidth;
        private int height;
        private float space;

        public Line(int maxWidth, float horizontalSpace) {
            this.maxWidth = maxWidth;
            this.space = horizontalSpace;
        }

        public void addView(View view) {
            int childWidth = view.getMeasuredWidth();
            int childHeight = view.getMeasuredHeight();

            if (views.size() == 0) {
                if (childWidth > maxWidth) {
                    usedWidth = maxWidth;
                    height = childHeight;
                } else {
                    usedWidth = childWidth;
                    height = childHeight;
                }
            } else {
                usedWidth += childWidth + space;
                height = childHeight > height ? childHeight : height;
            }

            views.add(view);
        }

        public boolean canAddView(View view) {
            if (views.size() == 0) {
                return true;
            }

            if (view.getMeasuredWidth() > (maxWidth - usedWidth - space)) {
                return false;
            }

            return true;
        }

        public void layout(int t, int l) {
            for (View view : views) {
                int measuredWidth = view.getMeasuredWidth();
                int measuredHeight = view.getMeasuredHeight();
                view.measure(MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY));
                measuredWidth = view.getMeasuredWidth();

                int top = t;
                int left = l;
                int right = measuredWidth + left;
                int bottom = measuredHeight + top;
                view.layout(left, top, right, bottom);

                l += measuredWidth + space;
            }
        }
    }
}
