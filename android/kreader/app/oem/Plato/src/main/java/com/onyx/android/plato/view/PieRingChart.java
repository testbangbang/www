package com.onyx.android.plato.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.plato.utils.DensityUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-10-18.
 */

public class PieRingChart extends View {

    private List<PieEntry> pieEntries;

    private Paint paint;
    private Paint paintOuter;

    private float centerX = 200;
    private float centerY = 200;
    private float outerRadius = 150;
    private float inRadius;
    private float textLineLength = 30;
    private float textSize = 15;

    private OnItemClickListener listener;
    private int width;
    private Paint centerTextPaint;
    private float ringWidth;
    private String centerText1;
    private String centerText2;
    private String centerText3;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PieRingChart(Context context) {
        super(context);
        init();
    }

    public PieRingChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieRingChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        pieEntries = new ArrayList<>();
        paint = new Paint();
        paint.setTextSize(DensityUtils.sp2px(getContext(), textSize));
        paint.setAntiAlias(true);
        paintOuter = new Paint();
        paintOuter.setStyle(Paint.Style.FILL);
        paintOuter.setColor(Color.WHITE);
        paintOuter.setAntiAlias(true);
        centerTextPaint = new Paint();
        centerTextPaint.setTextSize(DensityUtils.sp2px(getContext(), textSize));
        centerTextPaint.setAntiAlias(true);
        centerTextPaint.setTextAlign(Paint.Align.CENTER);
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

    public void setRingWidth(float ringWidth) {
        this.ringWidth = ringWidth;
    }

    public void setCenterText1(String centerText1) {
        this.centerText1 = centerText1;
    }

    public void setCenterText2(String centerText2) {
        this.centerText2 = centerText2;
    }

    public void setCenterText3(String centerText3) {
        this.centerText3 = centerText3;
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
            paint.setColor(getResources().getColor(pieEntries.get(i).colorRes));
            float radiusT;
            radiusT = outerRadius;
            RectF rectF = new RectF(centerX - radiusT, centerY - radiusT, centerX + radiusT, centerY + radiusT);
            canvas.drawArc(rectF, startC, sweep, true, paint);

            if ((pieEntries.get(i).getNumber() > 0 && total > 0) || (total <= 0 && pieEntries.get(i).getNumber() <= 0)) {
                float arcCenterC = startC + sweep / 2;
                float arcCenterX = 0;
                float arcCenterY = 0;

                float arcCenterX2 = 0;
                float arcCenterY2 = 0;

                float arcCenterX3 = 0;
                DecimalFormat numberFormat = new DecimalFormat("00.00");
                paint.setColor(Color.BLACK);

                if (arcCenterC >= 0 && arcCenterC < 90) {
                    arcCenterX = (float) (centerX + outerRadius * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY = (float) (centerY + outerRadius * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterX2 = (float) (arcCenterX + DensityUtils.dp2px(getContext(), textLineLength) * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY2 = (float) (arcCenterY + DensityUtils.dp2px(getContext(), textLineLength) * Math.sin(arcCenterC * Math.PI / 180));
                    canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                    arcCenterX3 = arcCenterX2 + 10;
                    canvas.drawLine(arcCenterX2, arcCenterY2, arcCenterX3, arcCenterY2, paint);
                    if (total <= 0) {
                        canvas.drawText(pieEntries.get(i).getText(), arcCenterX3, arcCenterY2 - paint.getTextSize() / 2, paint);
                        canvas.drawText(numberFormat.format(0) + "%", arcCenterX3, arcCenterY2 + paint.getTextSize() / 2, paint);
                    } else {
                        canvas.drawText(pieEntries.get(i).getText(), arcCenterX3, arcCenterY2 - paint.getTextSize() / 2, paint);
                        canvas.drawText(numberFormat.format(pieEntries.get(i).getNumber() / total * 100) + "%", arcCenterX3, arcCenterY2 + paint.getTextSize() / 2, paint);
                    }
                } else if (arcCenterC >= 90 && arcCenterC < 180) {
                    arcCenterC = 180 - arcCenterC;
                    arcCenterX = (float) (centerX - outerRadius * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY = (float) (centerY + outerRadius * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterX2 = (float) (arcCenterX - DensityUtils.dp2px(getContext(), textLineLength) * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY2 = (float) (arcCenterY + DensityUtils.dp2px(getContext(), textLineLength) * Math.sin(arcCenterC * Math.PI / 180));
                    canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                    arcCenterX3 = arcCenterX2 - 10;
                    canvas.drawLine(arcCenterX2, arcCenterY2, arcCenterX3, arcCenterY2, paint);
                    if (total <= 0) {
                        canvas.drawText(pieEntries.get(i).getText(), (float) (arcCenterX3 - paint.getTextSize() * 3.5), arcCenterY2 - paint.getTextSize() / 2, paint);
                        canvas.drawText(numberFormat.format(0) + "%", (float) (arcCenterX3 - paint.getTextSize() * 3.5), arcCenterY2 + paint.getTextSize() / 2, paint);
                    } else {
                        canvas.drawText(pieEntries.get(i).getText(), (float) (arcCenterX3 - paint.getTextSize() * 3.5), arcCenterY2 - paint.getTextSize() / 2, paint);
                        canvas.drawText(numberFormat.format(pieEntries.get(i).getNumber() / total * 100) + "%", (float) (arcCenterX3 - paint.getTextSize() * 3.5), arcCenterY2 + paint.getTextSize() / 2, paint);
                    }
                } else if (arcCenterC >= 180 && arcCenterC < 270) {
                    arcCenterC = 270 - arcCenterC;
                    arcCenterX = (float) (centerX - outerRadius * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterY = (float) (centerY - outerRadius * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterX2 = (float) (arcCenterX - DensityUtils.dp2px(getContext(), textLineLength) * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterY2 = (float) (arcCenterY - DensityUtils.dp2px(getContext(), textLineLength) * Math.cos(arcCenterC * Math.PI / 180));
                    canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                    arcCenterX3 = arcCenterX2 - 10;
                    canvas.drawLine(arcCenterX2, arcCenterY2, arcCenterX3, arcCenterY2, paint);
                    if (total <= 0) {
                        canvas.drawText(pieEntries.get(i).getText(), (float) (arcCenterX3 - paint.getTextSize() * 3.5), arcCenterY2 - paint.getTextSize(), paint);
                        canvas.drawText(numberFormat.format(0) + "%", (float) (arcCenterX3 - paint.getTextSize() * 3.5), arcCenterY2, paint);
                    } else {
                        canvas.drawText(pieEntries.get(i).getText(), (float) (arcCenterX3 - paint.getTextSize() * 3.5), arcCenterY2 - paint.getTextSize(), paint);
                        canvas.drawText(numberFormat.format(pieEntries.get(i).getNumber() / total * 100) + "%", (float) (arcCenterX3 - paint.getTextSize() * 3.5), arcCenterY2, paint);
                    }
                } else if (arcCenterC >= 270 && arcCenterC < 360) {
                    arcCenterC = 360 - arcCenterC;
                    arcCenterX = (float) (centerX + outerRadius * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY = (float) (centerY - outerRadius * Math.sin(arcCenterC * Math.PI / 180));
                    arcCenterX2 = (float) (arcCenterX + DensityUtils.dp2px(getContext(), textLineLength) * Math.cos(arcCenterC * Math.PI / 180));
                    arcCenterY2 = (float) (arcCenterY - DensityUtils.dp2px(getContext(), textLineLength) * Math.sin(arcCenterC * Math.PI / 180));
                    canvas.drawLine(arcCenterX, arcCenterY, arcCenterX2, arcCenterY2, paint);
                    arcCenterX3 = arcCenterX2 + 10;
                    canvas.drawLine(arcCenterX2, arcCenterY2, arcCenterX3, arcCenterY2, paint);
                    if (total <= 0) {
                        canvas.drawText(pieEntries.get(i).getText(), arcCenterX3, arcCenterY2 - paint.getTextSize(), paint);
                        canvas.drawText(numberFormat.format(0) + "%", arcCenterX3, arcCenterY2, paint);
                    } else {
                        canvas.drawText(pieEntries.get(i).getText(), arcCenterX3, arcCenterY2 - paint.getTextSize(), paint);
                        canvas.drawText(numberFormat.format(pieEntries.get(i).getNumber() / total * 100) + "%", arcCenterX3, arcCenterY2, paint);
                    }
                }
            }

            if (ringWidth == 0) {
                ringWidth = outerRadius * 0.1f;
            }

            inRadius = outerRadius - ringWidth;
            RectF rectFOuter = new RectF(centerX - inRadius, centerY - inRadius, centerX + inRadius, centerY + inRadius);
            canvas.drawArc(rectFOuter, startC, 360, true, paintOuter);
            centerTextPaint.setTextSize(DensityUtils.px2sp(getContext(), 20));
            canvas.drawText(StringUtils.isNotBlank(centerText1) ? centerText1 : "", centerX, centerY - 40, centerTextPaint);
            centerTextPaint.setTextSize(DensityUtils.px2sp(getContext(), 30));
            canvas.drawText(StringUtils.isNotBlank(centerText2) ? centerText2 : "", centerX, centerY, centerTextPaint);
            centerTextPaint.setTextSize(DensityUtils.px2sp(getContext(), 15));
            canvas.drawText(StringUtils.isNotBlank(centerText3) ? centerText3 : "", centerX, centerY + 30, centerTextPaint);
            pieEntries.get(i).setStartC(startC);
            pieEntries.get(i).setEndC(startC + sweep);
            startC += sweep;
        }
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
                            if (listener != null) {
                                listener.onItemClick(i);
                            }
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

    /**
     */
    public static class PieEntry {
        private float number;
        private int colorRes;
        private boolean selected;
        private float startC;
        private float endC;
        private float radius;
        private String text;

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

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
