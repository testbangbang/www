package com.neverland.engbook.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.PorterDuff;

import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.forpublic.AlPublicProfileOptions;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_SCREEN_PAGES_COUNT;
import com.neverland.engbook.level1.AlRandomAccessFile;
import com.neverland.engbook.unicode.AlUnicode;

import java.io.UnsupportedEncodingException;

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
	
	
	public void drawBackground(int width, int height, int color, AlBitmap image, int mode) {
		if (canvas == null)
			return;

        drawRect(0, 0, width, height, color | 0xff000000);
		//drawRect(0, 0, width, height, 0xffff0000);
		//drawRect(10, 1, width - 1, height - 1, color | 0xff000000);
		
		if (image != null) {
			int x = 0, y = 0;
			switch (mode) {
                case AlPublicProfileOptions.BACK_TILE_X | AlPublicProfileOptions.BACK_TILE_Y:
                    while (x < width) {
                        y = 0;
                        while (y < height) {
                            drawImage(x, y, image.width, image.height, image, true);
                            y += image.height;
                        }
                        x += image.width;
                    }
				    break;
				case AlPublicProfileOptions.BACK_TILE_X:
                    while (x < width) {
                        drawImage(x, 0, image.width, height, image, true);
                        x += image.width;
                    }
                    break;
				case AlPublicProfileOptions.BACK_TILE_Y:
                    while (y < height) {
                        drawImage(0, y, width, image.height, image, true);
                        y += image.height;
                    }
                    break;
				default:
                    drawImage(0, 0, width, height, image, true);
					break;
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

		linePaint.setColor((color & 0xff000000) == 0 ? color | 0xff000000 : color);
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

    private final char[] arrArabic = new char[EngBookMyType.AL_WORD_LEN + 2];
	public final void getTextWidthsArabic(AlPaintFont fparam,
		char[] str, int startPoint, int count, int[] widths, boolean modeLight) {

        int i, j, start = 0, end = 0;
        boolean stateArabic = false;
        for (i = 0; i < count; i++) {
            if (stateArabic) {
                if (!AlUnicode.isArabic(str[i + startPoint])) {

					final int t = i - start;
					System.arraycopy(str, start + startPoint, arrArabic, 0, t);
                    arrArabic[t] = 0x00;

					widths[start + startPoint] = (int) fparam.fnt.measureText(arrArabic, 0, t);
                    for (j = start + 1; j < i; j++)
                        widths[j + startPoint] = 0;

                    stateArabic = false;
                    start = i;
                }
            } else {
                if (AlUnicode.isArabic(str[i + startPoint])) {

                    if (i != start)
                        getTextWidths(fparam, str, start + startPoint, i - start, widths, modeLight);

                    stateArabic = true;
                    start = i;
                }
            }

            end = i;
        }

        if (end >= start) {
            if (stateArabic) {

                final int t = end - start + 1;
                System.arraycopy(str, start + startPoint, arrArabic, 0, t);
                arrArabic[t] = 0x00;

                widths[start + startPoint] = (int) fparam.fnt.measureText(arrArabic, 0, t);
                for (j = start + 1; j <= end; j++)
                    widths[j + startPoint] = 0;
            } else {
                getTextWidths(fparam, str, start + startPoint, end - start + 1, widths, modeLight);
            }
        }

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
