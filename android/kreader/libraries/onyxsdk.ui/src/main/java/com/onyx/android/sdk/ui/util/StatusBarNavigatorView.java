/**
 * 
 */
package com.onyx.android.sdk.ui.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author joy
 *
 */
public class StatusBarNavigatorView extends View
{
    private Paint mPaint = new Paint();
    private Rect mNavigatorContent = null;

    public StatusBarNavigatorView(Context context)
    {
        super(context);
    }

    public StatusBarNavigatorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StatusBarNavigatorView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    public void setNavigatorContent(Rect pageSize, Rect content)
    {
        double z_w = (double)this.getWidth() / pageSize.width();
        double z_h = (double)this.getHeight() / pageSize.height();
        
        int left = Math.max(0, (int)(content.left * z_w));
        int top = Math.max(0, (int)(content.top * z_h));
        int right = Math.min(this.getWidth(), (int)(content.right * z_w));
        int bottom = Math.min(this.getHeight(), (int)(content.bottom * z_h));
        
        mNavigatorContent = new Rect(left, top, right, bottom);
        
        this.postInvalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        mPaint.setColor(Color.BLACK);
        mPaint.setAlpha(200);
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), mPaint);
        
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(200);
        if (mNavigatorContent != null) {
            canvas.drawRect(mNavigatorContent, mPaint);
        }
        
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(0, 0, this.getWidth() - 1, 0, mPaint);
        canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight() - 1, mPaint);
        canvas.drawLine(this.getWidth() - 1, this.getHeight() - 1, 0, this.getHeight() - 1, mPaint);
        canvas.drawLine(0, this.getHeight() - 1, 0, 0, mPaint);
    }
}
