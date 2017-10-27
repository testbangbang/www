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

package com.onyx.android.sun.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jackdeng on 2017/10/27.
 */

public class SpiderWebScoreView extends View {
    private int angleCount = 5;
    private int hierarchyCount = 5;
    private int lineColor = 0xFF000000;
    private float lineWidth = -1;

    private float maxScore = 10f;
    private float[] scoresFill;
    private float[] scoresStroke;
    private int scoreColorFill = 0xAA000000;
    private int scoreStrokeColorFill = 0xffffffff;
    private int scoreColorStroke = 0xffffffff;
    private int scoreStrokeColorStroke = 0xff000000;
    private float scoreStrokeWidthFill = -1;
    private float scoreStrokeWidthStroke = 2;
    private boolean disableScoreStroke;
    private Paint scorePaint;
    private Paint scoreStrokePaintFill;
    private Paint scoreStrokePaintStroke;

    private float centerX;
    private float centerY;
    private float radius;
    private Paint linePaint;
    private Path pathFill;
    private Path pathStroke;

    public SpiderWebScoreView(Context context) {
        super(context);
        init(context, null);
    }

    public SpiderWebScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpiderWebScoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        if (lineWidth > 0) {
            linePaint.setStrokeWidth(lineWidth);
        }

        scorePaint = new Paint();
        scorePaint.setColor(scoreColorFill);
        scorePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        scorePaint.setAntiAlias(true);

        pathFill = new Path();
        pathStroke = new Path();

        if (isInEditMode()) {
            float[] randomScoreArray = new float[]{7.0f, 8.0f, 5.0f, 5.0f, 8.0f};
            float[] testScores = new float[angleCount];
            int index = 0;
            for (int w = 0; w < angleCount; w++) {
                testScores[w] = randomScoreArray[index++ % randomScoreArray.length];
            }
            setScores(10f, testScores);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        reset();
    }

    private void reset() {
        if (angleCount != 0 && hierarchyCount != 0) {
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            centerX = viewWidth / 2;
            centerY = viewHeight / 2;
            radius = Math.min(viewWidth, viewHeight) / 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawAllHierarchy(canvas);
        drawAllLine(canvas);
        drawScore(canvas);
    }


    private void drawAllHierarchy(Canvas canvas) {
        float averageRadius = radius / hierarchyCount;
        for (int w = 0; w < hierarchyCount; w++) {
            drawHierarchyByRadius(canvas, averageRadius * (w + 1));
        }
    }

    private void drawHierarchyByRadius(Canvas canvas, float currentRadius) {
        pathFill.reset();

        float nextAngle;
        float nextRadians;
        float nextPointX;
        float nextPointY;
        float averageAngle = 360 / angleCount;
        float offsetAngle = averageAngle > 0 && angleCount % 2 == 0 ? averageAngle / 2 : 0;
        for (int position = 0; position < angleCount; position++) {
            nextAngle = offsetAngle + (position * averageAngle);
            nextRadians = (float) Math.toRadians(nextAngle);
            nextPointX = (float) (centerX + Math.sin(nextRadians) * currentRadius);
            nextPointY = (float) (centerY - Math.cos(nextRadians) * currentRadius);

            if (position == 0) {
                pathFill.moveTo(nextPointX, nextPointY);
            }
            else {
                pathFill.lineTo(nextPointX, nextPointY);
            }
        }

        pathFill.close();
        canvas.drawPath(pathFill, linePaint);
    }

    private void drawAllLine(Canvas canvas) {
        float nextAngle;
        float nextRadians;
        float nextPointX;
        float nextPointY;
        float averageAngle = 360 / angleCount;
        float offsetAngle = averageAngle > 0 && angleCount % 2 == 0 ? averageAngle / 2 : 0;
        for (int position = 0; position < angleCount; position++) {
            nextAngle = offsetAngle + (position * averageAngle);
            nextRadians = (float) Math.toRadians(nextAngle);
            nextPointX = (float) (centerX + Math.sin(nextRadians) * radius);
            nextPointY = (float) (centerY - Math.cos(nextRadians) * radius);

            canvas.drawLine(centerX, centerY, nextPointX, nextPointY, linePaint);
        }
    }


    private void drawScore(Canvas canvas) {
        if (scoresFill == null || scoresFill.length <= 0) {
            return;
        }

        pathFill.reset();
        pathStroke.reset();

        float nextAngle;
        float nextRadians;
        float nextPointX;
        float nextPointY;
        float currentRadius;
        float averageAngle = 360 / angleCount;
        float offsetAngle = averageAngle > 0 && angleCount % 2 == 0 ? averageAngle / 2 : 0;
        for (int position = 0; position < angleCount; position++) {
            currentRadius = (scoresFill[position] / maxScore) * radius;
            nextAngle = offsetAngle + (position * averageAngle);
            nextRadians = (float) Math.toRadians(nextAngle);
            nextPointX = (float) (centerX + Math.sin(nextRadians) * currentRadius);
            nextPointY = (float) (centerY - Math.cos(nextRadians) * currentRadius);

            if (position == 0) {
                pathFill.moveTo(nextPointX, nextPointY);
            }
            else {
                pathFill.lineTo(nextPointX, nextPointY);
            }
        }

        if (null != scoresStroke && scoresStroke.length > 0) {
            for (int position = 0; position < angleCount; position++) {
                currentRadius = (scoresStroke[position] / maxScore) * radius;
                nextAngle = offsetAngle + (position * averageAngle);
                nextRadians = (float) Math.toRadians(nextAngle);
                nextPointX = (float) (centerX + Math.sin(nextRadians) * currentRadius);
                nextPointY = (float) (centerY - Math.cos(nextRadians) * currentRadius);

                if (position == 0) {
                    pathStroke.moveTo(nextPointX, nextPointY);
                }
                else {
                    pathStroke.lineTo(nextPointX, nextPointY);
                }
            }
            pathStroke.close();
        }

        pathFill.close();
        canvas.drawPath(pathFill, scorePaint);

        if (!disableScoreStroke) {
            if (scoreStrokePaintFill == null) {
                scoreStrokePaintFill = new Paint();
                scoreStrokePaintFill.setColor(scoreStrokeColorFill);
                scoreStrokePaintFill.setStyle(Paint.Style.STROKE);
                scoreStrokePaintFill.setAntiAlias(true);
                if (scoreStrokeWidthFill > 0) {
                    scoreStrokePaintFill.setStrokeWidth(scoreStrokeWidthFill);
                }
            }
            canvas.drawPath(pathFill, scoreStrokePaintFill);

            if (null != scoresStroke && scoresStroke.length > 0) {
                if (scoreStrokePaintStroke == null) {
                    scoreStrokePaintStroke = new Paint();
                    scoreStrokePaintStroke.setColor(scoreStrokeColorStroke);
                    scoreStrokePaintStroke.setStyle(Paint.Style.STROKE);
                    scoreStrokePaintStroke.setAntiAlias(true);
                    if (scoreStrokeWidthStroke > 0) {
                        scoreStrokePaintStroke.setStrokeWidth(scoreStrokeWidthStroke);
                    }
                }
                canvas.drawPath(pathStroke, scoreStrokePaintStroke);
            }

        }
    }

    private void setAngleCount(int angleCount) {
        if (angleCount <= 2) {
            throw new IllegalArgumentException("angleCount Can not be less than or equal to 2");
        }
        this.angleCount = angleCount;
        reset();
        postInvalidate();
    }


    private void setMaxScore(float maxScore) {
        if (maxScore <= 0) {
            throw new IllegalArgumentException("maxScore Can not be less than or equal to 0");
        }
        this.maxScore = maxScore;
    }

    public void setScores(float maxScore, float[] scoresFill, float[] scoresStroke) {
        if (scoresFill == null || scoresFill.length == 0) {
            throw new IllegalArgumentException("scoresFill Can't be null or empty");
        }
        if (scoresStroke == null || scoresStroke.length == 0) {
            throw new IllegalArgumentException("scoresStroke Can't be null or empty");
        }
        setMaxScore(maxScore);
        this.scoresFill = scoresFill;
        this.scoresStroke = scoresStroke;
        this.angleCount = this.scoresFill.length;
        reset();
        postInvalidate();
    }

    public void setScores(float maxScore, float[] scores) {
        if (scores == null || scores.length == 0) {
            throw new IllegalArgumentException("scores Can't be null or empty");
        }
        setMaxScore(maxScore);
        this.scoresFill = scores;
        this.angleCount = scores.length;
        reset();
        postInvalidate();
    }

    public void setHierarchyCount(int hierarchyCount) {
        if (hierarchyCount <= 0) {
            throw new IllegalArgumentException("hierarchyCount Can not be less than or equal to 0");
        }
        this.hierarchyCount = hierarchyCount;
        reset();
        postInvalidate();
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        if (linePaint != null) {
            linePaint.setColor(lineColor);
        }
        postInvalidate();
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        if (linePaint != null) {
            linePaint.setStrokeWidth(lineWidth);
        }
        postInvalidate();
    }

    public void setScoreColorFill(int scoreColorFill) {
        this.scoreColorFill = scoreColorFill;
        if (scorePaint != null) {
            scorePaint.setColor(scoreColorFill);
        }
        postInvalidate();
    }

    public void setScoreStrokeColorFill(int scoreStrokeColorFill) {
        this.scoreStrokeColorFill = scoreStrokeColorFill;
        if (scoreStrokePaintFill != null) {
            scoreStrokePaintFill.setColor(scoreStrokeColorFill);
        }
        postInvalidate();
    }

    public void setScoreStrokeWidthFill(float scoreStrokeWidthFill) {
        this.scoreStrokeWidthFill = scoreStrokeWidthFill;
        if (scoreStrokePaintFill != null) {
            scoreStrokePaintFill.setStrokeWidth(scoreStrokeWidthFill);
        }
        postInvalidate();
    }

    public void setDisableScoreStroke(boolean disableScoreStroke) {
        this.disableScoreStroke = disableScoreStroke;
        postInvalidate();
    }
}
