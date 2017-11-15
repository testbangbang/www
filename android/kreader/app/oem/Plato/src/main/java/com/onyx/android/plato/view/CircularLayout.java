/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onyx.android.plato.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jackdeng on 2017/10/27.
 */


public class CircularLayout extends ViewGroup {
    private static final int LOCATION_EAST = 1;
    private static final int LOCATION_WEST = 2;
    private static final int LOCATION_SOUTH = 3;
    private static final int LOCATION_NORTH = 4;
    private static final int LOCATION_EAST_NORTH = 5;
    private static final int LOCATION_EAST_SOUTH = 6;
    private static final int LOCATION_WEST_NORTH = 7;
    private static final int LOCATION_WEST_SOUTH = 8;

    private int spacing = 5;

    private float centerX;
    private float centerY;
    private float radius;
    private int childCount;

    public CircularLayout(Context context) {
        super(context);
        init(context, null);
    }

    public CircularLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircularLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        childCount = getChildCount();
        reset();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        reset();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void reset() {
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        centerX = viewWidth / 2;
        centerY = viewHeight / 2;
        radius = Math.min(viewWidth, viewHeight) / 2;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (childCount == 0) {
            return;
        }

        View childView;
        float nextAngle;
        float nextRadians;
        float nextPointX;
        float nextPointY;
        int childViewMeasuredWidth;
        int childViewMeasuredHeight;
        float childViewLeft;
        float childViewTop;
        float averageAngle = childCount > 0 ? 360 / childCount : 0;
        float offsetAngle = averageAngle > 0 && childCount % 2 == 0 ? averageAngle / 2 : 0;
        for (int position = 0, size = getChildCount(); position < size; position++) {
            childView = getChildAt(position);
            nextAngle = offsetAngle + (position * averageAngle);
            nextRadians = (float) Math.toRadians(nextAngle);
            nextPointX = (float) (centerX + Math.sin(nextRadians) * radius);
            nextPointY = (float) (centerY - Math.cos(nextRadians) * radius);

            childViewMeasuredWidth = childView.getMeasuredWidth();
            childViewMeasuredHeight = childView.getMeasuredHeight();
            childViewLeft = nextPointX;
            childViewTop = nextPointY;
            switch (calculateLocationByAngle(nextAngle)) {
                case LOCATION_NORTH:
                    childViewLeft -= childViewMeasuredWidth / 2;
                    childViewTop -= childViewMeasuredHeight;

                    childViewTop -= spacing;
                    break;
                case LOCATION_EAST_NORTH:
                    childViewTop -= childViewMeasuredHeight / 2;

                    childViewLeft += spacing;
                    childViewTop -= spacing;
                    break;
                case LOCATION_EAST:
                    childViewTop -= childViewMeasuredHeight / 2;

                    childViewLeft += spacing;
                    break;
                case LOCATION_EAST_SOUTH:
                    childViewLeft += spacing;
                    childViewTop += spacing;
                    break;
                case LOCATION_SOUTH:
                    childViewLeft -= childViewMeasuredWidth / 2;

                    childViewTop += spacing;
                    break;
                case LOCATION_WEST_SOUTH:
                    childViewLeft -= childViewMeasuredWidth;

                    childViewLeft -= spacing;
                    childViewTop += spacing;
                    break;
                case LOCATION_WEST:
                    childViewLeft -= childViewMeasuredWidth;
                    childViewTop -= childViewMeasuredHeight / 2;

                    childViewLeft -= spacing;
                    break;
                case LOCATION_WEST_NORTH:
                    childViewLeft -= childViewMeasuredWidth;
                    childViewTop -= childViewMeasuredHeight / 2;

                    childViewLeft -= spacing;
                    childViewTop -= spacing;
                    break;
            }

            childView.layout((int) childViewLeft, (int) childViewTop, (int) (childViewLeft + childViewMeasuredWidth), (int) (childViewTop + childViewMeasuredHeight));
        }
    }

    private int calculateLocationByAngle(float angle) {
        if ((angle >= 337.5f && angle <= 360f) || (angle >= 0f && angle <= 22.5f)) {
            return LOCATION_NORTH;
        }
        else if (angle >= 22.5f && angle <= 67.5f) {
            return LOCATION_EAST_NORTH;
        }
        else if (angle >= 67.5f && angle <= 112.5f) {
            return LOCATION_EAST;
        }
        else if (angle >= 112.5f && angle <= 157.5) {
            return LOCATION_EAST_SOUTH;
        }
        else if (angle >= 157.5 && angle <= 202.5) {
            return LOCATION_SOUTH;
        }
        else if (angle >= 202.5 && angle <= 247.5) {
            return LOCATION_WEST_SOUTH;
        }
        else if (angle >= 247.5 && angle <= 292.5) {
            return LOCATION_WEST;
        }
        else if (angle >= 292.5 && angle <= 337.5) {
            return LOCATION_WEST_NORTH;
        }
        else {
            throw new IllegalArgumentException("error angle " + angle);
        }
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
        requestLayout();
    }
}
