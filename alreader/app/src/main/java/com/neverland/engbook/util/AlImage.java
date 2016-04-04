package com.neverland.engbook.util;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level2.AlFormat;

public class AlImage {

	private int			storeIndex = 0;
	private final String[]		storeImage = {null, null};
	private final AlBitmap[]	storeBitmap = {new AlBitmap(), new AlBitmap()};
	private final int[]			storeScale = {0, 0};
	
	@Override
	protected void finalize() throws Throwable {
		System.gc();
		super.finalize();
	}
	
	public void init(AlEngineOptions opt ) {			
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;//ARGB_4444;//ARGB_8888;
		opts.inDither = true;
		opts.inInputShareable = false;
		opts.inPurgeable = true;
		opts.inPreferQualityOverSpeed = false;
	}
	
	public void initWork(AlOneImage ai, AlFormat format) {
		open(ai, format);
		ai.needScan = false;
	}
	
	private final BitmapFactory.Options opts = new BitmapFactory.Options();

	
	public AlBitmap getImage(AlOneImage ai, int scale) {
		if (storeImage[storeIndex] != null &&
				storeImage[storeIndex].contentEquals(ai.name) &&
				storeScale[storeIndex] <= scale) {
			return storeBitmap[storeIndex];
		}
		
		storeIndex = 1 - storeIndex;
		
		if (storeImage[storeIndex] != null &&
				storeImage[storeIndex].contentEquals(ai.name) &&
				storeScale[storeIndex] <= scale) {
			return storeBitmap[storeIndex];
		}
		
		boolean usePrevBitmap = false;
		
		if (storeBitmap[storeIndex].bmp != null) {
			
			if (storeBitmap[storeIndex].height == ai.height && 
				storeBitmap[storeIndex].width == ai.width &&
				storeScale[storeIndex] == scale && scale == 0)
				usePrevBitmap = true;
			
			if (!usePrevBitmap) {
				storeBitmap[storeIndex].bmp.recycle();
				storeBitmap[storeIndex].bmp = null;
				System.gc();
			}
		}
		
		storeImage[storeIndex] = null;
				
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = (scale != 0) ? 1 << scale : 1;
		opts.inBitmap = usePrevBitmap ? storeBitmap[storeIndex].bmp : null;
		
		if (ai.data != null) {
			storeBitmap[storeIndex].bmp = BitmapFactory.decodeByteArray(ai.data, 0, ai.data.length, opts);
			storeBitmap[storeIndex].height = opts.outHeight; 
			storeBitmap[storeIndex].width = opts.outWidth;
			storeImage[storeIndex] = ai.name;
			storeScale[storeIndex] = scale;
		}
		
		if (storeBitmap[storeIndex].bmp != null)					
			return storeBitmap[storeIndex];
		
		return null;
	}
	
	public boolean scanImage(AlOneImage ai) {
		
		opts.inJustDecodeBounds = true;
		opts.inSampleSize = 1;
		opts.inBitmap = null;
		
		if (ai.data != null) {
			BitmapFactory.decodeByteArray(ai.data, 0, ai.data.length, opts);
			if (opts.outHeight != -1 && opts.outWidth != -1) {
				ai.height = opts.outHeight;
				ai.width = opts.outWidth;
				if (opts.outMimeType.contentEquals(""))
				return true;
			}
		}
		
		return false;
	}
	
	public void resetStoredImages() {
		if (storeBitmap[0].bmp != null || storeBitmap[1].bmp != null) {
			
			if (storeBitmap[0].bmp != null)
				storeBitmap[0].bmp.recycle();
			storeBitmap[0].bmp = null;
			if (storeBitmap[1].bmp != null)
				storeBitmap[1].bmp.recycle();			
			storeBitmap[1].bmp = null;

			System.gc();
		}
		storeImage[0] = storeImage[1] = null;
	}
	
	private boolean open(AlOneImage ai, AlFormat format) {
		switch (ai.iType) {
		case AlOneImage.IMG_BASE64:
			return openBase64(ai, format);
		case AlOneImage.IMG_MEMO:
			return openMemo(ai, format);
		case AlOneImage.IMG_HEX:
			return openHex(ai, format);
		}
		return false;
	}
	
	private boolean openBase64(AlOneImage ai, AlFormat format) {
		
		int pos = 0;
		int size = ((ai.positionE - ai.positionS + 8) * 3) / 4;
		ai.data = new byte[size];
		for (int i = 0; i < size; i++)
			ai.data[i] = 0x00;
		
		int tmp_size = ai.positionE - ai.positionS + 4;
		byte[] tmp = new byte[tmp_size];
		for (int i = 0; i < tmp_size; i++)
			tmp[i] = 0x00;
		
		format.aFiles.getByteBuffer(ai.positionS, tmp, ai.positionE - ai.positionS);
		
		int shift = 18;
		int reg = 0;
		char ch;
		for (int i = 0; i < tmp_size; i++) {
			ch = (char) tmp[i];

			if (ch >= 0x80)
				continue;

			ch = base64Decode[ch];
			if (ch == 0xff)
				continue;
			
			reg |= (((int)ch) << shift);

			if (shift == 0) {
				ai.data[pos++] = (byte) ((reg >> 16) & 0xff);
				ai.data[pos++] = (byte) ((reg >> 8) & 0xff);
				ai.data[pos++] = (byte) (reg & 0xff);
				shift = 24;
				reg = 0;
			}

			shift -= 6;
		}
		
		return false;
	}
	
	private boolean openMemo(AlOneImage ai, AlFormat format) {
		int num = format.aFiles.getExternalFileNum(ai.name);
		if (num != AlFiles.LEVEL1_FILE_NOT_FOUND) {
			int size = format.aFiles.getExternalFileSize(num);
			if (size > 0) {
				ai.data = new byte[size];
				if (format.aFiles.fillBufFromExternalFile(num, 0, ai.data, 0, size)) {
					return true;
				} else {
					ai.data = null;
				}
			}
		}
		return false;
	}

	private boolean openHex(AlOneImage ai, AlFormat format) {
		return false;
	}
	
	private static final char[] base64Decode = {
		0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, //0x00
		0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, //0x08
		0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, //0x10
		0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, //0x18
		0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, //0x20
		0xff, 0xff, 0xff, 0x3e, 0xff, 0xff, 0xff, 0x3f, //0x28
		0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b, //0x30
		0x3c, 0x3d, 0xff, 0xff, 0xfe, 0x40, 0xff, 0xff, //0x38
		0xff, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, //0x40
		0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, //0x48
		0x0f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, //0x50
		0x17, 0x18, 0x19, 0xff, 0xff, 0xff, 0xff, 0xff, //0x58
		0xff, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f, 0x20, //0x60
		0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, //0x68
		0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f, 0x30, //0x70
		0x31, 0x32, 0x33, 0xff, 0xff, 0xff, 0xff, 0xff, //0x78
	};
	
}
