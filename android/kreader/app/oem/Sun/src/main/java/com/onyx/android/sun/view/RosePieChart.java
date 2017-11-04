package com.onyx.android.sun.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jackdeng on 2017/11/3.
 */

public class RosePieChart extends View {

    private List<PieEntry> pieEntries;

    private Paint paint;
    private Paint paintOuter;

    private float centerX = 200;
    private float centerY = 200;
    private float firstRadius;
    private float prevRadius;
    private float outerRadius = 150;
    private float textLineLength = 20;
    private float textSize = 15;
    private String bracketFront = SunApplication.getInstance().getString(R.string.rose_pie_chart_bracket_front);
    private String bracketBehind = SunApplication.getInstance().getString(R.string.rose_pie_chart_bracket_behind);

    private OnItemClickListener listener;
    private int width;
    private int height;
    private float outStorkeWidth = 1.5f;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RosePieChart(Context context) {
        super(context);
        init();
    }

    public RosePieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RosePieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        pieEntries = new ArrayList<>();
        paint = new Paint();
        paint.setTextSize(DensityUtils.sp2px(getContext(), textSize));
        paint.setAntiAlias(true);
        paintOuter = new Paint();
        paintOuter.setStyle(Paint.Style.STROKE);
        paintOuter.setStrokeWidth(outStorkeWidth);
        paintOuter.setColor(getResources().getColor(R.color.color_pie_chart_stroke));
        paintOuter.setAntiAlias(true);
    }

    public void setCenter(float centerX, float centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public void setOuterRadius(float outerRadius) {
        this.outerRadius = outerRadius;
    }

    public void setTextLineLength(float textLineLength) {
        this.textLineLength = textLineLength;
    }

    public void setTextSize(float textSize) {
        paint.setTextSize(DensityUtils.sp2px(getContext(), textSize));
    }

    /**
     * @param pieEntries
     */
    public void setPieEntries(List<PieEntry> pieEntries) {
        this.pieEntries = pieEntries;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        width = measuredWidth;
        height = measuredHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float total = 0;
        for (int i = 0; i < pieEntries.size(); i++) {
            total += pieEntries.get(i).getValue();
        }
        centerX = width / 2;
        centerY = height / 2;
        outerRadius = height / 3;

        float startC = 0;
        for (int i = 0; i < pieEntries.size(); i++) {
            float sweep;
            if (total <= 0) {
                sweep = 360 / pieEntries.size();
            }
            else {
                sweep = 360 * (pieEntries.get(i).getValue() / total);
            }
            paint.setColor(getResources().getColor(pieEntries.get(i).getColorRes() == 0 ? R.color.color_pie_chart_fill : pieEntries.get(i).getColorRes()));

            Random random = new Random();
            int max = (int) outerRadius;
            int min = (int) (outerRadius / 2);
            float radius = pieEntries.get(i).getRadius();
            if (radius == 0) {
                radius = random.nextInt(max) % (max - min + 1) + min;
                while (radius == prevRadius || lastEqualFirst(i, radius)) {
                    radius = random.nextInt(max) % (max - min + 1) + min;
                }
            }

            pieEntries.get(i).setRadius(radius);

            prevRadius = radius;
            if (i == 0) {
                firstRadius = radius;
            }

            float radiusT = (radius * 2.6666f) / 2;

            RectF rectF = new RectF(centerX - radiusT, centerY - radiusT, centerX + radiusT, centerY + radiusT);
            canvas.drawArc(rectF, startC, sweep, true, paint);

            RectF rectFOuter = new RectF(centerX - outerRadius, centerY - outerRadius, centerX + outerRadius, centerY + outerRadius);
            canvas.drawArc(rectFOuter, startC, sweep, true, paintOuter);

            if ((pieEntries.get(i).getValue() > 0 && total > 0) || (total <= 0 && pieEntries.get(i).getValue() <= 0)) {
                float arcCenterC = startC + sweep / 2;
                float arcCenterX = 0;
                float arcCenterY = 0;

                float arcCenterX2 = 0;
                float arcCenterY2 = 0;
                paint.setColor(Color.BLACK);

                if (arcCenterC >= 0 && arcCenterC < 90) {
                    arcCenterX = (float) (centerX + outerRadius * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY = (float) (centerY + outerRadius * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterX2 = (float) (arcCenterX + DensityUtils.dp2px(getContext(), textLineLength) * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY2 = (float) (arcCenterY + DensityUtils.dp2px(getContext(), textLineLength) * Math.sin(arcCenterC * Math.PI / 180));
                    canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                    canvas.drawText(pieEntries.get(i).name + bracketFront + pieEntries.get(i).getValue() + bracketBehind, arcCenterX2, arcCenterY2 + paint.getTextSize() / 2, paint);
                }
                else if (arcCenterC >= 90 && arcCenterC < 180) {
                    arcCenterC = 180 - arcCenterC;
                    arcCenterX = (float) (centerX - outerRadius * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY = (float) (centerY + outerRadius * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterX2 = (float) (arcCenterX - DensityUtils.dp2px(getContext(), textLineLength) * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY2 = (float) (arcCenterY + DensityUtils.dp2px(getContext(), textLineLength) * Math.sin(arcCenterC * Math.PI / 180));
                    canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                    String text = pieEntries.get(i).name + bracketFront + pieEntries.get(i).getValue() + bracketBehind;
                    float textWidth = paint.measureText(text);
                    canvas.drawText(text, arcCenterX2 - textWidth, arcCenterY2 + paint.getTextSize() / 2, paint);
                }
                else if (arcCenterC >= 180 && arcCenterC < 270) {
                    arcCenterC = 270 - arcCenterC;
                    arcCenterX = (float) (centerX - outerRadius * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterY = (float) (centerY - outerRadius * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterX2 = (float) (arcCenterX - DensityUtils.dp2px(getContext(), textLineLength) * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterY2 = (float) (arcCenterY - DensityUtils.dp2px(getContext(), textLineLength) * Math.cos(arcCenterC * Math.PI / 180));
                    canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                    String text = pieEntries.get(i).name + bracketFront + pieEntries.get(i).getValue() + bracketBehind;
                    float textWidth = paint.measureText(text);
                    canvas.drawText(text, arcCenterX2 - textWidth, arcCenterY2, paint);
                }
                else if (arcCenterC >= 270 && arcCenterC < 360) {
                    arcCenterC = 360 - arcCenterC;
                    arcCenterX = (float) (centerX + outerRadius * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY = (float) (centerY - outerRadius * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterX2 = (float) (arcCenterX + DensityUtils.dp2px(getContext(), textLineLength) * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY2 = (float) (arcCenterY - DensityUtils.dp2px(getContext(), textLineLength) * Math.sin(arcCenterC * Math.PI / 180));
                    canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                    canvas.drawText(pieEntries.get(i).name + bracketFront + pieEntries.get(i).getValue() + bracketBehind, arcCenterX2, arcCenterY2, paint);
                }
            }
            pieEntries.get(i).setStartC(startC);
            pieEntries.get(i).setEndC(startC + sweep);
            startC += sweep;
        }
    }

    private boolean lastEqualFirst(int i, float radius) {
        return i == pieEntries.size() - 1 && radius == firstRadius;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX;
        float touchY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
                if (Math.pow(touchX - centerX, 2) + Math.pow(touchY - centerY, 2) <= Math.pow(outerRadius, 2)) {
                    float touchC = getSweep(touchX, touchY);
                    for (int i = 0; i < pieEntries.size(); i++) {
                        if (touchC >= pieEntries.get(i).getStartC() && touchC < pieEntries.get(i).getEndC()) {
                            if (listener != null)
                                listener.onItemClick(i);
                        }
                    }
                    invalidate();
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * @param touchX
     * @param touchY
     */
    private float getSweep(float touchX, float touchY) {
        float xZ = touchX - centerX;
        float yZ = touchY - centerY;
        float a = Math.abs(xZ);
        float b = Math.abs(yZ);
        double c = Math.toDegrees(Math.atan(b / a));
        if (xZ >= 0 && yZ >= 0) {
            return (float) c;
        }
        else if (xZ <= 0 && yZ >= 0) {
            return 180 - (float) c;
        }
        else if (xZ <= 0 && yZ <= 0) {
            return (float) c + 180;
        }
        else {
            return 360 - (float) c;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class PieEntry {
        private String name;
        private float value;
        private int colorRes;
        private float startC;
        private float endC;
        private float radius;

        public PieEntry(String name, float value) {
            this.name = name;
            this.value = value;
        }

        public PieEntry() {

        }

        private float getStartC() {
            return startC;
        }

        private void setStartC(float startC) {
            this.startC = startC;
        }

        private float getEndC() {
            return endC;
        }

        private void setEndC(float endC) {
            this.endC = endC;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
            this.radius = value;
        }

        public int getColorRes() {
            return colorRes;
        }

        public void setColorRes(int colorRes) {
            this.colorRes = colorRes;
        }

        private float getRadius() {
            return radius;
        }

        private void setRadius(float radius) {
            this.radius = radius;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
