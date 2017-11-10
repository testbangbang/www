package com.neverland.engbook.util;

public final class AlStyles {
	public static final int 		CHAR_NONE = 0x0000;
	public static final int 		CHAR_LINK_S = 0x0001;
	public static final int 		CHAR_LINK_E = 0x0004;
	public static final int 		CHAR_IMAGE_S = 0x0002;
	public static final int 		CHAR_IMAGE_E = 0x0003;
	public static final int 		CHAR_SIZE = 0x0005; // reserved - not use
	public static final int 		CHAR_FONT = 0x0006; // reserved - not use
	public static final int 		CHAR_COVER = 0x0007;
	public static final int 		CHAR_ANYCHAR = 0x0008; // for find text
	public static final int 		CHAR_SOFTPAR = 0x000a;
	public static final int 		CHAR_TITLEIMG_START = 0x000b;
	public static final int 		CHAR_TITLEIMG_STOP = 0x000c;
	public static final int		CHAR_ROWS_S = 0x000e;
	public static final int		CHAR_ROWS_E = 0x000f;

	//public static final int		CHAR_INVISIBLE_SPACE = 0x2129;

	public static final int		CHAR_MARKER_FIND_S = 0x0016;
	public static final int		CHAR_MARKER_FIND_E = 0x0017;

	public static final int			STYLE_BASE_4CODECONVERT = 0xE400; // ! special
	public static final int			STYLE_MASK_4CODECONVERT = 0xF400; // ! special

	public static final int			STYLE_BASE0 = 0xE400; // ! special
	public static final int			STYLE_BASE1 = 0xEC00; // ! special	
	public static final int			STYLE_BASE_MASK = 0xFC00;

	public static final int			STYLE_BASE_SETTEXTCOLOR = 0x00000200;

	// text style specific value
	public static final long 			STYLE_BOLD =				0x0000000000000001L;
	public static final long 			STYLE_ITALIC =				0x0000000000000002L;
	public static final long 			STYLE_LINK =				0x0000000000000004L;
	public static final long 			STYLE_SUP =					0x0000000000000008L;
	public static final long 			STYLE_SUB =					0x0000000000000010L;
	public static final long 			STYLE_UNDER =				0x0000000000000020L;
	public static final long 			STYLE_STRIKE =				0x0000000000000040L;
	public static final long 			STYLE_CODE =				0x0000000000000080L;
	public static final long 			STYLE_RAZR =				0x0000000000000100L;
	public static final long 			STYLE_HIDDEN =				0x0000000000000200L;
		
	public static final long 			STYLE_MASK =				0x00000000000003ffL; // !!
	public static final long 			STYLE_IMASK =				0xfffffffffffffc00L; // !!
	public static final long 			STYLE_ICHARMASK =			0xfffffffffffffc00L;

	public static final long			SL_MASKFORLINK = STYLE_LINK | STYLE_UNDER | STYLE_SUP | STYLE_SUB;

	public static final long			SL_PRESERVE_SPACE =			0x0000000000000400L;
	public static final long			SL_IMAGE =					0x0000000000000800L;
	public static final long			SL_MARK =					0x0000000000001000L;
	public static final long			SL_NOHYPH =					0x0000000000002000L;
	public static final long			SL_SELECT =					0x0000000000004000L;	
	public static final long			SL_PAR =					0x0000000000008000L;

	public static final long			SL_SIZE_NORMAL =			0x0000000000640000L;
	public static final long			SL_SIZE_MASK =				0x0000000001ff0000L;
	public static final long			SL_SIZE_IMASK =				~SL_SIZE_MASK;
	public static final long			SL_SIZE_SHIFT =				16L;

	public static final long			SL_TABLE =					0x0000000002000000L;
	
	public static final long			SL_FONT_TEXT =				0x0000000000000000L;
	public static final long			SL_FONT_CODE =				0x0000000004000000L;
	public static final long			SL_FONT_NOTE =				0x0000000008000000L;
	public static final long			SL_FONT_FLET =				0x000000000c000000L;
	public static final long			SL_FONT_MASK =				0x000000000c000000L;
	public static final long			SL_FONT_IMASK =				~SL_FONT_MASK;
	public static final long			SL_FONT_SHIFT =				26L;

	public static final long			SL_COLOR_TEXT =				0x0000000000000000L; // 
	//public static final long			SL_COLOR_BACK =				0x0000000010000000L; //
	public static final long			SL_COLOR_NOVISIBLE =		0x0000000010000000L; // 
	public static final long			SL_COLOR_LINK =				0x0000000020000000L; // 
	public static final long			SL_COLOR_BOLD =				0x0000000030000000L; // 
	public static final long			SL_COLOR_ITALIC =			0x0000000040000000L; // 
	public static final long			SL_COLOR_BI =				0x0000000050000000L; // 
	public static final long			SL_COLOR_CODE =				0x0000000060000000L; // 
	public static final long			SL_COLOR_TITLE =			0x0000000070000000L; // 		 
	
	public static final long			SL_COLOR_MASK =				0x0000000070000000L;
	public static final long			SL_COLOR_IMASK =			~SL_COLOR_MASK;
	public static final long			SL_COLOR_SHIFT =			28L;
	// !!! special case - replace color value!!!
	public static final long			SL_IMAGE_OK =				SL_COLOR_LINK;
	public static final long			LMASK_SPECIALHYHP =			~(SL_COLOR_MASK | SL_PAR);

	public static final long			SL_SHADOW =					0x0000000080000000L;	
	public static final long			SL_COVER =					0x0000000100000000L;
	public static final long			SL_SPECIAL_PARAGRAPGH =		0x0000000200000000L;
	
	public static final long 			SL_FIRSTP =					0x0000000400000000L;
	public static final long			SL_MARKFIRTSTLETTER0 =		0x0000000800000000L;
	///
															   	
	public static final long			SL_MARKCOLOR0 =				0x0000000000000000L;
	public static final long			SL_MARKCOLOR1 =				0x0000001000000000L;
	public static final long			SL_MARKCOLOR2 =				0x0000002000000000L;
	public static final long			SL_MARKCOLOR3 =				0x0000003000000000L;
	public static final long			SL_MARKCOLOR4 =				0x0000004000000000L;
	public static final long			SL_MARKCOLOR5 =				0x0000005000000000L;
	public static final long			SL_MARKCOLOR6 =				0x0000006000000000L;
	public static final long			SL_MARKCOLOR7 =				0x0000007000000000L;
	public static final long			SL_MARKCOLOR_MASK =			0x0000007000000000L;
	public static final long			SL_MARKCOLOR_SHIFT =		36L;

	public static final long			SL_MARKNOTE =				0x0000008000000000L;

	//-----------------------------------------------------------------------------------
	
	public static final long			SL_CHINEZEADJUST =			0x8000000000000000L;

	public static final long			SL3_NUMBER_MASK =			0x7fffff0000000000L;
	public static final long			SL3_NUMBER_SHIFT = 40L;
	
	
	public static final long			SL_MASKSTYLESOVER =			0xFFFFFFFFFFFFFFFCL;	
	public static final long			MASK_FOR_FLETTER = SL_COVER | SL_SPECIAL_PARAGRAPGH | STYLE_MASK;
	
	public static final long 			LMASK_REAL_FONT = STYLE_CODE | STYLE_ITALIC | STYLE_BOLD | SL_FONT_MASK | STYLE_STRIKE | SL_MARKFIRTSTLETTER0 |
			STYLE_SUP | STYLE_SUB | SL_SIZE_MASK | SL_FONT_MASK;
	public static final long 			LMASK_PAINT_FONT = STYLE_SUP | STYLE_SUB | STYLE_RAZR | SL_SIZE_MASK | STYLE_STRIKE | SL_IMAGE | SL_MARKFIRTSTLETTER0;
	public static final long 			LMASK_DRAW_FONT = SL_COLOR_MASK | SL_SHADOW | STYLE_STRIKE | SL_MARKFIRTSTLETTER0;

	public static final long 		LMASK_CALC_STYLE = LMASK_PAINT_FONT | LMASK_REAL_FONT | SL_MARKFIRTSTLETTER0 | SL_SHADOW;
	public static final long 		LMASK_DRAW_STYLE = LMASK_CALC_STYLE | LMASK_DRAW_FONT | SL_SELECT | SL_MARK | SL_MARKFIRTSTLETTER0 |
			STYLE_UNDER | STYLE_LINK | STYLE_STRIKE | SL_CHINEZEADJUST;
	public static final long 		LMASK_ARABIC_STYLE = LMASK_DRAW_STYLE - SL_MARKFIRTSTLETTER0;


}
