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
	private final Paint 	imagePaint = new Paint();
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
	
	/*private void initMainWidth(AlPaintFont fparam) {
		float[] testWidth 	= new float[2];		
		
		AlRandomAccessFile rf = new AlRandomAccessFile();
		rf.open("/sdcard/testletter", 1);
		String ustr;
		byte[] bb = null;
		
		for (char i = 'W'; i <= 'W'; i++) {
		
				test[0] = i;
				fparam.fnt.getTextWidths(test, 0, 1, testWidth);
				mainWidth[i]  = (char) testWidth[0];
				for (char j = 0x21; j <= 0xfff0; j++) {
					
					test[1] = j;
					fparam.fnt.getTextWidths(test, 0, 2, testWidth);
												
					
					ustr = "\n\r" + (char)i + (char)j + " > " + Float.toString(testWidth[0]);
					try {
						bb = ustr.getBytes("UTF-8");
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}
					rf.write(bb);
					
				}		

		}
		
		rf.close();
		rf = null;
		
		mainWidth[0] = COMPLETE;
	}*/
	
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
