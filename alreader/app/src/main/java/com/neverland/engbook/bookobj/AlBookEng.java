package com.neverland.engbook.bookobj;

import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlBookProperties;
import com.neverland.engbook.forpublic.AlEngineNotifyForUI;
import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.forpublic.AlIntHolder;
import com.neverland.engbook.forpublic.AlOneSearchResult;
import com.neverland.engbook.forpublic.AlPublicProfileOptions;
import com.neverland.engbook.forpublic.AlTapInfo;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_FILE_TYPE;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_GOTOCOMMAND;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_ID;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_NOTIFY_RESULT;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_PAGE_INDEX;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_SCREEN_PAGES_COUNT;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_SCREEN_SELECTION_MODE;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_THREAD_TASK;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.level1.AlFileDoc;
import com.neverland.engbook.level1.AlFileZipEntry;
import com.neverland.engbook.level1.AlFiles;
import com.neverland.engbook.level1.AlFilesBypass;
import com.neverland.engbook.level1.AlFilesEPUB;
import com.neverland.engbook.level1.AlFilesZIP;
import com.neverland.engbook.level2.AlFormat;
import com.neverland.engbook.level2.AlFormatDOC;
import com.neverland.engbook.level2.AlFormatEPUB;
import com.neverland.engbook.level2.AlFormatFB2;
import com.neverland.engbook.level2.AlFormatNativeImages;
import com.neverland.engbook.level2.AlFormatTXT;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.AlArabicReverse;
import com.neverland.engbook.util.AlBookState;
import com.neverland.engbook.util.AlCalc;
import com.neverland.engbook.util.AlFonts;
import com.neverland.engbook.util.AlHyph;
import com.neverland.engbook.util.AlImage;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.util.AlOneImage;
import com.neverland.engbook.util.AlOneImageParam;
import com.neverland.engbook.util.AlOneItem;
import com.neverland.engbook.util.AlOneLink;
import com.neverland.engbook.util.AlOnePage;
import com.neverland.engbook.util.AlOneWord;
import com.neverland.engbook.util.AlPagePositionStack;
import com.neverland.engbook.util.AlPaintFont;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlProfileOptions;
import com.neverland.engbook.util.AlScreenParameters;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.EngBitmap;
import com.neverland.engbook.util.InternalConst;
import com.neverland.engbook.util.InternalConst.TAL_CALC_MODE;

import java.util.ArrayList;

/**
 * основной класс библиотеки.
 */
public class AlBookEng{
		
	private static final int AL_COUNTPAGES_FOR_AUTOCALC = 64;
	private static final int AL_COUNTPAGES_MAX_FORSCREEN = 512;
	private static final int AL_TIMESCALC_MAX_FORSCREEN = 4000;
	private static final int AL_FILESIZEMIN_FOR_AUTOCALC = (65536 << 1);

	private int bookPosition;

	private final AlIntHolder shtamp = new AlIntHolder(0);
	//private int isOpen;
	//private int bmp_active;

	private int screenWidth;
	private int screenHeight;

	private AlEngineOptions engOptions = null;
	private final AlBookState openState = new AlBookState();

	private AlEngineNotifyForUI notifyUI = new AlEngineNotifyForUI();
	private AlFormat format = null;
	private final AlThreadData threadData = new AlThreadData();
	private final AlFonts fonts = new AlFonts();
	private final AlCalc calc = new AlCalc();
	private final AlImage images = new AlImage();
	private final AlBitmap[] bmp = { new AlBitmap(), new AlBitmap() };
	private final AlHyph hyphen = new AlHyph();
	private final AlPaintFont fontParam = new AlPaintFont();
	private final AlOneImageParam imageParam = new AlOneImageParam();
    private AlArabicReverse arabicReverse = null;

	private final AlProfileOptions profiles = new AlProfileOptions();
	private final AlPreferenceOptions preferences = new AlPreferenceOptions();
	private final AlStylesOptions styles = new AlStylesOptions();

	private final ArrayList<AlPagePositionStack> pagePositionPointer = new ArrayList<AlPagePositionStack>(
			128);

	private final AlIntHolder hyphFlag = new AlIntHolder(0);
	
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

	private final AlScreenParameters screen_parameters = new AlScreenParameters();
	private final AlOneWord tmp_word = new AlOneWord();
	private final AlOneWord note_word = new AlOneWord();

	public class PairTextStyle {
		public char[] txt = null;
		public long[] stl = null;
	}
	
	private final PairTextStyle format_text_and_style = new PairTextStyle();
	private final PairTextStyle format_note_and_style = new PairTextStyle();

	private int notesItemsOnPage;
	private int notesCounter;

	private final AlOnePage page0 = new AlOnePage();
	private final AlOnePage page1 = new AlOnePage();

    private final AlOnePage page00 = new AlOnePage();
    private final AlOnePage page11 = new AlOnePage();

	class AlSelection {
		public EngBookMyType.TAL_SCREEN_SELECTION_MODE selectMode = EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE;
		public final Point selectPosition = new Point(-1, -1);
		public int shtampSelectRequred = 0;
		public int shtampSelectUsed = 0;
		public boolean tooManySelect = false;
	}

	private AlSelection selection = new AlSelection();
	
	////////////////////////////////////////////////////////

    /**
     * создание класса должно быть в экземпляре Application, дабы пересоздания Activity никак не влияли на работу с книгой
     */
	public AlBookEng() {
		openState.clearState();
		
		screenWidth = screenHeight = 0;
		
		threadData.clearAll();

		calcWordLenForPages = false;

		AlOnePage.init(page0);
		AlOnePage.init(page1);
        AlOnePage.init(page00);
        AlOnePage.init(page11);

		notesCounter = 0;		
		fontParam.fnt = new Paint();
	}

	@Override	
	public void finalize() throws Throwable {
		uninitializeBookEngine();
		super.finalize();
	}

	private int uninitializeBookEngine() {
        while (threadData.getWork0()) ;

        closeBook();

        EngBitmap.reCreateBookBitmap(bmp[0], 0, 0, shtamp);
        EngBitmap.reCreateBookBitmap(bmp[1], 0, 0, shtamp);

        return TAL_RESULT.OK;
    }

    /**
	* Первичная инициализация параметров работы библиотеки. Значения, задающиеся в
	* engOptions нельзя изменить во время работы (кроме языка переносов)
	* @param engOptions - структура, задающая параметры работы библиотеки
    * @see AlEngineOptions
    * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
	*/
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
		if (preferences.calcPagesModeRequest != TAL_SCREEN_PAGES_COUNT.SCREEN) {
			preferences.pageSize = AlEngineOptions.AL_DEFAULT_PAGESIZE0;
			preferences.needCalcAutoPageSize = false;
			preferences.useAutoPageSize = false;
			if (engOptions.pageSize4Use == AlEngineOptions.AL_USEDEF_PAGESIZE ) {
				preferences.pageSize = AlEngineOptions.AL_DEFAULT_PAGESIZE0;
			} else
			if (engOptions.pageSize4Use == AlEngineOptions.AL_USEAUTO_PAGESIZE) {
				preferences.useAutoPageSize =
				    preferences.needCalcAutoPageSize = true;
			} else
			if (engOptions.pageSize4Use > 0) {
				preferences.pageSize = engOptions.pageSize4Use;
			}
		}

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

    /**
     * Инициализация окна (Activity), с которым взаимодействует библиотека.
     * @param engUI - структура с полями, необходимыми для обеспечения "связи" с окном основного приложения
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public int initializeOwner(AlEngineNotifyForUI engUI) {
		notifyUI.appInstance = engUI.appInstance;
		notifyUI.hWND = engUI.hWND;		

		threadData.book_object = this;
		threadData.owner_window = notifyUI.hWND;
		
		return TAL_RESULT.OK;
	}

    /**
     * удаление связки с Activity
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public int freeOwner() {
		threadData.freeOwner();		
		return TAL_RESULT.OK;
	}

    /**
     * Метод, дающий возможность изменить визуальные параметры отображения книги. Наиболее логичное применение -
     изменение дневного-ночного профилей. Кроме того, изменение параметров отображения текста (например увеличение-
     уменьшение размера текста) также осуществляется посредством использования данного метода.
     * @param prof  - класс со свойствами нового визуального профиля отображения страницы @see AlPublicProfileOptions
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public int setNewProfileParameters(AlPublicProfileOptions prof) {
		profiles.font_bold[0] = prof.bold;	
		profiles.font_italic[0] = false;
		
		if (prof.interline < -50)
			prof.interline = -50;
		if (prof.interline > 50)
			prof.interline = 50;

		profiles.font_interline[0] = prof.interline;
		
		if (prof.font_name != null && prof.font_name.length() > 0) {
			profiles.font_names[0] = String.copyValueOf(prof.font_name.toCharArray());
		} else {
			profiles.font_names[0] = "Serif";
		}
		
		if (prof.font_monospace != null) {
            profiles.font_names[InternalConst.TAL_PROFILE_FONT_CODE] = String.copyValueOf(prof.font_monospace.toCharArray());
		} else {
            profiles.font_names[InternalConst.TAL_PROFILE_FONT_CODE] = profiles.font_names[0];
		}

		if (prof.font_title != null) {
            profiles.font_names[InternalConst.TAL_PROFILE_FONT_TITLE] = String.copyValueOf(prof.font_title.toCharArray());
		} else {
            profiles.font_names[InternalConst.TAL_PROFILE_FONT_TITLE] = profiles.font_names[0];
		}
		
		if (prof.font_size < InternalConst.AL_MIN_FONTSIZE)
			prof.font_size = InternalConst.AL_MIN_FONTSIZE;
		if (prof.font_size > InternalConst.AL_MAX_FONTSIZE)
			prof.font_size = InternalConst.AL_MIN_FONTSIZE;
		
		profiles.font_sizes[0] = prof.font_size;

		prof.marginLeft = prof.validateMargin(prof.marginLeft);
		prof.marginRight = prof.validateMargin(prof.marginRight);
		prof.marginTop = prof.validateMargin(prof.marginTop);
		prof.marginBottom = prof.validateMargin(prof.marginBottom);

		profiles.marginL = -prof.marginLeft;
		profiles.marginT = -prof.marginTop;
		profiles.marginR = -prof.marginRight;
		profiles.marginB = -prof.marginBottom;

		profiles.twoColumnRequest = prof.twoColumn;
		profiles.twoColumnUsed = profiles.twoColumnRequest;
		
		profiles.background = prof.background;
        profiles.backgroundMode = prof.backgroundMode;

		profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT] = prof.colorText;
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG] = prof.colorBack;
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_TITLE] = prof.colorTitle;

        preferences.justify = prof.justify;
        preferences.sectionNewScreen = prof.sectionNewScreen;

        preferences.notesOnPage = false;
        if (preferences.calcPagesModeRequest == TAL_SCREEN_PAGES_COUNT.SIZE) {
            preferences.notesOnPage = prof.notesOnPage;
        }

		profiles.specialModeRoll = false;
		if (preferences.calcPagesModeRequest == TAL_SCREEN_PAGES_COUNT.SIZE) {
			profiles.specialModeRoll = prof.specialModeRoll;
			if (profiles.specialModeRoll) {
				profiles.marginT = profiles.marginB = 0;
				preferences.notesOnPage = false;
                preferences.sectionNewScreen = false;
			}
		}

		adaptProfileParameters();
		
		if (openState.getState() == AlBookState.OPEN)		
			needNewCalcPageCount();	

		return returnOkWithRedraw();
	}

	private void adaptProfileParameters() {

		for (int i = 1; i < 7; i++) {
			profiles.font_bold[i] = profiles.font_bold[0];
			profiles.font_italic[i] = profiles.font_italic[0];
			if (i != InternalConst.TAL_PROFILE_FONT_CODE && i != InternalConst.TAL_PROFILE_FONT_TITLE)
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
		
		profiles.classicFirstLetter = false;
		profiles.showFirstLetter = 0;
		
		if (preferences.isASRoll)
			profiles.twoColumnUsed = false;
		
		profiles.isTransparentImage = 
				(profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG] & 0xff) > 0xa0 &&
				(profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG] & 0xff00) > 0xa000 &&
				(profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG] & 0xff0000) > 0xa00000;

		preferences.vjustifyUsed = preferences.vjustifyRequest;
		if (preferences.vjustifyUsed && (profiles.twoColumnUsed || profiles.specialModeRoll))
			preferences.vjustifyUsed = false;

		calc.clearMainWidth();
		fonts.clearFontCache();
		
		shtamp.value++;
	}

	private void initDefaultProfile() {
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

		profiles.marginL = - AlPublicProfileOptions.DEF_MARGIN;
		profiles.marginT = - AlPublicProfileOptions.DEF_MARGIN;
		profiles.marginR = - AlPublicProfileOptions.DEF_MARGIN;
		profiles.marginB = - AlPublicProfileOptions.DEF_MARGIN;
		
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT] =	0x0000ff00;
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG] = 0x00ffffff;

		profiles.background = null;
		profiles.style_summ = false;

		adaptProfileParameters();
	}

	private void initDefaultStyles() {
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
		styles.style[InternalConst.STYLES_STYLE_POEM] = AlStyles.SL_FONT_TEXT | AlStyles.SL_SIZE_0 | AlStyles.SL_COLOR_TEXT | AlStyles.SL_REDLINE |
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

    private static final String TESTSTRING_FOR_CALCPAGESIZE = "Ш .ангй";
	private void calcScreenParameters() {

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
		if (screen_parameters.marginL < 0) screen_parameters.marginL = screen_parameters.marginL * (-1) * min_dim  / (profiles.twoColumnUsed ? 100 : 100);
		if (screen_parameters.marginT < 0) screen_parameters.marginT = screen_parameters.marginT * (-1) * min_dim  / 100;
		if (screen_parameters.marginR < 0) screen_parameters.marginR = screen_parameters.marginR * (-1) * min_dim  / (profiles.twoColumnUsed ? 100 : 100);
		if (screen_parameters.marginB < 0) screen_parameters.marginB = screen_parameters.marginB * (-1) * min_dim  / 100;

		if (profiles.twoColumnUsed && screen_parameters.marginR < 30)
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
				screen_parameters.free_picture_width = (screenWidth >> (profiles.twoColumnUsed ? 1 : 0)) -
					screen_parameters.marginL - screen_parameters.marginR - 1;
				tmp = screen_parameters.free_picture_width % (fontParam.space_width_standart * 2);
				if (tmp < 2)
					break;
				screen_parameters.marginL++;
				screen_parameters.marginR++;
			}
		}
		
		screen_parameters.free_picture_width = (screenWidth >> (profiles.twoColumnUsed ? 1 : 0)) -
			screen_parameters.marginL - screen_parameters.marginR - 1;
		screen_parameters.free_picture_height = screenHeight - screen_parameters.marginT - screen_parameters.marginB - 3;

		screen_parameters.reservHeight0 = fontParam.def_reserv * preferences.picture_need_tuneK;
			
		//int paragraphHeight = 0x65656900;//PrefManager.getInt(R.string.keyscreen_paragraph);
		
		screen_parameters.redLineV = DEF_RED_LINEV_VALUE;
		screen_parameters.redParV = DEF_RED_PARV_VALUE;
		
		screen_parameters.redLine = DEF_RED_LINE_VALUE;
		if (screen_parameters.redLine >= 200) {
			screen_parameters.redLine = screen_parameters.free_picture_width * (screen_parameters.redLine - 200) / 100;
		} else
		if (screen_parameters.redLine >= 100) {
			screen_parameters.redLine = fontParam.space_width_standart * (screen_parameters.redLine - 100);
		}		
		if (screen_parameters.redLine > screen_parameters.free_picture_width * 0.1f) {
			screen_parameters.redLine = (int) (screen_parameters.free_picture_width * 0.1f);
		}

		if (preferences.chinezeFormatting)
			screen_parameters.redLine *= 2;
		
		screen_parameters.redList = (fontParam.space_width_standart * (preferences.chinezeFormatting ? 4 : 3));

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

		//noinspection PointlessBooleanExpression
		if (DEF_SCREEN_PUNCTUATION && !preferences.chinezeFormatting) {
			screen_parameters.vikluchL = (int) 
				((screen_parameters.marginL > fontParam.hyph_width / 2.5 ? fontParam.hyph_width / 2.5 : screen_parameters.marginL) + 0.5f);
			screen_parameters.vikluchR = (int) 
				((screen_parameters.marginR > fontParam.hyph_width / 2.5 ? fontParam.hyph_width / 2.5 : screen_parameters.marginR) + 0.5f);
		} else {
			screen_parameters.vikluchL = 0;
			screen_parameters.vikluchR = 0;
		}
		
		if (preferences.isASRoll) {
			screen_parameters.marginT = screen_parameters.marginB = 0;
		}

        if (preferences.useAutoPageSize && preferences.needCalcAutoPageSize) {
            int[]	testWidth = new int[256];

            calc.getTextWidths(fontParam, TESTSTRING_FOR_CALCPAGESIZE.toCharArray(), 0, TESTSTRING_FOR_CALCPAGESIZE.length(), testWidth, true);
            for (int i = 1; i < TESTSTRING_FOR_CALCPAGESIZE.length(); i++)
                testWidth[0] += testWidth[i];

            float charWidth = (float)(testWidth[0]) / TESTSTRING_FOR_CALCPAGESIZE.length() + 0.5f;

            int itemHeight = screen_parameters.interFH_0[0] +
                    screen_parameters.interFH_0[0] * screen_parameters.interFI0[InternalConst.INTER_TEXT] / 100;
            int rows = (screenHeight - screen_parameters.marginT - screen_parameters.marginB) / itemHeight;
            int cols = (int) (((profiles.twoColumnUsed ? (screenWidth >> 1) : screenWidth) -
                                screen_parameters.marginL - screen_parameters.marginR) / charWidth);

            float koef = 0;
            if (cols <= 37) {
                koef += 87.0f;
            } else
            if (cols > 37 && cols <= 93) {
                koef += 30.0f * Math.pow(cols / 80.0f, -0.6) + 37.0f;
            } else
            if (cols > 93 && cols < 193) {
                koef += 64.5f + (90.0f - cols) / 5.3f;
            } else
            if (cols > 193) {
                koef += 45.0f;
            }

            preferences.pageSize = (int) (cols * rows * koef * (profiles.twoColumnUsed ? 2 : 1) / 100);

            preferences.needCalcAutoPageSize = false;
        }
	}

	private void initDefaultPreference() {
		preferences.maxNotesItemsOnPageRequest = MAX_NOTESITEMS_ON_PAGE;
		preferences.delete0xA0 = true;
		preferences.need_dialog = 0x00;
		preferences.notesAsSUP = true;
		preferences.sectionNewScreen = false;
		preferences.styleSumm = false;
		preferences.u301mode = 0x00;		
		preferences.notesOnPage = true;
		preferences.justify = true;
		preferences.vjustifyRequest = false;
		preferences.isASRoll = false;
		preferences.useSoftHyphen = true;
		preferences.calcPagesModeRequest = TAL_SCREEN_PAGES_COUNT.SIZE;
	}



	private void drawPageFromPosition(int pos, boolean needRecalc, boolean activePage) {
		calc.drawBackground(screenWidth, screenHeight, profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG],
                profiles.background, profiles.backgroundMode);

		notesCounter++;
		if (profiles.twoColumnUsed) {
			if (needRecalc) {
				recalcColumn(
						(screenWidth >> 1) - screen_parameters.marginR - screen_parameters.marginL,
						screenHeight - screen_parameters.marginB - screen_parameters.marginT,
                        activePage ? page0 : page00, pos, TAL_CALC_MODE.NORMAL);
				prepareColumn(activePage ? page0 : page00);

				recalcColumn(
						(screenWidth >> 1) - screen_parameters.marginR - screen_parameters.marginL,
						screenHeight - screen_parameters.marginB - screen_parameters.marginT,
                        activePage ? page1 : page11, activePage ? page0.end_position : page00.end_position, TAL_CALC_MODE.NORMAL);
				prepareColumn(activePage ? page1 : page11);
			}

			markFindResultAndSelect(activePage ? page0 : page00);
			markFindResultAndSelect(activePage ? page1 : page11);

			drawColumn(activePage ? page0 : page00,
                    screen_parameters.marginL,
                    screen_parameters.marginT,
                    (screenWidth >> 1) - screen_parameters.marginR,
                    screenHeight - screen_parameters.marginB);
			drawColumn(activePage ? page1 : page11,
				(screenWidth >> 1) + screen_parameters.marginR, 
				screen_parameters.marginT, 
				screenWidth - screen_parameters.marginL, 
				screenHeight - screen_parameters.marginB);

            (activePage ? bmp[0] : bmp[1]).freeSpaceAfterPage = 0;
		} else {
			if (needRecalc) {
				recalcColumn(
						screenWidth - screen_parameters.marginR - screen_parameters.marginL,
						screenHeight - screen_parameters.marginB - screen_parameters.marginT,
                        activePage ? page0 : page00, pos, TAL_CALC_MODE.NORMAL);
				prepareColumn(activePage ? page0 : page00);
			}
			markFindResultAndSelect(activePage ? page0 : page00);

			drawColumn(activePage ? page0 : page00,
                    screen_parameters.marginL,
                    screen_parameters.marginT,
                    screenWidth - screen_parameters.marginR,
                    screenHeight - screen_parameters.marginB);

            if (activePage) {
                if (profiles.specialModeRoll) {
                    bmp[0].freeSpaceAfterPage = page0.pageHeight - page0.textHeight;
                    if (bmp[0].freeSpaceAfterPage < 0 || page0.notePresent || screen_parameters.marginT != 0 || screen_parameters.marginB != 0)
                        bmp[0].freeSpaceAfterPage = 0;
                } else {
                    bmp[0].freeSpaceAfterPage = 0;
                }
            } else {
                if (profiles.specialModeRoll) {
                    bmp[1].freeSpaceAfterPage = page00.pageHeight - page00.textHeight;
                    if (bmp[1].freeSpaceAfterPage < 0 || page00.notePresent || screen_parameters.marginT != 0 || screen_parameters.marginB != 0)
                        bmp[1].freeSpaceAfterPage = 0;
                } else {
                    bmp[1].freeSpaceAfterPage = 0;
                }
            }
		}
		
		if (selection.selectMode == TAL_SCREEN_SELECTION_MODE.CLEAR)
			selection.selectMode = TAL_SCREEN_SELECTION_MODE.NONE;
	}


    /**
     * Получение сгенерированной страницы текста. значение index определяет какую страницу необходимо получить,
     возможные варианты - текущую, предыдущую и следующую. Получение предыдущей и следующей страницы не влечет за собой изменения
     позиции чтения, т.е. может использоваться для реализации анимации листания и функции автопрокрутки.
     Кроме того, в метод необходимо передать размеры рабочей области страницы, что пересекается с setNewScreenSize, но
     так сложилось...
     * @param index - индекс запрашиваемой страницы @see TAL_PAGE_INDEX
     * @param width - ширина страницы текста
     * @param height - высота страницы текста
     * @return null в случае ошибки или класс AlBitmap
     */
	public AlBitmap	getPageBitmap(TAL_PAGE_INDEX index, int width, int height) {
		//int tmp_res;

        if (openState.getState() != AlBookState.OPEN) {
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
            calc.drawBackground(width, height, profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG], profiles.background, profiles.backgroundMode);
            if (openState.getState() != AlBookState.NOLOAD) {
                int x = (width - waitBitmap.width) >> 1;
                int y = (height - waitBitmap.height) >> 1;
                calc.drawImage(x, y, waitBitmap.width, waitBitmap.height, waitBitmap, profiles.isTransparentImage);
            }
            calc.endMain();
            return index == TAL_PAGE_INDEX.CURR ? bmp[1] : null;
        } else
		if (index == TAL_PAGE_INDEX.CURR) {

			if (bmp[0].shtamp != shtamp.value || bookPosition != bmp[0].position) {
                bmp[0].shtamp = shtamp.value;
                bmp[0].position = bookPosition;

                calc.beginMain(bmp[0].canvas, profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG]);
                calcScreenParameters();
                drawPageFromPosition(bookPosition, true, true);
                calc.endMain();


            } else
            if (selection.selectMode != EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE &&
                    selection.shtampSelectRequred != selection.shtampSelectUsed) {
                calc.beginMain(bmp[0].canvas, profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG]);
                calcScreenParameters();
                drawPageFromPosition(bookPosition, false, true);
                calc.endMain();
                selection.shtampSelectUsed = selection.shtampSelectRequred;


            }
			return bmp[0];
		} else {
            scrollPrevPagePointStop = -1;
            int addonPosition = bookPosition;
			int testPosition = addonPosition;
			if (index == TAL_PAGE_INDEX.PREV) {
                if (testPosition == 0)
                    return null;
				testPosition *= -1;
			} else {
                if (testPosition >= format.getSize())
                    return null;
            }

			int rW = (width + 0x03) & 0xfffc;
			int rH = (height + 0x03) & 0xfffc;

			if (bmp[1].width != rW || bmp[1].height != rH)
				EngBitmap.reCreateBookBitmap(bmp[1], width, height, shtamp);

			if (bmp[1].shtamp != shtamp.value || testPosition != bmp[1].position) {

                if (cachePrevNextPoint.current != bookPosition || cachePrevNextPoint.shtamp != shtamp.value) {
                    cachePrevNextPoint.shtamp = shtamp.value;
                    cachePrevNextPoint.current = bookPosition;
                    cachePrevNextPoint.next = cachePrevNextPoint.prev = -1;
                }

				if (index == TAL_PAGE_INDEX.NEXT) {
                    if (cachePrevNextPoint.next != -1) {
                        addonPosition = cachePrevNextPoint.next;
                    } else {
                        addonPosition = cachePrevNextPoint.next = calculateNextPagePoint(bookPosition);
                    }
				} else {

                    if (cachePrevNextPoint.prev != -1) {
                        addonPosition = cachePrevNextPoint.prev;
                    } else {
                        addonPosition = cachePrevNextPoint.prev = calculatePrevPagePoint(bookPosition);
                    }
				}

				if (addonPosition == bookPosition)
					return null;

				bmp[1].shtamp = shtamp.value;
				bmp[1].position = testPosition;

				calc.beginMain(bmp[1].canvas, profiles.colors[InternalConst.TAL_PROFILE_COLOR_BG]);
				calcScreenParameters();
                if (index == TAL_PAGE_INDEX.PREV)
                    scrollPrevPagePointStop = bookPosition;
                drawPageFromPosition(addonPosition, true, false);
                scrollPrevPagePointStop = -1;
				calc.endMain();
			}
        }
		
		return bmp[1];
	}

    private int scrollPrevPagePointStop = -1;

    private class CachePrevNextPoint {
        int     shtamp = -100;
        int     current = -1;
        int     prev = -1;
        int     next = -1;
    }
    private final CachePrevNextPoint cachePrevNextPoint = new CachePrevNextPoint();

	private void markFindResultAndSelect(AlOnePage page) {
		AlOneItem oi;
		switch (selection.selectMode) {
			case CLEAR:
				for (int item = 0; item < page.countItems; item++) {
					oi = page.items.get(item);
                    if (oi.isNote)
                        continue;
					for (int poschar = 0; poschar < oi.count; poschar++) {
						oi.style[poschar] &= ~AlStyles.SL_SELECT;
					}
				}
				break;
			case START:
			case END:
			case DICTIONARY:
				for (int item = 0; item < page.countItems; item++) {
					oi = page.items.get(item);
                    if (oi.isNote)
                        continue;
					for (int poschar = 0; poschar < oi.count; poschar++) {
                    	if (oi.pos[poschar] >= selection.selectPosition.x && oi.pos[poschar] <= selection.selectPosition.y) {
							oi.style[poschar] |= AlStyles.SL_SELECT;
						} else {
							oi.style[poschar] &= ~AlStyles.SL_SELECT;
						}
					}
				}
				break;
			default:
				if (format.resfind.size() > 0) {
					int spos, epos;
					for (int i = 0; i < format.resfind.size(); i++) {
						spos = format.resfind.get(i).pos_start;
						epos = format.resfind.get(i).pos_end;
						if ((spos >= page.start_position && spos < page.end_position) ||
								(epos >= page.start_position && epos < page.end_position)) {

							for (int item = 0; item < page.countItems; item++) {
								oi = page.items.get(item);
                                if (oi.isNote)
                                    continue;
								for (int poschar = 0; poschar < oi.count; poschar++) {
									if (oi.pos[poschar] >= spos && oi.pos[poschar] <= epos)
										oi.style[poschar] |= AlStyles.SL_SELECT;
								}
							}
						}
					}
				}
				break;
		}
	}

	private void prepareColumn(AlOnePage page) {
		AlOneItem oi;
		
		int ext_len, cnt_char, cnt_img;
		int i, j, jj, count_space, add;

		boolean needVJust = true;
		float a1, a2;
		char ch;
		
		int col_count = page.countItems;


        if (scrollPrevPagePointStop != -1) {
            boolean flag_remove = false;
            for (j = 0; j < col_count; j++) {
                oi = page.items.get(j);

                if (!flag_remove && !oi.isNote) {
                    int start_pos = getOverItemStartPos(oi);
                    int stop_pos = getOverItemEndPos(oi);

                    flag_remove = start_pos >= scrollPrevPagePointStop;
                }

                if (flag_remove) {
                    for (i = j; i < col_count; i++) {
                        oi = page.items.get(i);

                        page.textHeight -= oi.height + oi.base_line_down + oi.base_line_up + oi.interline;
                    }

                    col_count = page.countItems = j;
                    break;
                }
            }
        }

		
		if (preferences.isASRoll || (profiles.specialModeRoll && !profiles.twoColumnUsed)) {
			oi = page.items.get(col_count);
			if (oi.count > 0 && oi.pos[0] >= page.end_position &&
				(profiles.classicFirstLetter || oi.isEnd || ((oi.style[0] & AlStyles.SL_MARKFIRTSTLETTER) == 0)))
				col_count++;
		}
		
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
							if (page.pageHeight > page.textHeight) {
                                oi.height += (page.pageHeight - page.textHeight) >> 1;
                                page.textHeight += page.pageHeight - page.textHeight;
                            }
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

			// arabic reverse word
            for (i = 0; i < oilen; i++) {
				if (AlUnicode.isArabic(oi.text[i])) {
                    if (arabicReverse == null)
                        arabicReverse = new AlArabicReverse();
                    oi.isArabic = arabicReverse.scanArabic(oi);
					break;
				}
			}
			//
			
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
				
				if (oi.isEnd) {
                    if (oi.isArabic) {
                        ext_len = oi.allWidth - oi.textWidth;
                        oi.isLeft += ext_len;
                        if (oi.isStart)
                            oi.isLeft -= oi.isRed;
                    }
                    continue;
                }
				
				count_space = 0;

				//if (preferences.chinezeFormatting) {
					for (i = 0; i < oilen; i++) {
						ch = oi.text[i];
						if (ch == 0x20 || (AlUnicode.isChineze(ch) && i != oilen - 1 && !AlUnicode.isLetter(ch)))
							count_space++;
					}
				/*} else {
					for (i = 0; i < oilen; i++) {
						ch = oi.text[i];
						if (ch == 0x20)
							count_space++;
					}
				}*/
				

				if (count_space > 0) {
					switch (Character.getType(oi.text[oilen - 1])) {
					case Character.OTHER_PUNCTUATION:
						switch (oi.text[oilen - 1]) {
						case '!': case '\"': case '\'':
						case '*': case ',': case '.':
						case ':': case ';': case '\u037e':
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
					
					ext_len = oi.allWidth - oi.textWidth;
					
					for (i = 0; i < oilen; i++) {
						ch = oi.text[i];
						if (ch == 0x20) {
							add = ext_len / count_space;
							oi.width[i] += add;
							count_space--;
							ext_len -= add;
						} else {
                            if (/*preferences.chinezeFormatting && */AlUnicode.isChineze(ch) && i != oilen - 1 && !AlUnicode.isLetter(ch)) {
                                add = (int) (ext_len / count_space);
                                oi.width[i] += add;
                                count_space--;
                                ext_len -= add;

                                if (i == 0 || (oi.style[i - 1] & AlStyles.SL_CHINEZEADJUST) == 0)
                                    oi.style[i] |= AlStyles.SL_CHINEZEADJUST;
                            }
                        }
					}

                    if (oi.isArabic) {
                        if (oi.isStart)
                            oi.isLeft -= oi.isRed;
                    }

                } else
				if (preferences.chinezeFormatting) {
					count_space = 0;

					for (i = 0; i < oilen; i++) {
						ch = oi.text[i];
						if (AlUnicode.isChineze(ch))
							count_space++;
					}
					
					if (count_space > 0) {
						ext_len = oi.allWidth - oi.textWidth;
						
						for (i = 0; i < oilen; i++) {
							ch = oi.text[i];
							if (AlUnicode.isChineze(ch)) {
								add = ext_len / count_space;
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
				ext_len = oi.allWidth - oi.textWidth;
				ext_len >>= 1;
				oi.isLeft += ext_len;				
			} else
			if (oi.justify == AlStyles.SL_JUST_RIGHT) {
				switch (Character.getType(oi.text[oi.count - 1])) {
				case Character.OTHER_PUNCTUATION:
					switch (oi.text[oilen - 1]) {
					case '!': case '\"': case '\'':
					case '*': case ',': case '.':
					case ':': case ';': case '\u037e':
						oi.textWidth -= screen_parameters.vikluchR;
					}
					break;
				case Character.DASH_PUNCTUATION: 
				case Character.END_PUNCTUATION:
				case Character.FINAL_QUOTE_PUNCTUATION:
					oi.isLeft += screen_parameters.vikluchR;
				}
				
				ext_len = oi.allWidth - oi.textWidth;
				oi.isLeft += ext_len;
				continue;
			}
		}
		
		page.overhead = 0;	
		if (preferences.isASRoll/* || (profiles.specialModeRoll && !profiles.twoColumnUsed)*/) {
			
			oi = page.items.get(page.countItems);
			if (oi != null && oi.count > 0 && oi.pos[0] >= page.end_position &&
				(profiles.classicFirstLetter || oi.isEnd || ((oi.style[0] & AlStyles.SL_MARKFIRTSTLETTER) == 0)))
				page.overhead = page.pageHeight - page.textHeight;
		
		} else		
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

	private static final int ARABIC_WORD_TYPE_NONE = 0;
    private static final int ARABIC_WORD_TYPE_ARABIC = 1;
    private static final int ARABIC_WORD_TYPE_SPACE = 2;
    private static final int ARABIC_WORD_TYPE_PUNCTO = 3;
	private static final int ARABIC_WORD_TYPE_NUMERIC = 4;
	private static final int ARABIC_WORD_TYPE_NORMAL = 5;

	class arabicWord {
		int     start;
		int     end;
        int     type;
	}

    private AlOneItem arabicItem = new AlOneItem();

    private void reverseArabicWord(AlOneItem item, AlOneItem temp, ArrayList<arabicWord> token) {

        int reverse_word_last = token.size() - 1, place_old_start, place_new_start, token_length;
        if (token.get(reverse_word_last).type == ARABIC_WORD_TYPE_SPACE)
            reverse_word_last--;
        int last_position = token.get(reverse_word_last).end;

        for (int i = 0; i <= reverse_word_last; i++) {
            token_length = token.get(i).end - token.get(i).start + 1;
            place_new_start = last_position - token_length + 1;
            place_old_start = token.get(i).start;

            if (token_length == 1) {
                temp.text[place_new_start] = item.text[place_old_start];
                temp.pos[place_new_start] = item.pos[place_old_start];
                temp.style[place_new_start] = item.style[place_old_start];
                temp.width[place_new_start] = item.width[place_old_start];
            } else {
                System.arraycopy(item.text, place_old_start, temp.text, place_new_start, token_length);
                System.arraycopy(item.pos, place_old_start, temp.pos, place_new_start, token_length);
                System.arraycopy(item.style, place_old_start, temp.style, place_new_start, token_length);
                System.arraycopy(item.width, place_old_start, temp.width, place_new_start, token_length);
            }

            last_position -= token_length;
        }

        place_new_start = token.get(0).start;
        token_length = token.get(reverse_word_last).end - token.get(0).start + 1;

        System.arraycopy(temp.text, place_new_start, item.text, place_new_start, token_length);
        System.arraycopy(temp.pos, place_new_start, item.pos, place_new_start, token_length);
        System.arraycopy(temp.style, place_new_start, item.style, place_new_start, token_length);
        System.arraycopy(temp.width, place_new_start, item.width, place_new_start, token_length);

        for (int i = 0; i < token_length; i++) {
            item.style[place_new_start + i] &= ~AlStyles.SL_MARKFIRTSTLETTER;
        }
    }

	private void scanArabic(AlOneItem oi) {
        boolean arabicPresent = false;
		int i, len = oi.count, word_start = -1, tp = ARABIC_WORD_TYPE_NONE, count_arabian = 0;
		char ch;

		ArrayList<arabicWord> token = null;

		for (i = 0; i < len; i++) {
			ch = oi.text[i];

            if (!arabicPresent) {
                if (!AlUnicode.isArabic(ch))
                    continue;
                arabicPresent = true;

                if (token == null)
                    token = new ArrayList<arabicWord>();
                token.clear();

                count_arabian = 0;
                word_start = -1;
            }

            if (word_start == -1)
                word_start = i;

            if (AlUnicode.isArabic(ch)) {
                tp = ARABIC_WORD_TYPE_ARABIC;
                if (i == len - 1) {
                    arabicWord a = new arabicWord();
                    a.start = word_start;
                    a.end = i;
                    a.type = tp;
                    token.add(a);
                    count_arabian++;
                }
            } else {

                if (tp == ARABIC_WORD_TYPE_ARABIC) {
                    arabicWord a = new arabicWord();
                    a.start = word_start;
                    a.end = i - 1;
                    a.type = tp;
                    token.add(a);
                    count_arabian++;
                }

                if (AlUnicode.isLetter(ch)) {
                    if (count_arabian > 1) {
                        while (oi.count >= arabicItem.realLength)
                            AlOneItem.incItemLength(arabicItem);
                        reverseArabicWord(oi, arabicItem, token);
                    }

                    arabicPresent = false;
                } else {
                    tp = ch == 0x20 ? ARABIC_WORD_TYPE_SPACE : ARABIC_WORD_TYPE_PUNCTO;
                    arabicWord a = new arabicWord();
                    a.start = i;
                    a.end = i;
                    a.type = tp;
                    token.add(a);

                    word_start = -1;
                }
            }
		}

        if (arabicPresent && count_arabian > 1) {
            while (oi.count >= arabicItem.realLength)
                AlOneItem.incItemLength(arabicItem);
            reverseArabicWord(oi, arabicItem, token);
        }
	}

	private void drawImage(int pos, long style, int widthImage, int x, int y) {
		AlOneImage ai = null;
		String link;
		int scale = (int) ((style & AlStyles.SL_COLOR_MASK) >> AlStyles.SL_COLOR_SHIFT);	
			
		if ((style & AlStyles.SL_IMAGE_MASK) == AlStyles.SL_IMAGE_OK) {
			link = format.getLinkNameByPos(pos, false);
			if (link != null)
				ai = format.getImageByName(link);
			if (ai != null) {
				AlBitmap b = images.getImage(ai, scale);
				if (b != null) {
					int th = ai.height;
					int tw = ai.width;
					for (int i = 0; i < scale; i++) {
						th >>= 1;
						tw >>= 1;
					}
					
					final int w;
					final int h;
					final float f = (float)widthImage / tw;
					if (f <= 1.02f && f >= 0.99f) {
						w = tw;
						h = th;
					} else {
						w = (int) (tw * f);
						h = (int) (th * f);
					}
					
					calc.drawImage(x, y - h, w, h, b, profiles.isTransparentImage);
					
					if ((style & AlStyles.SL_SELECT) != 0) {
                        calc.drawRect(x - 1, y - h - 1, x + w + 1, y + 1,//y - 2 * preferences.picture_need_tuneK,
                                (profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT] & 0xffffff) | 0x80000000);
                        calc.drawLine(x, y - h, x, y, preferences.picture_need_tuneK, profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT]);
                        calc.drawLine(x, y - h, x + w, y - h, preferences.picture_need_tuneK, profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT]);
                        calc.drawLine(x + w, y - h, x + w, y, preferences.picture_need_tuneK, profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT]);
                        calc.drawRect(x, y - 3 * preferences.picture_need_tuneK,
                                x + w, y, profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT]);
					}
					
					return;
				}
			}
		}
		
		imageParam.real_height = errorBitmap.height;
		imageParam.real_width = errorBitmap.width;
		calc.drawImage(x, y - imageParam.real_height, errorBitmap.width, errorBitmap.height, errorBitmap, profiles.isTransparentImage);
	}


	private int drawPartItem(int start, int end,
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


	private void drawColumn(AlOnePage page, int x0, int y0, int x1, int y1) {

				
		boolean first_notes = true;
		AlOneItem oi;
		int x;
		int col_count = page.countItems;
		
		if (preferences.isASRoll || (profiles.specialModeRoll && !profiles.twoColumnUsed)) {
			oi = page.items.get(col_count);
			if (oi.count > 0 && oi.pos[0] >= page.end_position &&
				(profiles.classicFirstLetter || oi.isEnd || ((oi.style[0] & AlStyles.SL_MARKFIRTSTLETTER) == 0)))
				col_count++;
		}
		
		int z, i, j, y = y0 + page.topMarg, start, end;
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
                oi.yDrawPosition = y;
				
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
						/*x = */drawPartItem(start, end, old_style, x, y, oi, page);
					}
				}
				y += oi.base_line_down;
				y += oi.interline;
			}
		}
	}


	TAL_NOTIFY_RESULT openBookInThread(String fName, AlBookOptions bookOptions) {

		String currName;
		String prevExt;
		int	ftype;
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
			if (ft == TAL_FILE_TYPE.EPUB) {
				activeFile = new AlFilesZIP();
				activeFile.initState(AlFiles.LEVEL1_ZIP_FIRSTNAME_EPUB, a, fList);
				a = activeFile;
				activeFile = new AlFilesEPUB();
				activeFile.initState(currName, a, fList);
				break;
			}

			ft = AlFileDoc.isDOC(currName, a, fList, prevExt);
			if (ft == TAL_FILE_TYPE.DOC) {
				activeFile = new AlFileDoc();
				activeFile.initState(currName, a, fList);
				break;
			}

			break;
		}

		if (AlFormatEPUB.isEPUB(activeFile)) {
			format = new AlFormatEPUB();
		} else
		if (AlFormatDOC.isDOC(activeFile)) {
			format = new AlFormatDOC();
		} else
		if (AlFormatFB2.isFB2(activeFile)) {
			format = new AlFormatFB2();
		} else
		if (AlFormatNativeImages.isImage(activeFile, prevExt)) {
			format = new AlFormatNativeImages();
		} else
			format = new AlFormatTXT();

		bookOptions.formatOptions &= ~AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG;
		if (preferences.calcPagesModeRequest != TAL_SCREEN_PAGES_COUNT.SIZE)
			bookOptions.formatOptions |= AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG;
		
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
            if (preferences.useAutoPageSize)
                preferences.needCalcAutoPageSize = true;
			format.lastCalcTime = 0;
			break;
		}
		//Log.e("calc page end", Long.toString(System.currentTimeMillis()));
		
		bookPosition = getCorrectPosition(bookPosition);

		openState.incState();		
		return TAL_NOTIFY_RESULT.OK;
	}

	TAL_NOTIFY_RESULT createDebugFileInThread(String path) {
		TAL_NOTIFY_RESULT res = format.createDebugFile(path);
		openState.decState();		
		return res;
	}

	TAL_NOTIFY_RESULT calcPagesInThread() {
		calcCountPages();
		openState.incState();
		return TAL_NOTIFY_RESULT.OK;
	}

	private int needNewCalcPageCount() {
		if (openState.getState() != AlBookState.OPEN) 
			return TAL_RESULT.ERROR;

		if (preferences.calcPagesModeUsed == TAL_SCREEN_PAGES_COUNT.SIZE) {
            if (preferences.useAutoPageSize)
                preferences.needCalcAutoPageSize = true;
            pagePositionPointer.clear();
            return TAL_RESULT.OK;
        }

		openState.decState();		
		
		if (preferences.calcPagesModeRequest == TAL_SCREEN_PAGES_COUNT.SCREEN &&
			preferences.calcPagesModeUsed == TAL_SCREEN_PAGES_COUNT.AUTO)
			preferences.calcPagesModeUsed = TAL_SCREEN_PAGES_COUNT.SCREEN;

		AlThreadData.startThread(threadData, TAL_THREAD_TASK.NEWCALCPAGES);	
		return TAL_RESULT.OK;
	}

    /**
     * Отладочный метод. Практического смысла в приложении для конечно пользователя не имеет
     * @param path - путь к каталогу, в котором будут созданы отладочные файлы
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public int createDebugFile(String path) {
		if (openState.getState() != AlBookState.OPEN) 
			return TAL_RESULT.ERROR;
		
		openState.incState();		

		threadData.param_char1 = path;
		AlThreadData.startThread(threadData, TAL_THREAD_TASK.CREATEDEBUG);	
		return TAL_RESULT.OK;
	}

    /**
     * Открытие книги (пути к файлу) с заданными параметрами.
     Путь может задаваться в том числе с учетом пути внутри zip архива
     * @param fName - путь к файлу книги
     * @param bookOptions - параметры открытия книги @see AlBookOptions
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
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

    /**
     * Закрытие книги
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public int closeBook() {
		if (openState.getState() != AlBookState.OPEN) 
			return TAL_RESULT.ERROR;
		
		openState.decState();	
		openState.decState();
		format = null;
		images.resetStoredImages();
		openState.decState();

		return returnOkWithRedraw();
		/*AlThreadData.startThread(threadData, TAL_THREAD_TASK.CLOSEBOOK);	
		return TAL_RESULT.OK;*/
	}

    /* *
     * получение строки с именем автора текста.
     * @return null или список авторов
     *//*
	public ArrayList<String> getAuthors(){
		if (openState.getState() != AlBookState.OPEN)
			return null;
		
		if (format.bookAuthors.size() < 1)
			return null;
		
		return format.bookAuthors;
	}
*/
    /* *
     * получение содержания.
     * @return null или список глав
     *//*
	public ArrayList<AlOneContent> getContents(){
		if (openState.getState() != AlBookState.OPEN)
			return null;

		if (format.ttl.size() < 1)
			return null;

		return format.ttl;
	}*/

    /**
     * получение информации о книге
     * @return null или AlBookProperties
     */
    public AlBookProperties getBookProperties() {
        if (openState.getState() != AlBookState.OPEN)
            return null;

        AlBookProperties a = new AlBookProperties();

        a.title = format.bookTitle;
        if (format.bookAuthors.size() > 0)
            a.authors = format.bookAuthors;
        if (format.ttl.size() > 0)
            a.content = format.ttl;
        a.size = format.getSize();
        if (format.bookGenres.size() > 0)
            a.genres = format.bookGenres;
        if (format.bookSeries.size() > 0)
            a.series = format.bookSeries;

        return a;
    }

    /* *
     * получение строки с названием книги.
     * @return null или название книги
     *//*
	public String getTitle(){
		if (openState.getState() != AlBookState.OPEN)
			return null;
			
		return format.bookTitle;
	}*/

    /**
     * получение результатов поиска заданной строки. Результатом является список, содержащий позиции
     найденных слов (фраз) в тексте.
     * @return null если ничего не было найдено или сам список
     */
	public ArrayList<AlOneSearchResult> getFindTextResult() {	
		if (openState.getState() != AlBookState.OPEN)
			return null;
		
		if (format.resfind.size() < 1)
			return null;
		
		return format.resfind;
	}

	protected TAL_NOTIFY_RESULT findTextInThread(String find) {
		TAL_NOTIFY_RESULT res = format.findText(find);
		shtamp.value++;
		openState.decState();		
		return res;
	}

    /**
     * Поиск текстовой строки в книге. Максимальная длина строки 32 символа, минимальная длина 2 символа.
     В строке допустимо использование символа "*" - в маска для любого символа, т.е. строка
     "гла*а" бедет искать вхождения слова "глаза", "глава" и т.д. При поиске все символы пунктуации
     воспринимаются одинаково, т.е. поиск "глаз!" эквивалентен поиску слова "глаз.", "глаз," и т.д.
     * @param find - строка поиска
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public int  findText(String find) {
		if (openState.getState() != AlBookState.OPEN)
			return TAL_RESULT.ERROR;
		
		openState.incState();

		threadData.param_char1 = find;
		AlThreadData.startThread(threadData, TAL_THREAD_TASK.FIND);
		return TAL_RESULT.OK;
	}

    /**
     * установка картинок, которые использует библиотека в процессе генерации страниц
     * @param errorImage - картинка, которая будет оборажена в случае невозможности чтения реальной картинки из книги. задавать обязательно
     * @param tableImage - картинка, которая оборазиться  для таблиц, заданных в формате фб2. При клике на картинку образиться тело таблицы.
    Задавать обязательно
     * @param waitImage - картинка-заставка, выводится при осуществлении долговременных операций, например, загрузке книги или поиске строки.
    Задавать обязательно
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public int setServiceBitmap(AlBitmap errorImage, AlBitmap tableImage, AlBitmap waitImage) {
		errorBitmap = errorImage;
		tableBitmap = tableImage;
		waitBitmap = waitImage;	
		return TAL_RESULT.OK;
	}

    /**
     * Установка новых размеров рабочей области для вывода страницы
     * @param width - ширина страницы текста
     * @param height - высота страницы текста
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
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

	private static void addW2I(AlOneItem oi, AlOneWord tword, int cnt) {
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
			oi.width[oi.count] = tword.width[wcurr];
			
			if (oi.base_line_down < tword.base_line_down[wcurr])
				oi.base_line_down = tword.base_line_down[wcurr];
			if (oi.base_line_up < tword.base_line_up[wcurr])
				oi.base_line_up = tword.base_line_up[wcurr];
					
			oi.count++;
			if (oi.count >= oi.realLength) 
				AlOneItem.incItemLength(oi);
		}

	}

	private static void addC2I0(AlOneItem oi, char ch, int need_width) {
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

	private void initOneItem(AlOneItem oi, AlOneItem poi, long style,
							 int pos, int width, boolean addEmptyLine, TAL_CALC_MODE calcMode) {

        if (profiles.specialModeRoll)
            addEmptyLine = true;

		oi.allWidth = width;
		oi.textWidth = 0;		
		oi.height = 0;
		oi.needHeihtImage0 = addEmptyLine;
		oi.cntImage = 0;
		oi.isEnd = oi.isStart = false;		
		oi.isRed = oi.isLeft = oi.isRight = 0;
		oi.start_pos = pos;
		oi.justify = style & AlStyles.SL_JUST_MASK;
		oi.isArabic = false;
        oi.yDrawPosition = -1;

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
			
			if (addEmptyLine || preferences.isASRoll) {
				
				if ((poi == null && (style & AlStyles.SL_STANZA) != 0) ||
                        ((poi != null) && (style & AlStyles.SL_STANZA) != 0 && (poi.style[0] & AlStyles.SL_STANZA) != 0)) {

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
				
				
				if (!preferences.isASRoll) {
					if ((style & AlStyles.SL_BREAK) != 0)
						oi.height += InternalConst.BREAK_HEIGHT;
					if (poi != null && poi.count == 1 && ((poi.style[0] & AlStyles.SL_IMAGE) != 0) && 
							((poi.style[0] & AlStyles.SL_MARKCOVER) != 0)) {
						oi.height += InternalConst.BREAK_HEIGHT;
					}
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
			int u = (width >> 3) - oi.allWidth;
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
				} else {
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

	private void addLinkToEndNotes(AlOneItem oi, int pos) {
		int num = oi.count - 1;

		boolean isArabic = false;
		int last_space = 0, count_space = 0;

		for (int i = num; i >= 0; i--) {
			if (oi.text[i] == 0x20) {
				if (last_space == 0)
					last_space = i;
				count_space++;
			}
			if (AlUnicode.isArabic(oi.text[i]))
			    isArabic = true;
		}

		if (num < 0) {
			oi.count = 1;
			oi.style[0] = 0;
			num++;
		} 
		
		if (num < 1) {
			oi.width[num] *= 2;
		} else {
            if (isArabic && last_space > 0 && count_space > 2) {
                num = last_space + 1;

                int ext_len = 0;

                for (int i = num; i < oi.count; i++) {
                    ext_len += oi.width[i];
                    oi.width[i] = 0x00;
                }

                if (ext_len > (fontParam.space_width_current << 1)) {
                    oi.width[num] = (fontParam.space_width_current << 1);
                    ext_len -= oi.width[num];
                    oi.width[last_space] += ext_len;
                } else {
                    oi.width[num] = ext_len;
                }

                oi.count = num + 1;
            } else {
                num--;
                oi.width[num] += oi.width[num + 1];
                oi.count--;
            }
		}

        oi.style[num] &= AlStyles.SL_COLOR_IMASK - AlStyles.SL_SUB - AlStyles.SL_SUP;
        oi.style[num] |= AlStyles.SL_COLOR_LINK | AlStyles.SL_LINK;
        oi.pos[num] = pos;
        oi.text[num] = 0x2026;
	}

	private boolean addNotesToPage(int width, AlOnePage page,
								   int start_point, int end_point) {
			
		note_word.need_flags = 0;
		note_word.count = 0;
		note_word.hyph[0]	= InternalConst.TAL_HYPH_INPLACE_DISABLE;
			
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
					if (0xad == ch) {
						note_word.hyph[note_word.count]	= InternalConst.TAL_HYPH_INPLACE_ENABLE;
					} else
					if (0x301 == ch && note_word.count > 0 && preferences.u301mode != 0) {
						if (2 == preferences.u301mode)
							continue;
						note_word.style[note_word.count - 1] ^= 0x03;
					} else {
						note_word.text[note_word.count] 	= format_note_and_style.txt[i];
						note_word.style[note_word.count] 	= format_note_and_style.stl[i];
						note_word.pos[note_word.count]	= start;
						
						note_word.count++;
						note_word.hyph[note_word.count]	= InternalConst.TAL_HYPH_INPLACE_DISABLE;
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

	private boolean addItem2Page0(AlOneItem oi, AlOnePage page, TAL_CALC_MODE calcMode, int width) {
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
					String link = format.getLinkNameByPos(page.items.get(test_item).pos[k], true);
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

	private boolean addWordToItem0(AlOneWord tword, AlOnePage page, int width, TAL_CALC_MODE calcMode) {
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
                    if (format.softHyphenPresent) {
                        hyphen.getHyph4Soft(tword.text, tword.hyph, tword.count, hyphFlag);
                    } else {
                        hyphen.getHyph(tword.text, tword.hyph, tword.count, hyphFlag);
                    }
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
			@SuppressWarnings("ConstantConditionalExpression") int need_space_len = false ? 0 : fontParam.space_width_current;
			
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
                    if (format.softHyphenPresent) {
                        hyphen.getHyph4Soft(tword.text, tword.hyph, tword.count, hyphFlag);
                    } else {
                        hyphen.getHyph(tword.text, tword.hyph, tword.count, hyphFlag);
                    }
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
				
					(page.textHeight + oi.height + oi.base_line_down + oi.base_line_up + (oi.interline > 0 ? oi.interline : 0) +
					(calcMode == TAL_CALC_MODE.NOTES && !page.notePresent ? page.notesShift : 0) -					 
					((preferences.isASRoll ? 0 : screen_parameters.reservHeight0)) <= page.pageHeight) || 

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

	private void getImageSize(AlOneWord tword, int pos_in_word, AlOnePage page, int width, int num_item, TAL_CALC_MODE calcMode) {
		
		int pos = tword.pos[pos_in_word];

        int devDiff = screen_parameters.interFH_1[(int) ((tword.style[pos_in_word] & AlStyles.SL_INTER_MASK) >> AlStyles.SL_INTER_SHIFT)];
        if (devDiff < 2)
            devDiff = 2;
		int maxHeight = screen_parameters.free_picture_height -
                devDiff;
		int maxWight = screen_parameters.free_picture_width;

		if (calcMode == TAL_CALC_MODE.NOTES) {
			maxHeight >>= 3;
		}
			
		tword.style[pos_in_word] &= AlStyles.SL_COLOR_IMASK & AlStyles.SL_IMAGE_IMASK;
		AlOneImage ai = null;
		String link = format.getLinkNameByPos(pos, false);
		if (link != null)
			ai = format.getImageByName(link);

		if (ai != null && (ai.iType != AlOneImage.NOT_EXTERNAL_IMAGE)) {
			if (ai.needScan) {
				images.initWork(ai, format);
				images.scanImage(ai);
			}  
			if (ai.width != -1) {
				imageParam.real_height = ai.height;
				imageParam.real_width = ai.width;
				tword.style[pos_in_word] |= AlStyles.SL_IMAGE_OK;
			}		
		}
		
		if ((tword.style[pos_in_word] & AlStyles.SL_IMAGE_OK) != 0) {
			int scale = 0;
			imageParam.height = imageParam.real_height;
			imageParam.width = imageParam.real_width;
			
			while ((imageParam.height > maxHeight || imageParam.width > maxWight) && scale < 31) {
				imageParam.height >>= 1;
				imageParam.width >>= 1;
				scale++;
			}
			
			if ((tword.style[pos_in_word] & AlStyles.SL_MARKCOVER) != 0) {
				while (imageParam.height < maxHeight && imageParam.width < maxWight) {
					imageParam.height <<= 1;
					imageParam.width <<= 1;
					if (scale > 0)
						scale--;
				}
			} else 
			if (preferences.picture_need_tuneK > 1) {
				int k = preferences.picture_need_tuneK - 1;
				while (imageParam.height < maxHeight && imageParam.width < maxWight && k > 0) {
					imageParam.height <<= 1;
					imageParam.width <<= 1;
					if (scale > 0)
						scale--;
					k--;
				}
			}

			if (imageParam.height > maxHeight || imageParam.width > maxWight || scale > 0) {
				float f = Math.min((float)maxHeight / (float)imageParam.height, 
						(float)maxWight / (float)imageParam.width);
				imageParam.height = (int) (imageParam.height * f);
				imageParam.width = (int) (imageParam.width * f);
			}

			if (scale <= 30) {
				tword.style[pos_in_word] |= ((long)(scale)) << AlStyles.SL_COLOR_SHIFT;
				return;
			}
			
			tword.style[pos_in_word] &= AlStyles.SL_IMAGE_IMASK;
		}

		if (errorBitmap != null) {
			imageParam.height = errorBitmap.height;
			imageParam.width = errorBitmap.width;
		} else {
			imageParam.height = 16;
			imageParam.width = 16;
		}
	}

	private void updateWordLength(final AlOneWord tword/*, final AlOnePage page, int width*/) {
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
							
							//if (preferences.chinezeFormatting) {
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
							//}
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

				//if (preferences.chinezeFormatting) {
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
				//}
			}
			
			if (fontParam.correct_italic != 0)
				tword.width[end] += fontParam.correct_italic;

			if ((old_style & AlStyles.SL_SHADOW) != 0)
				tword.width[end] += preferences.picture_need_tuneK;		
		}
	}


	private void calculateWordLength(final AlOneWord tword, final AlOnePage page,
									 int width, TAL_CALC_MODE calcMode, boolean modeCalcLight) {
		int i, j, start = 0, end = 0;

        boolean isArabic = false, stateArabic = false;
		for (i = 0; i < tword.count; i++) {
			if (stateArabic) {
				if (!AlUnicode.isArabic(tword.text[i])) {
					end = i - 1;
				} else
				if (i == tword.count - 1) {
					end = i;
				}

				if (start != end) {
					for (j = start + 1; j <= end; j++) {
						tword.style[j] &= ~AlStyles.LMASK_DRAW_STYLE;
						tword.style[j] |= tword.style[start] & AlStyles.LMASK_DRAW_STYLE;
					}
					stateArabic = false;
				}
			} else {
				if (AlUnicode.isArabic(tword.text[i])) {
					stateArabic = isArabic = true;
					start = end = i;
				}
			}
		}

		start = end = 0;
		
		for (i = 0; i < tword.count; i++) {
			if ((tword.style[i] & AlStyles.LMASK_CALC_STYLE) != (old_style & AlStyles.LMASK_CALC_STYLE)) {						
				if (end >= start && i != 0) {
					if ((old_style & AlStyles.SL_IMAGE) != 0) {
						for (j = 0; j < end - start + 1; j++) {
							getImageSize(tword, start + j, page, width, page.countItems, calcMode);
							tword.width[start + j] = imageParam.width;
							tword.base_line_up[start + j] = imageParam.height;
							tword.base_line_down[start + j] = 0;
						}
					} else {
						int t = end - start + 1;

						//calc.getTextWidths(fontParam, tword.text, start, t, tword.width, modeCalcLight);
						if (isArabic) {
							calc.getTextWidthsArabic(fontParam, tword.text, start, t, tword.width, modeCalcLight);
						} else {
							if (fontParam.style == 0) {
								char ch;
								for (j = 0; j < t; j++) {
									ch = tword.text[start + j];
									if (calc.mainWidth[ch] == AlCalc.UNKNOWNWIDTH) {
										tword.width[start + j] = calc.getOneMainTextCharWidth(fontParam, ch);
									} else {
										tword.width[start + j] = calc.mainWidth[ch];
									}
								}
							} else {
								calc.getTextWidths(fontParam, tword.text, start, t, tword.width, modeCalcLight);
							}
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
						
						//if (preferences.chinezeFormatting) {
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
						//}
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
					tword.base_line_down[start + j] = 0;
				}
			} else {
				int t = end - start + 1;

				//calc.getTextWidths(fontParam, tword.text, start, t, tword.width, modeCalcLight);
// inc speed???
				if (isArabic) {
					calc.getTextWidthsArabic(fontParam, tword.text, start, t, tword.width, modeCalcLight);
				} else {
					if (fontParam.style == 0) {
						char ch;
						for (j = 0; j < t; j++) {
							ch = tword.text[start + j];
							if (calc.mainWidth[ch] == AlCalc.UNKNOWNWIDTH) {
								tword.width[start + j] = calc.getOneMainTextCharWidth(fontParam, ch);
							} else {
								tword.width[start + j] = calc.mainWidth[ch];
							}
						}
					} else {
						calc.getTextWidths(fontParam, tword.text, start, t, tword.width, modeCalcLight);
					}
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

				//if (preferences.chinezeFormatting) {
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
				//}
			}
			
			if (fontParam.correct_italic != 0)
				tword.width[end] += fontParam.correct_italic;

			if ((old_style & AlStyles.SL_SHADOW) != 0)
				tword.width[end] += preferences.picture_need_tuneK;		
		}
	}

	private boolean addWord(AlOneWord tword, AlOnePage page, int width, TAL_CALC_MODE calcMode) {
		boolean res = false;
		int i, start;
		
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
				
				if (AlUnicode.isChineze(tword.text[0]) && ((tword.need_flags & InternalConst.AL_ONEWORD_FLAG_NOINSERTALL) != 0))
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
						
							(page.textHeight + oi.height + oi.base_line_down + oi.base_line_up + (oi.interline > 0 ? oi.interline : 0) +
							(calcMode == TAL_CALC_MODE.NOTES && !page.notePresent ? page.notesShift : 0) -
							((preferences.isASRoll ? 0 : screen_parameters.reservHeight0)) <= page.pageHeight) ||
							(page.countItems == 0))
						) {
						
						////////////////////////////////////////////////////////////////
						int addedItem = page.countItems;
						if (addItem2Page0(oi, page, calcMode, width))
							return true;
						oi = page.items.get(addedItem);
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

	private void recalcColumn(int width, int height, AlOnePage page, int start_point, TAL_CALC_MODE calc_mode) {

		page.start_position = start_point;
		page.countItems = 0;
		page.items.get(0).count = 0;
		page.selectStart = page.selectEnd = -1;		
		page.pageHeight = height;		
		if (preferences.isASRoll) {
			page.topMarg = -page.overhead;
			page.textHeight = -page.overhead;
		} else {
			page.topMarg = 0;
			page.textHeight = 0;
		}					
		page.notePresent = false;
		page.notesShift = (int) (screen_parameters.interFH_0[0] * 0.6f/* >> 1*/);
		/*if (screen_parameters.interFI0[0] < 0)
			page.notesShift -= screen_parameters.interFI0[0];*/

		tmp_word.need_flags = 0;
		tmp_word.count = 0;
		tmp_word.hyph[0]	= InternalConst.TAL_HYPH_INPLACE_DISABLE;
			
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
					if (0xad == ch) {
						tmp_word.hyph[tmp_word.count]	= InternalConst.TAL_HYPH_INPLACE_ENABLE;
					} else
					if (0x301 == ch && tmp_word.count > 0 && preferences.u301mode != 0) {
						if (2 == preferences.u301mode)
							continue;
						tmp_word.style[tmp_word.count - 1] ^= 0x03;					
					} else {
						tmp_word.text[tmp_word.count] 	= ch;
						tmp_word.style[tmp_word.count] 	= format_text_and_style.stl[i];
						tmp_word.pos[tmp_word.count]	= start;
						
						tmp_word.count++;
						tmp_word.hyph[tmp_word.count]	= InternalConst.TAL_HYPH_INPLACE_DISABLE;
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

	private void calcCountPages() {
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
					Log.e("calc pages interrupted", Long.toString(times) + '/' + Integer.toString(pagePositionPointer.size()));
				}
			}
			//
			
			notesCounter++;
			if (profiles.twoColumnUsed) {
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
                if (preferences.useAutoPageSize)
                    preferences.needCalcAutoPageSize = true;
                preferences.pageSize = AlEngineOptions.AL_DEFAULT_PAGESIZE0;
			} else {
				int sz = pagePositionPointer.get(pagePositionPointer.size() - 1).start - pagePositionPointer.get(1).start;
				int pg = pagePositionPointer.size() - 2;
                preferences.pageSize = sz / pg;
				if (preferences.pageSize < 1)
                    preferences.pageSize = 1;
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

	private int	getCorrectPosition(int pos) {
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

    /**
     * получение номера текущей страницы, общего количества страниц и актуальной позиции чтения.
     * @param current - текущая страница в книге
     * @param all - общее количество страниц
     * @param readPosition - текущая позиция чтения
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
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
				current.value = (int)(0.5f + (bookPosition / preferences.pageSize)) + 1;
				all.value = (int)(0.5f + (format.getSize() / preferences.pageSize)) + 1;
				return TAL_RESULT.OK;
			}
		}
		readPosition.value = -1;
		return TAL_RESULT.ERROR;
	}

	private int getOverItemEndPos(AlOneItem oi) {
		int i, e = oi.count, res = oi.pos[0];
		for (i = 1; i < e; i++) {
			if (oi.pos[i] > res)
				res = oi.pos[i];
		}
		return res;
	}

    private int getOverItemStartPos(AlOneItem oi) {
        int i, e = oi.count, res = oi.pos[0];
        for (i = 1; i < e; i++) {
            if (oi.pos[i] < res && oi.pos[i] >= 0)
                res = oi.pos[i];
        }
        return res;
    }


	private int calcPrevStartPoint(int width, int height, AlOnePage page, int start_point) {
		int num_par = format.getNumParagraphByPoint(start_point);
		int end = format.getSize();
		int tmp, start0 = format.getStartPragarphByNum(num_par);
			
		if (start0 == start_point) {
			if (num_par == 0) {
				recalcColumn(width, height, page, 0, TAL_CALC_MODE.NORMAL);
				return 0;			
			}
			start0 = format.getStartPragarphByNum(--num_par);
		}

        int nextStart, nextEnd;

        while (true) {
            recalcColumn(width, height, page, start0, TAL_CALC_MODE.NORMAL);

            if (page.realLength > page.countItems && page.items.get(page.countItems).pos[0] >= page.end_position) {
                nextEnd = getOverItemEndPos(page.items.get(page.countItems));
                nextStart = getOverItemStartPos(page.items.get(page.countItems));
            } else {
                nextEnd = getOverItemEndPos(page.items.get(page.countItems - 1));
                nextStart = getOverItemStartPos(page.items.get(page.countItems - 1));
            }

            if (nextStart <= start_point) {
                if (nextEnd >= start_point)
                    return start0;
                break;
            }

            if (num_par == 0)
                return 0;

            if ((format.getStylePragarphByNum(num_par) & AlStyles.PAR_BREAKPAGE) != 0
                    && preferences.sectionNewScreen)
                return start0;

            start0 = format.getStartPragarphByNum(--num_par);
        }

        int /*nextStart1, nextEnd1,*/ start1;

        while (true) {
            start1 = page.items.get(1).start_pos;
            if (start1 == start_point)
                return start0;

            recalcColumn(width, height, page, start1, TAL_CALC_MODE.NORMAL);

            if (page.realLength > page.countItems && page.items.get(page.countItems).pos[0] >= page.end_position) {
                nextEnd = getOverItemEndPos(page.items.get(page.countItems));
            } else {
                nextEnd = getOverItemEndPos(page.items.get(page.countItems - 1));
            }

            if (nextEnd >= start_point) {
                return start1;
            }

            start0 = start1;
            //nextStart0 = nextStart1;
            //nextEnd0 = nextEnd1;
        }

		/*while (true) {
			recalcColumn(width, height, page, start, TAL_CALC_MODE.NORMAL);		
			
			if (page.end_position <= start_point && page.end_position != end) {
				if (page.realLength > page.countItems) {
					tmp = getOverItemEndPos(page.items.get(page.countItems));
				} else {
					tmp = end;
				}

				if (tmp >= start_point) {
                    return start;
                } *//*else {
                    if (page.realLength > page.countItems) {
                        scrollPrevPagePointStop = start_point;
                        return getOverItemStartPos(page.items.get(page.countItems));
                    }
                }*//*
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
		}*/
		
		//return 0;
	}

	private int calculatePrevPagesPoint(int pos) {
		int res = pos;
		calcWordLenForPages = true;
		
		//calc.beginMain();
		calcScreenParameters();
		
		notesCounter++;
		if (profiles.twoColumnUsed) {
			res = calcPrevStartPoint(
					(screenWidth >> 1) - screen_parameters.marginR - screen_parameters.marginL, 
					screenHeight - screen_parameters.marginB - screen_parameters.marginT, 
					page00, res);
			res = calcPrevStartPoint(
					(screenWidth >> 1) - screen_parameters.marginR - screen_parameters.marginL, 
					screenHeight - screen_parameters.marginB - screen_parameters.marginT, 
					page00, res);
		} else {
			res = calcPrevStartPoint(
					screenWidth - screen_parameters.marginR - screen_parameters.marginL,
					screenHeight - screen_parameters.marginB - screen_parameters.marginT,
					page00, res);
		}

		//calc.endMain();
		calcWordLenForPages = false;

		return res;
	}

	private int returnOkWithRedraw() {
		threadData.sendNotifyForUIThread(TAL_NOTIFY_ID.NEEDREDRAW, TAL_NOTIFY_RESULT.OK);
		return TAL_RESULT.OK;
	}

    private int	calculateNextPagePoint(int pos) {
        int current_page;
        switch (preferences.calcPagesModeUsed) {
            case SCREEN:
                current_page = getCorrectPosition(bookPosition);
                if (current_page < pagePositionPointer.size() - 1)
                    return pagePositionPointer.get(current_page + 1).start;
                break;
            case AUTO:
            case SIZE:
                current_page = pos;
                if (profiles.twoColumnUsed) {
                    current_page = page1.end_position;
                    if (page0.end_position >= format.getSize())
                        current_page = page0.end_position;
                } else {
                    current_page = page0.end_position;
                }
                if (current_page < format.getSize())
                    return current_page;
                break;
        }
        return pos;
    }

    private int	calculatePrevPagePoint(int pos) {
        int current_page;
        switch (preferences.calcPagesModeUsed) {
            case SCREEN:
                current_page = getCorrectPosition(bookPosition);
                if (current_page > 0)
                    return pagePositionPointer.get(current_page - 1).start;
                break;
            case AUTO:
            case SIZE:
                current_page = AlPagePositionStack.getBackPage(pagePositionPointer, pos);
                if (current_page == -1)
                    current_page = calculatePrevPagesPoint(bookPosition);
                return current_page;
        }
        return pos;
    }

    /**
     * Навигация по книге. Доступны перемещения на первую, последнюю, следующую, предыдущую страницы и переход на заданную позицию в тексте.
     Для всех значений mode, кроме TAL_GOTOCOMMAND_POSITION значение pos не имеет смысла. Для TAL_GOTOCOMMAND_POSITION pos означает значение в книге, на которое
     необходимо переместиться. Конкретная позиция начала страницы, на которую будет осуществлено перемещение, зависит от используемого алгоритма просчета
     страниц и совcем не обязательно будет равно pos
     * @param mode - запрашиваемая команда перехода @see TAL_GOTOCOMMAND
     * @param pos - позиция в текста (только для mode = TAL_GOTOCOMMAND.POSITION)
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public int	gotoPosition(TAL_GOTOCOMMAND mode, int pos) {
		if (openState.getState() != AlBookState.OPEN) 
			return TAL_RESULT.ERROR;

		int current_page;
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
                case POSITION:

                    break;
				}
			}
			break;
		case AUTO:
		case SIZE: {
				switch (mode) {
				case NEXTPAGE:
					current_page = bookPosition;
					if (profiles.twoColumnUsed) {
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
				case POSITION:
					pagePositionPointer.clear();
					bookPosition = pos;
					return returnOkWithRedraw();
				}
			}
			break;
		}

		return TAL_RESULT.ERROR;
	}

	private final AlTapInfo tapInfo = new AlTapInfo();

    /**
     * Получение информации о месте в книге, по которому "тапнули" на экране. Результат зависит от текущего режима выделения.
     * @param x - координата x тапа
     * @param y - координата y тапа
     * @param gotoDictMode - необходимость перехода в режим словаря
     * @return AlTapInfo
     */
    public AlTapInfo getInfoByTap(int x, int y, boolean gotoDictMode) {
    	tapInfo.clearInfo();
    	
    	tapInfo.x = x;
    	tapInfo.y = y;

		if (openState.getState() != AlBookState.OPEN || !getPositionByXY())
			return null;

        int start, stop;

		switch (selection.selectMode) {
		case NONE:
			if (gotoDictMode) {
				setSelection(EngBookMyType.TAL_SCREEN_SELECTION_MODE.DICTIONARY, tapInfo.tapWordStart, tapInfo.tapWordStop);
			} else {
				if (tapInfo.isImage) {
					AlOneImage ai;
					String link = format.getLinkNameByPos(tapInfo.pos, false);
					if (link != null) {
						ai = format.getImageByName(link);
						if (ai != null && ai.iType != AlOneImage.IMG_UNKNOWN) {
							tapInfo.image.append(ai.name);
						} else {
							tapInfo.isImage = false;
						}
					} else {
						tapInfo.isImage = false;
					}
				}
	
				if (tapInfo.isLocalLink) {
					AlOneLink al;
					String link = format.getLinkNameByPos(tapInfo.pos, true);
					if (link != null) {
						al = format.getLinkByName(link, true);
						if (al != null) {
							tapInfo.linkLocalPosition = al.positionS;
						} else {
							tapInfo.isLocalLink = false;
							tapInfo.isExtLink = true;
							tapInfo.link.append(link);
						}
					} else {
						tapInfo.isLocalLink = false;
					}
				}
			}		
			break;
		case START:
			start = tapInfo.tapWordStart;
            stop = selection.selectPosition.y;
            if (stop < start)
                stop = tapInfo.tapWordStop;

            setSelection(selection.selectMode, start, stop);
			break;
		case END:
            start = selection.selectPosition.x;
            stop = tapInfo.tapWordStop;
            if (stop < start)
                start = tapInfo.tapWordStart;

            setSelection(selection.selectMode, start, stop);
			break;
		case DICTIONARY:
			setSelection(EngBookMyType.TAL_SCREEN_SELECTION_MODE.DICTIONARY, tapInfo.tapWordStart, tapInfo.tapWordStop);
			break;
		}

        return tapInfo;
    }

    private boolean getPositionByXY() {
        if (profiles.twoColumnUsed && tapInfo.x >= (screenWidth >> 1))
            return getPositionInPageByXY(page1, (screenWidth >> 1) + screen_parameters.marginR);
        return getPositionInPageByXY(page0, screen_parameters.marginL);
    }
    
    private boolean getPositionInPageByXY(AlOnePage page, int margLeft) {

        int areal = 0, x, y;
        for (int z = 0; z < 2; z++) {
            if (z != 0)
                areal = EngBookMyType.AL_DEFAULT_TAP_AREAL * preferences.picture_need_tuneK;

            for (int j = 0; j < page.countItems; j++) {
            	AlOneItem oi = page.items.get(j);

                if (oi.isNote && selection.selectMode != TAL_SCREEN_SELECTION_MODE.NONE)
                    continue;

            	y = oi.yDrawPosition;
            	
            	if (y - oi.base_line_up - areal <= tapInfo.y && 
            		y + oi.base_line_down + areal >= tapInfo.y) {
            		
            		x = margLeft + oi.isLeft + oi.isRed;
            		
            		tapInfo.tapWordStart = tapInfo.tapWordStop = -1;
            		
            		for (int i = 0; i < oi.count; i++) {
            			
            			if (oi.text[i] == 0x20) {
							if (tapInfo.pos != AlTapInfo.TAP_ON_CLEAR_SPACE)
            					break;
            				
            				tapInfo.tapWordStart = tapInfo.tapWordStop = -1;
            			} else {
            				if (oi.pos[i] >= 0) {
                                if (AlUnicode.isChineze(oi.text[i])) {
                                    if (tapInfo.pos != AlTapInfo.TAP_ON_CLEAR_SPACE)
                                        break;
                                    tapInfo.tapWordStart = oi.pos[i];
                                }
	            				if (tapInfo.tapWordStart == -1)
	            					tapInfo.tapWordStart = oi.pos[i];
	            				
	            				if (tapInfo.tapWordStart > oi.pos[i])
	            					tapInfo.tapWordStart = oi.pos[i];
	            				if (tapInfo.tapWordStop < oi.pos[i])
	            					tapInfo.tapWordStop = oi.pos[i];
            				}
            			}
            			
            			if (oi.pos[i] >= 0 && 
            				x - areal <= tapInfo.x && 
            				x + oi.width[i] + areal >= tapInfo.x) {
            				
            				tapInfo.isNote = oi.isNote;
                            tapInfo.isLocalLink = (oi.style[i] & AlStyles.SL_LINK) != 0;
                            tapInfo.isImage = oi.text[i] == AlStyles.CHAR_IMAGE_E;
                            tapInfo.pos = oi.pos[i];

							if (AlUnicode.isChineze(oi.text[i]))
								break;
            			}
            			
            			x += oi.width[i];
            		}
            		
            		return tapInfo.pos != AlTapInfo.TAP_ON_CLEAR_SPACE;
            	}
            }
        }

        return false;
    }

    /**
     * Получение выделенного на странице (страницах) текста.
     * @return null если ошибка или выделенный текст
     */
	public String getSelectedText() {
        if (openState.getState() == AlBookState.OPEN) {
            switch (selection.selectMode) {
                case DICTIONARY:
                    return format.getDictWordByPos(selection.selectPosition.x, selection.selectPosition.y);
                case END:
                case START:
                	if (!selection.tooManySelect)
                		return format.getTextByPos(selection.selectPosition.x, selection.selectPosition.y);
                	break;
            }
        }
        return null;
	}

    /**
     * Определение в каком режиме выделения находится библиотека в текущий момент
     * @return текущий режим выделения
     */
	public EngBookMyType.TAL_SCREEN_SELECTION_MODE getSelectionMode() {
		return selection.selectMode;
	}

    /**
     * установка нового режима выделения. В случае, если логика работы не позволяет инициировать заданный режим выделения -
     новый режим устанвлен не будет. Т.е. при вызове необходимо проверять результат работы с методом. Если
     задаваемое и возвращаемое значения не совпадают - значит уставка не прошла.
     * @param newMode - задаваемый режим выделения @see TAL_SCREEN_SELECTION_MODE
     * @return - текущий режим выделения. В случае успешного вызова - должен быть равен newMode
     */
	public EngBookMyType.TAL_SCREEN_SELECTION_MODE setSelectionMode(EngBookMyType.TAL_SCREEN_SELECTION_MODE newMode) {
        int NO_SELECTED = -100;

		if (openState.getState() == AlBookState.OPEN) {
			
			if (newMode == EngBookMyType.TAL_SCREEN_SELECTION_MODE.CLEAR)
				newMode = EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE;
			
			if (newMode == selection.selectMode)
				return selection.selectMode;
			
			if (newMode == EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE && 
					selection.selectMode == EngBookMyType.TAL_SCREEN_SELECTION_MODE.CLEAR)
				return EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE;
			
			int start, stop;			
			
			switch (selection.selectMode) {
			case NONE:
				switch (newMode) {
				case DICTIONARY:
					start = stop = page0.start_position;
					
					AlOneItem oi = page0.items.get(0);
					for (int i = 0; i < oi.count; i++) {	            			
            			if (oi.text[i] == 0x20) {	            				
            				break;
            			} else {
            				if (oi.pos[i] >= 0) {
	            				if (start > oi.pos[i])
	            					start = oi.pos[i];
	            				if (stop < oi.pos[i])
	            					stop = oi.pos[i];
            				}
            			}
					}				
					
					setSelection(newMode, start, stop);
					break;
				case START:
				case END:
					start = page0.start_position;
					stop = page0.end_position - 1;
					if (profiles.twoColumnUsed && page1.countItems > 0)
						stop = page1.end_position - 1;
					setSelection(newMode, start, stop);
					break;
				}
				return selection.selectMode;
			case DICTIONARY:

                switch (newMode) {
				case NONE:
					setSelection(EngBookMyType.TAL_SCREEN_SELECTION_MODE.CLEAR, NO_SELECTED, NO_SELECTED);
					return EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE;				
				}
				return selection.selectMode;
			case START:
			case END:
				switch (newMode) {
				case NONE:
					setSelection(EngBookMyType.TAL_SCREEN_SELECTION_MODE.CLEAR, NO_SELECTED, NO_SELECTED);
					return EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE;				
				case START:
				case END:
					setSelection(newMode, selection.selectPosition.x, selection.selectPosition.y);
					break;
				}
				return selection.selectMode;
			}
			
			
		}
		
		return TAL_SCREEN_SELECTION_MODE.NONE;
	}


	private void setSelection(EngBookMyType.TAL_SCREEN_SELECTION_MODE val, int start, int stop) {
		if (val != selection.selectMode || start != selection.selectPosition.x || stop != selection.selectPosition.y) {
			selection.shtampSelectRequred++;
			selection.selectMode = val;
			selection.selectPosition.set(start, stop);
			selection.tooManySelect = stop - start > EngBookMyType.AL_MAXIMUM_SELECT_BLOCK_SIZE;
			returnOkWithRedraw();
		}
	}

	public void getScrollShift(final boolean fromCurrentPage, int shift, AlIntHolder outShift, AlIntHolder outPos) {
        AlOnePage page = fromCurrentPage ? page0 : page00;

        //int startItem = 0;
		int resultPos = fromCurrentPage ? page.end_position : page.start_position;
		int tmpDiff = Math.abs(outShift.value - shift);
		int tmpShift, resultShift = outShift.value;

        if (!fromCurrentPage) {
            if (shift > outShift.value - bmp[1].freeSpaceAfterPage ) {
                outPos.value = resultPos;
                outShift.value = shift;
                return;
            }

            shift = outShift.value - bmp[1].freeSpaceAfterPage - shift;
            tmpDiff = outShift.value - bmp[1].freeSpaceAfterPage;
            //startItem++;
            /*AlOneItem oi = page.items.get(0);

            tmpShift = oi.yDrawPosition - oi.base_line_up - oi.height - screen_parameters.marginT;
            if (tmpShift < 0)
                tmpShift = 0;

            if (Math.abs(shift - tmpShift) < tmpDiff) {
                tmpDiff = Math.abs(shift - tmpShift);
                resultShift = tmpShift;
            }*/
        }

        for (int i = 0; i < page.countItems; i++) {
            AlOneItem oi = page.items.get(i);

            if (!oi.isNote) {
                tmpShift = oi.yDrawPosition - oi.base_line_up - oi.height - screen_parameters.marginT;
                if (tmpShift < 0)
                    tmpShift = 0;

                if (Math.abs(shift - tmpShift) < tmpDiff) {
                    tmpDiff = Math.abs(shift - tmpShift);
                    resultShift = tmpShift;
                    resultPos = (i == 0) ? page.start_position : getOverItemStartPos(oi);
                }
            }
        }

		if (!fromCurrentPage) {
            outShift.value = outShift.value - bmp[1].freeSpaceAfterPage - resultShift;
        } else {
            outShift.value = resultShift;
        }

        outPos.value = resultPos;
	}

}
