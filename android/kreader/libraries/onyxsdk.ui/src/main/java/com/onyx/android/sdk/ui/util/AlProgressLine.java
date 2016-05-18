package com.onyx.android.sdk.ui.util;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;


public class AlProgressLine extends View {
	private Paint  linePaint = new Paint(); 

	private boolean mode = true; 
	
	private int battery_value = 100;
	private boolean battery_charge = false;
	private long progress_value = 0;
	private int sizeBook = 0;
    private boolean isShowBatteryGraphic=false;
    private Context mContext;
	
	public AlProgressLine(Context context) {
		super(context);
		linePaint.setTextSize(42);
		setPadding(0, 0, 0, 0);
		linePaint.setAntiAlias(true);
        mContext=context;
    }

    public AlProgressLine(Context context, AttributeSet set) {
        super(context, set);
        setPadding(0, 0, 0, 0);
        linePaint.setAntiAlias(true);
        mContext=context;
    }
	
	public void setMode0(boolean val) {
		mode = val;
	}
	
	private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
        	result = (int) ((mode ? 12 : 7 + getPaddingBottom()));
            if (specMode == MeasureSpec.AT_MOST) {            
                result = Math.min(result, specSize);
            }
        }
        return result;
    } 
	
	private int measureWidth(int measureSpec) {
		return MeasureSpec.getSize(measureSpec);
    } 
	
	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec)); 
	}
	
	public void setBattery(int new_value, boolean charge) {
		if (new_value != battery_value && new_value >= 0 && new_value <= 100) {
			battery_value = new_value;
			battery_charge = charge;
			invalidate();
		}
	}
	
	public void resetProgress() {
		progress_value += progress_value > 0 ? -1 : 1;
	}
	
	public void setProgress(int current, int max) {
		if (max > 0 && current <= max && current >= 0) {			
			long new_value = (long)current * 2000L / max;
			if (new_value != progress_value || max != sizeBook) {
				progress_value = new_value;
				sizeBook = max;
				invalidate();				
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int screen_width = this.getWidth();
		int screen_height = this.getHeight();
		
		if (screen_width < 51)
			return;
			
		boolean modeProgess = true;
		linePaint.setColor(Color.BLACK);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        boolean useCustomValue=true;
        int lineH = useCustomValue?((int)(metrics.density)):(int)metrics.density << 1;
        int dividerRadius=useCustomValue?(int)(4*metrics.density):1 + (int) (1 + 0.5f);
        int powerPointRadius=useCustomValue?(int)(4*metrics.density):(int)(2 + 1 + 2 * metrics.density);
		int base_line = mode ? (screen_height - 1) : (screen_height >> 1), 
			content_lineDN = mode ? (0) : (screen_height >> 2) * 3 + 2,
			content_lineUP = mode ? (base_line) : (screen_height >> 2) - 1;
			
		if (true) {
			base_line =dividerRadius;
		}

		int pp, i;
		if (modeProgess) {
			if (false) {
				int prevX = -2, cnt = 10, currX;
				float d = sizeBook / screen_width;
				
				linePaint.setAntiAlias(false);
				linePaint.setStrokeWidth(0);
				
				for (i = 0; i < cnt; i++) {
					currX = (int) (10 / d);
					if (currX > prevX) {
						canvas.drawLine(currX, content_lineDN, currX, content_lineUP, linePaint);
						prevX = currX + 1;
					}
				}
				linePaint.setAntiAlias(true);
			}
		} else {
			//if (!mode)
			//	base_line = content_lineDN - 1;
		}
		//TODO:Progress Line Stroke Width
		linePaint.setStrokeWidth(lineH);
		canvas.drawLine(0, base_line, screen_width, base_line, linePaint);
		
		
		//canvas.drawLine(0, 0, screen_width, 0, linePaint);
		//canvas.drawLine(0, screen_height - 1, screen_width - 1, screen_height - 1, linePaint);
		//TODO:Progress Indicator Stroke Width
		linePaint.setColor(Color.BLACK);
		linePaint.setStrokeWidth(3*lineH);
		//linePaint.setStrokeWidth(lineH - 1);
		int tmp = (int) (progress_value * screen_width / 2000L);
		if (tmp < 2) tmp = 2;
//		canvas.drawLine(0, base_line - (mode ? 1 : 0), tmp , base_line - (mode ? 1 : 0), linePaint);
        canvas.drawLine(0, base_line,tmp,  base_line, linePaint);
        {
            //TODO:Divider Indicator Radius
			linePaint.setColor(Color.BLACK);
			for (i = 1; i < 5; i++) {
				pp = screen_width * i / 5;
				canvas.drawCircle(pp, base_line,
                        dividerRadius,
						linePaint);
			}
		}
            //TODO:PowerPoint Indicator Radius
		if (isShowBatteryGraphic) {
//			int addSize = 1;
			if ((battery_value < 20) && (!battery_charge)) {
				if (true) {
					linePaint.setColor(0xff808080);
//					addSize = 1;
				} else {
					linePaint.setColor(0xffff0000);
				}
			} else {
				linePaint.setColor(Color.BLACK);
			}
			pp = screen_width * battery_value / 100;
			canvas.drawCircle(pp, base_line,powerPointRadius, linePaint);
		}
	}

	
	private boolean doit = true;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
        /*
		int cnt_point = APIWrap.getPointerCount(event);
		if (cnt_point > 1)
			doit = true;
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_CANCEL:
				doit = true;	
				break;
			case MotionEvent.ACTION_DOWN:
				doit = false;
				if (AlApp.lock_touch)
					doit = true;
				break;
			case MotionEvent.ACTION_MOVE:
				
				break;
			case MotionEvent.ACTION_UP:
				if (doit)
					break;
				
				doit = true;
				if (AlApp.lock_touch)
					return true;
				
				doit = true;
				parent.onCustomCommand(PrefManager.getInt(R.string.keytap_status), ActionCommand.MODE_DEFAULT);
				break;
		}	
        */
		return true;
	}

    public void setShowBatteryGraphic(boolean isShowBatteryGraphic) {
        this.isShowBatteryGraphic = isShowBatteryGraphic;
    }
}
