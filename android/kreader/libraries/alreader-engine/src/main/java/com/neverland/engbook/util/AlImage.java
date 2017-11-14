package com.neverland.engbook.util;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.PorterDuff;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level2.AlFormat;
import com.neverland.engbook.unicode.AlUnicode;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AlImage {

	private int					storeIndex = 0;
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
		ai.tm = System.currentTimeMillis();
	}

	private class gcPair {
		public gcPair(long t, int n) {
			tm = t;
			num = n;
		}
		public long tm;
		public int  num;
	}

	public void gc(ArrayList<AlOneImage> im) {
		gc(im, InternalConst.AL_IMAGE_SAVED_ACTUAL_COUNT);
	}

	public void gc(ArrayList<AlOneImage> im, int maxCount) {
		if (im == null || im.size() <= maxCount)
			return;

		long now = System.currentTimeMillis();
		ArrayList<gcPair> coll = new ArrayList<>();
		for (int i = 0; i < im.size(); i++) {
			if (im.get(i).data != null && now - im.get(i).tm > 10000) {
				gcPair p = new gcPair(im.get(i).tm, i);
				coll.add(p);
			}
		}

		if (coll.size() <= maxCount)
			return;

		Collections.sort(coll, new Comparator<gcPair>() {
			public int compare(gcPair o1, gcPair o2) {
				return o2.tm > o1.tm ? 1 : (o2.tm < o1.tm ? -1 : 0);
			}
		});

		for (int i = maxCount; i < coll.size(); i++) {
			AlOneImage m = im.get(coll.get(i).num);
			m.data = null;
			m.needScan = true;
			m.tm = 0;
		}

		System.gc();
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
				storeBitmap[storeIndex].canvas = null;
				System.gc();
			}
		}

		if (ai.otherRender != null) {

			if (ai.otherRender instanceof SVG) {
				EngBitmap.reCreateBookBitmap(storeBitmap[storeIndex], ai.width, ai.height, null);

				storeBitmap[storeIndex].canvas.drawColor(0xffffffff, PorterDuff.Mode.CLEAR );

				try {
					((SVG) ai.otherRender).getPicture().draw(storeBitmap[storeIndex].canvas);
				} catch (Exception e) {
					e.printStackTrace();
				}

				storeBitmap[storeIndex].height = ai.height;
				storeBitmap[storeIndex].width = ai.width;
				storeImage[storeIndex] = ai.name;
				storeScale[storeIndex] = scale;
			}

        } else
		{
            storeImage[storeIndex] = null;

            opts.inJustDecodeBounds = false;
            opts.inSampleSize = (scale - 1 > 0) ? 1 << (scale - 1) : 1;//(scale != 0) ? 1 << scale : 1;
            opts.inBitmap = usePrevBitmap ? storeBitmap[storeIndex].bmp : null;

			if (ai.lowQuality) {
				opts.inPreferredConfig = Bitmap.Config.RGB_565;
			} else {
				opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			}

            if (ai.data != null) {
                storeBitmap[storeIndex].bmp = BitmapFactory.decodeByteArray(ai.data, 0, ai.data.length, opts);
                storeBitmap[storeIndex].height = opts.outHeight;
                storeBitmap[storeIndex].width = opts.outWidth;
                storeImage[storeIndex] = ai.name;
                storeScale[storeIndex] = scale;
            }
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

            if (ai.data.length > 4 && ai.data[0] == '<' && ai.data[1] == '?' && ai.data[2] == 'x' && ai.data[3] == 'm' && ai.data[4] == 'l') {
				try {
					SVG svg = SVGParser.getSVGFromInputStream(new ByteArrayInputStream(ai.data));
					if (svg != null) {
						Picture pict = svg.getPicture();
						if (pict != null) {
							ai.width = pict.getWidth();
							ai.height = pict.getHeight();

							if (ai.width > 0 && ai.height > 0) {
								ai.otherRender = svg;
								return true;
							}

							ai.width = ai.height = -1;

							return false;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
            } else
			{
                BitmapFactory.decodeByteArray(ai.data, 0, ai.data.length, opts);
                if (opts.outHeight != -1 && opts.outWidth != -1) {
                    ai.height = opts.outHeight;
                    ai.width = opts.outWidth;
                    if (opts.outMimeType == null || opts.outMimeType.contentEquals(""))
                        return true;
                }
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
		case AlOneImage.IMG_BINARYINFILE:
			return openBinInFile(ai, format);
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

			if (ch == '&') {
				for (int j = i + 1; j < tmp_size; j++) {
					if (tmp[j] == ';') {
						for (int k = i + 1; k <= j; k++)
							tmp[k] = 0x00;
						break;
					} else
					if (AlUnicode.isHEXDigit((char) tmp[j]) || tmp[j] == '#') {

					} else {
						break;
					}
				}
			}

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

	private boolean  openBinInFile(AlOneImage ai, AlFormat format) {
		/*int pos = 0;*/
		ai.data = new byte [ai.positionE - ai.positionS];
		format.aFiles.getByteBuffer(ai.positionS, ai.data, ai.positionE - ai.positionS);
		return true;
	}

	private boolean openHex(AlOneImage ai, AlFormat format) {
		int pos = 0;
		int size = (ai.positionE - ai.positionS + 2) / 2;
		ai.data = new byte[size];
		for (int i = 0; i < size; i++)
			ai.data[i] = 0x00;

		int tmp_size = ai.positionE - ai.positionS + 2;
		byte[] tmp = new byte[tmp_size];
		for (int i = 0; i < tmp_size; i++)
			tmp[i] = 0x00;

		format.aFiles.getByteBuffer(ai.positionS, tmp, ai.positionE - ai.positionS);

        char ch, res = 0x00;
        int state = 0;
        for (int i = 0; i < tmp_size; i++) {
            ch = (char) tmp[i];

            switch (ch) {
                case '0': break;
                case '1': res |= 0x01; break; case '2': res |= 0x02; break; case '3': res |= 0x03; break;
                case '4': res |= 0x04; break; case '5': res |= 0x05; break; case '6': res |= 0x06; break;
                case '7': res |= 0x07; break; case '8': res |= 0x08; break; case '9': res |= 0x09; break;
                case 'a': case 'A': res |= 0x0a; break;
                case 'b': case 'B': res |= 0x0b; break;
                case 'c': case 'C': res |= 0x0c; break;
                case 'd': case 'D': res |= 0x0d; break;
                case 'e': case 'E': res |= 0x0e; break;
                case 'f': case 'F': res |= 0x0f; break;
                default: continue;
            }

            if (state == 1) {
                ai.data[pos++] = (byte) res;
                res = 0;
            } else {
                res <<= 4;
            }
            state = 1 - state;
        }

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
