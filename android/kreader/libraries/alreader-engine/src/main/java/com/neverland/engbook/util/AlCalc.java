package com.neverland.engbook.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.PorterDuff;
import android.util.Log;

import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.forpublic.AlPublicProfileOptions;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.unicode.AlUnicode;

public class AlCalc {
	
	private Canvas 	canvas = null;
	
	public static final char UNKNOWNWIDTH = 0xffff;
	
	public final char[] mainWidth = new char[0x10000]; 
	
	private final Rect 	rc = new Rect();

	public  final Paint 	fontPaint = new Paint();
	private final Paint 	linePaint = new Paint();
	private final Paint 	imagePaint = new Paint(Paint.FILTER_BITMAP_FLAG);

	private final char[] 	oneCharArray = new char[1];
	
	private final float[] textWidths = new float[EngBookMyType.AL_WORD_LEN];
	private float multiplexer = 1;
	
	private boolean isNeedQuickWidth = false;
	private AlPaintFont fparam;
	
	public void init(AlEngineOptions opt, AlPaintFont fontparam ) {

		fparam = fontparam;
		isNeedQuickWidth = true;//opt.useScreenPages == TAL_SCREEN_PAGES_COUNT.SCREEN;
		
		multiplexer = opt.multiplexer;

		linePaint.setDither(false);
		linePaint.setAntiAlias(false);
		linePaint.setStrokeWidth(multiplexer);
	}
	
	public void beginMain(AlBitmap b, int colorBackground) {
		canvas = b.canvas;
		if (canvas != null) {
			canvas.drawColor(colorBackground & 0x00ffffff, PorterDuff.Mode.CLEAR );
            //canvas.drawColor(colorBackground & 0x00ffffff );
		}
	}

	public void endMain() {
		canvas = null;
	}

	public void setViewPort(int left, int top, int width, int height) {
		if (canvas == null)
			return;

        if (left == -100) {
            canvas.restore();
            return;
        }

        canvas.save();

		rc.top = top;
		rc.left = left;
		rc.right = top + width;
		rc.bottom = left + height;

		boolean r = canvas.clipRect(rc);
        Log.e("calc clip ", Boolean.toString(r));
    }
	
	public void drawBackground(int width, int height, int color, AlBitmap image, int mode) {
		if (canvas == null)
			return;

        if ((mode & AlPublicProfileOptions.BACK_RESERVED) != 0) {
            rc.top = 0;
            rc.left = 0;
            rc.right = width;
            rc.bottom = height;
            linePaint.setColor(color);
            canvas.drawRect(rc, linePaint);
        } else {
            drawRect(0, 0, width, height, color | 0xff000000);
        }

		if (image != null) {
			int x = 0, y = 0;
			switch (mode & ~AlPublicProfileOptions.BACK_RESERVED) {
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

		linePaint.setColor(((color & 0xff000000) == 0) ? (color | 0xff000000) : color);
		canvas.drawRect(rc, linePaint);		
	}
	
	public void drawText(int x, int y, char text) {
		if (fparam.unvisible_text)
			return;
		if (canvas == null)
			return;
		oneCharArray[0] = text;
		canvas.drawText(oneCharArray, 0, 1, x, y, fontPaint);
	}
	
	public void drawText(int x, int y, char[] text, int start, int count) {
		if (fparam.unvisible_text)
			return;
		if (canvas == null)
			return;
		
		canvas.drawText(text, start, count, x, y, fontPaint);
	}
	
	public void clearMainWidth() {
		for (int i = 0; i <= 0xffff; i++)
			mainWidth[i] = UNKNOWNWIDTH;
	}
		
	public final int getOneMainTextCharWidth(char ch) {
		oneCharArray[0] = ch;
		fontPaint.getTextWidths(oneCharArray, 0, 1, textWidths);
		mainWidth[ch] = (char) textWidths[0];
		return (int) textWidths[0];
	}

    private final char[] arrArabic = new char[EngBookMyType.AL_WORD_LEN + 2];
	public final void getTextWidthsArabic(
		char[] str, int startPoint, int count, int[] widths, boolean modeLight) {

		        int i, j, start = 0, end = 0;
        boolean stateArabic = false;
        for (i = 0; i < count; i++) {
            if (stateArabic) {
                if (!AlUnicode.isArabic(str[i + startPoint])) {

					final int t = i - start;
					System.arraycopy(str, start + startPoint, arrArabic, 0, t);
                    arrArabic[t] = 0x00;

					widths[start + startPoint] = (int) fontPaint.measureText(arrArabic, 0, t);
                    for (j = start + 1; j < i; j++)
                        widths[j + startPoint] = 0;

                    stateArabic = false;
                    start = i;
                }
            } else {
                if (AlUnicode.isArabic(str[i + startPoint])) {

                    if (i != start)
                        getTextWidths(str, start + startPoint, i - start, widths, modeLight);

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

                widths[start + startPoint] = (int) fontPaint.measureText(arrArabic, 0, t);
                for (j = start + 1; j <= end; j++)
                    widths[j + startPoint] = 0;
            } else {
                getTextWidths(str, start + startPoint, end - start + 1, widths, modeLight);
            }
        }

	}

	public final void getTextWidths(
		char[] text, int start, int count, int[] widths, boolean modeLight) {

		if (isNeedQuickWidth && modeLight && fparam.style == 0) {
			char ch;
			for (int i = 0; i < count; i++) {
				ch = text[start + i];
				if (mainWidth[ch] == UNKNOWNWIDTH) {
					oneCharArray[0] = ch;
					fontPaint.getTextWidths(oneCharArray, 0, 1, textWidths);
					mainWidth[ch] = (char) textWidths[0];
				}
				widths[start + i] = mainWidth[ch];
			}
		} else
		if (modeLight) {
			widths[start] = (int) fontPaint.measureText(text, start, count);
			for (int i = 1; i < count; i++)
				widths[start + i] = 0;			
		} else {		
			fontPaint.getTextWidths(text, start, count, textWidths);
			for (int i = 0; i < count; i++)
				widths[start + i] = (int) textWidths[i];
		}

	}
	
}
