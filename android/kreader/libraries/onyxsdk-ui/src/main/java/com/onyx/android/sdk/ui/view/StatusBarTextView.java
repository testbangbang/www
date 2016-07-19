package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import com.onyx.android.sdk.ui.R;

public class StatusBarTextView extends View {
	private String mText = "";
	private final Paint mPaint = new Paint();
	private int mGravity = Gravity.CENTER;
	private int truncateLength = 0;
	private int addonDotLength = 0;
	private int YTop = 0;
    private int textSize = 35;
	private final FontMetrics mFM = new FontMetrics();
	private static final String addonDot= "\u2026";
	
	private String mPattern = null;
	
	public StatusBarTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        getAttrs(context,attrs);
		init();
	}
	
	public StatusBarTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
	}
	
	public StatusBarTextView(Context context) {
	    this(context,null,0);
    }
	
	private void init() {
		mPaint.setAntiAlias(true);
	    mPaint.setTextSize(textSize);
	    mPaint.setColor(0xFF000000);
	    setPadding(0, 0, 0, 0);
	}
	
	private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        mPaint.getFontMetrics(mFM);
        YTop = (int) -mFM.top;
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int) (-mFM.top + mFM.bottom) + getPaddingTop()
                    + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    } 
	
	private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        
        addonDotLength = (int) mPaint.measureText(addonDot);
        
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
        	if (mPattern != null) {
        		result = (int) mPaint.measureText(mPattern) + getPaddingLeft()
                    + getPaddingRight();
        	} else {
        		result = (int) mPaint.measureText(mText) + getPaddingLeft()
                        + getPaddingRight();
        	}
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    } 
	
	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec)); 
	}
	
	private int getOutPos(final String s, int w) {
		/*int res = s.length();		
		while (true) {
			res--;
			if (res == 0)
				break;
			
			truncateLength = (int) (mPaint.measureText(s, 0, res) + 0.5f);
			if (truncateLength < w - addonDotLength)
				break;
		}*/
		int res = s.length();	
		float[] fl = new float[res];
		mPaint.getTextWidths(s, fl);
		float sum = 0;
		for (int i = 0; i < res; i++)
			sum += fl[i];
		
		while (true) {
			res--;
			sum -= fl[res];
			if (res == 0)
				break;
			
			truncateLength = (int) sum;
			if (truncateLength < w - addonDotLength)
				break;
		}
		return res;
	}



    private int shift = 10;
	
	private void HeaderPaintLine(Canvas canvas, int x0, int x1, int height) {
		boolean aa = mPaint.isAntiAlias();
		if (aa)
			mPaint.setAntiAlias(false);
		mPaint.setStrokeWidth(0);
		canvas.drawLine(x0, height - 1, x1, height - 1, mPaint);
		if (aa)
			mPaint.setAntiAlias(true);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int width = this.getWidth();
		final int height = this.getHeight();
		int len = (int) mPaint.measureText(mText);
		int tmp;
		
		if (linePresent) 
			if (false) {
				tmp = 10;
				HeaderPaintLine(canvas, 0, (width >> 1) - tmp, height);
				HeaderPaintLine(canvas, (width >> 1) + tmp, width, height);
			} else {
				HeaderPaintLine(canvas, 0, width + 20, height);				
			}
		
		tmp = mText.indexOf(0xffff); 
		if (tmp != -1) {
			String s1 = mText.substring(0, tmp);
			String s2 = mText.substring(tmp + 1, mText.length());
			
			width >>= 1;			
			
			len = (int) mPaint.measureText(s1);
			if (len > width - shift) {
				tmp = getOutPos(s1, width - shift);
				if (tmp > 0) {
					canvas.drawText(s1, 0, tmp, 0, YTop, mPaint);
					canvas.drawText(addonDot, 0 + truncateLength, YTop, mPaint);
				}
			} else canvas.drawText(s1, 0, YTop, mPaint);

			len = (int) mPaint.measureText(s2);
			if (len > width - shift) {
				tmp = getOutPos(s2, width - shift);
				if (tmp > 0) {
					canvas.drawText(s2, 0, tmp, width + shift, YTop, mPaint);
					canvas.drawText(addonDot, width + shift + truncateLength, YTop, mPaint);
				}
			} else canvas.drawText(s2, width * 2 - len, YTop, mPaint);
			
			return;
		}
		
		if (len > width) {
			tmp = getOutPos(mText, width);
			if (tmp > 0) {
				canvas.drawText(mText, 0, tmp, 0, YTop, mPaint);
				canvas.drawText(addonDot, 0 + truncateLength, YTop, mPaint);
			}			
		} else {
			switch (mGravity) {
			case Gravity.LEFT:
				canvas.drawText(mText, 0, YTop, mPaint);
				break;
			case Gravity.RIGHT:
				canvas.drawText(mText, width - len, YTop, mPaint);
				break;
			case Gravity.CENTER:
				canvas.drawText(mText, this.getPaddingLeft() + ((width - len) >> 1), 
					this.getPaddingTop() + YTop, mPaint);
				break;
			}
		}
	}

	public void setText(String s) {
		if (s == null)
			s = "";
		if (!s.equalsIgnoreCase(mText)) {
			mText = s;
			if (mPattern == null)
				requestLayout();
	        invalidate(); 
		}
	}
	
	private boolean linePresent = false;
	public void setLine(boolean is_line) {
		linePresent = is_line;
	}
	
	public void setPaintFlags(int flags) {
		if (mPaint.getFlags() != flags) {
			mPaint.setFlags(flags);
			this.invalidate();
		}
	}
	
	public int getPaintFlags() {
		return mPaint.getFlags();
	}
	
	public void setTextColor(int color) {
		if (mPaint.getColor() != (0xff000000 | color)) {
			mPaint.setColor(0xff000000 | color);
			invalidate();
		}
	}
	
	public int getTextSize() {
		return (int) mPaint.getTextSize();
	}
	
	public void setTextSize(float dim, int size) {		
		if (mPaint.getTextSize() != size) {			
			mPaint.setTextSize(size);		
			requestLayout();
			invalidate();
		}
	}
	
	public void setTextSize(int size) {		
		if (mPaint.getTextSize() != TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP
                , size, getContext().getResources().getDisplayMetrics())) {
			mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP
                    , size, getContext().getResources().getDisplayMetrics()));
			requestLayout();
			invalidate();
		}
	}
	
	public void setGravity(int gravity) {
		if (gravity != mGravity) {
			mGravity = gravity;
			invalidate();
		}
	}
	
	public void setTypeface(Typeface tpf) {
		mPaint.setTypeface(tpf);
		requestLayout();
		invalidate();
	}
	
	public void setPattern(String pat) {
		mPattern = pat;
		requestLayout();
        invalidate(); 
	}
	
	public void setTextScaleX(float scale) {
		mPaint.setTextScaleX(scale);
		requestLayout();
		invalidate();
	}

    /**
     * getAttrsInXml
     *
     * @param context
     * @param attrs
     */
    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray typed_array = context.obtainStyledAttributes(attrs, R.styleable.StatusBarTextView);
        textSize = typed_array.getDimensionPixelSize(R.styleable.StatusBarTextView_textSize, 35);
    }
	
}
