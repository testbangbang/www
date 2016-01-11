package com.neverland.engbook.bookobj;

import java.util.ArrayList;
import java.util.Arrays;



import android.graphics.Paint;
import android.util.Log;

import com.neverland.engbook.forpublic.AlEngineNotifyForUI;
import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.forpublic.AlIntHolder;
import com.neverland.engbook.forpublic.AlOneSearchResult;
import com.neverland.engbook.forpublic.AlPublicProfileOptions;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_FILE_TYPE;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_GOTOCOMMAND;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_ID;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_RESULT;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_PAGE_INDEX;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_THREAD_TASK;
import com.neverland.engbook.forpublic.TAL_RESULT;

import com.neverland.engbook.forpublic.EngBookMyType.TAL_SCREEN_PAGES_COUNT;
import com.neverland.engbook.level1.AlFileZipEntry;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlFilesBypass;
import com.neverland.engbook.level1.AlFilesZIP;
import com.neverland.engbook.level2.AlFormat;
import com.neverland.engbook.level2.AlFormatFB2;
import com.neverland.engbook.level2.AlFormatTXT;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.AlBookState;
import com.neverland.engbook.util.AlCalc;
import com.neverland.engbook.util.AlFonts;
import com.neverland.engbook.util.AlHyph;
import com.neverland.engbook.util.AlImage;
import com.neverland.engbook.util.AlOneImage;
import com.neverland.engbook.util.AlOneImageParam;
import com.neverland.engbook.util.AlOneItem;
import com.neverland.engbook.util.AlOneLink;
import com.neverland.engbook.util.AlOnePage;
import com.neverland.engbook.util.AlOneWord;
import com.neverland.engbook.util.AlPaintFont;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlProfileOptions;
import com.neverland.engbook.util.AlScreenParameters;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.AlPagePositionStack;
import com.neverland.engbook.util.EngBitmap;
import com.neverland.engbook.util.InternalConst;
import com.neverland.engbook.util.InternalConst.TAL_CALC_MODE;

public class AlBookEng{
		
	private static final int AL_COUNTPAGES_FOR_AUTOCALC = 64;
	private static final int AL_COUNTPAGES_MAX_FORSCREEN = 512;
	private static final int AL_TIMESCALC_MAX_FORSCREEN = 2000;
	private static final int AL_FILESIZEMIN_FOR_AUTOCALC = (65536 << 1);

	private int bookPosition;

	private AlIntHolder shtamp = new AlIntHolder(0);
	//private int isOpen;
	//private int bmp_active;

	private int screenWidth;
	private int screenHeight;

	private AlEngineOptions engOptions = null;
	private AlBookState openState = new AlBookState();

	private AlEngineNotifyForUI notifyUI = new AlEngineNotifyForUI();
	private AlFormat format = null;
	private AlThreadData threadData = new AlThreadData();
	private AlFonts fonts = new AlFonts();
	private AlCalc calc = new AlCalc();
	private AlImage images = new AlImage();
	private AlBitmap[] bmp = { new AlBitmap(), new AlBitmap() };
	private AlHyph hyphen = new AlHyph();
	private AlPaintFont fontParam = new AlPaintFont();
	private AlOneImageParam imageParam = new AlOneImageParam();

	private AlProfileOptions profiles = new AlProfileOptions();
	private AlPreferenceOptions preferences = new AlPreferenceOptions();
	private AlStylesOptions styles = new AlStylesOptions();

	private int pageSizes;
	private ArrayList<AlPagePositionStack> pagePositionPointer = new ArrayList<AlPagePositionStack>(
			128);

	private AlIntHolder hyphFlag = new AlIntHolder(0);
	
	private long old_style;

	/*
	 * #ifdef CORRECTCALCLENGTHFORANDROID int tmp_wl[AL_WORD_LEN + 2]; char16_t
	 * arrCalc[AL_WORD_LEN + 2]; static const char16_t CHAR4CALC_STD = 'a';
	 * static const char16_t CHAR4CALC_ARA = 0x00; #endif
	 */
	private boolean calcWordLenForPages;

	private AlBitmap errorBitmap = null;
	private AlBitmap tableBitmap = null;
	private AlBitmap waitBitmap = null;

	private AlScreenParameters screen_parameters = new AlScreenParameters();
	private AlOneWord tmp_word = new AlOneWord(), note_word = new AlOneWord();

	public class PairTextStyle {
		public char[] txt = null;
		public long[] stl = null;
	}
	
	private PairTextStyle format_text_and_style = new PairTextStyle();
	private PairTextStyle format_note_and_style = new PairTextStyle();

	private int notesItemsOnPage;
	private int notesCounter;

	private AlOnePage page0 = new AlOnePage();
	private AlOnePage page1 = new AlOnePage();
	
	
	////////////////////////////////////////////////////////
	
	public AlBookEng() {
		openState.clearState();
		
		screenWidth = screenHeight = 0;
		
		threadData.clearAll();

		calcWordLenForPages = false;

		AlOnePage.init(page0);
		AlOnePage.init(page1);

		notesCounter = 0;		
		fontParam.fnt = new Paint();
	}

	@Override	
	public void finalize() {
		uninitializeBookEngine();		
	}

	int uninitializeBookEngine() {
		while (threadData.getWork0()) ;
		
		closeBook();

		EngBitmap.reCreateBookBitmap(bmp[0], 0, 0, shtamp);
		EngBitmap.reCreateBookBitmap(bmp[1], 0, 0, shtamp);

		return TAL_RESULT.OK;
	}

	public int initializeBookEngine(AlEngineOptions engOptions) {
		this.engOptions = engOptions;
		pagePositionPointer.clear();
		
		preferences.chinezeFormatting = engOptions.chinezeFormatting;

		old_style = 0;
		initDefaultPreference();
		initDefaultProfile();
		initDefaultStyles();	
		
		calc.init(engOptions);
		fonts.init(engOptions, calc);		
		images.init(engOptions);
		hyphen.init(engOptions);
		
		switch (engOptions.DPI) {
		case TAL_SCREEN_DPI_120:
		case TAL_SCREEN_DPI_160:
		case TAL_SCREEN_DPI_240:
			preferences.picture_need_tune = false;
			preferences.picture_need_tuneK = 1;
			break;
		case TAL_SCREEN_DPI_320:
			preferences.picture_need_tune = true;
			preferences.picture_need_tuneK = 2;
			break;
		case TAL_SCREEN_DPI_480:
			preferences.picture_need_tune = true;
			preferences.picture_need_tuneK = 3;
			break;
		case TAL_SCREEN_DPI_640:
			preferences.picture_need_tune = true;
			preferences.picture_need_tuneK = 4;
			break;
		default:
			preferences.picture_need_tune = false;
			preferences.picture_need_tuneK = 1;
			break;
		}

		preferences.calcPagesModeRequest = engOptions.useScreenPages;
		if (preferences.calcPagesModeRequest == TAL_SCREEN_PAGES_COUNT.SIZE)
			pageSizes = EngBookMyType.AL_DEFAULT_PAGESIZE;

		preferences.maxNotesItemsOnPageUsed = preferences.maxNotesItemsOnPageRequest;
		if (preferences.calcPagesModeRequest != TAL_SCREEN_PAGES_COUNT.SIZE) {
			// this disable notes on page. if enable, calc pages will be slowly
			preferences.notesOnPage = false;
			//
			preferences.maxNotesItemsOnPageUsed = 1;
		}

		preferences.vjustifyUsed = preferences.vjustifyRequest;


		EngBitmap.reCreateBookBitmap(bmp[0], 0, 0, shtamp);
		EngBitmap.reCreateBookBitmap(bmp[1], 0, 0, shtamp);

		return TAL_RESULT.OK;
	}

	public int initializeOwner(AlEngineNotifyForUI engUI) {
		notifyUI.appInstance = engUI.appInstance;
		notifyUI.hWND = engUI.hWND;		

		threadData.book_object = this;
		threadData.owner_window = notifyUI.hWND;
		
		return TAL_RESULT.OK;
	}
	
	public int freeOwner() {
		threadData.freeOwner();		
		return TAL_RESULT.OK;
	}

	public int setNewProfileParameters(AlPublicProfileOptions prof) {
		profiles.font_bold[0] = prof.bold;	
		profiles.font_italic[0] = false;
		
		if (prof.interline < -50)
			prof.interline = -50;
		if (prof.interline > 50)
			prof.interline = 50;

		profiles.font_interline[0] = prof.interline;
		
		if (prof.font_name == null) {
			profiles.font_names[0] = String.copyValueOf(prof.font_name.toCharArray());
		} else {
			profiles.font_names[0] = "Serif";
		}
		
		if (prof.font_monospace == null) {
			profiles.font_names[InternalConst.TAL_PROFILE_FONT_CODE] = profiles.font_names[0];
		} else {
			profiles.font_names[InternalConst.TAL_PROFILE_FONT_CODE] = String.copyValueOf(prof.font_name.toCharArray());
		}
		
		if (prof.font_size < InternalConst.AL_MIN_FONTSIZE)
			prof.font_size = InternalConst.AL_MIN_FONTSIZE;
		if (prof.font_size > InternalConst.AL_MAX_FONTSIZE)
			prof.font_size = InternalConst.AL_MIN_FONTSIZE;
		
		profiles.font_sizes[0] = prof.font_size;

		profiles.marginL = prof.margin;
		profiles.marginT = prof.margin;
		profiles.marginR = prof.margin;
		profiles.marginB = prof.margin;

		profiles.twoColumn = prof.twoColumn;
		profiles.background = prof.background;

		profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT] = prof.colorText;
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG] = prof.colorBack;
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_TITLE] = prof.colorTitle;	

		adaptProfileParameters();
		
		if (openState.getState() == AlBookState.OPEN)		
			needNewCalcPageCount();	

		return returnOkWithRedraw();
	}

	void adaptProfileParameters() {

		for (int i = 1; i < 7; i++) {
			profiles.font_bold[i] = profiles.font_bold[0];
			profiles.font_italic[i] = profiles.font_italic[0];
			if (i != InternalConst.TAL_PROFILE_FONT_CODE)
				profiles.font_names[i] = profiles.font_names[0];
			profiles.font_sizes[i] = profiles.font_sizes[0];			
			if (i == InternalConst.TAL_PROFILE_FONT_NOTE) { 
				profiles.font_widths[i] = 80;
			} else {
				profiles.font_widths[i] = profiles.font_widths[0];
			}
			profiles.font_weigths[i] = 0;
			profiles.font_interline[i] = profiles.font_interline[0];
			if (i == InternalConst.TAL_PROFILE_FONT_NOTE) {
				profiles.font_interline[i] -= 15; 
			}
		}

		if (profiles.font_sizes[InternalConst.TAL_PROFILE_FONT_NOTE] > profiles.font_sizes[0])
			profiles.font_sizes[InternalConst.TAL_PROFILE_FONT_NOTE] = profiles.font_sizes[0];		
		
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_LINK] =	0x2196f3;//0xffcc00;	
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK] =	0x4caf50;//0x66ff33;
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT] = 0x3f51b5;//0x66ffff;
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECTMARK] = 0x009688;//0xffcccc;
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_SHADOW] = 0x00808080;

		profiles.colors[InternalConst.TAL_PROFILE_COLOR_BOLD] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_ITALIC] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_BOLDITALIC] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_CODE] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];
		
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_CUSTOM1] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_CUSTOM2] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_CUSTOM3] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_CUSTOM4] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_CUSTOM5] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_CUSTOM6] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_CUSTOM7] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_CUSTOM8] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_CUSTOM9] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_CUSTOMA] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_CUSTOMB] = profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT];

		profiles.DPIMultiplex = preferences.picture_need_tuneK;

		preferences.vjustifyUsed = preferences.vjustifyRequest;
		if (preferences.vjustifyUsed && profiles.twoColumn)
			preferences.vjustifyUsed = false;

		
		calc.clearMainWidth();
		fonts.clearFontCache();
		
		shtamp.value++;
	}

	void initDefaultProfile() {
		profiles.font_bold[0] = false;
		profiles.font_italic[0] = false;
		profiles.font_names[0] = "Serif";
		profiles.font_sizes[0] = 21;
		profiles.font_widths[0] = 100;
		profiles.font_weigths[0] = 0;
		profiles.font_interline[0] = 0;

		profiles.font_space = 1;
		profiles.useCT = true;	
		profiles.isTransparentImage = false;

		profiles.showFirstLetter = 0;
		profiles.classicFirstLetter = false;

		profiles.marginL = -2;
		profiles.marginT = -2;
		profiles.marginR = -2;
		profiles.marginB = -2;
		
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT] =	0x0000ff00;
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG] =		0x00ffffff;

		profiles.background = null;
		profiles.style_summ = false;

		adaptProfileParameters();
	}

	void initDefaultStyles() {
		styles.style[InternalConst.STYLES_STYLE_TITLE] =  AlStyles.SL_FONT_TITLE | AlStyles.SL_SIZE_P2 | AlStyles.SL_COLOR_TITLE | 
			AlStyles.SL_JUST_CENTER | AlStyles.SL_KONTUR1 | AlStyles.SL_BOLD | AlStyles.SL_MARGL1 | 
			AlStyles.SL_MARGR1 | AlStyles.SL_SHADOW | AlStyles.SL_INTER_ADDFONT;
		styles.style[InternalConst.STYLES_STYLE_STITLE] = AlStyles.SL_FONT_TITLE | AlStyles.SL_SIZE_P1 | AlStyles.SL_COLOR_TITLE | 
			AlStyles.SL_JUST_CENTER | AlStyles.SL_MARGL1 | AlStyles.SL_MARGR1 | //AlStyles.SL_SHADOW | 
			AlStyles.SL_HYPH | AlStyles.SL_INTER_ADDFONT;
		styles.style[InternalConst.STYLES_STYLE_ANNOTATION] = AlStyles.SL_FONT_TEXT | AlStyles.SL_SIZE_M3 | AlStyles.SL_COLOR_TEXT | 
			AlStyles.SL_JUST_LEFT | AlStyles.SL_ITALIC | AlStyles.SL_MARGL3 | AlStyles.SL_HYPH | 
			AlStyles.SL_INTER_ADDTEXT;
		styles.style[InternalConst.STYLES_STYLE_EPIGRAPH] = AlStyles.SL_FONT_TEXT | AlStyles.SL_SIZE_M2 | AlStyles.SL_COLOR_TEXT | 
			AlStyles.SL_JUST_LEFT | AlStyles.SL_MARGL3 | AlStyles.SL_HYPH | AlStyles.SL_INTER_ADDTEXT;
		styles.style[InternalConst.STYLES_STYLE_AUTHOR] = AlStyles.SL_FONT_TEXT | AlStyles.SL_SIZE_M3 | AlStyles.SL_COLOR_TEXT | 
			AlStyles.SL_JUST_RIGHT | AlStyles.SL_MARGL2 | AlStyles.SL_MARGR1 | AlStyles.SL_HYPH | AlStyles.SL_INTER_ADDTEXT;
		styles.style[InternalConst.STYLES_STYLE_CITE] = AlStyles.SL_FONT_TEXT | AlStyles.SL_SIZE_M2 | AlStyles.SL_COLOR_TEXT | 
			AlStyles.SL_JUST_NONE | AlStyles.SL_ITALIC |
	    				AlStyles.SL_HYPH | AlStyles.SL_INTER_ADDTEXT;
		styles.style[InternalConst.STYLES_STYLE_PRE] = AlStyles.SL_FONT_CODE | AlStyles.SL_SIZE_M1 | AlStyles.SL_JUST_LEFT | 
			AlStyles.SL_COLOR_TEXT | AlStyles.SL_INTER_ADDFONT;
		styles.style[InternalConst.STYLES_STYLE_POEM] = AlStyles.SL_FONT_TEXT | AlStyles.SL_SIZE_0 | AlStyles.SL_COLOR_TEXT | 
			AlStyles.SL_JUST_LEFT | AlStyles.SL_JUSTIFY_POEM | AlStyles.SL_ITALIC | AlStyles.SL_INTER_ADDTEXT;
		styles.style[InternalConst.STYLES_STYLE_BOLD] = AlStyles.SL_FONT_TEXT | AlStyles.SL_SIZE_0 | AlStyles.SL_COLOR_TEXT | 
			AlStyles.REMAP_NONEF | AlStyles.REMAP_NONEC;
		styles.style[InternalConst.STYLES_STYLE_ITALIC] = AlStyles.SL_FONT_TEXT | AlStyles.SL_SIZE_0 | AlStyles.SL_COLOR_TEXT | 
			AlStyles.REMAP_NONEF | AlStyles.REMAP_NONEC;
		styles.style[InternalConst.STYLES_STYLE_BOLDITALIC] = AlStyles.SL_FONT_TEXT | AlStyles.SL_SIZE_0 | AlStyles.SL_COLOR_TEXT | 
			AlStyles.REMAP_NONEF | AlStyles.REMAP_NONEC;
		styles.style[InternalConst.STYLES_STYLE_FLETTER0] = AlStyles.SL_FONT_TITLE | AlStyles.SL_SIZE_P5 | AlStyles.SL_COLOR_TITLE | 
			AlStyles.SL_KONTUR1 | AlStyles.SL_SHADOW;
		styles.style[InternalConst.STYLES_STYLE_FOOTNOTES] = AlStyles.SL_FONT_TEXT | AlStyles.SL_SIZE_M2 | AlStyles.SL_COLOR_STATUS | 
			AlStyles.SL_HYPH | AlStyles.SL_INTER_ADDFONT;
		styles.style[InternalConst.STYLES_STYLE_CODE] = AlStyles.SL_FONT_CODE | AlStyles.SL_SIZE_0 | AlStyles.SL_COLOR_TEXT | 
			AlStyles.REMAP_ALLF | AlStyles.REMAP_NONEC;
		styles.style[InternalConst.STYLES_STYLE_CSTYLE] = AlStyles.SL_FONT_TEXT | AlStyles.SL_SIZE_0 | AlStyles.SL_COLOR_TITLE | 
			AlStyles.REMAP_NONEF | AlStyles.REMAP_NONEC;
		styles.style[InternalConst.STYLES_STYLE_FLETTER1] = AlStyles.SL_FONT_TITLE | AlStyles.SL_SIZE_P5 | AlStyles.SL_COLOR_TITLE;
	}


	private static final int MAX_NOTESITEMS_ON_PAGE	= 2;
	private static final int DEF_RED_LINE_VALUE =		104;	
	private static final int DEF_RED_LINEV_VALUE =		0;
	private static final int DEF_STYLEV_VALUE =		0;
	private static final int DEF_RED_PARV_VALUE =		20;
	private static final int DEF_STYLE1_VALUE =		210;
	private static final int DEF_STYLE2_VALUE =		225;
	private static final int DEF_STYLE3_VALUE =		240;
	private static final int DEF_RED_SUMMV_VALUE =		0;
	private static final boolean DEF_SCREEN_PUNCTUATION =	true;

	void calcScreenParameters() {		

		screen_parameters.fletter_mask0 = AlStyles.SL_MARKFIRTSTLETTER | 
			(styles.style[InternalConst.STYLES_STYLE_FLETTER0] & (AlStyles.SL_COLOR_MASK | AlStyles.SL_SHADOW | 
			AlStyles.SL_FONT_MASK | AlStyles.SL_SIZE_MASK | AlStyles.SL_KONTUR_MASK));

		if (profiles.classicFirstLetter) {
			screen_parameters.fletter_mask0 &= AlStyles.SL_MARKFIRTSTLETTER | AlStyles.SL_FONT_MASK | AlStyles.SL_SIZE_MASK | AlStyles.SL_COLOR_MASK;
			screen_parameters.fletter_mask0 |= styles.style[InternalConst.STYLES_STYLE_FLETTER1] & (AlStyles.SL_SHADOW | AlStyles.SL_KONTUR_MASK);
			screen_parameters.fletter_mask1 = styles.style[InternalConst.STYLES_STYLE_FLETTER1] & (AlStyles.SL_BOLD | AlStyles.SL_ITALIC);
		} else
			screen_parameters.fletter_mask1 = styles.style[InternalConst.STYLES_STYLE_FLETTER0] & (AlStyles.SL_BOLD | AlStyles.SL_ITALIC);

		screen_parameters.style_notes = styles.style[InternalConst.STYLES_STYLE_FOOTNOTES];
		screen_parameters.style_titlenotes = styles.style[InternalConst.STYLES_STYLE_TITLE] & AlStyles.SL_COLOR_MASK;
		screen_parameters.fletter_colored = true;
				
		screen_parameters.marginL = profiles.marginL;
		screen_parameters.marginR = profiles.marginR;
		screen_parameters.marginT = profiles.marginT;
		screen_parameters.marginB = profiles.marginB;
		int min_dim = Math.min(screenWidth >> 1, screenHeight);
		if (screen_parameters.marginL < 0) screen_parameters.marginL = screen_parameters.marginL * (-1) * min_dim  / (profiles.twoColumn ? 100 : 100);
		if (screen_parameters.marginT < 0) screen_parameters.marginT = screen_parameters.marginT * (-1) * min_dim  / 100;
		if (screen_parameters.marginR < 0) screen_parameters.marginR = screen_parameters.marginR * (-1) * min_dim  / (profiles.twoColumn ? 100 : 100);
		if (screen_parameters.marginB < 0) screen_parameters.marginB = screen_parameters.marginB * (-1) * min_dim  / 100;

		if (profiles.twoColumn && screen_parameters.marginR < 30)
			screen_parameters.marginR = 30;
			
		int tmp;

		for (int i = 0; i < 7; i++) {
			fonts.modifyPaint(fontParam, 0xffffffffffffffffL, (long)i << AlStyles.SL_FONT_SHIFT, profiles, false);
			screen_parameters.interFI0[i] = profiles.font_interline[i];
			screen_parameters.interFH_0[i] = fontParam.base_line_down + fontParam.base_line_up;
			screen_parameters.interFH_1[i] = fontParam.base_line_down;
			screen_parameters.interFH_2[i] = fontParam.base_line_up;
		}
		screen_parameters.interFI0[InternalConst.INTER_TEXT] = profiles.font_interline[InternalConst.TAL_PROFILE_FONT_TEXT];
		screen_parameters.interFI0[InternalConst.INTER_NOTE] = profiles.font_interline[InternalConst.TAL_PROFILE_FONT_NOTE];
		screen_parameters.interFI0[InternalConst.INTER_FLET] = profiles.font_interline[InternalConst.TAL_PROFILE_FONT_FLET];
		//
		screen_parameters.interFI0[0] = screen_parameters.interFI0[InternalConst.INTER_TEXT];
		screen_parameters.interFI0[3] = screen_parameters.interFI0[InternalConst.INTER_NOTE];
		//
		fonts.modifyPaint(fontParam, 0xffffffffffffffffL, 0, profiles, false);		
		old_style = 0;

		if (preferences.chinezeFormatting) {
			while (true) {
				screen_parameters.free_picture_width = (int)((screenWidth >> (profiles.twoColumn ? 1 : 0))) - 
					screen_parameters.marginL - screen_parameters.marginR - 1;
				tmp = (int) (screen_parameters.free_picture_width % (fontParam.space_width_standart * 2));
				if (tmp < 2)
					break;
				screen_parameters.marginL++;
				screen_parameters.marginR++;
			}
		}
		
		screen_parameters.free_picture_width = (int)((screenWidth >> (profiles.twoColumn ? 1 : 0))) - 
			screen_parameters.marginL - screen_parameters.marginR - 1;
		screen_parameters.free_picture_height = screenHeight - screen_parameters.marginT - screen_parameters.marginB - 3;

		screen_parameters.reservHeight0 = fontParam.def_reserv * preferences.picture_need_tuneK;
			
		//int paragraphHeight = 0x65656900;//PrefManager.getInt(R.string.keyscreen_paragraph);
		
		screen_parameters.redLineV = DEF_RED_LINEV_VALUE;
		screen_parameters.redParV = DEF_RED_PARV_VALUE;
		
		screen_parameters.redLine = DEF_RED_LINE_VALUE;
		if (screen_parameters.redLine >= 200) {
			screen_parameters.redLine = (int) (screen_parameters.free_picture_width * (screen_parameters.redLine - 200) / 100);
		} else
		if (screen_parameters.redLine >= 100) {
			screen_parameters.redLine = (int) (fontParam.space_width_standart * (screen_parameters.redLine - 100));
		}		
		if (screen_parameters.redLine > screen_parameters.free_picture_width * 0.1f) {
			screen_parameters.redLine = (int) (screen_parameters.free_picture_width * 0.1f);
		}

		if (preferences.chinezeFormatting)
			screen_parameters.redLine *= 2;
		
		screen_parameters.redList = (int) (fontParam.space_width_standart * (preferences.chinezeFormatting ? 4 : 3));

		screen_parameters.redStyle1 = DEF_STYLE1_VALUE;
		if (screen_parameters.redStyle1 >= 200) {
			screen_parameters.redStyle1 = (screen_parameters.free_picture_width * (screen_parameters.redStyle1 - 200) / 100);
		} else
		if (screen_parameters.redStyle1 >= 100) {
			screen_parameters.redStyle1 = (int) (fontParam.space_width_standart * (screen_parameters.redStyle1 - 100));
		}		
		if (screen_parameters.redStyle1 < 1) {
			screen_parameters.redStyle1 = 1;
		}

		//paragraphHeight = 0x656565;//PrefManager.getInt(R.string.keyscreen_parlevel);
			
		screen_parameters.summRedV = DEF_RED_SUMMV_VALUE;
		
		screen_parameters.redStyle2 = DEF_STYLE2_VALUE;
		if (screen_parameters.redStyle2 >= 200) {
			screen_parameters.redStyle2 = (int) (screen_parameters.free_picture_width * (screen_parameters.redStyle2 - 200) / 100);
		} else
		if (screen_parameters.redStyle2 >= 100) {
			screen_parameters.redStyle2 = (int) (fontParam.space_width_standart * (screen_parameters.redStyle2 - 100));
		}			
		if (screen_parameters.redStyle2 < 1) {
			screen_parameters.redStyle2 = 1;
		}
		
		screen_parameters.redStyle3 = DEF_STYLE3_VALUE;
		if (screen_parameters.redStyle3 >= 200) {
			screen_parameters.redStyle3 = (int) (screen_parameters.free_picture_width * (screen_parameters.redStyle3 - 200) / 100);
		} else
		if (screen_parameters.redStyle3 >= 100) {
			screen_parameters.redStyle3 = (int) (fontParam.space_width_standart * (screen_parameters.redStyle3 - 100));
		}		
		if (screen_parameters.redStyle3 < 1) {
			screen_parameters.redStyle3 = 1;
		}

		screen_parameters.redStyleV = DEF_STYLEV_VALUE;

		if (DEF_SCREEN_PUNCTUATION && !preferences.chinezeFormatting) {
			screen_parameters.vikluchL = (int) 
				((screen_parameters.marginL > fontParam.hyph_width / 2.5 ? fontParam.hyph_width / 2.5 : screen_parameters.marginL) + 0.5f);
			screen_parameters.vikluchR = (int) 
				((screen_parameters.marginR > fontParam.hyph_width / 2.5 ? fontParam.hyph_width / 2.5 : screen_parameters.marginR) + 0.5f);
		} else {
			screen_parameters.vikluchL = 0;
			screen_parameters.vikluchR = 0;
		}
	}

	void initDefaultPreference() {
		preferences.maxNotesItemsOnPageRequest = MAX_NOTESITEMS_ON_PAGE;
		preferences.delete0xA0 = true;
		preferences.need_dialog = 0x00;
		preferences.notesAsSUP = true;
		preferences.sectionNewScreen = true;
		preferences.styleSumm = false;
		preferences.u301mode = 0x00;		
		preferences.notesOnPage = true;
		preferences.justify = true;
		preferences.vjustifyRequest = true;
		preferences.calcPagesModeRequest = TAL_SCREEN_PAGES_COUNT.SIZE;
	}

	void drawPageFromPosition(int pos) {
		calc.drawBackground(screenWidth, screenHeight, profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG], profiles.background);

		notesCounter++;
		if (profiles.twoColumn) {
			recalcColumn(
				(screenWidth >> 1) - screen_parameters.marginR - screen_parameters.marginL, 
				screenHeight - screen_parameters.marginB - screen_parameters.marginT, 
				page0, pos, TAL_CALC_MODE.NORMAL);
			prepareColumn(page0);
			markFindResult(page0);

			recalcColumn(
				(screenWidth >> 1) - screen_parameters.marginR - screen_parameters.marginL, 
				screenHeight - screen_parameters.marginB - screen_parameters.marginT, 
				page1, page0.end_position, TAL_CALC_MODE.NORMAL);
			prepareColumn(page1);
			markFindResult(page1);

			drawColumn(page0,
				screen_parameters.marginL, 
				screen_parameters.marginT, 
				(screenWidth >> 1) - screen_parameters.marginR, 
				screenHeight - screen_parameters.marginB);
			drawColumn(page1,
				(screenWidth >> 1) + screen_parameters.marginR, 
				screen_parameters.marginT, 
				screenWidth - screen_parameters.marginL, 
				screenHeight - screen_parameters.marginB);
		} else {
			recalcColumn(
				screenWidth - screen_parameters.marginR - screen_parameters.marginL, 
				screenHeight - screen_parameters.marginB - screen_parameters.marginT, 
				page0, pos, TAL_CALC_MODE.NORMAL);
			prepareColumn(page0);
			markFindResult(page0);

			drawColumn(page0,
				screen_parameters.marginL, 
				screen_parameters.marginT, 
				screenWidth - screen_parameters.marginR, 
				screenHeight - screen_parameters.marginB);
		}
	}
	
	public AlBitmap	getPageBitmap(TAL_PAGE_INDEX index, int width, int height) {
		int tmp_res;	

		if (index == TAL_PAGE_INDEX.CURR) {

			if (openState.getState() == AlBookState.OPEN) {
				if (bmp[0].shtamp != shtamp.value || bookPosition != bmp[0].position) {
					bmp[0].shtamp = shtamp.value;
					bmp[0].position = bookPosition;

					calc.beginMain(bmp[0].canvas, profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG]);
					calcScreenParameters();
					drawPageFromPosition(bookPosition);		
					calc.endMain();
				}
				return bmp[0];
			}

			int rW = (width + 0x03) & 0xfffc;
			int rH = (height + 0x03) & 0xfffc;

			if (bmp[1].width != rW || bmp[1].height != rH)			
				EngBitmap.reCreateBookBitmap(bmp[1], width, height, shtamp);		

			int waitposition = openState.getState() != AlBookState.NOLOAD ? -2 : -1;
			if (bmp[1].shtamp == shtamp.value && bmp[1].position == waitposition)
				return bmp[1];
			bmp[1].shtamp = shtamp.value;
			bmp[1].position = waitposition;

			calc.beginMain(bmp[1].canvas, profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG]);
			calc.drawBackground(width, height, profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG], profiles.background);
			if (openState.getState() != AlBookState.NOLOAD) {
				int x = (width - waitBitmap.width) >> 1;
				int y = (height - waitBitmap.height) >> 1;
				calc.drawImage(x, y, waitBitmap.width, waitBitmap.height, waitBitmap, 0x00000000);
			}
			calc.endMain();
			return bmp[1];
		}
		
		return bmp[1];
	}

	
	void markFindResult(AlOnePage page) {
		if (format.resfind.size() > 0) {
			int spos, epos;
			for (int i = 0; i < format.resfind.size(); i++) {
				spos = format.resfind.get(i).pos_start;
				epos = format.resfind.get(i).pos_end;
				if ((spos >= page.start_position && spos < page.end_position) ||
					(epos >= page.start_position && epos < page.end_position)) {


					AlOneItem oi;
					for (int item = 0; item < page.countItems; item++) {
						oi = page.items.get(item);
						for (int poschar = 0; poschar < oi.count; poschar++) {
							if (oi.pos[poschar] >= spos && oi.pos[poschar] <= epos)
								oi.style[poschar] |= AlStyles.SL_SELECT;
						}
					}
				}
			}
		}
	}

	void prepareColumn(AlOnePage page) {
		AlOneItem oi;
		
		int ext_len, cnt_char, cnt_img = 0;
		int i, j, jj, count_space, add;

		boolean needVJust = true;
		float a1, a2;
		char ch;
		
		int col_count = page.countItems;
		
		for (j = 0; j < col_count; j++) {
			oi = page.items.get(j);
			if (oi.isPrepare)
				continue;
			oi.isPrepare = true;
			int oilen = oi.count;
			
			if (oi.spaceAfterHyph0 != 0) {
				oi.textWidth -= oi.spaceAfterHyph0;
				oi.spaceAfterHyph0 = 0;
			}
			
			if (profiles.classicFirstLetter && oi.height < 0) {
				
				AlOneItem poi = null;
				for (jj = j - 1; jj >= 0; jj--)
					if (!page.items.get(jj).isNote) {
						poi = page.items.get(jj);
						break;
					}
				
				
				if (poi != null && (poi.style[0] & AlStyles.SL_MARKFIRTSTLETTER) != 0) {	

					poi.base_line_down -= fontParam.height + oi.interline;
					oi.height += fontParam.height + oi.interline;
				}
			}
						
			cnt_char = cnt_img = 0;
			for (i = 0; i < oilen; i++) {
				if (oi.text[i] >= 0x20)
					cnt_char++;
				if (oi.text[i] == AlStyles.CHAR_IMAGE_E)
					cnt_img++;
			}
			if (cnt_img > 0) {
				if (cnt_char == 0 && cnt_img == 1) {
					
					AlOneItem poi = null;
					if (oi.count == 1 && profiles.classicFirstLetter) {
						for (jj = j - 1; jj >= 0; jj--)
							if (!page.items.get(jj).isNote) {
								poi = page.items.get(jj);
								break;
							}
					}
					
					if (poi != null && (poi.style[0] & AlStyles.SL_MARKFIRTSTLETTER) != 0) {
						if (oi.width[0] > oi.allWidth) {
							oi.width[0] = oi.allWidth;
							oi.textWidth = oi.allWidth;
						}						
					} else {
						oi.justify = AlStyles.SL_JUST_CENTER;
						oi.allWidth += oi.isRed + oi.isLeft + oi.isRight;
						oi.isLeft = 0;
						oi.isRed = 0;
						oi.isRight = 0;					
						if (page.countItems == 1) {
							if (page.pageHeight > page.textHeight)
								oi.height += (page.pageHeight - page.textHeight) >> 1;
							needVJust = false;
						}
					}
				} else {
					if (page.countItems == 1)
						needVJust = false;
				}
			}
			
			if (profiles.font_space != 1.0f &&
					(oi.isEnd || oi.justify != AlStyles.SL_JUST_NONE || !preferences.justify)) {
				count_space = 0;
				for (i = 0; i < oilen; i++) {
					if (oi.text[i] == 0x20 && (oi.style[i] & AlStyles.SL_FONT_MASK) == 0)
						count_space++;
				}
				if (count_space != 0) {
					a1 = (oi.allWidth - oi.textWidth) / count_space;
				
					for (i = 0; i < oilen; i++) {
						if (oi.text[i] == 0x20 && (oi.style[i] & AlStyles.SL_FONT_MASK) == 0) {
							a2 = (oi.width[i] / profiles.font_space) - oi.width[i];
							if (a2 > a1) {
								oi.width[i] += a1;
								oi.textWidth += a1;
							} else {
								oi.width[i] += a2;
								oi.textWidth += a2;
							}
						}
					}
				}
			}
			
			boolean specialJust = false;
			if (oi.count > 0 && oi.justify == AlStyles.SL_JUST_CENTER && 
				((oi.style[0] & (AlStyles.SL_MARKTITLE | AlStyles.SL_JUSTIFY_POEM)) == 
					(AlStyles.SL_MARKTITLE | AlStyles.SL_JUSTIFY_POEM)) &&
				!oi.isEnd) {
				
				count_space = 0;
				for (i = 0; i < oilen; i++) {
					if (oi.text[i] == 0x20)
						count_space++;
				}
				
				if (count_space >= 2) {
					oi.justify = AlStyles.SL_JUST_NONE;
					specialJust = true;
				}
			}
			
			if (oi.justify == AlStyles.SL_JUST_NONE) {

				if (oi.count > 0 && ((oi.style[0] >> AlStyles.SL_UL_SHIFT) & AlStyles.SL_UL_MASK) == 0) {
					switch (Character.getType(oi.text[0])) {
					case Character.START_PUNCTUATION:
					case Character.INITIAL_QUOTE_PUNCTUATION:
						oi.allWidth += screen_parameters.vikluchL;
						oi.isLeft -= screen_parameters.vikluchL;
						break;
					}
				}
				
				if (!specialJust && !preferences.justify)
					continue;
				
				if (oi.isEnd)
					continue;
				
				count_space = 0;

				if (preferences.chinezeFormatting) {
					for (i = 0; i < oilen; i++) {
						ch = oi.text[i];
						if (ch == 0x20 || (ch > 0x3000 && i != oilen - 1 && !AlUnicode.isLetter(ch)))
							count_space++;
					}
				} else {
					for (i = 0; i < oilen; i++) {
						ch = oi.text[i];
						if (ch == 0x20)
							count_space++;
					}
				}
				

				if (count_space > 0) {
					switch (Character.getType(oi.text[oilen - 1])) {
					case Character.OTHER_PUNCTUATION:
						switch (oi.text[oilen - 1]) {
						case '!': case '\"': case '\'':
						case '*': case ',': case '.':
						case ':': case ';': case ';':
						case 0x2022: case 0x2023: case 0x2027:		
						case 0x2024: case 0x2025: case 0x2026:	
							oi.textWidth -= screen_parameters.vikluchR;
						}
						break;
					case Character.DASH_PUNCTUATION: 
					case Character.END_PUNCTUATION:
					case Character.FINAL_QUOTE_PUNCTUATION:
						oi.textWidth -= screen_parameters.vikluchR;
					}
					
					ext_len = (int) (oi.allWidth - oi.textWidth);
					
					for (i = 0; i < oilen; i++) {
						ch = oi.text[i];
						if (ch == 0x20) {
							add = (int) (ext_len / count_space);
							oi.width[i] += add;
							count_space--;
							ext_len -= add;
						} else
						if (preferences.chinezeFormatting && ch > 0x3000 && i != oilen - 1 && !AlUnicode.isLetter(ch)) {
							add = (int) (ext_len / count_space);
							oi.width[i] += add;
							count_space--;
							ext_len -= add;
							
							if (i == 0 || (oi.style[i - 1] & AlStyles.SL_CHINEZEADJUST) == 0)
								oi.style[i] |= AlStyles.SL_CHINEZEADJUST;
						}
					}		
								
				} else
				if (preferences.chinezeFormatting) {
					count_space = 0;

					for (i = 0; i < oilen; i++) {
						ch = oi.text[i];
						if (ch > 0x3000)
							count_space++;
					}
					
					if (count_space > 0) {
						ext_len = (int) (oi.allWidth - oi.textWidth);
						
						for (i = 0; i < oilen; i++) {
							ch = oi.text[i];
							if (ch > 0x3000) {
								add = (int) (ext_len / count_space);
								count_space--;
								if (add > 0) {
									oi.width[i] += add;									
									ext_len -= add;									
									if (i == 0 || (oi.style[i - 1] & AlStyles.SL_CHINEZEADJUST) == 0)
										oi.style[i] |= AlStyles.SL_CHINEZEADJUST;
								}
							}
						}
					}
				}
			} else
			if (oi.justify == AlStyles.SL_JUST_LEFT) {

					switch (Character.getType(oi.text[0])) {
					case Character.START_PUNCTUATION:
					case Character.INITIAL_QUOTE_PUNCTUATION:
						oi.isLeft -= screen_parameters.vikluchL;
						break;
					}
			} else
			if (oi.justify == AlStyles.SL_JUST_CENTER) {
				ext_len = (int) (oi.allWidth - oi.textWidth);
				ext_len >>= 1;
				oi.isLeft += ext_len;				
			} else
			if (oi.justify == AlStyles.SL_JUST_RIGHT) {
				switch (Character.getType(oi.text[oi.count - 1])) {
				case Character.OTHER_PUNCTUATION:
					switch (oi.text[oilen - 1]) {
					case '!': case '\"': case '\'':
					case '*': case ',': case '.':
					case ':': case ';': case ';':
						oi.textWidth -= screen_parameters.vikluchR;
					}
					break;
				case Character.DASH_PUNCTUATION: 
				case Character.END_PUNCTUATION:
				case Character.FINAL_QUOTE_PUNCTUATION:
					oi.isLeft += screen_parameters.vikluchR;
				}
				
				ext_len = (int) (oi.allWidth - oi.textWidth);
				oi.isLeft += ext_len;
				continue;
			}
		}
		
		page.overhead = 0;	
		if (preferences.vjustifyUsed && needVJust && !page.notePresent) {
			ext_len = page.pageHeight - page.textHeight;
			
			for (i = 1, jj = 0; i < col_count; i++) {
				oi = page.items.get(i);					
				if (oi.isNote)
					continue;
				if (oi.count > 0) {
					jj = oi.interline;
				} 	
			}
			if (jj < 0)
				ext_len += jj;
			
		
			if (ext_len > 0 && (ext_len >> 1) <= (fontParam.height >> 1)) {
				for (i = 1, jj = add = 0; i < col_count; i++) {
					oi = page.items.get(i);					
					if (oi.count < 1)
						continue;
					
					if (((oi.style[0] & (AlStyles.SL_PREV_EMPTY_1 + AlStyles.SL_PREV_EMPTY_0)) != 0) && oi.isStart) {
						jj++;
					} else
					if (oi.isStart) {
						add++;
					} 	
				}
					
				int pt = 2 * preferences.picture_need_tuneK;			
				while (pt > 0 && add * pt >= ext_len) {
					pt--;
				}
							
				if (add * pt < ext_len) {
					for (i = 1; i < col_count; i++) {
						oi = page.items.get(i);
						if (oi.isNote)
							continue;
						if (oi.count < 1)
							continue;
						
						if (((oi.style[0] & (AlStyles.SL_PREV_EMPTY_1 + AlStyles.SL_PREV_EMPTY_0)) != 0) && oi.isStart) {
							
						} else
						if (oi.isStart) {
							oi.height += pt;
							ext_len -= pt;
						} 
					}						
				}			
				
				if (jj != 0) {
					j = ext_len / jj;
										
					for (i = 1; i < col_count; i++) {
						oi = page.items.get(i);
						if (oi.isNote)
							continue;
						if (oi.count < 1)
							continue;
						
						if (((oi.style[0] & (AlStyles.SL_PREV_EMPTY_1 + AlStyles.SL_PREV_EMPTY_0)) != 0) && oi.isStart) {
							oi.height += j;
							ext_len -= j;
							jj--;
							if (jj != 0)
								j = ext_len / jj;
						}
					}					
				}
			} 
			
			ext_len >>= 1;
			if (ext_len <= (fontParam.height >> 1))
				page.topMarg = ext_len;
			
		}
	}

	void drawImage(int pos, long style, int widthImage, int x, int y) {
		AlImage ai = null;
		String link = null;		
		int scale = (int) ((style & AlStyles.SL_COLOR_MASK) >> AlStyles.SL_COLOR_SHIFT);	
			
		/*if ((style & AlStyles.SL_IMAGE_MASK) == AlStyles.SL_IMAGE_OK) {
			link = format.getImageNameByPos(pos);
			if (link != null)
				ai = format.getImageByName(link);
			if (ai != null) {
				Bitmap b = getStoredImage(link, scale, ai);
				if (b != null) {
					int th = ai.height;
					int tw = ai.width;
					for (int i = 0; i < scale; i++) {
						th >>= 1;
						tw >>= 1;
					}
					
					final int w;
					final int h;
					final float f = widthImage / tw;
					if (f <= 1.01f && f >= 0.999f) {
						w = (int) (tw);
						h = (int) (th);
					} else {
						w = (int) (tw * f);
						h = (int) (th * f);
					}
					
					rImageDst.left = x; rImageDst.top = y - h;
					rImageDst.right = x + w; rImageDst.bottom = y;
					
					switch (ai.iType & AlImage.IMG_MASKTYPE) {
					case AlImage.IMG_BMP:
					case AlImage.IMG_JPG:
						break;
					default:
						if (!profiles.isTransparentImage) {
							linePaint.setColor(0xffffffff);	
							canvas.drawRect(rImageDst, linePaint);
						}
					}
					
					canvas.drawBitmap(b, null, rImageDst, imagePaint);
					
					if ((style & AlStyles.SL_SELECT) != 0) {
						linePaint.setColor(ProfileManager.getColor(ProfileManager.COLOR_SELECT, false));						
						canvas.drawRect(rImageDst, linePaint);
					}
					
					return;
				}
			}
		}*/
		
		imageParam.real_height = errorBitmap.height;
		imageParam.real_width = errorBitmap.width;
		calc.drawImage(x, y - imageParam.real_height, errorBitmap.width, errorBitmap.height, errorBitmap, 0x00000000);
	}


	int drawPartItem(int start, int end,
			long old_style, int x, int y,
			AlOneItem oi, AlOnePage page) {
		int i, k;
		int x2;
		
		int ySelect0 = y;
		if (profiles.classicFirstLetter && ((old_style & AlStyles.SL_MARKFIRTSTLETTER) != 0)) {
			y += fontParam.base_ascent - (fontParam.height - fontParam.def_line_down);
			//y -= screen_parameters.interFI0[InternalConst.INTER_FLET];
		}
		int ySelect1 = y;
		
		if ((old_style & AlStyles.SL_IMAGE) != 0) {
			for (i = start; i <= end; i++) {
				drawImage(oi.pos[i], oi.style[i], oi.width[i], x, y);
				if ((oi.style[i] & AlStyles.STYLE_LINK) != 0)
					calc.drawLine(x, y + 2, x + oi.width[i] + 1, y + 2, 
						preferences.picture_need_tuneK, profiles.colors[InternalConst.TAL_PROFILE_COLOR_LINK]);			
				x += oi.width[i];
			}
		} else {

			int sm;
			if ((old_style & (AlStyles.SL_MARK | AlStyles.SL_SELECT)) != 0) {
				if ((old_style & (AlStyles.SL_MARK | AlStyles.SL_SELECT)) == (AlStyles.SL_MARK | AlStyles.SL_SELECT)) {
					sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECTMARK];				
				} else
				if ((old_style & AlStyles.SL_SELECT) != 0) {														
					sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT];
				} else {
					sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK];
				}
				
				x2 = x; 
				for (i = start; i <= end; i++)
					x2 += oi.width[i];				

				calc.drawRect(x, ySelect0 - oi.base_line_up, x2, ySelect1 + oi.base_line_down, sm);
			}

			
			if ((old_style & AlStyles.STYLE_SUB) != 0) {
				y += fontParam.base_line_down / 2; 
			} else
			if ((old_style & AlStyles.STYLE_SUP) != 0) {
				y -= fontParam.base_line_up / 2; 
			}
			
			if ((old_style & (AlStyles.STYLE_LINK | AlStyles.STYLE_UNDER)) != 0) {						
				x2 = x; 
				for (i = start; i <= end; i++)
					x2 += oi.width[i];
				calc.drawLine(x, y + 2, x2 + 1, y + 2, 
					preferences.picture_need_tuneK, fontParam.color);
			}
			
			k = (int) ((old_style & AlStyles.SL_KONTUR_MASK) >> AlStyles.SL_KONTUR_SHIFT);	
			if ((old_style & AlStyles.STYLE_RAZR) != 0) {
				for (i = start; i <= end; i++) {
					//drawArr[0] = oi.text[i];
					/*if (k != 0) {
						int cl = textPaint.getColor();
						
						textPaint.setStyle(Style.STROKE);
						textPaint.setStrokeJoin(Join.ROUND);
						textPaint.setStrokeMiter(10);		
						textPaint.setStrokeWidth(k * 3 * AlApp.main_metrics.density);
						if ((old_style & AlStyles.SL_STRIKE) != 0) {
							textPaint.setStrikeThruText(false);
							canvas.drawText(drawArr, 0, 1, x, y, textPaint);
							textPaint.setStrikeThruText(true);
						} else 
							canvas.drawText(drawArr, 0, 1, x, y, textPaint);
		                
		                textPaint.setStyle(Style.FILL);
		                textPaint.setColor(ProfileManager.getColor(ProfileManager.COLOR_BG, true));
		                canvas.drawText(drawArr, 0, 1, x, y, textPaint);
						
		                textPaint.setColor(cl);
					} else {*/
						if ((old_style & AlStyles.SL_SHADOW) != 0) {
							x2 = fontParam.color;
							fontParam.color = profiles.colors[InternalConst.TAL_PROFILE_COLOR_SHADOW];
							calc.drawText(x + preferences.picture_need_tuneK, y + preferences.picture_need_tuneK, 
								oi.text, start, end - start + 1, fontParam);
							fontParam.color = x2;
						}
						calc.drawText(x, y, oi.text, i, 1, fontParam);
					//}
					x += oi.width[i];
				}
			} else {
				//System.arraycopy(oi.text, start, drawArr, 0, end - start + 1);
				
				/*if (k != 0) {
					int cl = textPaint.getColor();
					
					textPaint.setStyle(Style.STROKE);
					textPaint.setStrokeJoin(Join.ROUND);
					textPaint.setStrokeMiter(10);		
					textPaint.setStrokeWidth(k * 3 * AlApp.main_metrics.density);
					if ((old_style & AlStyles.SL_STRIKE) != 0) {
						textPaint.setStrikeThruText(false);
						canvas.drawText(drawArr, 0, end - start + 1, x, y, textPaint);
						textPaint.setStrikeThruText(true);
					} else
						canvas.drawText(drawArr, 0, end - start + 1, x, y, textPaint);
	                
	                textPaint.setStyle(Style.FILL);
	                textPaint.setColor(ProfileManager.getColor(ProfileManager.COLOR_BG, true));
	                canvas.drawText(drawArr, 0, end - start + 1, x, y, textPaint);
					
	                textPaint.setColor(cl);
				} else {*/
					if ((old_style & AlStyles.SL_SHADOW) != 0) {
						x2 = fontParam.fnt.getColor();
						fontParam.fnt.setColor(profiles.colors[InternalConst.TAL_PROFILE_COLOR_SHADOW] | 0xff000000);
						calc.drawText(x + preferences.picture_need_tuneK, y + preferences.picture_need_tuneK, 
							oi.text, start, end - start + 1, fontParam);
						fontParam.fnt.setColor(x2);
					}
					calc.drawText(x, y, oi.text, start, end - start + 1, fontParam);

				//}
				
				for (i = start; i <= end; i++) 
					x += oi.width[i];
			}
		}
		
		return x;
	}


	void drawColumn(AlOnePage page, int x0, int y0, int x1, int y1) {

				
		boolean first_notes = true;
		AlOneItem oi = null;
		int x;
		int col_count = page.countItems;
		
		int z, i, j, y = y0 + page.topMarg, start = 0, end = 0;
		for (z = 0; z < 2; z++) {
			for (j = 0; j < col_count; j++) {
				oi = page.items.get(j);
				if (z == 0) {
					if (oi.isNote)
						continue;
				} else {
					if (!oi.isNote)
						continue;
					if (first_notes) {
						y += page.pageHeight - page.textHeight - page.topMarg + page.notesShift;
						calc.drawLine(x0 + 7, y + 1, x0 + oi.allWidth * 3 / 4, y + 1, 
							preferences.picture_need_tuneK,
							profiles.colors[(int) ((screen_parameters.style_notes & AlStyles.SL_COLOR_MASK) >> AlStyles.SL_COLOR_SHIFT)]);
						first_notes = false;
					}
				}

				start = end = 0;
				x = x0 + oi.isLeft + oi.isRed;
				x = (int)(x + 0.5f);
				y += oi.height + oi.base_line_up;
				
				if (oi.count > 0) {
					
					if (oi.isStart && ((oi.justify & AlStyles.SL_JUST_RIGHT) == 0) && ((oi.style[0] & AlStyles.SL_UL_BASE) != 0)) {
						int ul = (int) ((oi.style[0] >> AlStyles.SL_UL_SHIFT) & AlStyles.SL_UL_MASK);
						long stl = oi.style[0] & (~((long)AlStyles.PAR_STYLE_MASK));
						fonts.modifyPaint(fontParam, old_style, stl, profiles, true);	
						old_style = stl;
						char ch = 0x2022;
						
						switch (ul) {
						case 0x01: case 0x04: case 0x07: case 0x0a: case 0x0d: ch = (char)0x2022; break;
						case 0x02: case 0x05: case 0x08: case 0x0b: case 0x0e: ch = (char)0x25E6; break;
						case 0x03: case 0x06: case 0x09: case 0x0c: case 0x0f: ch = (char)0x25AA; break;
						}
						
						calc.drawText(x - screen_parameters.redList, y, ch, fontParam);
					}
					
					for (i = 0; i < oi.count; i++) {
						if (oi.text[i] == 0x20) {
							if (end >= start) {
								x = drawPartItem(start, end, old_style, x, y, oi, page);
							}
							if (((old_style & (AlStyles.STYLE_LINK | AlStyles.STYLE_UNDER)) != 0)
									&& (i + 1 < oi.count)
									&& ((oi.style[i + 1] & (AlStyles.STYLE_LINK | AlStyles.STYLE_UNDER)) != 0)) {
								calc.drawLine(x, y + 2, x + oi.width[i] + 1, y + 2, 
									preferences.picture_need_tuneK, fontParam.color);
							}
							if (((old_style & (AlStyles.SL_SELECT | AlStyles.SL_MARK)) != 0)
									&& (i + 1 < oi.count)
									&& ((oi.style[i + 1] & (AlStyles.SL_SELECT | AlStyles.SL_MARK)) != 0)) {

								int sm;
								if ((old_style & (AlStyles.SL_MARK | AlStyles.SL_SELECT)) == (AlStyles.SL_MARK | AlStyles.SL_SELECT)) {
									sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECTMARK];
								} else
								if ((old_style & AlStyles.SL_SELECT) != 0) {
									sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT];
								} else {
									sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK];
								}
								calc.drawRect(x, y - oi.base_line_up, x + oi.width[i], y + oi.base_line_down, sm);
							}
							x += oi.width[i];

							start = i + 1;
						} else if ((oi.style[i] & AlStyles.LMASK_DRAW_STYLE) != (old_style & AlStyles.LMASK_DRAW_STYLE)) {
							if (end > start) {
								x = drawPartItem(start, end, old_style, x, y, oi, page);
							} else if (end == start && i != 0 && oi.text[start] != 0x20) {
								x = drawPartItem(start, end, old_style, x, y, oi, page);
							}
							fonts.modifyPaint(fontParam, old_style, oi.style[i], profiles, true);
							old_style = oi.style[i];
							start = i;
						}
						end = i;
					}
					if (end >= start) {
						x = drawPartItem(start, end, old_style, x, y, oi, page);
					}
				}
				y += oi.base_line_down;
				y += oi.interline;
			}
		}
	}


	protected TAL_NOTIFY_RESULT openBookInThread(String fName, AlBookOptions bookOptions) {

		String currName;
		String prevExt;
		int	ftype = 0;	
		TAL_FILE_TYPE ft;
		
		ftype = fName.indexOf(EngBookMyType.AL_FILENAMES_SEPARATOR);
		if (ftype == -1) {
			currName = fName;
			fName = "";
		} else {
			currName = fName.substring(0, ftype);
			fName = fName.substring(ftype + 1);
		}		

		//Log.e("files open start", Long.toString(System.currentTimeMillis()));
		
		AlFiles activeFile = new AlFilesBypass();
		activeFile.setLoadTime(true);

		activeFile.initState(currName, null, null);
		while (true) {
			if (activeFile.getSize() < 1) {
				activeFile = null;
				openState.decState();				
				return TAL_NOTIFY_RESULT.ERROR;
			}

			ftype = currName.lastIndexOf('.');
			if (ftype == -1) {
				prevExt = null;
			} else {
				prevExt = currName.substring(ftype);
			}

			ftype = fName.indexOf(':');
			if (ftype == -1) {
				currName = fName;
				fName = "";
			} else {
				currName = fName.substring(0, ftype);
				fName = fName.substring(ftype + 1);
			}
		
			AlFiles a = activeFile; 

			ArrayList<AlFileZipEntry> fList = new ArrayList<AlFileZipEntry>(0);
			fList.clear();
			ft = AlFilesZIP.isZIPFile(currName, a, fList, prevExt);		
			if (ft == TAL_FILE_TYPE.ZIP) {
				activeFile = new AlFilesZIP();
				activeFile.initState(currName, a, fList);
				continue;
			}

			break;
		}

		if (AlFormatFB2.isFB2(activeFile)) {
			format = new AlFormatFB2();
		} else 
			format = new AlFormatTXT();

		//Log.e("files open end", Long.toString(System.currentTimeMillis()));
		format.initState(bookOptions, activeFile, preferences, styles);
		format.prepareAll();
		//Log.e("format open end", Long.toString(System.currentTimeMillis()));

		activeFile.setLoadTime(false);

		if (format.getSize() < 1) {
			openState.decState();
			return TAL_NOTIFY_RESULT.ERROR;
		}

		bookPosition = bookOptions.readPosition;
		if (bookPosition < 0 || bookPosition >= format.getSize())
			bookPosition = 0;

		preferences.calcPagesModeUsed = preferences.calcPagesModeRequest;
		if (preferences.calcPagesModeUsed == TAL_SCREEN_PAGES_COUNT.AUTO && 
			format.getSize() < AL_FILESIZEMIN_FOR_AUTOCALC)
			preferences.calcPagesModeUsed = TAL_SCREEN_PAGES_COUNT.SCREEN;

		openState.incState();		
		switch (preferences.calcPagesModeUsed) {
		case SCREEN:
		case AUTO:
			calcCountPages();
			break;
		default:
			format.lastCalcTime = 0;
			break;
		}
		//Log.e("calc page end", Long.toString(System.currentTimeMillis()));
		
		bookPosition = getCorrectPosition(bookPosition);

		openState.incState();		
		return TAL_NOTIFY_RESULT.OK;
	}

	protected TAL_NOTIFY_RESULT createDebugFileInThread(String path) {
		TAL_NOTIFY_RESULT res = format.createDebugFile(path);
		openState.decState();		
		return res;
	}

	protected TAL_NOTIFY_RESULT calcPagesInThread() {
		calcCountPages();
		openState.incState();
		return TAL_NOTIFY_RESULT.OK;
	}

	int needNewCalcPageCount() {
		if (openState.getState() != AlBookState.OPEN) 
			return TAL_RESULT.ERROR;

		if (preferences.calcPagesModeUsed == TAL_SCREEN_PAGES_COUNT.SIZE)
			return TAL_RESULT.OK;

		openState.decState();		
		
		if (preferences.calcPagesModeRequest == TAL_SCREEN_PAGES_COUNT.SCREEN &&
			preferences.calcPagesModeUsed == TAL_SCREEN_PAGES_COUNT.AUTO)
			preferences.calcPagesModeUsed = TAL_SCREEN_PAGES_COUNT.SCREEN;

		AlThreadData.startThread(threadData, TAL_THREAD_TASK.NEWCALCPAGES);	
		return TAL_RESULT.OK;
	}

	public int createDebugFile(String path) {
		if (openState.getState() != AlBookState.OPEN) 
			return TAL_RESULT.ERROR;
		
		openState.incState();		

		threadData.param_char1 = path;
		AlThreadData.startThread(threadData, TAL_THREAD_TASK.CREATEDEBUG);	
		return TAL_RESULT.OK;
	}

	
	public int openBook(String fName, AlBookOptions bookOptions) {
		switch (openState.getState()) {
		case AlBookState.OPEN:
			//openState.decState();
			//closeBookInThread();
			closeBook();
			//break;
		case AlBookState.NOLOAD:
			openState.incState();		
			break;
		default:
			return TAL_RESULT.ERROR;
		}		
				

		threadData.param_void1 = bookOptions;
		threadData.param_char1 = fName;
		AlThreadData.startThread(threadData, TAL_THREAD_TASK.OPENBOOK);	
		return TAL_RESULT.OK;
	}

	/*protected TAL_NOTIFY_RESULT closeBookInThread() {
		format = null;
		openState.decState();
		return TAL_NOTIFY_RESULT.OK;
	}*/

	public int closeBook() {
		if (openState.getState() != AlBookState.OPEN) 
			return TAL_RESULT.ERROR;
		
		openState.decState();	
		openState.decState();
		format = null;
		openState.decState();

		return returnOkWithRedraw();
		/*AlThreadData.startThread(threadData, TAL_THREAD_TASK.CLOSEBOOK);	
		return TAL_RESULT.OK;*/
	}

	ArrayList<String> getAuthors(){	
		if (openState.getState() != AlBookState.OPEN)
			return null;
		
		if (format.bookAuthors.size() < 1)
			return null;
		
		return format.bookAuthors;
	};

	String getTitle(){	
		if (openState.getState() != AlBookState.OPEN)
			return null;
			
		return format.bookTitle;
	};	

	public ArrayList<AlOneSearchResult> getFindTextResult() {	
		if (openState.getState() != AlBookState.OPEN)
			return null;
		
		if (format.resfind.size() < 1)
			return null;
		
		return format.resfind;
	};

	protected TAL_NOTIFY_RESULT findTextInThread(String find) {
		TAL_NOTIFY_RESULT res = format.findText(find);
		shtamp.value++;
		openState.decState();		
		return res;
	}

	public int  findText(String find) {
		if (openState.getState() != AlBookState.OPEN)
			return TAL_RESULT.ERROR;
		
		openState.incState();

		threadData.param_char1 = find;
		AlThreadData.startThread(threadData, TAL_THREAD_TASK.FIND);
		return TAL_RESULT.OK;
	}

	public int setServiceBitmap(AlBitmap errorImage, AlBitmap tableImage, AlBitmap waitImage) {
		errorBitmap = errorImage;
		tableBitmap = tableImage;
		waitBitmap = waitImage;	
		return TAL_RESULT.OK;
	}

	public int setNewScreenSize(int width, int height) {
		if (screenWidth == width && screenHeight == height)
			return TAL_RESULT.OK;

		if (width <= 0 || height <= 0)
			return TAL_RESULT.ERROR;

		switch (openState.getState()) {
		case AlBookState.NOLOAD:
			screenWidth = width;
			screenHeight = height;			
			EngBitmap.reCreateBookBitmap(bmp[0], screenWidth, screenHeight, shtamp);
			shtamp.value++;
			break;
		case AlBookState.OPEN:
			screenWidth = width;
			screenHeight = height;			
			EngBitmap.reCreateBookBitmap(bmp[0], screenWidth, screenHeight, shtamp);
			shtamp.value++;			
			needNewCalcPageCount();
			break;
		} 

		return bmp[0].bmp != null ? TAL_RESULT.OK : TAL_RESULT.ERROR;
	}

	final static void addW2I(AlOneItem oi, AlOneWord tword, int cnt) {
		for (int wcurr = 0; wcurr < cnt; wcurr++) {
			if ((tword.style[wcurr] & AlStyles.SL_IMAGE) != 0) {
				if (oi.needHeihtImage0 && oi.interline < 0) {
					oi.height -= oi.interline; 
					oi.needHeihtImage0 = false;
				}
				oi.cntImage++;
			}
			oi.text[oi.count] = tword.text[wcurr];
			oi.style[oi.count] = tword.style[wcurr];
			oi.pos[oi.count] = tword.pos[wcurr];
			oi.width[oi.count] = (int) tword.width[wcurr];
			
			if (oi.base_line_down < tword.base_line_down[wcurr])
				oi.base_line_down = tword.base_line_down[wcurr];
			if (oi.base_line_up < tword.base_line_up[wcurr])
				oi.base_line_up = tword.base_line_up[wcurr];
					
			oi.count++;
			if (oi.count >= oi.realLength) 
				AlOneItem.incItemLength(oi);
		}

	}

	final static void addC2I0(AlOneItem oi, char ch, int need_width) {
		oi.text[oi.count] = ch;
		oi.style[oi.count] = oi.style[oi.count - 1];
		if ((oi.style[oi.count] & AlStyles.SL_IMAGE) != 0)
			oi.style[oi.count] &= AlStyles.LMASK_SPECIALHYHP;
		oi.pos[oi.count] = -1;
		oi.width[oi.count] = need_width;
				
		oi.count++;
		if (oi.count >= oi.realLength) 
			AlOneItem.incItemLength(oi);
	}

	void initOneItem(AlOneItem oi, AlOneItem poi, long style,
								  int pos, int width, boolean addEmptyLine, TAL_CALC_MODE calcMode) {
		oi.allWidth = width;
		oi.textWidth = 0;		
		oi.height = 0;
		oi.needHeihtImage0 = addEmptyLine;
		oi.cntImage = 0;
		oi.isEnd = oi.isStart = false;		
		oi.isRed = oi.isLeft = oi.isRight = 0;
		oi.start_pos = pos;
		oi.justify = style & AlStyles.SL_JUST_MASK;
		
		oi.base_line_down = screen_parameters.interFH_1[(int) ((style & AlStyles.SL_INTER_MASK) >> AlStyles.SL_INTER_SHIFT)];
		oi.base_line_up = screen_parameters.interFH_2[(int) ((style & AlStyles.SL_INTER_MASK) >> AlStyles.SL_INTER_SHIFT)];		
		if (oi.base_line_down < 2)
			oi.base_line_down = 2;
		if (oi.base_line_up < 2)
			oi.base_line_up = 2;
		
		oi.isNote = false;
		oi.isPrepare = false;
		oi.spaceAfterHyph0 = 0;		
		
		switch ((int) (style & AlStyles.SL_INTER_ADDMASK)) {
		case (int)AlStyles.SL_INTER_ADD100: 
			oi.interline = 0;
			break;
		case (int)AlStyles.SL_INTER_ADDTEXT: 
			oi.interline = screen_parameters.interFI0[InternalConst.INTER_TEXT] * 
				screen_parameters.interFH_0[(int) ((style & AlStyles.SL_INTER_MASK) >> AlStyles.SL_INTER_SHIFT)] / 100;
			break;	
		case (int)AlStyles.SL_INTER_ADDNOTES: 
			oi.interline = screen_parameters.interFI0[InternalConst.INTER_NOTE] * 
				screen_parameters.interFH_0[(int) ((style & AlStyles.SL_INTER_MASK) >> AlStyles.SL_INTER_SHIFT)] / 100;
			break;	
		case (int)AlStyles.SL_INTER_ADDFONT: 
			oi.interline = screen_parameters.interFI0[(int) ((style & AlStyles.SL_INTER_MASK) >> AlStyles.SL_INTER_SHIFT)] * 
				screen_parameters.interFH_0[(int) ((style & AlStyles.SL_INTER_MASK) >> AlStyles.SL_INTER_SHIFT)] / 100;
			break;		
		}

		if (calcMode == TAL_CALC_MODE.NOTES) {
			oi.justify = 0;
			oi.isNote = true;
			return;
		}
		
		if ((style & AlStyles.SL_PAR) != 0) {	
			oi.isStart = true;
			
			if (((style & AlStyles.SL_REDLINE) != 0) && ((style & AlStyles.SL_UL_BASE) == 0)) {
				if (!profiles.classicFirstLetter || (style & AlStyles.SL_MARKFIRTSTLETTER) == 0) {
					oi.isRed = screen_parameters.redLine;
					oi.allWidth -= oi.isRed;
				}
			}
			
			if (addEmptyLine) {
				
				if ((poi != null) && (style & AlStyles.SL_STANZA) != 0 && (poi.style[0] & AlStyles.SL_STANZA) != 0) {

				} else {
					switch (screen_parameters.redParV) {
					case 10: oi.height += fontParam.height * 0.1f; break;
					case 20: oi.height += fontParam.height * 0.2f; break;
					case 30: oi.height += fontParam.height * 0.3f; break;
					case 40: oi.height += fontParam.height * 0.4f; break;
					case 50: oi.height += fontParam.height * 0.5f; break;
				}
				}
			
				if (screen_parameters.summRedV == 0 && (style & (AlStyles.SL_PREV_EMPTY_1 + AlStyles.SL_PREV_EMPTY_0)) == (AlStyles.SL_PREV_EMPTY_1 + AlStyles.SL_PREV_EMPTY_0)) {
					if ((style & AlStyles.SL_PREV_EMPTY_1) != 0) { 
						switch (screen_parameters.redLineV > screen_parameters.redStyleV ? screen_parameters.redLineV : screen_parameters.redStyleV) {
						case  25: oi.height += fontParam.height * 0.25f;  break;
						case  50: oi.height += fontParam.height * 0.5f;  break;
						case  75: oi.height += fontParam.height * 0.75f; break;
						case 125: oi.height += fontParam.height * 1.25f; break;
						case 150: oi.height += fontParam.height * 1.5f;  break;
						default:  oi.height += fontParam.height; break;
						}
					}
				} else {
					if ((style & (AlStyles.SL_PREV_EMPTY_1 + AlStyles.SL_PREV_EMPTY_0)) != 0) { 
						switch (screen_parameters.redLineV) {
						case  25: oi.height += fontParam.height * 0.25f;  break;
						case  50: oi.height += fontParam.height * 0.5f;  break;
						case  75: oi.height += fontParam.height * 0.75f; break;
						case 125: oi.height += fontParam.height * 1.25f; break;
						case 150: oi.height += fontParam.height * 1.5f;  break;
						default:  oi.height += fontParam.height; break;
						}
					}				
				}
				
				
				if ((style & AlStyles.SL_BREAK) != 0)
					oi.height += InternalConst.BREAK_HEIGHT;
				if (poi != null && poi.count == 1 && ((poi.style[0] & AlStyles.SL_IMAGE) != 0) && 
						((poi.style[0] & AlStyles.SL_MARKCOVER) != 0)) {
					oi.height += InternalConst.BREAK_HEIGHT;
				}

			}
		} else {
			if ((style & AlStyles.SL_JUSTIFY_POEM) != 0) {
				if ((style & AlStyles.SL_MARKTITLE) != 0) { 
							
				} else {
					if (oi.justify == AlStyles.SL_JUST_NONE || oi.justify == AlStyles.SL_JUST_LEFT) {
						oi.justify = AlStyles.SL_JUST_RIGHT;
						oi.isRed = screen_parameters.redLine;
						oi.allWidth -= oi.isRed;
					} else 
					if (oi.justify == AlStyles.SL_JUST_RIGHT) {
						oi.justify = AlStyles.SL_JUST_LEFT;
						oi.isRed = screen_parameters.redLine;
						oi.allWidth -= oi.isRed;
					}
				}
			}
			
			if (profiles.classicFirstLetter) {
				if (poi != null && poi.count > 0 && (poi.style[0] & AlStyles.SL_MARKFIRTSTLETTER) != 0) {
					oi.isRed = poi.isRed + poi.width[0];
					oi.isLeft = poi.isLeft;
					oi.allWidth -= oi.isLeft + oi.isRed;					
					oi.height -= fontParam.height + screen_parameters.interFI0[InternalConst.INTER_TEXT] * screen_parameters.interFH_0[0] / 100;
					
					for (int j = 1; j < poi.count; j++) {
						if ((poi.style[j] & AlStyles.SL_MARKFIRTSTLETTER) == 0)
							break;
						oi.isRed += poi.width[j];
						oi.allWidth -= poi.width[j];
					}					
				}
			}
		}
		
		if ((style & AlStyles.SL_MARGL_MASK) != 0) {
			if ((style & AlStyles.SL_MARGL_MASK) == AlStyles.SL_MARGL1) {
				oi.isLeft = screen_parameters.redStyle1;
			} else
			if ((style & AlStyles.SL_MARGL_MASK) == AlStyles.SL_MARGL2) {
				oi.isLeft = screen_parameters.redStyle2;	
			} else {
				oi.isLeft = screen_parameters.redStyle3;
			}
			oi.allWidth -= oi.isLeft;
		}
		if ((style & AlStyles.SL_MARGR_MASK) != 0) {
			if ((style & AlStyles.SL_MARGR_MASK) == AlStyles.SL_MARGR1) {
				oi.isRight = screen_parameters.redStyle1;
			} else
			if ((style & AlStyles.SL_MARGR_MASK) == AlStyles.SL_MARGR2) {
				oi.isRight = screen_parameters.redStyle2;	
			} else {
				oi.isRight = screen_parameters.redStyle3;		
			}
			oi.allWidth -= oi.isRight;
		}
		
		if (oi.allWidth < (width >> 3)) {
			int u = (int) ((width >> 3) - oi.allWidth);
			oi.allWidth += u;
			
			if (oi.isRight > u) {
				oi.isRight -= u;
				u = 0;
			} else {
				u -= oi.isRight;
				oi.isRight = 0;
			}
			
			if (u != 0) {
				if (oi.isLeft > u) {
					oi.isLeft -= u;
					u = 0;
				} else {
					u -= oi.isLeft;
					oi.isLeft = 0;
				}
			}
		}
		
		if (((oi.justify & AlStyles.SL_JUST_RIGHT) == 0) && ((style & AlStyles.SL_UL_BASE) != 0)) {
			int ul = (int) ((style >> AlStyles.SL_UL_SHIFT) & AlStyles.SL_UL_MASK);
			if (ul > 0) {
				ul *= screen_parameters.redList; 
				while (ul > (oi.allWidth / 2)) 
					ul -= screen_parameters.redList;
				
				oi.isLeft += ul;
				oi.allWidth -= ul;
			}			
		}
	}

	void addLinkToEndNotes(AlOneItem oi, int pos) {
		int num = oi.count - 1;
		
		if (num < 0) {
			oi.count = 1;
			oi.style[0] = 0;
			num++;
		} 
		
		if (num < 1) {
			oi.style[num] &= AlStyles.SL_COLOR_IMASK;
			oi.style[num] |= AlStyles.SL_COLOR_LINK;
			oi.style[num] |= AlStyles.SL_LINK;
			
			oi.pos[num] = pos;
			oi.width[num] *= 2;
			
			oi.text[num] = 0x2026;
		} else {										
			num--;
			
			oi.style[num] &= AlStyles.SL_COLOR_IMASK - AlStyles.SL_SUB - AlStyles.SL_SUP;
			oi.style[num] |= AlStyles.SL_COLOR_LINK;
			oi.style[num] |= AlStyles.SL_LINK;
			
			oi.pos[num] = pos;
			oi.text[num] = 0x2026;
			
			oi.width[num] += oi.width[num + 1];
			
			oi.count--;
		}
	}


	boolean addNotesToPage(int width, AlOnePage page,
				int start_point, int end_point) {
			
		note_word.need_flags = 0;
		note_word.count = 0;
			
		int start = start_point, i, j;
		char ch;
		while (start < end_point) {
			j = format.getNoteBuffer(start, format_note_and_style, shtamp.value, profiles);
			i = start - (start & AlFiles.LEVEL1_FILE_BUF_MASK);
			
			for (; i < j; i++, start++) {
				if (start >= end_point)
					break;
				if ((ch = format_note_and_style.txt[i]) == 0x00)
					continue;	
				
				if ((format_note_and_style.stl[i] & AlStyles.SL_PAR) != 0) {					
					if (note_word.count + 3 < EngBookMyType.AL_WORD_LEN && 
							note_word.count > 0 &&
							(note_word.style[note_word.count - 1] & AlStyles.SL_MARKTITLE) != 0) {
						note_word.text[note_word.count] = 0xa0;
						note_word.pos[note_word.count]	= start;
						note_word.style[note_word.count] = 0L;
						note_word.count++;
						note_word.text[note_word.count] = 0x2022;//0x2014;
						note_word.pos[note_word.count]	= start;
						note_word.style[note_word.count] = 0L;
						note_word.count++;
						note_word.text[note_word.count] = 0xa0;
						note_word.pos[note_word.count]	= start;
						note_word.style[note_word.count] = 0L;
						note_word.count++;
					} else
					if (note_word.count != 0) {
						if (addWord(note_word, page, width, TAL_CALC_MODE.NOTES))
							return false;
					}
				}
				
				if (ch == 0x20 || ch == 0x3000) {
					if (note_word.count != 0) {
						if (addWord(note_word, page, width, TAL_CALC_MODE.NOTES))
							return false;			
					}
				} else {
					if (0x301 == ch && note_word.count > 0 && preferences.u301mode != 0) {
						if (2 == preferences.u301mode)
							continue;
						note_word.style[note_word.count - 1] ^= 0x03;
					} else {
						note_word.text[note_word.count] 	= format_note_and_style.txt[i];
						note_word.style[note_word.count] 	= format_note_and_style.stl[i];
						note_word.pos[note_word.count]	= start;
						
						note_word.count++;
						if (note_word.count >= EngBookMyType.AL_WORD_LEN) {
							note_word.need_flags |= InternalConst.AL_ONEWORD_FLAG_NOINSERTALL;
							if (addWord(note_word, page, width, TAL_CALC_MODE.NOTES))
								return false;
							note_word.need_flags &= ~InternalConst.AL_ONEWORD_FLAG_NOINSERTALL;
						}
					}
				}				
			}
		}
		boolean res = true;
		
		if (note_word.count != 0)
			res = !addWord(note_word, page, width, TAL_CALC_MODE.NOTES);				
		res = res && (!addWord(note_word, page, width, TAL_CALC_MODE.NOTES));			
		
		return res;
	}

	boolean addItem2Page0(AlOneItem oi, AlOnePage page, TAL_CALC_MODE calcMode, int width) {
		if (oi.count == 0)
			return false;	
		
		int old_h = page.textHeight;
		
		if (calcMode == TAL_CALC_MODE.NOTES) {
			page.textHeight += oi.height + oi.base_line_down + oi.base_line_up;
			if (page.notePresent && oi.interline < 0) {
				page.textHeight += oi.interline;				
			}
			
			if (!page.notePresent) {
				page.textHeight += page.notesShift;
			}			
			
			page.notePresent = true;
		} else {
			page.textHeight += oi.height + oi.base_line_down + oi.base_line_up + oi.interline;
		}
		
		if (oi.cntImage > 0 && oi.interline < 0) {		
			if (page.textHeight - oi.interline < page.pageHeight) {
				oi.base_line_down -= oi.interline;
				page.textHeight -= oi.interline;
			}
		}
		
		if (page.textHeight - old_h < InternalConst.MIN_ITEM_HEIGHT) {
			if (!(profiles.classicFirstLetter && 
					page.countItems > 0 && 
					(page.items.get(page.countItems - 1).style[0] & AlStyles.SL_MARKFIRTSTLETTER) != 0)) {
				old_h = InternalConst.MIN_ITEM_HEIGHT - page.textHeight + old_h;
				oi.base_line_down += old_h;
				page.textHeight += old_h;
			}
		}

		int test_item = page.countItems; 

		page.countItems++;
		if (page.countItems >= page.realLength)
			AlOnePage.addItem(page);
		page.items.get(page.countItems).count = 0;

		if (calcMode == TAL_CALC_MODE.NORMAL && preferences.notesOnPage) {
			int k;					
			for (k = 0; k < page.items.get(test_item).count; k++) {
				if ((page.items.get(test_item).style[k] & AlStyles.SL_MARKNOTE0) != 0) {
					AlOneLink al = null;
					String link = format.getLinkNameByPos(page.items.get(test_item).pos[k]);
					if (link != null)
						al = format.getLinkByName(link, true);
					if (al != null && al.iType == 1 && al.positionE != -1) {
						if (al.counter != notesCounter) {
							int fc0 = page.countItems;
							notesItemsOnPage = 0;
							boolean notes_full = addNotesToPage(width, page, al.positionS, al.positionE);

							if (//preferences.calcPagesModeUsed != TAL_SCREEN_PAGES_COUNT_BY_SIZE &&
								preferences.maxNotesItemsOnPageUsed == 1 && 
								notesItemsOnPage == 0 && 
								page.countItems > 1) {
								for (int m = page.countItems - 1; m >= test_item; m--) {
									page.items.get(test_item - 1).base_line_down += 
										page.items.get(m).height + page.items.get(m).base_line_down + page.items.get(m).base_line_up;
								}
								page.countItems = test_item;
							} else
							if (fc0 < page.countItems) {
								al.counter = notesCounter;
								if (!notes_full)									
									addLinkToEndNotes(page.items.get(page.countItems - 1), page.items.get(test_item).pos[k]);
							}
						}
					}
				}
			}
		}
		
		return false;
	}

	boolean addWordToItem0(AlOneWord tword, AlOnePage page, int width, TAL_CALC_MODE calcMode) {
		AlOneItem oi = page.items.get(page.countItems);
		
		int wlen;
		int word_len = 0;
		int i, j;
		for (i = 0; i < tword.count; i++)
			word_len += tword.width[i];
		
		if (oi.count == 0) { 
			{
				AlOneItem poi = null;
				for (j = page.countItems - 1; j >= 0; j--)
					if (!page.items.get(j).isNote) {
						poi = page.items.get(j);
						break;
					}
				
				initOneItem(oi, poi, tword.style[0], tword.pos[0], width, page.countItems != 0, calcMode);				
			}
			
			if (word_len <= oi.allWidth || tword.count == 1) {
				oi.textWidth += word_len;
				addW2I(oi, tword, tword.count);				
				return false;
			} 

			if (tword.count > 3) {
				if ((tword.need_flags & InternalConst.AL_ONEWORD_FLAG_DOHYPH) == 0) {
					hyphFlag.value = tword.need_flags;
					hyphen.getHyph(tword.text, tword.hyph, tword.count, hyphFlag);
					tword.need_flags = hyphFlag.value;
				}
				
				if (calcWordLenForPages)
					updateWordLength(tword);
				
				tword.complete = tword.count;
				wlen = word_len;			
				do  {
					tword.complete--;
					wlen -= tword.width[tword.complete];
					
					if (tword.hyph[tword.complete] == 'D') {
						if (wlen <= oi.allWidth) {
							oi.textWidth += wlen;
							addW2I(oi, tword, tword.complete);
							
							oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
							oi.textWidth += oi.spaceAfterHyph0;
							return false;						
						}
					}			
					if (tword.complete == 1)
						break;
				} while (true);
				
				if ((tword.style[0] & AlStyles.SL_HYPH) != 0) {
					// РїСЂРѕРІРµСЂСЏРµРј РЅР° РґРµС„РёСЃС‹ Рё РїРµСЂРµРЅРѕСЃС‹, РµСЃР»Рё РїРѕСЃР»РµРґРЅРёРµ СЂР°Р·СЂРµС€РµРЅС‹
					tword.complete = tword.count;
					wlen = word_len;			
					do  {
						tword.complete--;
						wlen -= tword.width[tword.complete];
						
						switch (tword.hyph[tword.complete]) {
						case 'B':
							if (wlen <= oi.allWidth) {
								oi.textWidth += wlen;
								addW2I(oi, tword, tword.complete);
								
								oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
								oi.textWidth += oi.spaceAfterHyph0;
								return false;						
							}
							break;
						case '-':
							if (tword.width[tword.complete] != 0) {
								if (wlen + fontParam.hyph_width_current <= oi.allWidth) {
									oi.textWidth += wlen + fontParam.hyph_width_current;
									
									addW2I(oi, tword, tword.complete);
									addC2I0(oi, '-', fontParam.hyph_width_current);
									
									oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
									oi.textWidth += oi.spaceAfterHyph0;
									return false;
								}
							}
							break;							
						}			
						if (tword.complete == 1)
							break;
					} while (true);
				} else {
					// РїСЂРѕРІРµСЂСЏРµРј РЅР° РґРµС„РёСЃС‹ 
					tword.complete = tword.count;
					wlen = word_len;			
					do  {
						tword.complete--;
						wlen -= tword.width[tword.complete];
						
						if (tword.hyph[tword.complete] == 'B') {
							if (wlen <= oi.allWidth) {
								oi.textWidth += wlen;
								addW2I(oi, tword, tword.complete);
								
								oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
								oi.textWidth += oi.spaceAfterHyph0;
								return false;						
							}
						}			
						if (tword.complete == 1)
							break;
					} while (true);
					
					// РїСЂРѕРІРµСЂСЏРµРј РЅР° РїРµСЂРµРЅРѕСЃС‹, РґР°Р¶Рµ РµСЃР»Рё РѕРЅРё Р·Р°РїСЂРµС‰РµРЅС‹ - РїРµСЂРµРЅРѕСЃРёС‚СЊ РІСЃРµ СЂР°РІРЅРѕ РЅР°РґРѕ
					tword.complete = tword.count;
					wlen = word_len;			
					do  {
						tword.complete--;
						wlen -= tword.width[tword.complete];
						
						if (tword.hyph[tword.complete] == '-' && tword.width[tword.complete] != 0) {					
							if (wlen + fontParam.hyph_width_current <= oi.allWidth) {
								oi.textWidth += wlen + fontParam.hyph_width_current;
								
								addW2I(oi, tword, tword.complete);
								addC2I0(oi, '-', fontParam.hyph_width_current);
								
								oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
								oi.textWidth += oi.spaceAfterHyph0;
								return false;
							}
						}			
						if (tword.complete == 1)
							break;
					} while (true);
				}
				
				// РїРµСЂРµРЅРѕСЃРёРј РіРґРµ РїСЂРёРґРµС‚СЃСЏ, С‚.Рµ. С‚Р°Рј, РіРґРµ РјРµРЅСЊС€Рµ Р·Р°РїСЂРµС‰РµРЅРѕ
				tword.complete = tword.count;
				wlen = word_len;			
				do  {
					tword.complete--;
					wlen -= tword.width[tword.complete];
					
					if (tword.hyph[tword.complete] == '0') {
						if (wlen + fontParam.hyph_width_current <= oi.allWidth) {
							oi.textWidth += wlen + fontParam.hyph_width_current;
							
							addW2I(oi, tword, tword.complete);
							addC2I0(oi, '-', fontParam.hyph_width_current);
							
							oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
							oi.textWidth += oi.spaceAfterHyph0;
							return false;
						}
					}			
					if (tword.complete == 1)
						break;
				} while (true);
			}
			
			// РїРµСЂРµРЅРѕСЃРёРј РїРѕ Р»СЋР±РѕРјСѓ - РїРѕ РїРѕСЃР»РµРґРЅРµРјСѓ СЃРёРјРІРѕР»Сѓ, РІР»Р°Р·Р°СЋС‰РµРјСѓ РЅР° СЌРєСЂР°РЅРµ
			tword.complete = tword.count;
			wlen = word_len;
			do  {
				tword.complete--;
				
				wlen -= tword.width[tword.complete];	
				if (wlen + fontParam.hyph_width_current <= oi.allWidth || tword.complete == 1) {
					oi.textWidth += wlen + fontParam.hyph_width_current;
					
					addW2I(oi, tword, tword.complete);
					addC2I0(oi, '-', fontParam.hyph_width_current);
					
					oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
					oi.textWidth += oi.spaceAfterHyph0;
					return false;
				}
			} while (true);
			
		} else {
			int need_space_len = false ? 0 : fontParam.space_width_current;			
			
			if ((oi.style[oi.count - 1] & AlStyles.STYLE_RAZR) != 0)
				need_space_len += fontParam.space_width_current;
			
			if (oi.textWidth + need_space_len + word_len <= oi.allWidth) {
				oi.textWidth += need_space_len + word_len;
				
				addC2I0(oi, ' ', need_space_len);
				addW2I(oi, tword, tword.count);
				return false;
			} 
			
			if (tword.count > 3) {
				if ((tword.need_flags & InternalConst.AL_ONEWORD_FLAG_DOHYPH) == 0) {
					hyphFlag.value = tword.need_flags;
					hyphen.getHyph(tword.text, tword.hyph, tword.count, hyphFlag);
					tword.need_flags = hyphFlag.value;
				}
					
				if (calcWordLenForPages)
					updateWordLength(tword);
				
				//
				int word_len2 = word_len;
				int hyph_end_position = tword.count;
				int hyph_start_position = 1;
				
				boolean hyph_last_word = true;
				if ((tword.style[tword.count - 1] & AlStyles.SL_ENDPARAGRAPH) != 0) {
					j = 0;
					for (i = 0; i < oi.count; i++) {
						if (oi.text[i] == 0x20)
							j++;
					}
					
					for (i = hyph_end_position; i > 0; i--) {								
						if (AlUnicode.isLetter(tword.text[i - 1])) {
							hyph_end_position = i;								
							break;
						}
					}
					
					if (hyph_end_position < tword.count) {
						for (i = hyph_end_position; i < tword.count; i++) {								
							word_len2 -= tword.width[i];
						}
					}
									
					if (hyph_end_position < 6) {
						hyph_last_word = (j < 2);							
					} else {
						for (i = 0; i < 3; i++) {								
							hyph_end_position--;								
							word_len2 -= tword.width[hyph_end_position];
						}
					}
					
					hyph_start_position++;					
					if (hyph_start_position >= hyph_end_position) {
						hyph_last_word = false;
					}
				}
				//
				
				if ((tword.style[0] & AlStyles.SL_HYPH) != 0 && hyph_last_word) {
					// РїРµСЂРµРЅРѕСЃС‹ СЂР°Р·СЂРµС€РµРЅС‹
					
					// РїРµСЂРµРЅРѕСЃРёРј РїРѕ РґР»РёРЅРЅРѕРјСѓ С‚РёСЂРµ
					tword.complete = hyph_end_position;//tword.count;
					wlen = word_len2;			
					do {
						tword.complete--;
						wlen -= tword.width[tword.complete];
						
						if (tword.complete != tword.count - 1 && tword.hyph[tword.complete] == 'D') {
							if (oi.textWidth + wlen + need_space_len <= oi.allWidth) {
								oi.textWidth += need_space_len + wlen;
								
								addC2I0(oi, ' ', need_space_len);
								addW2I(oi, tword, tword.complete);
								
								oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
								oi.textWidth += oi.spaceAfterHyph0;
								return false;
							}
						}
						if (tword.complete == hyph_start_position/*1*/)
							break;
					} while (true);
					
					// РїРµСЂРµРЅРѕСЃРёРј РїРѕ РґРµС„РёСЃСѓ РёР»Рё РїРµСЂРµРЅРѕСЃСѓ
					tword.complete = hyph_end_position;//tword.count;
					wlen = word_len2;			
					do {
						tword.complete--;
						wlen -= tword.width[tword.complete];
						
						switch (tword.hyph[tword.complete]) {
						case '-':
							if (tword.width[tword.complete] != 0) {
								if (oi.textWidth + wlen + need_space_len + fontParam.hyph_width_current <= oi.allWidth) {
									oi.textWidth += wlen + need_space_len + fontParam.hyph_width_current;
									
									addC2I0(oi, ' ', need_space_len);
									addW2I(oi, tword, tword.complete);
									addC2I0(oi, '-', fontParam.hyph_width_current);
									
									oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
									oi.textWidth += oi.spaceAfterHyph0;
									return false;
								}
							}
							break;							
						case 'B':
							if (tword.complete != tword.count - 1 && oi.textWidth + wlen + need_space_len <= oi.allWidth) {
								oi.textWidth += need_space_len + wlen;
								
								addC2I0(oi, ' ', need_space_len);
								addW2I(oi, tword, tword.complete);
								
								oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
								oi.textWidth += oi.spaceAfterHyph0;
								return false;
							}
						}			
						if (tword.complete == hyph_start_position/*1*/)
							break;
					} while (true);
				} else {
					// РїРµСЂРµРЅРѕСЃС‹ Р·Р°РїСЂРµС‰РµРЅС‹
					
					// РїСЂРѕРІРµСЂСЏРµРј РЅР° РґР»РёРЅРЅРѕРµ С‚РёСЂРµ
					tword.complete = tword.count;
					wlen = word_len;			
					do {
						tword.complete--;
						wlen -= tword.width[tword.complete];
						
						if (tword.complete != tword.count - 1 && tword.hyph[tword.complete] == 'D') {
							if (oi.textWidth + wlen + need_space_len <= oi.allWidth) {
								oi.textWidth += need_space_len + wlen;
								
								addC2I0(oi, ' ', need_space_len);
								addW2I(oi, tword, tword.complete);
								
								oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
								oi.textWidth += oi.spaceAfterHyph0;
								return false;
							}
						}
						if (tword.complete == hyph_start_position/*1*/)
							break;
					} while (true);
					
					// РїСЂРѕРІРµСЂСЏРµРј РЅР° РґРµС„РёСЃС‹
					tword.complete = tword.count;
					wlen = word_len;			
					do {
						tword.complete--;
						wlen -= tword.width[tword.complete];
						
						if (tword.complete != tword.count - 1 && tword.hyph[tword.complete] == 'B') {
							if (oi.textWidth + wlen + need_space_len <= oi.allWidth) {
								oi.textWidth += need_space_len + wlen;
								
								addC2I0(oi, ' ', need_space_len);
								addW2I(oi, tword, tword.complete);
								
								oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
								oi.textWidth += oi.spaceAfterHyph0;
								return false;
							}
						}
						if (tword.complete == hyph_start_position/*1*/)
							break;
					} while (true);
				}
			}
			
			if ((calcMode != TAL_CALC_MODE.NOTES || notesItemsOnPage < preferences.maxNotesItemsOnPageUsed) && (
				
					(page.textHeight + oi.height + oi.base_line_down + oi.base_line_up + 
					(calcMode == TAL_CALC_MODE.NOTES && !page.notePresent ? page.notesShift : 0) -					 
					(screen_parameters.reservHeight0) <= page.pageHeight) || 

					(page.countItems == 0))
				) {
				
				
				///////////////////////////////////////////////////////////
				int addedItem = page.countItems;
				if (addItem2Page0(oi, page, calcMode, width))
					return true;
				oi = page.items.get(addedItem);
				///////////////////////////////////////////////////////////
				
				if (oi.isNote)
					notesItemsOnPage++;

				tword.complete = tword.count;
			} else {	
				if (oi.isNote) {
					oi.count = 0;
				} else {
					page.end_position = oi.start_pos;
				}
				return true;
			}
		}
		return false;
	}

	void getImageSize(AlOneWord tword, int pos_in_word, AlOnePage page, int width, int num_item, TAL_CALC_MODE calcMode) {
		
		int pos = tword.pos[pos_in_word];
			
		int maxHeight = screen_parameters.free_picture_height;
		int maxWight = screen_parameters.free_picture_width;

		if (calcMode == TAL_CALC_MODE.NOTES) {
			maxHeight >>= 3;
		}
			
		tword.style[pos_in_word] &= AlStyles.SL_COLOR_IMASK & AlStyles.SL_IMAGE_IMASK;
		AlOneImage ai = null;
		String link = null;//format.getImageNameByPos(pos);
		//if (link != null)
		//	ai = format.getImageByName(link);

		/*if (ai != null && ((ai.iType & IMG_MASKGET) != NOT_EXTERNAL_IMAGE)) {
			if (ai.needScan) {
				ai.needScan = false;
				InputStream  is0 = null;
				
				boolean image_in_bytearray = false;
				if (ai.isReadyStream()) {
					image_in_bytearray = true;
				} else {
					switch (ai.iType & AlImage.IMG_MASKGET) {
					case IMG_BASE64:					
						is0 = new MIMEInputStream(format, ai.positionS, ai.positionE);
						break;
					case IMG_MEMO:					
						is0 = new MEMOInputStream(format, ai.positionS, ai.positionE);
						break;
					case IMG_HEX:					
						is0 = new HEXInputStream(format, ai.positionS, ai.positionE);
						break;
					case IMG_TABLE:
						is0 = AlApp.main_resource.openRawResource(R.drawable.im025); 
						break;
					}
					
					if (ai.isAcceptedStream(false)) {
						if (ai.fillStream(is0) && ai.isReadyStream()) {
							image_in_bytearray = true;
						}
					}
				}

				if (is0 != null || image_in_bytearray) {
					try {
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inJustDecodeBounds = true;
						if (image_in_bytearray) {
							BitmapFactory.decodeByteArray(ai.image_data, 0, ai.image_data.length, opts);
						} else {
							switch (ai.iType & AlImage.IMG_MASKGET) {
							case AlImage.IMG_MEMO:
								BitmapFactory.decodeByteArray(((MEMOInputStream)is0).mem, 0, ((MEMOInputStream)is0).memsz, opts);
								break;
							default:
								BitmapFactory.decodeStream(is0, null, opts);
								break;
							}
						}
						if (opts.outMimeType != null)
							if (opts.outHeight != -1 && opts.outWidth != -1) {					
								imageParam.real_height = ai.height = opts.outHeight;
								imageParam.real_width = ai.width = opts.outWidth;
								tword.style[pos_in_word] |= AlStyles.SL_IMAGE_OK;
								
								ai.iType &= ~AlImage.IMG_MASKTYPE;
								if (opts.outMimeType.equalsIgnoreCase("image/jpeg")) {
									ai.iType |= AlImage.IMG_JPG;
								} else
								if (opts.outMimeType.equalsIgnoreCase("image/bmp")) {
									ai.iType |= AlImage.IMG_BMP;
								} else
								if (opts.outMimeType.equalsIgnoreCase("image/gif")) {
									ai.iType |= AlImage.IMG_GIF;
								} else
								if (opts.outMimeType.equalsIgnoreCase("image/png")) {
									ai.iType |= AlImage.IMG_PNG;
								}
							}

					} catch (Exception e) {
						Log.e("read image error", Integer.toString(ai.positionS));
					}
				}
			} else 
			if (ai.width != -1) {
				imageParam.real_height = ai.height;
				imageParam.real_width = ai.width;
				tword.style[pos_in_word] |= SL_IMAGE_OK;
			}
		}*/
		
		if ((tword.style[pos_in_word] & AlStyles.SL_IMAGE_OK) != 0) {
			int scale = 0;
			imageParam.height = imageParam.real_height;
			imageParam.width = imageParam.real_width;
			
			while (imageParam.height > maxHeight || imageParam.width > maxWight) {
				imageParam.height >>= 1;
				imageParam.width >>= 1;
				scale++;
			}
			
			if (preferences.picture_need_tune && 
				((tword.style[pos_in_word] & AlStyles.SL_MARKCOVER) != 0 || scale != 0)) {
				if (scale > 0) {
					scale--;
					imageParam.height <<= 1;
					imageParam.width <<= 1;
				}
				
				float f = Math.min((float)maxHeight / (float)imageParam.height, 
						(float)maxWight / (float)imageParam.width);
				imageParam.height = (int) (imageParam.height * f);
				imageParam.width = (int) (imageParam.width * f);
			} else
			if (preferences.picture_need_tuneK != 1.0f) { 
				if (imageParam.height * preferences.picture_need_tuneK <= maxHeight && 
						imageParam.width * preferences.picture_need_tuneK <= maxWight) {
					
					imageParam.height *= preferences.picture_need_tuneK;
					imageParam.width *= preferences.picture_need_tuneK;
					
					if (scale > 0) scale--;
					if (scale > 0 && preferences.picture_need_tuneK > 2.0f) scale--;
					if (scale > 0 && preferences.picture_need_tuneK > 4.0f) scale--;
				} else 
				if (preferences.picture_need_tune) {
					float f = Math.min((float)maxHeight / (float)imageParam.height, 
							(float)maxWight / (float)imageParam.width);
					
					if (f > 1.1f) {
						imageParam.height = (int) (imageParam.height * f);
						imageParam.width = (int) (imageParam.width * f);
					
						if (scale > 0) scale--;
						if (scale > 0 && f > 2.0f) scale--;
						if (scale > 0 && f > 4.0f) scale--;
					}
				}
			}
			
			if (scale <= 31) {
				tword.style[pos_in_word] |= (long)(scale) << AlStyles.SL_COLOR_SHIFT;
				return;
			}
			
			tword.style[pos_in_word] &= AlStyles.SL_IMAGE_IMASK;
		}

		imageParam.height = errorBitmap.height;
		imageParam.width = errorBitmap.width;
	}

	void updateWordLength(final AlOneWord tword/*, final AlOnePage page, int width*/) {
		int i, j, start = 0, end = 0;
		
		for (i = 0; i < tword.count; i++) {
			if ((tword.style[i] & AlStyles.LMASK_CALC_STYLE) != (old_style & AlStyles.LMASK_CALC_STYLE)) {						
				if (end >= start && i != 0) {
					if ((old_style & AlStyles.SL_IMAGE) != 0) {
						
					} else {
						int t = end - start + 1;

						if (fontParam.style != 0) {
							calc.getTextWidths(fontParam, tword.text, start, t, tword.width, false);						
							
							if ((old_style & AlStyles.SL_SHADOW) != 0
								&& (tword.style[i] & AlStyles.SL_SHADOW) == 0) {
									tword.width[t] += preferences.picture_need_tuneK;
							}
	
							if ((old_style & AlStyles.STYLE_RAZR) != 0) {
								for (j = 0; j < t; j++) {
									tword.width[start + j] += fontParam.space_width_current;											
								}
							}
							
							if (preferences.chinezeFormatting) {
								for (j = 0; j < t; j++) {
									switch (tword.text[j]) {
									case 0xff01:
									case 0xff02:
									case 0xff07:
									case 0xff09:
									case 0xff0c:
									case 0xff0e:
									case 0xff1a:
									case 0xff1b:
									case 0xff1f:
										
									case 0x3001:
									case 0x3002:
									
										tword.width[j] *= 0.7f;
										if (j == 0 || (tword.style[j - 1] & AlStyles.SL_CHINEZEADJUST) == 0)
											tword.style[j] |= AlStyles.SL_CHINEZEADJUST;
										break;
									default:
										break;
									}
								}
							}
						}
					}

				}

				int old_correctitalic = fontParam.correct_italic;
					
				fonts.modifyPaint(fontParam, old_style, tword.style[i], profiles, true);	
				old_style = tword.style[i];

				if (i != 0 && old_correctitalic != 0 && fontParam.correct_italic != 0)
					tword.width[end] += old_correctitalic;

				start = i;
			}
			end = i;
		}				
		if (end >= start && fontParam.style != 0) {
			if ((old_style & AlStyles.SL_IMAGE) != 0) {
				
			} else {
				int t = end - start + 1;

				calc.getTextWidths(fontParam, tword.text, start, t, tword.width, false);
				
				if ((old_style & AlStyles.STYLE_RAZR) != 0) {
					for (j = 0; j < t; j++) {
						tword.width[start + j] += fontParam.space_width_current;						
					}
				}

				if (preferences.chinezeFormatting) {
					for (j = 0; j < t; j++) {
						switch (tword.text[j]) {
						case 0xff01:
						case 0xff02:
						case 0xff07:
						case 0xff09:
						case 0xff0c:
						case 0xff0e:
						case 0xff1a:
						case 0xff1b:
						case 0xff1f:
							
						case 0x3001:
						case 0x3002:
						
							tword.width[j] *= 0.7;
							if (j == 0 || (tword.style[j - 1] & AlStyles.SL_CHINEZEADJUST) == 0)
								tword.style[j] |= AlStyles.SL_CHINEZEADJUST;
							break;
						default:
							break;
						}
					}
				}
			}
			
			if (fontParam.correct_italic != 0)
				tword.width[end] += fontParam.correct_italic;

			if ((old_style & AlStyles.SL_SHADOW) != 0)
				tword.width[end] += preferences.picture_need_tuneK;		
		}
	}


	void calculateWordLength(final AlOneWord tword, final AlOnePage page, 
										  int width, TAL_CALC_MODE calcMode, boolean modeCalcLight) {
		int i, j, start = 0, end = 0;
		
		for (i = 0; i < tword.count; i++) {
			if ((tword.style[i] & AlStyles.LMASK_CALC_STYLE) != (old_style & AlStyles.LMASK_CALC_STYLE)) {						
				if (end >= start && i != 0) {
					if ((old_style & AlStyles.SL_IMAGE) != 0) {
						for (j = 0; j < end - start + 1; j++) {
							getImageSize(tword, start + j, page, width, page.countItems, calcMode);
							tword.width[start + j] = imageParam.width;
							tword.base_line_up[start + j] = imageParam.height;
							tword.base_line_down[start + j] = 2;
						}
					} else {
						int t = end - start + 1;

						//calc.getTextWidths(fontParam, tword.text, start, t, tword.width, modeCalcLight);
						if (fontParam.style == 0) {
							char ch;
							for (j = 0; j < t; j++) {
								ch =  tword.text[start + j];
								if (calc.mainWidth[ch] == AlCalc.UNKNOWNWIDTH) {
									tword.width[start + j] = calc.getOneMainTextCharWidth(fontParam, ch);
								} else {
									tword.width[start + j] = calc.mainWidth[ch];
								}
							}
						} else {
							calc.getTextWidths(fontParam, tword.text, start, t, tword.width, modeCalcLight);
						}
						
						if ((old_style & AlStyles.SL_SHADOW) != 0
							&& (tword.style[i] & AlStyles.SL_SHADOW) == 0) {
								tword.width[t] += preferences.picture_need_tuneK;
						}

						if ((old_style & AlStyles.STYLE_RAZR) != 0) {
							for (j = 0; j < t; j++) {
								tword.base_line_down[start + j] = fontParam.base_line_down;
								tword.base_line_up[start + j] = fontParam.base_line_up;
								tword.width[start + j] += fontParam.space_width_current;											
							}
						} else {
							for (j = 0; j < t; j++) {
								tword.base_line_down[start + j] = fontParam.base_line_down;
								tword.base_line_up[start + j] = fontParam.base_line_up;
							}
						}
						
						if (preferences.chinezeFormatting) {
							for (j = 0; j < t; j++) {
								switch (tword.text[j]) {
								case 0xff01:
								case 0xff02:
								case 0xff07:
								case 0xff09:
								case 0xff0c:
								case 0xff0e:
								case 0xff1a:
								case 0xff1b:
								case 0xff1f:
									
								case 0x3001:
								case 0x3002:
								
									tword.width[j] *= 0.7f;
									if (j == 0 || (tword.style[j - 1] & AlStyles.SL_CHINEZEADJUST) == 0)
										tword.style[j] |= AlStyles.SL_CHINEZEADJUST;
									break;
								default:
									break;
								}
							}
						}
					}

				}

				int old_correctitalic = fontParam.correct_italic;
					
				fonts.modifyPaint(fontParam, old_style, tword.style[i], profiles, true);	
				old_style = tword.style[i];

				if (i != 0 && old_correctitalic != 0 && fontParam.correct_italic != 0)
					tword.width[end] += old_correctitalic;

				start = i;
			}
			end = i;
		}				
		if (end >= start) {
			if ((old_style & AlStyles.SL_IMAGE) != 0) {
				for (j = 0; j < end - start + 1; j++) {
					getImageSize(tword, start + j, page, width, page.countItems, calcMode);
					tword.width[start + j] = imageParam.width;
					tword.base_line_up[start + j] = imageParam.height;
					tword.base_line_down[start + j] = 2;	
				}
			} else {
				int t = end - start + 1;

				//calc.getTextWidths(fontParam, tword.text, start, t, tword.width, modeCalcLight);
// inc speed???
				if (fontParam.style == 0) {
					char ch;
					for (j = 0; j < t; j++) {
						ch =  tword.text[start + j];
						if (calc.mainWidth[ch] == AlCalc.UNKNOWNWIDTH) {
							tword.width[start + j] = calc.getOneMainTextCharWidth(fontParam, ch);
						} else {
							tword.width[start + j] = calc.mainWidth[ch];
						}
					}
				} else {
					calc.getTextWidths(fontParam, tword.text, start, t, tword.width, modeCalcLight);
				}
//				
				
				if ((old_style & AlStyles.STYLE_RAZR) != 0) {
					for (j = 0; j < t; j++) {
						tword.base_line_down[start + j] = fontParam.base_line_down;
						tword.base_line_up[start + j] = fontParam.base_line_up;
						tword.width[start + j] += fontParam.space_width_current;						
					}
				} else {
					//Arrays.fill(tword.base_line_down, start, start + t, fontParam.base_line_down);
					//Arrays.fill(tword.base_line_up, start, start + t, fontParam.base_line_up);
					for (j = 0; j < t; j++) {
						tword.base_line_down[start + j] = fontParam.base_line_down;
						tword.base_line_up[start + j] = fontParam.base_line_up;
					}
				}

				if (preferences.chinezeFormatting) {
					for (j = 0; j < t; j++) {
						switch (tword.text[j]) {
						case 0xff01:
						case 0xff02:
						case 0xff07:
						case 0xff09:
						case 0xff0c:
						case 0xff0e:
						case 0xff1a:
						case 0xff1b:
						case 0xff1f:
							
						case 0x3001:
						case 0x3002:
						
							tword.width[j] *= 0.7;
							if (j == 0 || (tword.style[j - 1] & AlStyles.SL_CHINEZEADJUST) == 0)
								tword.style[j] |= AlStyles.SL_CHINEZEADJUST;
							break;
						default:
							break;
						}
					}
				}
			}
			
			if (fontParam.correct_italic != 0)
				tword.width[end] += fontParam.correct_italic;

			if ((old_style & AlStyles.SL_SHADOW) != 0)
				tword.width[end] += preferences.picture_need_tuneK;		
		}
	}

	boolean addWord(AlOneWord tword, AlOnePage page, int width, TAL_CALC_MODE calcMode) {
		boolean res = false;
		int i, j, start = 0, end = 0;;
		
		tword.need_flags &= ~InternalConst.AL_ONEWORD_FLAG_DOHYPH;
		tword.complete = 0;
		do {	
			if (tword.complete == tword.count && tword.count != 0) {
				tword.complete = 0;
				res = addWordToItem0(tword, page, width, calcMode);
				if (res) {
					tword.count = 0;
					return res;
				}
			} else
			if (tword.complete != 0) {
				start = 0;
				for (i = tword.complete; i < tword.count; i++, start++) {
					tword.text[start] = tword.text[i];
					tword.style[start] = tword.style[i];
					tword.pos[start] = tword.pos[i];
					tword.width[start] = tword.width[i];
					tword.base_line_down[start] = tword.base_line_down[i];
					tword.base_line_up[start] = tword.base_line_up[i];
					tword.hyph[start] = tword.hyph[i];
				}
				tword.count -= tword.complete;
				tword.complete = 0;
				
				if (tword.width[0] == 0 && AlUnicode.isLetterOrDigit(tword.text[0]))
					calculateWordLength(tword, page, width, calcMode, calcWordLenForPages);
				
				if (tword.text[0] > 0x3000 && ((tword.need_flags & InternalConst.AL_ONEWORD_FLAG_NOINSERTALL) != 0))
					return false;
				
				res = addWordToItem0(tword, page, width, calcMode);
				if (res) {
					tword.count = 0;
					return res;
				}
			} else {		
				if (calcMode == TAL_CALC_MODE.NOTES) {
					for (i = 0; i < tword.count; i++) {
						if ((tword.style[i] & AlStyles.SL_CODE) != 0) {
							tword.style[i] &= AlStyles.SL_FONT_MASK | 
								AlStyles.SL_MARKTITLE | AlStyles.SL_BOLD | AlStyles.SL_MASKFORLINK | 
								AlStyles.SL_ITALIC | AlStyles.SL_SUB | AlStyles.SL_SUP | AlStyles.SL_LINK | AlStyles.SL_IMAGE |
								AlStyles.SL_CSTYLE | AlStyles.SL_STRIKE | AlStyles.SL_UNDER;
							if (preferences.styleSumm) 
								tword.style[i] |= screen_parameters.style_notes & AlStyles.SL_FONT_IMASK; else 
								tword.style[i] ^= screen_parameters.style_notes & AlStyles.SL_FONT_IMASK;
						} else 
						if ((tword.style[i] & AlStyles.SL_CSTYLE) != 0 && (tword.style[i] & AlStyles.SL_REMAPFONT) != 0 ) {
							tword.style[i] &= AlStyles.SL_FONT_MASK | 
								AlStyles.SL_MARKTITLE | AlStyles.SL_BOLD | AlStyles.SL_MASKFORLINK | 
								AlStyles.SL_ITALIC | AlStyles.SL_SUB | AlStyles.SL_SUP | AlStyles.SL_LINK | AlStyles.SL_IMAGE |
								AlStyles.SL_CSTYLE | AlStyles.SL_STRIKE | AlStyles.SL_UNDER;
							if (preferences.styleSumm) 
								tword.style[i] |= screen_parameters.style_notes & AlStyles.SL_FONT_IMASK; else 
								tword.style[i] ^= screen_parameters.style_notes & AlStyles.SL_FONT_IMASK;
						} else{
							tword.style[i] &= AlStyles.SL_MARKTITLE | AlStyles.SL_BOLD | AlStyles.SL_MASKFORLINK | 
								AlStyles.SL_ITALIC | AlStyles.SL_SUB | AlStyles.SL_SUP | AlStyles.SL_LINK | AlStyles.SL_IMAGE |
								AlStyles.SL_CSTYLE | AlStyles.SL_STRIKE | AlStyles.SL_UNDER;
							if (preferences.styleSumm) tword.style[i] |= 
								screen_parameters.style_notes; else tword.style[i] ^= screen_parameters.style_notes;
						}
						
						if ((tword.style[i] & AlStyles.SL_LINK) != 0x00) {
							tword.style[i] &= AlStyles.SL_COLOR_IMASK;
							tword.style[i] |= AlStyles.SL_COLOR_LINK;
						} else						
						if ((tword.style[i] & AlStyles.SL_MARKTITLE) != 0x00) {
							tword.style[i] &= AlStyles.SL_COLOR_IMASK & 0xfffffffffffffffcL;
							tword.style[i] |= screen_parameters.style_titlenotes + AlStyles.SL_BOLD;
						}
						
						tword.style[i] |= (tword.style[i] & AlStyles.SL_FONT_MASK) >> 8;
					}
				} else {
					for (i = 0; i < tword.count; i++) {
						if ((tword.style[i] & AlStyles.SL_MARKFIRTSTLETTER) != 0) {
							if (screen_parameters.fletter_colored && !AlUnicode.isLetterOrDigit(tword.text[i])) {
								if (profiles.classicFirstLetter) {
									tword.style[i] &= AlStyles.SL_FONT_IMASK | AlStyles.SL_SIZE_IMASK;
									tword.style[i] |= screen_parameters.fletter_mask0 & (AlStyles.SL_FONT_MASK | AlStyles.SL_SIZE_MASK);
								}
								continue;
							} else {
								tword.style[i] &= AlStyles.SL_COLOR_IMASK | AlStyles.SL_FONT_IMASK | AlStyles.SL_SIZE_IMASK;
								tword.style[i] |= screen_parameters.fletter_mask0;
							}
							tword.style[i] &= ~(0x03);
							tword.style[i] |= screen_parameters.fletter_mask1;
						}
					}

				}
				
				if (tword.count == 0) {
					AlOneItem oi = page.items.get(page.countItems);
					oi.isEnd = true;
					
					if ((calcMode != TAL_CALC_MODE.NOTES || notesItemsOnPage < preferences.maxNotesItemsOnPageUsed) && (
						
							(page.textHeight + oi.height + oi.base_line_down + oi.base_line_up +
							(calcMode == TAL_CALC_MODE.NOTES && !page.notePresent ? page.notesShift : 0) -
							(screen_parameters.reservHeight0) <= page.pageHeight) ||
							(page.countItems == 0))
						) {
						
						////////////////////////////////////////////////////////////////
						if (addItem2Page0(oi, page, calcMode, width))
							return true;
						////////////////////////////////////////////////////////////////

						if (oi.isNote)
							notesItemsOnPage++;
						
					} else {
						if (oi.isNote) {
							oi.count = 0;
						} else {
							page.end_position = oi.start_pos;
						}
						res = true;
					}			
				} else {
					
					calculateWordLength(tword, page, width, calcMode, calcWordLenForPages);

					// clear big space "down line" after first letter
					if ((tword.style[0] & AlStyles.SL_MARKFIRTSTLETTER) != 0) {
						if (profiles.classicFirstLetter) {
							tword.base_line_down[0] = fontParam.def_line_down;
							tword.base_line_up[0] = fontParam.height - fontParam.base_line_down;
							tword.base_line_down[0] += fontParam.height + screen_parameters.interFI0[InternalConst.INTER_TEXT] * 
								screen_parameters.interFH_0[0] / 100;							
						} else {
							if (tword.base_line_down[0] > fontParam.def_line_down)
								tword.base_line_down[0] = fontParam.def_line_down;
						}
						int t;
						for (t = 1; t < tword.count; t++) {
							if ((tword.style[t] & AlStyles.SL_MARKFIRTSTLETTER) == 0) 
								break;
							tword.base_line_down[t] = tword.base_line_down[0];
							tword.base_line_up[t] = tword.base_line_up[0];
						}
					}
					//
					res = addWordToItem0(tword, page, width, calcMode);
					if (res) {
						tword.count = 0;
						return res;
					}
				}
			}

		} while (tword.complete != 0);
		
		tword.count = 0;
		return res;
	}

	void recalcColumn(int width, int height, AlOnePage page, int start_point, TAL_CALC_MODE calc_mode) {
		page.start_position = start_point;
		page.countItems = 0;
		page.items.get(0).count = 0;
		page.selectStart = page.selectEnd = -1;
		page.pageHeight = height;
		page.textHeight = 0;
		page.topMarg = 0;			
		page.notePresent = false;
		page.notesShift = (int) (screen_parameters.interFH_0[0] * 0.6f/* >> 1*/);
		/*if (screen_parameters.interFI0[0] < 0)
			page.notesShift -= screen_parameters.interFI0[0];*/

		tmp_word.need_flags = 0;
		tmp_word.count = 0;		
			
		int start = start_point, i, j;			
		int end = format.getSize();
		boolean	noFirstAdd = false;
		while (start < end) {
			
			j = format.getTextBuffer(start, format_text_and_style, shtamp.value, profiles);		
			i = start - (start & AlFiles.LEVEL1_FILE_BUF_MASK);
			
			char ch;
			for (; i < j; i++, start++) {
				
				if ((ch = format_text_and_style.txt[i]) == 0x00)
					continue;	
				
				if ((format_text_and_style.stl[i] & AlStyles.SL_PAR) != 0) {
					if (tmp_word.count > 0) {
						tmp_word.style[tmp_word.count - 1] |= AlStyles.SL_ENDPARAGRAPH;
						if (addWord(tmp_word, page, width, calc_mode))
							return;
						noFirstAdd = true;
					}
					if (noFirstAdd) {						
						if (addWord(tmp_word, page, width, calc_mode))
							return;
						noFirstAdd = false;
					}
				}

				if (ch == 0x20 ) {
					if (tmp_word.count != 0) {
						if (addWord(tmp_word, page, width, calc_mode))
							return;
						noFirstAdd = true;
					}
				} else {
					if (0x301 == ch && tmp_word.count > 0 && preferences.u301mode != 0) {
						if (2 == preferences.u301mode)
							continue;
						tmp_word.style[tmp_word.count - 1] ^= 0x03;					
					} else {
						tmp_word.text[tmp_word.count] 	= ch;
						tmp_word.style[tmp_word.count] 	= format_text_and_style.stl[i];
						tmp_word.pos[tmp_word.count]	= start;
						
						tmp_word.count++;
						if (tmp_word.count >= EngBookMyType.AL_WORD_LEN) {
							tmp_word.need_flags |= InternalConst.AL_ONEWORD_FLAG_NOINSERTALL;
							if (addWord(tmp_word, page, width, calc_mode))
								return;
							tmp_word.need_flags &= ~InternalConst.AL_ONEWORD_FLAG_NOINSERTALL;
							noFirstAdd = true;
						}
					}
				}
			}
		}		
		if (tmp_word.count > 0)
			addWord(tmp_word, page, width, calc_mode);
		if (!addWord(tmp_word, page, width, calc_mode))
			page.end_position = end;
	}

	void calcCountPages() {
		//calc.beginMain();
		
		calcScreenParameters();
		
		format.lastCalcTime = System.currentTimeMillis();
		pagePositionPointer.clear();

		calcWordLenForPages = true;

		int start_point = 0;
		int end_point = format.getSize();
		while (start_point < end_point) {
			pagePositionPointer.add(AlPagePositionStack.add(start_point, 0));
			
			if ((preferences.calcPagesModeUsed == TAL_SCREEN_PAGES_COUNT.AUTO) &&
				(pagePositionPointer.size() > AL_COUNTPAGES_FOR_AUTOCALC))
				break;
			
			// hack!!!
			if ((preferences.calcPagesModeUsed == TAL_SCREEN_PAGES_COUNT.SCREEN)) {
				
				long times = System.currentTimeMillis() - format.lastCalcTime;
				if (pagePositionPointer.size() > AL_COUNTPAGES_MAX_FORSCREEN && 
					times > AL_TIMESCALC_MAX_FORSCREEN) {				
					preferences.calcPagesModeUsed = TAL_SCREEN_PAGES_COUNT.AUTO;
					Log.e("calculator pages interrupted", Long.toString(times) + '/' + Integer.toString(pagePositionPointer.size()));
				}
			}
			//
			
			notesCounter++;
			if (profiles.twoColumn) {
				recalcColumn(
					(screenWidth >> 1) - screen_parameters.marginR - screen_parameters.marginL, 
					screenHeight - screen_parameters.marginB - screen_parameters.marginT, 
					page0, start_point, TAL_CALC_MODE.NORMAL);
				recalcColumn(
					(screenWidth >> 1) - screen_parameters.marginR - screen_parameters.marginL, 
					screenHeight - screen_parameters.marginB - screen_parameters.marginT, 
					page1, page0.end_position, TAL_CALC_MODE.NORMAL);			
				start_point = page1.end_position;
			} else {
				recalcColumn(
					screenWidth - screen_parameters.marginR - screen_parameters.marginL, 
					screenHeight - screen_parameters.marginB - screen_parameters.marginT, 
					page0, start_point, TAL_CALC_MODE.NORMAL);
				start_point = page0.end_position;
			}
		}

		if (preferences.calcPagesModeUsed == TAL_SCREEN_PAGES_COUNT.AUTO) {				
			if (pagePositionPointer.size() < 4) {
				pageSizes = EngBookMyType.AL_DEFAULT_PAGESIZE;
			} else {
				int sz = pagePositionPointer.get(pagePositionPointer.size() - 1).start - pagePositionPointer.get(1).start;
				int pg = pagePositionPointer.size() - 2;
				pageSizes = sz / pg;
				if (pageSizes < 1)
					pageSizes = 1;
			}
		}

		calcWordLenForPages = false;
		
		format.lastCalcTime = System.currentTimeMillis() - format.lastCalcTime;
		format.lastPageCount = pagePositionPointer.size();
		
		Log.e("last open/calc time", Long.toString(AlFiles.time_load) + '/' + Long.toString(format.lastCalcTime));

		bookPosition = getCorrectPosition(bookPosition);
		if (preferences.calcPagesModeUsed == TAL_SCREEN_PAGES_COUNT.SCREEN) {
			bookPosition = pagePositionPointer.get(bookPosition).start;			
		} else {
			pagePositionPointer.clear();
		}

		//calc.endMain();
	}

	int	getCorrectPosition(int pos) {
		if (preferences.calcPagesModeUsed == TAL_SCREEN_PAGES_COUNT.SCREEN) {
			for (int i = 0; i < pagePositionPointer.size(); i++) {
				if (pagePositionPointer.get(i).start == pos)
					return i;
				if (pagePositionPointer.get(i).start > pos)
					return i > 0 ? i - 1 : 0;
			}
			return pagePositionPointer.size() - 1; 
		}
		return pos;
	}

	public int	getPageCount(AlIntHolder current, AlIntHolder all, AlIntHolder readPosition) {
		if (openState.getState() == AlBookState.OPEN) {
			readPosition.value = bookPosition;

			switch (preferences.calcPagesModeUsed) {
			case SCREEN:
				current.value = getCorrectPosition(bookPosition);
				all.value = pagePositionPointer.size();
				return TAL_RESULT.OK;
			case AUTO:
			case SIZE:
				current.value = (int)(0.5f + (bookPosition / pageSizes)) + 1;
				all.value = (int)(0.5f + (format.getSize() / pageSizes)) + 1;
				return TAL_RESULT.OK;
			}
		}
		readPosition.value = -1;
		return TAL_RESULT.ERROR;
	}

	int getOverItemEndPos(AlOneItem oi) {
		int i, e = oi.count, res = oi.pos[0];
		for (i = 1; i < e; i++) {
			if (oi.pos[i] > res)
				res = oi.pos[i];
		}
		return res;
	}


	int calcPrevStartPoint(int width, int height, AlOnePage page, int start_point) {
		int num_par = format.getNumParagraphByPoint(start_point);
		int end = format.getSize();
		int tmp, start = format.getStartPragarphByNum(num_par);
			
		if (start == start_point) {
			if (num_par == 0) {
				recalcColumn(width, height, page, 0, TAL_CALC_MODE.NORMAL);
				return 0;			
			}
			start = format.getStartPragarphByNum(--num_par);
		}
		
		while (true) {
			recalcColumn(width, height, page, start, TAL_CALC_MODE.NORMAL);		
			
			if (page.end_position <= start_point && page.end_position != end) {
				if (page.realLength > page.countItems) {
					tmp = getOverItemEndPos(page.items.get(page.countItems));
				} else {
					tmp = end;
				}

				if (tmp >= start_point)
					return start;
				break;
			}

			if (num_par == 0)
				return 0;
			
			if ((format.getStylePragarphByNum(num_par) & AlStyles.PAR_BREAKPAGE) != 0
					 && preferences.sectionNewScreen)
				break;
			
			start = format.getStartPragarphByNum(--num_par);
		}

		int save_start;
		while (true) {
			save_start = start;
			
			if (page.countItems == 1)
				return start;			

			start = page.items.get(1).start_pos;
			recalcColumn(width, height, page, start, TAL_CALC_MODE.NORMAL);
			
			if (page.end_position <= start_point) {				
				if (page.realLength > page.countItems) {
					tmp = getOverItemEndPos(page.items.get(page.countItems));
				} else {
					tmp = end;
				}

				if (tmp >= start_point)
					return start;
			} else 
			if (page.end_position > start_point) {
				//recalcColumn(width, height, page, save_start, TAL_CALC_MODE_NORMAL);
				return save_start;			
			}
		}
		
		//return 0;
	}

	int calculatePrevPagesPoint(int pos) {
		int res = pos;
		calcWordLenForPages = true;
		
		//calc.beginMain();
		calcScreenParameters();
		
		notesCounter++;
		if (profiles.twoColumn) {
			res = calcPrevStartPoint(
					(screenWidth >> 1) - screen_parameters.marginR - screen_parameters.marginL, 
					screenHeight - screen_parameters.marginB - screen_parameters.marginT, 
					page0, res);		
			res = calcPrevStartPoint(
					(screenWidth >> 1) - screen_parameters.marginR - screen_parameters.marginL, 
					screenHeight - screen_parameters.marginB - screen_parameters.marginT, 
					page0, res);		
		} else {
			res = calcPrevStartPoint(
					screenWidth - screen_parameters.marginR - screen_parameters.marginL, 
					screenHeight - screen_parameters.marginB - screen_parameters.marginT,
					page0, res);
		}

		//calc.endMain();
		calcWordLenForPages = false;

		return res;
	}

	int returnOkWithRedraw() {
		threadData.sendNotifyForUIThread(TAL_NOTIFY_ID.NEEDREDRAW, TAL_NOTIFY_RESULT.OK);
		return TAL_RESULT.OK;
	}
	
	public int	gotoPosition(TAL_GOTOCOMMAND mode) {
		if (openState.getState() != AlBookState.OPEN) 
			return TAL_RESULT.ERROR;

		int current_page = 0;
		switch (preferences.calcPagesModeUsed) {
		case SCREEN: {
				current_page = getCorrectPosition(bookPosition);
				switch (mode) {
				case NEXTPAGE:
					if (current_page < pagePositionPointer.size() - 1) {
						bookPosition = pagePositionPointer.get(current_page + 1).start;
						return returnOkWithRedraw();
					}
					break;
				case PREVPAGE:
					if (current_page > 0) {
						bookPosition = pagePositionPointer.get(current_page - 1).start;
						return returnOkWithRedraw();
					}
					break;
				case FIRSTPAGE:
					if (current_page > 0) {
						bookPosition = pagePositionPointer.get(0).start;
						return returnOkWithRedraw();
					}
					break;
				case LASTPAGE:
					if (current_page < pagePositionPointer.size() - 1) {
						bookPosition = pagePositionPointer.get(pagePositionPointer.size() - 1).start;
						return returnOkWithRedraw();
					}
					break;
				}
			}
			break;
		case AUTO:
		case SIZE: {
				switch (mode) {
				case NEXTPAGE:
					current_page = bookPosition;
					if (profiles.twoColumn) {
						current_page = page1.end_position;
						if (page0.end_position >= format.getSize()) 
							current_page = page0.end_position;
					} else {
						current_page = page0.end_position;
					}
					if (current_page < format.getSize()) {
						// add to stack position
						AlPagePositionStack.addBackPage(pagePositionPointer, current_page, bookPosition);
						//
						bookPosition = current_page;
						return returnOkWithRedraw();
					}
					break;		
				
				case FIRSTPAGE:
					if (bookPosition != 0) {
						bookPosition = 0;
						return returnOkWithRedraw();
					}
					break;	
				case LASTPAGE:
				case PREVPAGE:
					current_page = AlPagePositionStack.getBackPage(pagePositionPointer, 
						mode == TAL_GOTOCOMMAND.LASTPAGE ? format.getSize() : bookPosition);
					if (current_page == -1) {
						pagePositionPointer.clear();
						current_page = calculatePrevPagesPoint(bookPosition);				
					}
					if (bookPosition != current_page) {
						bookPosition = current_page;
						return returnOkWithRedraw();
					}
					break;			
				}
			}
			break;
		}

		return TAL_RESULT.ERROR;
	}
}
