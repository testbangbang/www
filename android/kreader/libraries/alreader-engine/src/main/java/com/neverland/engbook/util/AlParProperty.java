package com.neverland.engbook.util;

public class AlParProperty {
    public static final long 		    SL2_MARG_MAX_VALUE =		90L;

	public static final long 			SL2_MARGL_MASK =			0x00000000000000ffL;
	public static final long 			SL2_MARGL_SHIFT = 0L;

	public static final long 			SL2_MARGR_MASK =			0x000000000000ff00L;
	public static final long 			SL2_MARGR_SHIFT =			8L;

	public static final long 			SL2_MARGT_MASK =			0x0000000000ff0000L;
	public static final long 			SL2_MARGT_SHIFT =			16L;

	public static final long 			SL2_MARGB_MASK =			0x00000000ff000000L;
	public static final long 			SL2_MARGB_SHIFT =			24L;

	public static final long 			SL2_MARG_MASK_WIDTH = SL2_MARGL_MASK | SL2_MARGR_MASK;

	public static final long			SL2_INDENT_MAX_VALUE =		15L;
	public static final long			SL2_INDENT_MASK =			0x000000ff00000000L;
	public static final long			SL2_INDENT_SHIFT =			32L;
	public static final long			SL2_INDENT_DEFAULT =		0x0000000c00000000L;

    //~!~!
    //public static final long			 =							0x0000fff000000000L;

	public static final long 			SL2_JUST_NONE =				0x0000000000000000L;
	public static final long			SL2_JUST_LEFT =				0x0001000000000000L;
	public static final long			SL2_JUST_RIGHT =			0x0002000000000000L;
	public static final long			SL2_JUST_CENTER =			0x0003000000000000L;
	public static final long			SL2_JUST_MASK =				0x0003000000000000L;
	public static final long			SL2_JUST_SHIFT =			48L;

	public static final long			SL2_INTER_TEXT1 =			0x0000000000000000L;
	public static final long			SL2_INTER_100_ =			0x0004000000000000L;
	public static final long			SL2_INTER_NOTES =			0x0008000000000000L;
	public static final long			SL2_INTER_FONT =			0x000c000000000000L;
	public static final long			SL2_INTER_MASK =			0x000c000000000000L;
	public static final long			SL2_INTER_SHIFT =			50L;

	public static final long 			SL2_EMPTY_BEFORE =			0x0010000000000000L;
	public static final long			SL2_BREAK_BEFORE =			0x0020000000000000L;
	public static final long 			SL2_BREAK_BEFORE_ALWAYS =	0x0040000000000000L;
	public static final long 			SL2_JUSTIFY_POEM =			0x0080000000000000L;

	public static final long			SL2_UL_BASE =				0x0f00000000000000L;
	public static final long			SL2_UL_MASK =				0x0f00000000000000L;
	public static final long			SL2_UL_SHIFT =				56L;

	public static final long 			SL2_EMPTY_AFTER =			0x1000000000000000L;
	public static final long			SL2_BREAK_AFTER =			0x2000000000000000L;


	public static final long 			SL2_SHIFT_FOR_AFTER = 8L; // for SL2_EMPTY_AFTER SL2_BREAK_AFTER SL2_MARGB_MASK

    //~!~!
    //public static final long			 =							0xc000000000000000L;

	public static final long 			DEFALULT_TABLE =			SL2_INTER_NOTES;
	public static final long 			DEFALULT_NOTE =				SL2_INTER_NOTES;

}
