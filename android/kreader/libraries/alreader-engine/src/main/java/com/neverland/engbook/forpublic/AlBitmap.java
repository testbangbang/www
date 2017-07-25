package com.neverland.engbook.forpublic;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * структура внутреннего хранения изображений в библиотеке
 поля структуры не изменяются основным приложением.
 */
public class AlBitmap {
	public int					shtamp = -1;
	public int					width = -1;
	public int					height = -1;
	public int					position = -100;

	public int					freeSpaceAfterPage = 0;

	public Bitmap				bmp = null;
	public Canvas				canvas = null;

	public void release() {
		if (canvas != null) {
			canvas.setBitmap(null);
		}
	}
}
