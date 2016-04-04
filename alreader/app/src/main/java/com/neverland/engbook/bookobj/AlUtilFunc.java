package com.neverland.engbook.bookobj;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.neverland.engbook.forpublic.AlBitmap;

public class AlUtilFunc {

	/**
	 *
	 * @param r
	 * @param res
	 * @return
	 */
	public static AlBitmap loadImageFromResources(Resources r, int res) {
		Bitmap bmp = BitmapFactory.decodeResource(r, res);

		AlBitmap b = null;
		if (bmp != null) {
			b = new AlBitmap();
			b.bmp = bmp;
			b.width = bmp.getWidth();
			b.height = bmp.getHeight();
		}
		return b;
	}

	/**
	 *
	 * @param b
	 */
	public static void freeImage(AlBitmap b) {
		if (b != null) {
			if (b.bmp != null) {
				b.bmp.recycle();
				b.bmp = null;
				System.gc();				
			}
			b.bmp = null;
		}
	}

}
