package com.neverland.engbook.util;

public class AlParProperty {
    public static final long 		    SL2_MARG_MAX_VALUE =		90L;
	public static final long 		    SL2_MARG_MIN_VALUE =		-38;

	public static final long			SL2_MARGL_PERCENT_MASK =	0x000000000000007fL;
	public static final long			SL2_MARGL_PERCENT_SHIFT =	0L;

	public static final long			SL2_MARGR_PERCENT_MASK =	0x0000000000003f80L;
	public static final long			SL2_MARGR_PERCENT_SHIFT =	7L;

	public static final long			SL2_MARGT_MASK =			0x00000000001fc000L;
	public static final long			SL2_MARGT_SHIFT =			14L;

	public static final long			SL2_MARGB_MASK =			0x000000000fe00000L;
	public static final long			SL2_MARGB_SHIFT =			21L;

	public static final long			SL2_MARGL_EM_MASK =			0x00000007f0000000L;
	public static final long			SL2_MARGL_EM_SHIFT =		28L;

	public static final long			SL2_MARGR_EM_MASK =			0x000003f800000000L;
	public static final long			SL2_MARGR_EM_SHIFT =		35L;

	public static final long			SL2_MARG_MASK_WIDTH =		SL2_MARGL_PERCENT_MASK | SL2_MARGR_PERCENT_MASK | SL2_MARGL_EM_MASK | SL2_MARGR_EM_MASK;

	public static final long			SL2_INDENT_MASK =			0x0003fc0000000000L;
	public static final long			SL2_INDENT_SHIFT =			42L;
	public static final long			SL2_INDENT_EM =				0x0002000000000000L;
	public static final long			SL2_INDENT_DEFAULT =		SL2_INDENT_EM | 0x0000200000000000L;

	public static final long 			SL2_JUST_NONE =				0x0000000000000000L;
	public static final long			SL2_JUST_LEFT =				0x0004000000000000L;
	public static final long			SL2_JUST_RIGHT =			0x0008000000000000L;
	public static final long			SL2_JUST_CENTER =			0x000c000000000000L;
	public static final long			SL2_JUST_MASK =				0x000c000000000000L;
	public static final long			SL2_JUST_SHIFT =			50L;

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

	public static final long			SL2_INTER_TEXT1 =			0x0000000000000000L;
	public static final long			SL2_INTER_100_ =			0x4000000000000000L;
	public static final long			SL2_INTER_NOTES =			0x8000000000000000L;
	public static final long			SL2_INTER_FONT =			0xc000000000000000L;
	public static final long			SL2_INTER_MASK =			0xc000000000000000L;
	public static final long			SL2_INTER_SHIFT =			62L;

	public static final long 			DEFALULT_TABLE =			SL2_INTER_NOTES;
	public static final long 			DEFALULT_NOTE =				SL2_INTER_NOTES;

}
