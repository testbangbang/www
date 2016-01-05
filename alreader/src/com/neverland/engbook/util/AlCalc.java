package com.neverland.engbook.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.PorterDuff;

import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.forpublic.EngBookMyType;

public class AlCalc {
	
	private Canvas 	canvas = null;
	
	private Rect 	rc = new Rect();
	
	private Paint 	linePaint = new Paint();
	private Paint 	imagePaint = new Paint();
	private char[] OneCharArray = new char[1];
	
	private float[] textWidths = new float[EngBookMyType.AL_WORD_LEN];

	public void init(AlEngineOptions opt ) {		
		
	}
	
	public void beginMain(Canvas c, int colorBackground) {
		canvas = c;
		if (canvas != null) {
			canvas.drawColor(colorBackground & 0x00ffffff, PorterDuff.Mode.CLEAR );		
		}
	}

	public void endMain() {
		canvas = null;
	}
	
	
	public void drawBackground(int width, int height, int color, AlBitmap image) {
		if (canvas == null)
			return;

		drawRect(0, 0, width, height, color | 0xff000000);
		
		if (image != null) {
			int x = 0, y;
			while (x < width) {
				y = 0;
				while (y < height) {
					drawImage(x, y, image.width, image.height, image, 0x00000000);
					y += image.height;
				}
				x += image.width;
			}
		}		
	}
	
	public void drawImage(int x, int y, int width, int height, AlBitmap image, int fillColor) {
		if (canvas == null)
			return;
		
		rc.left = x;
		rc.top = y;		
		rc.right = x + width;
		rc.bottom = y + height;
		
		if ((fillColor & 0xff000000) != 0x00) {
			linePaint.setColor(fillColor);
			canvas.drawRect(rc, linePaint);
		}
		
		canvas.drawBitmap(image.bmp, null, rc, imagePaint);		
	}
	
	public void drawLine(int x0, int y0, int x1, int y1, int width, int color) {
		if (canvas == null)
			return;
		
		linePaint.setColor(color | 0xff000000);
		canvas.drawLine(x0, y0, x1, y1, linePaint);
	}

	public void drawRect(int x0, int y0, int x1, int y1, int color) {
		if (canvas == null)
			return;
		
		rc.top = y0;
		rc.left = x0;
		rc.right = x1;
		rc.bottom = y1;
		
		linePaint.setColor(color | 0xff000000);
		canvas.drawRect(rc, linePaint);		
	}
	
	
	
	public void drawText(int x, int y, char text, AlPaintFont fontParam) {
		if (canvas == null)
			return;
		OneCharArray[0] = text;
		canvas.drawText(OneCharArray, 0, 1, x, y, fontParam.fnt);
	}
	
	public void drawText(int x, int y, char[] text, int start,  int count, AlPaintFont fontParam) {
		if (canvas == null)
			return;
		
		canvas.drawText(text, start, count, x, y, fontParam.fnt);
	}
	
	public void getTextWidths(AlPaintFont fparam, 
		char[] str, int start_text, int count, int[] widths, int start_width, boolean modeLight) {
		
		if (modeLight) {
			widths[start_width] = (int) fparam.fnt.measureText(str, start_text, count);
			for (int i = 1; i < count; i++)
				widths[start_width + i] = 0;			
		} else {		
			fparam.fnt.getTextWidths(str, start_text, count, textWidths);
			for (int i = 0; i < count; i++)
				widths[start_width + i] = (int) textWidths[i];
		}
	}
	
	
}
