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
import com.onyx.android.sun.utils.DensityUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hehai on 17-10-18.
 */

public class PieChart extends View {

    private List<PieEntry> pieEntries;

    private Paint paint;
    private Paint paintOuter;

    private float centerX = 200;
    private float centerY = 200;
    private float firstRadius;
    private float prevRadius;
    private float outerRadius = 150;
    private float textLineLength = 10;
    private float textSize = 15;

    private OnItemClickListener listener;
    private int width;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PieChart(Context context) {
        super(context);
        init();
    }

    public PieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        paintOuter.setColor(getResources().getColor(R.color.black));
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
        width = measuredWidth < measuredHeight ? measuredWidth : measuredHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float total = 0;
        for (int i = 0; i < pieEntries.size(); i++) {
            total += pieEntries.get(i).getNumber();
        }
        centerX = width / 2;
        centerY = width / 2;
        outerRadius = width / 4;

        float startC = 0;
        for (int i = 0; i < pieEntries.size(); i++) {
            float sweep;
            if (total <= 0) {
                sweep = 360 / pieEntries.size();
            } else {
                sweep = 360 * (pieEntries.get(i).getNumber() / total);
            }
            paint.setColor(getResources().getColor(pieEntries.get(i).colorRes == 0 ? R.color.chart_gray : pieEntries.get(i).colorRes));

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
            float radiusT;
            if (pieEntries.get(i).isSelected()) {
                radiusT = outerRadius;
            } else {
                radiusT = radius;
            }

            RectF rectF = new RectF(centerX - radiusT, centerY - radiusT, centerX + radiusT, centerY + radiusT);
            canvas.drawArc(rectF, startC, sweep, true, paint);

            RectF rectFOuter = new RectF(centerX - outerRadius, centerY - outerRadius, centerX + outerRadius, centerY + outerRadius);
            canvas.drawArc(rectFOuter, startC, sweep, true, paintOuter);


            if ((pieEntries.get(i).getNumber() > 0 && total > 0) || (total <= 0 && pieEntries.get(i).getNumber() <= 0)) {
                float arcCenterC = startC + sweep / 2;
                float arcCenterX = 0;
                float arcCenterY = 0;

                float arcCenterX2 = 0;
                float arcCenterY2 = 0;
                DecimalFormat numberFormat = new DecimalFormat("00.00");
                paint.setColor(Color.BLACK);

                if (arcCenterC >= 0 && arcCenterC < 90) {
                    arcCenterX = (float) (centerX + outerRadius * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY = (float) (centerY + outerRadius * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterX2 = (float) (arcCenterX + DensityUtils.dp2px(getContext(), textLineLength) * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY2 = (float) (arcCenterY + DensityUtils.dp2px(getContext(), textLineLength) * Math.sin(arcCenterC * Math.PI / 180));
                    canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                    if (total <= 0) {
                        canvas.drawText(numberFormat.format(0) + "%", arcCenterX2, arcCenterY2 + paint.getTextSize() / 2, paint);
                    } else {
                        canvas.drawText(numberFormat.format(pieEntries.get(i).getNumber() / total * 100) + "%", arcCenterX2, arcCenterY2 + paint.getTextSize() / 2, paint);
                    }
                } else if (arcCenterC >= 90 && arcCenterC < 180) {
                    arcCenterC = 180 - arcCenterC;
                    arcCenterX = (float) (centerX - outerRadius * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY = (float) (centerY + outerRadius * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterX2 = (float) (arcCenterX - DensityUtils.dp2px(getContext(), textLineLength) * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY2 = (float) (arcCenterY + DensityUtils.dp2px(getContext(), textLineLength) * Math.sin(arcCenterC * Math.PI / 180));
                    canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                    if (total <= 0) {
                        canvas.drawText(numberFormat.format(0) + "%", (float) (arcCenterX2 - paint.getTextSize() * 3.5), arcCenterY2 + paint.getTextSize() / 2, paint);
                    } else {
                        canvas.drawText(numberFormat.format(pieEntries.get(i).getNumber() / total * 100) + "%", (float) (arcCenterX2 - paint.getTextSize() * 3.5), arcCenterY2 + paint.getTextSize() / 2, paint);
                    }
                } else if (arcCenterC >= 180 && arcCenterC < 270) {
                    arcCenterC = 270 - arcCenterC;
                    arcCenterX = (float) (centerX - outerRadius * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterY = (float) (centerY - outerRadius * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterX2 = (float) (arcCenterX - DensityUtils.dp2px(getContext(), textLineLength) * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterY2 = (float) (arcCenterY - DensityUtils.dp2px(getContext(), textLineLength) * Math.cos(arcCenterC * Math.PI / 180));
                    canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                    if (total <= 0) {
                        canvas.drawText(numberFormat.format(0) + "%", (float) (arcCenterX2 - paint.getTextSize() * 3.5), arcCenterY2, paint);
                    } else {
                        canvas.drawText(numberFormat.format(pieEntries.get(i).getNumber() / total * 100) + "%", (float) (arcCenterX2 - paint.getTextSize() * 3.5), arcCenterY2, paint);
                    }
                } else if (arcCenterC >= 270 && arcCenterC < 360) {
                    arcCenterC = 360 - arcCenterC;
                    arcCenterX = (float) (centerX + outerRadius * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY = (float) (centerY - outerRadius * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterX2 = (float) (arcCenterX + DensityUtils.dp2px(getContext(), textLineLength) * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY2 = (float) (arcCenterY - DensityUtils.dp2px(getContext(), textLineLength) * Math.sin(arcCenterC * Math.PI / 180));
                    canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                    if (total <= 0) {
                        canvas.drawText(numberFormat.format(0) + "%", arcCenterX2, arcCenterY2, paint);
                    } else {
                        canvas.drawText(numberFormat.format(pieEntries.get(i).getNumber() / total * 100) + "%", arcCenterX2, arcCenterY2, paint);
                    }
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
                            pieEntries.get(i).setSelected(true);
                            if (listener != null)
                                listener.onItemClick(i);
                        } else {
                            pieEntries.get(i).setSelected(false);
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
        } else if (xZ <= 0 && yZ >= 0) {
            return 180 - (float) c;
        } else if (xZ <= 0 && yZ <= 0) {
            return (float) c + 180;
        } else {
            return 360 - (float) c;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class PieEntry {
        private float number;
        private int colorRes;
        private boolean selected;
        private float startC;
        private float endC;
        private float radius;

        public PieEntry(float number) {
            this.number = number;
        }

        public PieEntry(float number, int colorRes, boolean selected) {
            this.number = number;
            this.colorRes = colorRes;
            this.selected = selected;
        }

        public float getStartC() {
            return startC;
        }

        public void setStartC(float startC) {
            this.startC = startC;
        }

        public float getEndC() {
            return endC;
        }

        public void setEndC(float endC) {
            this.endC = endC;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public float getNumber() {
            return number;
        }

        public void setNumber(float number) {
            this.number = number;
        }

        public int getColorRes() {
            return colorRes;
        }

        public void setColorRes(int colorRes) {
            this.colorRes = colorRes;
        }

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }
    }
}
