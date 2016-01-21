package com.neverland.engbook.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.PorterDuff;

import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_SCREEN_PAGES_COUNT;

public class AlCalc {
	
	private Canvas 	canvas = null;
	
	public static final char UNKNOWNWIDTH = 0xffff;
	
	public final char[] mainWidth = new char[0x10000]; 
	
	private final Rect 	rc = new Rect();
	
	private final Paint 	linePaint = new Paint();
	private final Paint 	imagePaint = new Paint(Paint.FILTER_BITMAP_FLAG);

	private final char[] 	oneCharArray = new char[1];
	
	private final float[] textWidths = new float[EngBookMyType.AL_WORD_LEN];
	private int multiplexer = 1;
	
	private boolean isNeedQuickWidth = false;
	
	public void init(AlEngineOptions opt ) {		
		
		isNeedQuickWidth = opt.useScreenPages == TAL_SCREEN_PAGES_COUNT.SCREEN;
		
		switch (opt.DPI) {
		case TAL_SCREEN_DPI_320:
			multiplexer = 2;
			break;
		case TAL_SCREEN_DPI_480:
			multiplexer = 3;
			break;
		case TAL_SCREEN_DPI_640:
			multiplexer = 4;
			break;
		default:
			break;
		}
		
		linePaint.setDither(false);
		linePaint.setAntiAlias(false);
		linePaint.setStrokeWidth(multiplexer);
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
					drawImage(x, y, image.width, image.height, image, true);
					y += image.height;
				}
				x += image.width;
			}
		}		
	}
	
	public void drawImage(int x, int y, int width, int height, AlBitmap image, boolean transparentEnable) {
		if (canvas == null)
			return;
		
		rc.left = x;
		rc.top = y;		
		rc.right = x + width;
		rc.bottom = y + height;
		
		if (!transparentEnable) {
			linePaint.setColor(0xffffffff);
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
		oneCharArray[0] = text;
		canvas.drawText(oneCharArray, 0, 1, x, y, fontParam.fnt);
	}
	
	public void drawText(int x, int y, char[] text, int start,  int count, AlPaintFont fontParam) {
		if (canvas == null)
			return;
		
		canvas.drawText(text, start, count, x, y, fontParam.fnt);
	}
	
	public void clearMainWidth() {
		for (int i = 0; i <= 0xffff; i++)
			mainWidth[i] = UNKNOWNWIDTH;
	}
		
	public final int getOneMainTextCharWidth(AlPaintFont fparam, char ch) {
		oneCharArray[0] = ch;
		fparam.fnt.getTextWidths(oneCharArray, 0, 1, textWidths);
		mainWidth[ch] = (char) textWidths[0];
		return (int) textWidths[0];
	}
	
	public final void getTextWidths(AlPaintFont fparam, 
		char[] text, int start, int count, int[] widths, boolean modeLight) {
		
		if (isNeedQuickWidth && modeLight && fparam.style == 0) {
			char ch;
			for (int i = 0; i < count; i++) {
				ch = text[start + i];
				if (mainWidth[ch] == UNKNOWNWIDTH) {
					oneCharArray[0] = ch;
					fparam.fnt.getTextWidths(oneCharArray, 0, 1, textWidths);
					mainWidth[ch] = (char) textWidths[0];
				}
				widths[start + i] = mainWidth[ch];
			}
		} else
		if (modeLight) {
			widths[start] = (int) fparam.fnt.measureText(text, start, count);
			for (int i = 1; i < count; i++)
				widths[start + i] = 0;			
		} else {		
			fparam.fnt.getTextWidths(text, start, count, textWidths);
			for (int i = 0; i < count; i++)
				widths[start + i] = (int) textWidths[i];
		}

	}
	
}
