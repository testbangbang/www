package com.neverland.engbook.util;

public class AlOneImage {
	public static final int IMG_UNKNOWN = 		 0x00;
	/*public static final int IMG_TEST = 			 0x0000;
	public static final int IMG_JPG =  			 0x0001;
	public static final int IMG_BMP =  			 0x0002;
	public static final int IMG_PNG =  			 0x0003;
	public static final int IMG_GIF =  			 0x0004;
	public static final int IMG_SVG =  			 0x0005;
	public static final int IMG_TIF =  			 0x0006;
	public static final int IMG_EMF =  			 0x00df;
	public static final int IMG_WMF =  			 0x00ef;
	public static final int IMG_MASKTYPE =		 0x00ff;*/
		//...
	public static final int IMG_BASE64 =  		 0x01;
	public static final int IMG_MEMO =			 0x02;
	public static final int IMG_HEX =			 0x04;
	public static final int IMG_HTMLHEX =		 0x08;
	public static final int IMG_BINARYINFILE =	 0x10;
		
	public static final int  NOT_EXTERNAL_IMAGE = 0xff;
	
	public String				name = null;
	public int					positionS = 0;
	public int					positionE = 0;	
	public int					iType = IMG_UNKNOWN;
	public boolean				needScan = true;
	public int					width = -1;
	public int					height = -1;
	
	public byte[]				data = null;
	public Object				otherRender = null;

	public boolean				lowQuality = false;

	public long					tm = 0;

	/*public static String getExtension(AlOneImage a) {
		switch (a.iType & 0x0f) {
		case IMG_JPG: return ".jpg";
		case IMG_BMP: return ".bmp";
		case IMG_PNG: return ".png";
		case IMG_GIF: return ".gif";
		case IMG_SVG: return ".svg";
		}
		return ".xxx";
	}*/

	public static AlOneImage add(String name, int posS, int posE, int iT) {
		AlOneImage a = new AlOneImage();
		a.name = name;
		a.positionS = posS;
		a.positionE = posE;
		a.iType = iT;
		a.needScan = true;
		a.lowQuality = false;
		return a;
	}

	public static AlOneImage addLowQuality(String name, int posS, int posE, int iT) {
		AlOneImage a = new AlOneImage();
		a.name = name;
		a.positionS = posS;
		a.positionE = posE;
		a.iType = iT;
		a.needScan = true;
		a.lowQuality = true;
		return a;
	}


	@Override
	public String toString() {
		return name + '/' + positionS + '/' + positionE + '/' + iType;
	}
}
