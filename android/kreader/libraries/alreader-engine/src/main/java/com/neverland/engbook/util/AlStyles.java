package com.neverland.engbook.util;

public final class AlStyles {
	//////////////////////////
	
	public static final int	CHAR_NONE = 		0x0000;
	public static final int	CHAR_LINK_S =		0x0001;
	public static final int	CHAR_LINK_E =		0x0004;
	public static final int	CHAR_IMAGE_S =		0x0002;
	public static final int	CHAR_IMAGE_E = 		0x0003;	
	public static final int	CHAR_SIZE = 		0x0005; // reserved - not use
	public static final int CHAR_FONT = 		0x0006; // reserved - not use
	public static final int CHAR_COVER = 		0x0007;
	public static final int CHAR_ANYCHAR = 		0x0008; // for find text
	public static final int CHAR_SOFTPAR =		0x000a;	
	public static final int CHAR_TITLEIMG_START=0x000b;
	public static final int CHAR_TITLEIMG_STOP =0x000c;
	public static final int	CHAR_ROWS_S =		0x000e;
	public static final int	CHAR_ROWS_E =		0x000f;

	
	// text style specific value
	public static final int	STYLE_BOLD =						0x00000001;
	public static final int	STYLE_ITALIC =						0x00000002;
	public static final int	STYLE_LINK =						0x00000004;
	public static final int	STYLE_SUP =							0x00000008;
	public static final int	STYLE_SUB =							0x00000010;
	public static final int	STYLE_UNDER =						0x00000020;
	public static final int	STYLE_STRIKE =						0x00000040;
	public static final int	STYLE_CODE =						0x00000080;
	public static final int	STYLE_RAZR =						0x00000100;
	public static final int	STYLE_HIDDEN =						0x00000200;
	public static final int STYLE_MASK = 						0x000003ff; // !!
	public static final int STYLE_IMASK = 						0xfffffc00; // !!
	public static final int STYLE_ICHARMASK =						0xfc00;

	public static final long	PAR_TEXT =					0x0000000000000000L;
	//public static final long	 = 							0x0000000100000000L; //! value use with PAR_NATIVEJUST
	//public static final long	 =							0x0000000200000000L; //! value use with PAR_NATIVEJUST
	public static final long	PAR_TABLE = 				0x0000000400000000L;
	public static final long	PAR_STANZA =				0x0000000800000000L;
	public static final long	PAR_POEM = 					0x0000001000000000L;	//?
	public static final long	PAR_HIDDEN =				0x0000002000000000L; // view only drawing table and footnotes
	public static final long 	PAR_UL =					0x0000004000000000L;
	public static final long 	PAR_V =						0x0000008000000000L;
	public static final long 	PAR_COVER =					0x0000010000000000L;
	public static final long 	PAR_FIRSTP =				0x0000020000000000L;
	public static final long	PAR_NOTE =					0x0000040000000000L;
	public static final long	PAR_ANNOTATION =			0x0000080000000000L; //!
	public static final long	PAR_EPIGRAPH = 				0x0000100000000000L; //!
	public static final long	PAR_AUTHOR	=				0x0000200000000000L; //!
	public static final long	PAR_SKIPFIRSTLet =  		0x0000400000000000L; //!
	public static final long	PAR_CITE =					0x0000800000000000L;
	public static final long	PAR_PRE =					0x0001000000000000L;
	public static final long	PAR_TITLE =					0x0002000000000000L; //!
	public static final long	PAR_SUBTITLE =				0x0004000000000000L; //!
	public static final long	PAR_NATIVEJUST = 			0x0008000000000000L;

	
	public static final int		PAR_BREAKPAGE =			       		0x00040000;
	public static final int		PAR_PREVIOUS_EMPTY_0 =	       		0x00080000;
	public static final int		PAR_PREVIOUS_EMPTY_1 =	       		0x00020000;
	
	public static final long	PAR_PREVIOUS_EMPTY_MASK =	0xfffffffffff7ffffL;
	//public static final long	PAR_PARAGRAPH_MASK_1 =		0x000fffff000e0000L;
	//public static final long	PAR_PARAGRAPH_MASK_1 =		0x000fffff00040000L;
	public static final long	PAR_PARAGRAPH_MASK =		0x000fffff00000000L;
	
	public static final long	MASK_FOR_FLETTER = 			0x000fffff000003ffL - PAR_NOTE;
	public static final long	MASK_FOR_REMAPTEXT = 		PAR_PARAGRAPH_MASK - PAR_FIRSTP - PAR_NOTE;
	
	public static final int	PAR_CSS_MASK0 =	    					0x0ff00000;
	
	//...
	// format specific value								// fb2				// html
	public static final long	PAR_DESCRIPTION1 =						0x10000000L; // description 		// head
	public static final long	PAR_DESCRIPTION2 =						0x20000000L; // title-info		//
	public static final long	PAR_DESCRIPTION3 =						0x40000000L; // document-info	//
	public static final long	PAR_DESCRIPTION4 =						0x80000000L; // publich-info		//
	public static final long	PAR_DESCRIPTIONMASK =					0xf0000000L;
	
	public static final long	PAR_UL_BASE = 				0xf000000000000000L;
	
	public static final long	PAR_UL_L0 = 				0x0000000000000000L;
	public static final long	PAR_UL_L1 = 				0x1000000000000000L;
	public static final long	PAR_UL_L2 = 				0x2000000000000000L;
	public static final long	PAR_UL_L3 = 				0x3000000000000000L;
	public static final long	PAR_UL_L4 = 				0x4000000000000000L;
	public static final long	PAR_UL_L5 = 				0x5000000000000000L;
	public static final long	PAR_UL_L6 = 				0x6000000000000000L;
	public static final long	PAR_UL_L7 = 				0x7000000000000000L;
	public static final long	PAR_UL_L8 = 				0x8000000000000000L;
	public static final long	PAR_UL_L9 = 				0x9000000000000000L;
	public static final long	PAR_UL_LA = 				0xa000000000000000L;
	public static final long	PAR_UL_LB = 				0xb000000000000000L;
	public static final long	PAR_UL_LC = 				0xc000000000000000L;
	public static final long	PAR_UL_LD = 				0xd000000000000000L;
	public static final long	PAR_UL_LE = 				0xe000000000000000L;
	public static final long	PAR_UL_LF = 				0xf000000000000000L;
	
	public static final long	PAR_UL_MASK = 				0xfL;
	public static final long	PAR_UL_SHIFT = 	60;

	public static final long	SL_MASKFORLINK =	STYLE_LINK | STYLE_UNDER | STYLE_SUP | STYLE_SUB;
	
	public static final int		STYLE_BASE0 = 		0xE400; // ! special
	//public static final int		STYLE_BASE1 = 		0xE000; // ! special	
	public static final int		STYLE_BASE_MASK =	0xFC00; // ! special
	//
	
	public static final long	SL_BREAK =		  	0x0000000000000400L;
	public static final long	SL_IMAGE =		  	0x0000000000000800L;	
	public static final long	SL_MARK =			0x0000000000001000L;
	public static final long	SL_PREV_EMPTY_1 =	0x0000000000002000L;
	public static final long	SL_PREV_EMPTY_0 =	0x0000000000004000L;
	public static final long	SL_PAR = 			0x0000000000008000L;
	// Real Styles Paragraph
	
	public static final long	SL_MARKCOLOR0 =		0x0000000000000000L;
	public static final long	SL_MARKCOLOR1 =		0x0000000000010000L;
	public static final long	SL_MARKCOLOR2 =		0x0000000000020000L;
	public static final long	SL_MARKCOLOR3 =		0x0000000000030000L;
	public static final long	SL_MARKCOLOR4 =		0x0000000000040000L;
	public static final long	SL_MARKCOLOR5 =		0x0000000000050000L;
	public static final long	SL_MARKCOLOR6 =		0x0000000000060000L;
	public static final long	SL_MARKCOLOR7 =		0x0000000000070000L;
	public static final long	SL_MARKCOLOR_MASK =	0x0000000000070000L;
	public static final long	SL_MARKCOLOR_SHIFT =16L;
	
	public static final long	SL_SELECT =			0x0000000000080000L;

	public static final long	SL_INTER_TEXT = 	0x0000000000000000L;
	public static final long	SL_INTER_100 = 		0x0000000000100000L;
	public static final long	SL_INTER_NOTES =	0x0000000000200000L;
	public static final long	SL_INTER_FONT = 	0x0000000000300000L;
	public static final long	SL_INTER_MASK = 	0x0000000000300000L;
	public static final long	SL_INTER_SHIFT = 	20L;
	
	public static final long	SL_REDLINE =		0x0000000000400000L;
	public static final long	SL_SHADOW =			0x0000000000800000L;
	
	
	public static final long	SL_FONT_TEXT =		0x0000000000000000L;
	public static final long	SL_FONT_TITLE =		0x0000000001000000L;
	public static final long	SL_FONT_CODE =		0x0000000002000000L;
	public static final long	SL_FONT_STATUS =	0x0000000003000000L;
	public static final long	SL_FONT_FLET =		0x0000000004000000L;
	public static final long	SL_FONT_CUSTOM1 =	0x0000000005000000L;
	public static final long	SL_FONT_CUSTOM2 =	0x0000000006000000L;
	public static final long	SL_FONT_R1 =		0x0000000007000000L;
	public static final long	SL_FONT_MASK =		0x0000000007000000L;
	public static final long	SL_FONT_IMASK =		0xfffffffff8ffffffL;
	public static final long	SL_FONT_SHIFT =		24;
	
	public static final long	SL_REMAPFONT =		0x0000000008000000L;
	
	public static final long	SL_SIZE_0 = 		0x0000000000000000L;//default
	public static final long	SL_SIZE_M7 =		0x0000000010000000L;//-7
	public static final long	SL_SIZE_M6 =		0x0000000020000000L;//-6
	public static final long	SL_SIZE_M5 = 		0x0000000030000000L;//-5
	public static final long	SL_SIZE_M4 =		0x0000000040000000L;//-4
	public static final long	SL_SIZE_M3 =		0x0000000050000000L;//-3
	public static final long	SL_SIZE_M2 = 		0x0000000060000000L;//-2
	public static final long	SL_SIZE_M1 =		0x0000000070000000L;//-1
	public static final long	SL_SIZE_P1 = 		0x0000000080000000L;//+1
	public static final long	SL_SIZE_P2 =		0x0000000090000000L;//+2
	public static final long	SL_SIZE_P3 =		0x00000000a0000000L;//+3
	public static final long	SL_SIZE_P4 = 		0x00000000b0000000L;//+4
	public static final long	SL_SIZE_P5 =		0x00000000c0000000L;//+5
	public static final long	SL_SIZE_P6 =		0x00000000d0000000L;//+6
	public static final long	SL_SIZE_P7 = 		0x00000000e0000000L;//+7
	public static final long	SL_SIZE_P8 =		0x00000000f0000000L;//+8
	public static final long	SL_SIZE_MASK = 		0x00000000f0000000L;
	public static final long	SL_SIZE_IMASK = 	0xffffffff0fffffffL;
	public static final long	SL_SIZE_SHIFT = 	28;	
	
	public static final long 	SL_JUST_NONE =		0x0000000000000000L;
	public static final long	SL_JUST_LEFT =		0x0000000100000000L;
	public static final long	SL_JUST_RIGHT =		0x0000000200000000L;
	public static final long	SL_JUST_CENTER =  	0x0000000300000000L;
	public static final long	SL_JUST_MASK = 		0x0000000300000000L;
	public static final long	SL_JUST_SHIFT = 	32;
	
	public static final long	SL_HYPH =			0x0000000400000000L;
	public static final long	SL_MARKFIRTSTLETTER=0x0000000800000000L;
	public static final long	SL_MASKSTYLESOVER=	0xFFFFFFFFFFFFFFFCL;
	
	public static final long	SL_COLOR_TEXT =		0x0000000000000000L; // 
	public static final long	SL_COLOR_BACK =		0x0000001000000000L; // 
	public static final long	SL_COLOR_TITLE =	0x0000002000000000L; // 
	public static final long	SL_COLOR_LINK =		0x0000003000000000L; // 
	public static final long	SL_COLOR_BOLD =		0x0000004000000000L; // 
	public static final long	SL_COLOR_ITALIC =	0x0000005000000000L; // 
	public static final long	SL_COLOR_BI =		0x0000006000000000L; // 
	public static final long	SL_COLOR_STATUS =	0x0000007000000000L; // 
	public static final long	SL_COLOR_PROGRESS =	0x0000008000000000L; // 
	public static final long	SL_COLOR_MARK =		0x0000009000000000L; // 
	public static final long	SL_COLOR_SELECT =	0x000000a000000000L; // 
	public static final long	SL_COLOR_AUTOSCRL =	0x000000b000000000L; // 
	public static final long	SL_COLOR_BATT =		0x000000c000000000L; // 
	public static final long	SL_COLOR_CLOCK =	0x000000d000000000L; // 
	public static final long	SL_COLOR_ANNOTATION=0x000000e000000000L; // 
	public static final long	SL_COLOR_EPIGRAPH =	0x000000f000000000L; // 
	public static final long	SL_COLOR_AUTHOR =	0x0000010000000000L; // 
	public static final long	SL_COLOR_CITE =		0x0000011000000000L; // 
	public static final long	SL_COLOR_CUSTOM =	0x0000012000000000L; // 
	public static final long	SL_COLOR_SHADOW =	0x0000013000000000L; // 
	public static final long	SL_COLOR_CODE =		0x0000014000000000L; // 
	public static final long	SL_COLOR_A05 =		0x0000015000000000L; // 
	public static final long	SL_COLOR_A06 =		0x0000016000000000L; // 
	public static final long	SL_COLOR_A07 =		0x0000017000000000L; // 
	public static final long	SL_COLOR_A08 =		0x0000018000000000L; // 
	public static final long	SL_COLOR_A09 =		0x0000019000000000L; // 
	public static final long	SL_COLOR_A0A =		0x000001a000000000L; // 
	public static final long	SL_COLOR_A0B =		0x000001b000000000L; // 
	public static final long	SL_COLOR_A0C =		0x000001c000000000L; // 
	public static final long	SL_COLOR_A0D =		0x000001d000000000L; // 
	public static final long	SL_COLOR_A0E =		0x000001e000000000L; // 
	public static final long	SL_COLOR_A0F =		0x000001f000000000L;
	public static final long	SL_COLOR_MASK = 	0x000001f000000000L; 
	public static final long	SL_COLOR_IMASK =	0xfffffe0fffffffffL;
	public static final long	SL_COLOR_SHIFT = 	36;
	
	public static final long	SL_JUSTIFY_POEM = 	0x0000020000000000L; // shift 41
	
	public static final long 	SL_MARGR0 =			0x0000000000000000L;
	public static final long	SL_MARGR1 =			0x0000040000000000L;
	public static final long	SL_MARGR2 =			0x0000080000000000L;
	public static final long	SL_MARGR3 =  		0x00000c0000000000L;
	public static final long	SL_MARGR_MASK = 	0x00000c0000000000L;
	public static final long	SL_MARGR_SHIFT = 	42;
	
	public static final long	SL_IMAGE_OK =	  	0x0000000000800000L;
	public static final long	SL_IMAGE_MASK =	  	0x0000000000800000L;
	public static final long	SL_IMAGE_IMASK =  	0xffffffffff7fffffL;
	
	public static final long	SL_MARKNOTE0 = 		0x0000100000000000L;

	public static final long	LMASK_SPECIALHYHP = 0xfffffe0ffffff7ffL;
	
	public static final long	SL_MARKCOVER =      0x0000200000000000L; 
	public static final long	SL_MARKTITLE =      0x0000400000000000L; 
	public static final long	SL_IMAGETITLE =     0x0000800000000000L; 
	
	public static final long 	SL_MARGL0 =			0x0000000000000000L;
	public static final long	SL_MARGL1 =			0x0001000000000000L;
	public static final long	SL_MARGL2 =			0x0002000000000000L;
	public static final long	SL_MARGL3 =  		0x0003000000000000L;
	public static final long	SL_MARGL_MASK = 	0x0003000000000000L;
	public static final long	SL_MARGL_SHIFT = 	48;

	public static final long 	SL_KONTUR0 =		0x0000000000000000L;
	public static final long	SL_KONTUR1 =		0x0004000000000000L;
	public static final long	SL_KONTUR2 =		0x0008000000000000L;
	public static final long	SL_KONTUR3 =  		0x000c000000000000L;
	public static final long	SL_KONTUR_MASK = 	0x000c000000000000L;
	public static final long	SL_KONTUR_SHIFT = 	50;

	public static final long	SL_CHINEZEADJUST =	0x0010000000000000L;
	public static final long	SL_STANZA =			0x0020000000000000L;
	public static final long	SL_ENDPARAGRAPH =	0x0040000000000000L;

	public static final long	SL_TABLE = 			0x0080000000000000L;
	public static final long	SL_RESERV =			0x0F00000000000000L;
		
	public static final long	SL_UL_BASE = 		0xf000000000000000L;
	public static final long	SL_UL_MASK = 		0xf;
	public static final long	SL_UL_SHIFT = 		60;
	
	public static final long 	LMASK_REAL_FONT = 	/*SL_CODE |*/ STYLE_ITALIC | STYLE_BOLD | SL_FONT_MASK;
	public static final long 	LMASK_PAINT_FONT = 	STYLE_SUP | STYLE_SUB | STYLE_RAZR | SL_SIZE_MASK | STYLE_STRIKE  | SL_IMAGE | SL_MARKFIRTSTLETTER;
	public static final long 	LMASK_DRAW_FONT = 	SL_COLOR_MASK | SL_SHADOW | STYLE_STRIKE | SL_MARKFIRTSTLETTER;// | STYLE_SELECT | STYLE_MARK | STYLE_UNDER | STYLE_STRIKE | STYLE_LINK;
		
	public static final long 	LMASK_CALC_STYLE = 	LMASK_REAL_FONT | LMASK_PAINT_FONT | SL_MARKFIRTSTLETTER | SL_KONTUR_MASK | SL_SHADOW;
	public static final long 	LMASK_DRAW_STYLE = 	LMASK_CALC_STYLE | LMASK_DRAW_FONT | SL_SELECT | SL_MARK | SL_MARKFIRTSTLETTER | 
														SL_KONTUR_MASK | STYLE_UNDER | STYLE_LINK | STYLE_STRIKE | SL_CHINEZEADJUST;
	
	public static final long 	LMASK_DRAWSPACIAL_STYLE = 	SL_SELECT | SL_MARK | STYLE_LINK | STYLE_UNDER;// | S_SHADOW;
	
	public static final long 	LDEFAULT_PAR_STYLE = SL_REDLINE | SL_HYPH | SL_INTER_TEXT;
	
	
	public static final int    REMAP_MASKF = 0x0c;
	public static final int    REMAP_TEXTF = 0x00;
	public static final int    REMAP_FONTF = 0x04;
	public static final int    REMAP_ALLF =  0x08;
	public static final int    REMAP_NONEF = 0x0c;
	
	public static final int    REMAP_MASKC = 0x30;
	public static final int    REMAP_TEXTC = 0x00;
	public static final int    REMAP_FONTC = 0x10;
	public static final int    REMAP_ALLC =  0x20;
	public static final int    REMAP_NONEC = 0x30;
	
	public static final long SL_FLETTER_RESTORE = ~(SL_COLOR_MASK | SL_SHADOW | SL_MARKFIRTSTLETTER | 
														SL_FONT_MASK | SL_SIZE_MASK | SL_KONTUR_MASK); 

}
