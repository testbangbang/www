package com.neverland.engbook.util;

import com.neverland.engbook.forpublic.EngBookMyType;


import android.graphics.Paint;

public class InternalConst {

	public final static int AL_MIN_FONTSIZE = 5;
	public final static int AL_MAX_FONTSIZE = 200;

	public final static int AL_ONEWORD_FLAG_NONE =				  0x00;
	public final static int AL_ONEWORD_FLAG_DOHYPH =				  0x01;
	public final static int AL_ONEWORD_FLAG_NOINSERTALL =			0x02;

	public final static char TAL_HYPH_INPLACE_ENABLE = 				'-';
	public final static char TAL_HYPH_INPLACE_DISABLE =				'0';
	public final static char TAL_HYPH_INPLACE_DISABLESPACE =			'A';
	public final static char TAL_HYPH_INPLACE_PREDISABLE =				'B';

	public final static int INTER_TEXT = 8;
	public final static int INTER_NOTE = 9;
	public final static int INTER_FLET = 10;


	public final static int PAR_STYLE_INTERNAL_MAX =					32;
	public final static int PAR_STYLE_NUMBER_MASK =			0xFFFF0000;

	public final static int BREAK_HEIGHT =						111111;
	public final static int MIN_ITEM_HEIGHT =							5;

	public final static int FLET_MODE_LETTER =  0;
	public final static int FLET_MODE_START =  1;
	public final static int FLET_MODE_DIALOG =  2;

	public final static int 	FIND_LEN = 32;
	public final static int 	FIND_MASK = 0x1f;


	public final static int 	STYLES_MAX_STYLE = 16;
	public final static int 	STYLES_STYLE_TITLE = 0;
	public final static int 	STYLES_STYLE_STITLE = 1;
	public final static int 	STYLES_STYLE_ANNOTATION = 2;	
	public final static int 	STYLES_STYLE_EPIGRAPH = 3;
	public final static int 	STYLES_STYLE_AUTHOR = 4;
	public final static int 	STYLES_STYLE_CITE = 5;	
	public final static int 	STYLES_STYLE_PRE = 6;	
	public final static int 	STYLES_STYLE_POEM = 7;		
	public final static int 	STYLES_STYLE_BOLD = 8;	
	public final static int 	STYLES_STYLE_ITALIC = 9;	
	public final static int 	STYLES_STYLE_BOLDITALIC = 10;
	public final static int 	STYLES_STYLE_FLETTER0 = 11;
	public final static int 	STYLES_STYLE_FOOTNOTES = 12;
	public final static int 	STYLES_STYLE_CODE = 13;
	public final static int 	STYLES_STYLE_CSTYLE = 14;
	public final static int 	STYLES_STYLE_FLETTER1 = 15;

	public enum TAL_CALC_MODE {
		NORMAL,	
		NOTES,
	}

	
	public final static int TAL_PROFILE_COLOR_TEXT = 0;
	public final static int TAL_PROFILE_COLOR_BG = 1;
	public final static int TAL_PROFILE_COLOR_TITLE = 2;	
	public final static int TAL_PROFILE_COLOR_LINK = 3;
	public final static int TAL_PROFILE_COLOR_BOLD = 4;
	public final static int TAL_PROFILE_COLOR_ITALIC = 5;
	public final static int TAL_PROFILE_COLOR_BOLDITALIC = 6;	
	public final static int TAL_PROFILE_COLOR_CUSTOM1 = 7;
	public final static int TAL_PROFILE_COLOR_CUSTOM2 = 8;
	public final static int TAL_PROFILE_COLOR_MARK = 9;
	public final static int TAL_PROFILE_COLOR_SELECT = 10;
	public final static int TAL_PROFILE_COLOR_SELECTMARK = 11;
	public final static int TAL_PROFILE_COLOR_CUSTOM3 = 12;
	public final static int TAL_PROFILE_COLOR_CUSTOM4 = 13;
	public final static int TAL_PROFILE_COLOR_CUSTOM5 = 14;
	public final static int TAL_PROFILE_COLOR_CUSTOM6 = 15;
	public final static int TAL_PROFILE_COLOR_CUSTOM7 = 16;
	public final static int TAL_PROFILE_COLOR_CUSTOM8 = 17;
	public final static int TAL_PROFILE_COLOR_CUSTOM9 = 18;
	public final static int TAL_PROFILE_COLOR_SHADOW = 19;
	public final static int TAL_PROFILE_COLOR_CODE = 20;
	public final static int TAL_PROFILE_COLOR_CUSTOMA = 21;
	public final static int TAL_PROFILE_COLOR_CUSTOMB = 22;	

	public final static int TAL_PROFILE_COLOR_COUNT = 23;

	public final static int TAL_PROFILE_FONT_TEXT		= 0;
	public final static int TAL_PROFILE_FONT_TITLE		= 1;
	public final static int TAL_PROFILE_FONT_CODE		= 2;
	public final static int TAL_PROFILE_FONT_NOTE		= 3;
	public final static int TAL_PROFILE_FONT_FLET		= 4;
	public final static int TAL_PROFILE_FONT_CUSTOM1	= 5;
	public final static int TAL_PROFILE_FONT_CUSTOM2	= 6;

	public final static int TAL_PROFILE_FONT_COUNT = 7;

}
