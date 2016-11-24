package com.neverland.engbook.util;

public class AlOneStyleStack {
	public long real_style = 0;
	public int  tag = 0;

	public static AlOneStyleStack addStyleStack(long style, int tag) {
		AlOneStyleStack a = new AlOneStyleStack();
		a.real_style = style;
		a.tag = tag;
		return a;
	}

}
