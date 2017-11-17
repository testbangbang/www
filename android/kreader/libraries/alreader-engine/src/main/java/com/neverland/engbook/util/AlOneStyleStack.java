package com.neverland.engbook.util;

public class AlOneStyleStack {
	public int					cp;
	public int					tag;
	public int					inCounter;

	public long					paragraph;
	public long					prop;
	public float				fontSize0;

	public char					listPrefix;

	public AlOneXMLAttrClass	cls = new AlOneXMLAttrClass();

	public AlOneStyleStack() {
		tag = 0x00;
		cp = 0;
		inCounter = 0;
		paragraph = prop = 0x00L;
		fontSize0 = 100;
		cls.clear();
	}

	public void format() {

	}

	public void copyTo(AlOneStyleStack dst) {
		dst.cp = cp;
		dst.tag = tag;
		dst.inCounter = inCounter;
		dst.paragraph = paragraph;
		dst.prop = prop;
		dst.fontSize0 = fontSize0;
		dst.listPrefix = listPrefix;
	}

	public void copyFrom(AlOneStyleStack src) {
		cp = src.cp;
		tag = src.tag;
		inCounter = src.inCounter;
		paragraph = src.paragraph;
		prop = src.prop;
		fontSize0 = src.fontSize0;
		listPrefix = src.listPrefix;
	}
}
