package com.neverland.engbook.bookobj;

import android.graphics.Bitmap;
import android.util.Log;

import com.neverland.engbook.allstyles.AlCSSHtml;
import com.neverland.engbook.forpublic.AlBitmap;
import com.neverland.engbook.forpublic.AlBookOptions;
import com.neverland.engbook.forpublic.AlBookProperties;
import com.neverland.engbook.forpublic.AlBookStyles;
import com.neverland.engbook.forpublic.AlCurrentPosition;
import com.neverland.engbook.forpublic.AlEngineNotifyForUI;
import com.neverland.engbook.forpublic.AlEngineOptions;
import com.neverland.engbook.forpublic.AlIntHolder;
import com.neverland.engbook.forpublic.AlOneBookmark;
import com.neverland.engbook.forpublic.AlOneContent;
import com.neverland.engbook.forpublic.AlOneSearchResult;
import com.neverland.engbook.forpublic.AlPoint;
import com.neverland.engbook.forpublic.AlPublicProfileOptions;
import com.neverland.engbook.forpublic.AlRect;
import com.neverland.engbook.forpublic.AlSourceImage;
import com.neverland.engbook.forpublic.AlTapInfo;
import com.neverland.engbook.forpublic.AlTextOnScreen;
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
import com.neverland.engbook.level1.AlFilesBypassDecrypt;
import com.neverland.engbook.level1.AlFilesBypassNative;
import com.neverland.engbook.level1.AlFilesBypassRAR;
import com.neverland.engbook.level1.AlFilesCBZ;
import com.neverland.engbook.level1.AlFilesCHM;
import com.neverland.engbook.level1.AlFilesDocx;
import com.neverland.engbook.level1.AlFilesEPUB;
import com.neverland.engbook.level1.AlFilesFB3;
import com.neverland.engbook.level1.AlFilesMOBI;
import com.neverland.engbook.level1.AlFilesODT;
import com.neverland.engbook.level1.AlFilesPDB;
import com.neverland.engbook.level1.AlFilesPDBUnk;
import com.neverland.engbook.level1.AlFilesRAR;
import com.neverland.engbook.level1.AlFilesZIP;
import com.neverland.engbook.level1.JEBFilesEPUB;
import com.neverland.engbook.level1.JEBFilesZIP;
import com.neverland.engbook.level2.AlFormat;

import com.neverland.engbook.level2.AlFormatCHM;
import com.neverland.engbook.level2.AlFormatCOMICS;
import com.neverland.engbook.level2.AlFormatDOC;
import com.neverland.engbook.level2.AlFormatDOCX;
import com.neverland.engbook.level2.AlFormatEPUB;
import com.neverland.engbook.level2.AlFormatFB2;
import com.neverland.engbook.level2.AlFormatFB3;
import com.neverland.engbook.level2.AlFormatHTML;
import com.neverland.engbook.level2.AlFormatMOBI;
import com.neverland.engbook.level2.AlFormatNativeImages;
import com.neverland.engbook.level2.AlFormatODT;
import com.neverland.engbook.level2.AlFormatRTF;
import com.neverland.engbook.level2.AlFormatTXT;
import com.neverland.engbook.level2.AlScanMOBI;
import com.neverland.engbook.level2.JEBFormatEPUB;
import com.neverland.engbook.unicode.AlUnicode;
import com.neverland.engbook.util.AlArabicReverse;
import com.neverland.engbook.util.AlBookState;
import com.neverland.engbook.util.AlCalc;
import com.neverland.engbook.util.AlFonts;
import com.neverland.engbook.util.AlHyph;
import com.neverland.engbook.util.AlImage;
import com.neverland.engbook.util.AlMutex;
import com.neverland.engbook.util.AlOneFont;
import com.neverland.engbook.util.AlOneImage;
import com.neverland.engbook.util.AlOneImageParam;
import com.neverland.engbook.util.AlOneItem;
import com.neverland.engbook.util.AlOneLink;
import com.neverland.engbook.util.AlOnePage;
import com.neverland.engbook.util.AlOneTable;
import com.neverland.engbook.util.AlOneTableCell;
import com.neverland.engbook.util.AlOneWord;
import com.neverland.engbook.util.AlPagePositionStack;
import com.neverland.engbook.util.AlPaintFont;
import com.neverland.engbook.util.AlParProperty;
import com.neverland.engbook.util.AlPreferenceOptions;
import com.neverland.engbook.util.AlProfileOptions;
import com.neverland.engbook.util.AlScreenParameters;
import com.neverland.engbook.util.AlStyles;
import com.neverland.engbook.util.AlStylesOptions;
import com.neverland.engbook.util.ChineseTextUtils;
import com.neverland.engbook.util.EngBitmap;
import com.neverland.engbook.util.InternalConst;
import com.neverland.engbook.util.InternalConst.TAL_CALC_MODE;
import com.neverland.engbook.util.InternalFunc;

import java.util.ArrayList;

/**
 * основной класс библиотеки.
 */
public class AlBookEng{

	private static final boolean AL_SUPPORT_COPYPAGE = true;
		
	private static final int AL_COUNTPAGES_FOR_AUTOCALC = 64;
	private static final int AL_COUNTPAGES_MAX_FORSCREEN = 512;
	private static final int AL_TIMESCALC_MAX_FORSCREEN = 24000;
	private static final int AL_FILESIZEMIN_FOR_AUTOCALC = (65536 << 1);

	private int bookPosition;

	private final AlIntHolder shtamp = new AlIntHolder(0);
	//private int isOpen;
	//private int bmp_active;

	private int screenWidth;
	private int screenHeight;

	private AlEngineOptions engOptions = null;
	public final AlBookState openState = new AlBookState();

	private final AlEngineNotifyForUI notifyUI = new AlEngineNotifyForUI();
	public AlFormat format = null;
	public AlFormat formatDelay = null;
	private final AlThreadData threadData = new AlThreadData();
	private final AlFonts fonts = new AlFonts();
	private final AlCalc calc = new AlCalc();
	private final AlImage images = new AlImage();
	private final AlBitmap[] bmp = { new AlBitmap(), new AlBitmap(), new AlBitmap() };
	private final AlHyph hyphen = new AlHyph();
	private final AlPaintFont fontParam = new AlPaintFont();
	private final AlOneImageParam imageParam = new AlOneImageParam();
	private AlArabicReverse arabicReverse = null;
    private final AlBookProperties	bookProperties = new AlBookProperties();
    private final AlBookProperties	bookMetaData = new AlBookProperties();

	private final AlProfileOptions profiles = new AlProfileOptions();
	private final AlPreferenceOptions preferences = new AlPreferenceOptions();
	private final AlStylesOptions styles = new AlStylesOptions();

	private final ArrayList<AlPagePositionStack> pagePositionPointer = new ArrayList<>(
			128);

	private final AlIntHolder hyphFlag = new AlIntHolder(0);
	
	private long old_style;

	/*
	 * #ifdef CORRECTCALCLENGTHFORANDROID int tmp_wl[AL_WORD_LEN + 2]; char16_t
	 * arrCalc[AL_WORD_LEN + 2]; static const char16_t CHAR4CALC_STD = 'a';
	 * static const char16_t CHAR4CALC_ARA = 0x00; #endif
	 */
	private boolean calcWordLenForPages;

	//private AlBitmap externalBitmap = null;

	private AlBitmap errorBitmap = null;
	private AlBitmap tableBitmap = null;
	private AlBitmap waitBitmap = null;
	private AlBitmap turnBitmap = null;
    private AlBitmap selectStartBitmap = null;
    private AlBitmap selectEndBitmap = null;

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

    private final AlOnePage[][] mpage = {{new AlOnePage(), new AlOnePage()}, {new AlOnePage(), new AlOnePage()}, {new AlOnePage(), new AlOnePage()}};
	/*private final AlOnePage page0 = new AlOnePage();
	private final AlOnePage page1 = new AlOnePage();

    private final AlOnePage page00 = new AlOnePage();
    private final AlOnePage page11 = new AlOnePage();*/

	class AlSelection {
		public EngBookMyType.TAL_SCREEN_SELECTION_MODE selectMode = EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE;
		public final AlPoint selectPosition = new AlPoint();
		public int shtampSelectRequred = 0;
		public int shtampSelectUsed = 0;
		public boolean tooManySelect = false;
        public final AlPoint selectMarkerStart = new AlPoint();
        public final AlPoint selectMarkerEnd = new AlPoint();

        public void clearSelectMarker() {
			selectMarkerStart.set(-1, -1, -1);
            selectMarkerEnd.set(-1, -1, -1);
        }
	}

	private final AlSelection selection = new AlSelection();

	public enum SimplifiedAndTraditionalChineseConvert {
		NONE, SIMPLIFIED_TO_TRADITIONAL, TRADITIONAL_TO_SIMPLIFIED
	}

	public SimplifiedAndTraditionalChineseConvert chineseConvert = SimplifiedAndTraditionalChineseConvert.NONE;

	////////////////////////////////////////////////////////

    /**
     * создание класса должно быть в экземпляре Application, дабы пересоздания Activity никак не влияли на работу с книгой
     */
	public AlBookEng() {
		openState.clearState();
		
		screenWidth = screenHeight = 0;
		
		threadData.clearAll();

		scrollPrevPagePointStop = -1;
		calcWordLenForPages = false;

		AlOnePage.init(mpage[0][0], InternalConst.TAL_PAGE_MODE.MAIN);
		AlOnePage.init(mpage[0][1], InternalConst.TAL_PAGE_MODE.MAIN);
        AlOnePage.init(mpage[1][0], InternalConst.TAL_PAGE_MODE.ADDON);
        AlOnePage.init(mpage[1][1], InternalConst.TAL_PAGE_MODE.ADDON);
        AlOnePage.init(mpage[2][0], InternalConst.TAL_PAGE_MODE.ADDON);
        AlOnePage.init(mpage[2][1], InternalConst.TAL_PAGE_MODE.ADDON);

		notesCounter = 0;		
	}

	@Override	
	public void finalize() throws Throwable {
		uninitializeBookEngine();
		super.finalize();
	}

	private int uninitializeBookEngine() {

		while (threadData.getObjOpen()) ;

		synchronized(_lockObjAddon) {

			while (threadData.getWork0()) ;

			closeBook();

			if (engOptions.externalBitmap == null) {
				EngBitmap.reCreateBookBitmap(bmp[0], 0, 0, null);
				EngBitmap.reCreateBookBitmap(bmp[1], 0, 0, null);
				EngBitmap.reCreateBookBitmap(bmp[2], 0, 0, null);
			}

			return TAL_RESULT.OK;
		}
    }

    /**
	* Первичная инициализация параметров работы библиотеки. Значения, задающиеся в
	* engOptions нельзя изменить во время работы (кроме языка переносов)
	* @param engOptions - структура, задающая параметры работы библиотеки
    * @see AlEngineOptions
    * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
	*/
	public synchronized int initializeBookEngine(AlEngineOptions engOptions) {
		this.engOptions = engOptions;
		clearPagePosition();

        preferences.chinezeFormatting = engOptions.chinezeFormatting;
        preferences.onlyPopupFootnote = engOptions.onlyPopupFootnote;

		old_style = 0;
		initDefaultPreference();
		initDefaultStyles();
		initDefaultProfile();

		if (engOptions.defaultFB2 != null)
			AlCSSHtml.DEFAULT_CSS_FB2 = engOptions.defaultFB2;
		if (engOptions.defaultFB3 != null)
			AlCSSHtml.DEFAULT_CSS_FB3 = engOptions.defaultFB3;
		if (engOptions.defaultHTML != null)
			AlCSSHtml.DEFAULT_CSS_HTML = engOptions.defaultHTML;
		if (engOptions.defaultEPUB != null)
			AlCSSHtml.DEFAULT_CSS_EPUB = engOptions.defaultEPUB;
		if (engOptions.defaultMOBI != null)
			AlCSSHtml.DEFAULT_CSS_MOBI = engOptions.defaultMOBI;
		
		
		calc.init(engOptions, fontParam);
		fonts.init(engOptions, calc, fontParam);
		images.init(engOptions);
		hyphen.init(engOptions);

		preferences.multiplexer = (float) engOptions.multiplexer;
		if (preferences.multiplexer < 1.0f)
			preferences.multiplexer = 1.0f;
		if (preferences.multiplexer > 5.0f)
			preferences.multiplexer = 5.0f;
		preferences.picture_need_tune = preferences.multiplexer != 1.0f;

        preferences.tableMode = engOptions.tableMode;
        preferences.value2CalcMargins = engOptions.value2CalcMargins;

		preferences.chinezeFormatting = engOptions.chinezeFormatting;

		preferences.calcPagesModeRequest = engOptions.useScreenPages;

		if (engOptions.useScreenPages == TAL_SCREEN_PAGES_COUNT.SCREEN) {
			preferences.calcPagesModeRequest = TAL_SCREEN_PAGES_COUNT.SIZE;
		} else {
			preferences.calcPagesModeRequest = engOptions.useScreenPages;
		}

		//if (preferences.calcPagesModeRequest != TAL_SCREEN_PAGES_COUNT.SCREEN) {
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
		//}

		preferences.maxNotesItemsOnPageUsed = engOptions.notesItemsOnPageCount;
		if (preferences.maxNotesItemsOnPageUsed < 1 || preferences.maxNotesItemsOnPageUsed > 255)
			preferences.maxNotesItemsOnPageUsed = preferences.maxNotesItemsOnPageRequest;
		if (preferences.calcPagesModeRequest != TAL_SCREEN_PAGES_COUNT.SIZE) {
			// this disable notes on page. if enable, calc pages will be slowly
			preferences.notesOnPage = false;
			//
			preferences.maxNotesItemsOnPageUsed = 1;
		}

		preferences.vjustifyUsed = preferences.vjustifyRequest;

		if (engOptions.externalBitmap == null) {
			EngBitmap.reCreateBookBitmap(bmp[0], 0, 0, shtamp);
			EngBitmap.reCreateBookBitmap(bmp[1], 0, 0, null);
			EngBitmap.reCreateBookBitmap(bmp[2], 0, 0, null);
		}

		return TAL_RESULT.OK;
	}

	private final AlTextOnScreen textOnScreen = new AlTextOnScreen();

	public static String drmDeviceId;
	public static String drmCertificate;

	public void activateDeviceDRM(String deviceId, String certificate) {
		drmDeviceId = deviceId;
		drmCertificate = certificate;
    }


    /**
     * Инициализация окна (Activity), с которым взаимодействует библиотека.
     * @param engUI - структура с полями, необходимыми для обеспечения "связи" с окном основного приложения
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public synchronized int initializeOwner(AlEngineNotifyForUI engUI) {
		notifyUI.appInstance = engUI.appInstance;
		notifyUI.hWND = engUI.hWND;		

		threadData.book_object = this;
		//threadData.owner_window = (WeakReference<EngBookListener>) notifyUI.hWND;
        threadData.owner_window = notifyUI.hWND;
		
		return TAL_RESULT.OK;
	}

    /**
     * удаление связки с Activity
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public synchronized int freeOwner() {
		threadData.freeOwner();		
		return TAL_RESULT.OK;
	}

	public synchronized ArrayList <AlOneFont> getFontList() {
		return fonts.getFontList();
	}


	/**
     * Метод, дающий возможность изменить визуальные параметры отображения книги. Наиболее логичное применение -
     изменение дневного-ночного профилей. Кроме того, изменение параметров отображения текста (например увеличение-
     уменьшение размера текста) также осуществляется посредством использования данного метода.
     * @param prof  - класс со свойствами нового визуального профиля отображения страницы @see AlPublicProfileOptions
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public synchronized int setNewProfileParameters(AlPublicProfileOptions prof) {

        if (preferences.isASRoll)
            return TAL_RESULT.ERROR;

        int fSize;
        /*if (prof.font_size < 0) {
            fSize = (int)(-prof.font_size * engOptions.textMultiplexer + 0.5f);
            if (fSize < InternalConst.AL_MIN_FONTSIZE) {
                fSize = InternalConst.AL_MIN_FONTSIZE;
                prof.font_size = (int)(-fSize / preferences.textMultiplexer + 0.5f);
            }
            if (fSize > InternalConst.AL_MAX_FONTSIZE) {
                fSize = InternalConst.AL_MAX_FONTSIZE;
                prof.font_size = (int)(-fSize / preferences.textMultiplexer + 0.5f);
            }
        } else {*/
            if (prof.font_size < InternalConst.AL_MIN_FONTSIZE)
                prof.font_size = InternalConst.AL_MIN_FONTSIZE;
            if (prof.font_size > InternalConst.AL_MAX_FONTSIZE)
                prof.font_size = InternalConst.AL_MAX_FONTSIZE;
            fSize = prof.font_size;
        //}

        profiles.font_bold[0] = prof.bold;
        profiles.font_sizes[0] = fSize;
        profiles.font_italic[0] = false;

        if (prof.font_name != null && prof.font_name.length() > 0) {
            profiles.font_names[0] = String.copyValueOf(prof.font_name.toCharArray());
        } else {
            profiles.font_names[0] = "Serif";
        }
        if (prof.font_monospace != null) {
            profiles.font_names[InternalConst.TAL_PROFILE_FONTTYPE_CODE] = String.copyValueOf(prof.font_monospace.toCharArray());
        } else {
            profiles.font_names[InternalConst.TAL_PROFILE_FONTTYPE_CODE] = profiles.font_names[0];
        }
        if (prof.font_title != null) {
            profiles.font_names[InternalConst.TAL_PROFILE_FONTTYPE_FLET] = String.copyValueOf(prof.font_title.toCharArray());
        } else {
            profiles.font_names[InternalConst.TAL_PROFILE_FONTTYPE_FLET] = profiles.font_names[0];
        }

        if (prof.interline < -50)
            prof.interline = -50;
        if (prof.interline > 50)
            prof.interline = 50;
        profiles.font_interline[0] = prof.interline;

        prof.marginLeft = prof.validateMargin(prof.marginLeft);
        prof.marginRight = prof.validateMargin(prof.marginRight);
        prof.marginTop = prof.validateMargin(prof.marginTop);
        prof.marginBottom = prof.validateMargin(prof.marginBottom);

        profiles.marginL = -prof.marginLeft;
        profiles.marginT = -prof.marginTop;
        profiles.marginR = -prof.marginRight;
        profiles.marginB = -prof.marginBottom;

        profiles.background = prof.background;
        profiles.backgroundMode = prof.backgroundMode & 0x07;

        profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT] = prof.colorText;
        profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK] = prof.colorBack;
        profiles.colors[InternalConst.TAL_PROFILE_COLOR_TITLE] = prof.overrideColorTitle ? prof.colorTitle : prof.colorText;

		profiles.colors[InternalConst.TAL_PROFILE_COLOR_BOLD] = prof.overrideColorBold ? prof.colorBold : prof.colorText;
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_ITALIC] = prof.overrideColorItalic ? prof.colorItalic : prof.colorText;
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_BOLDITALIC] = prof.overrideColorBoldItalic ? prof.colorBoldItalic : prof.colorText;
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_CODE] = prof.overrideColorCode ? prof.colorCode : prof.colorText;


		preferences.justify = prof.justify;
        preferences.sectionNewScreen = prof.sectionNewScreen;

        preferences.notesOnPage = false;
        if (preferences.calcPagesModeRequest == TAL_SCREEN_PAGES_COUNT.SIZE) {
            preferences.notesOnPage = prof.notesOnPage;
        }

		profiles.specialModeMadRoll = false;
		profiles.specialModeRoll = false;
		if (preferences.calcPagesModeRequest == TAL_SCREEN_PAGES_COUNT.SIZE) {
			profiles.specialModeRoll = prof.specialModeRoll;
			if (profiles.specialModeRoll) {
                profiles.specialModeMadRoll = prof.specialModeMadRoll;
                if (!profiles.specialModeMadRoll)
                    profiles.marginT = profiles.marginB = 0;
				preferences.notesOnPage = false;
                preferences.sectionNewScreen = false;
			}
		}

		profiles.twoColumnRequest = prof.twoColumn && !profiles.specialModeRoll;
		profiles.twoColumnUsed = profiles.twoColumnRequest;

		/*profiles.margin1Style = prof.margin1Style;
		profiles.margin2Style = prof.margin2Style;
		profiles.margin3Style = prof.margin3Style;*/

		adaptProfileParameters();
		
		if (openState.getState() == AlBookState.OPEN)		
			needNewCalcPageCount();	

		return returnOkWithRedraw();
	}

	private void adaptProfileParameters() {

		for (int i = 1; i < InternalConst.TAL_PROFILE_FONTTYPE_COUNT; i++) {
			profiles.font_bold[i] = profiles.font_bold[0];
			profiles.font_italic[i] = profiles.font_italic[0];

			if (i == InternalConst.TAL_PROFILE_FONTTYPE_CODE)
				profiles.font_names[i] = profiles.font_names[0];

			profiles.font_sizes[i] = profiles.font_sizes[0];
			/*if (i == InternalConst.TAL_PROFILE_FONTTYPE_FLET) {
				profiles.font_sizes[i] *= 2;
			} else
			if (i == InternalConst.TAL_PROFILE_FONTTYPE_CODE) {
				profiles.font_sizes[i] -= 1;
			}*/

			if (i == InternalConst.TAL_PROFILE_FONTTYPE_NOTE) {
				profiles.font_widths[i] = profiles.font_widths[0] - 10;
			} else {
				profiles.font_widths[i] = profiles.font_widths[0];
			}

			profiles.font_weigths[i] = 0;

			profiles.font_interline[i] = profiles.font_interline[0];
			/*if (i == InternalConst.TAL_PROFILE_FONTTYPE_NOTE) {
				profiles.font_interline[i] -= 15; 
			}*/
		}

		if (profiles.font_sizes[InternalConst.TAL_PROFILE_FONTTYPE_NOTE] > profiles.font_sizes[0])
			profiles.font_sizes[InternalConst.TAL_PROFILE_FONTTYPE_NOTE] = profiles.font_sizes[0];
		
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_LINK] =	0x2196f3;//0xffcc00;
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_SHADOW] = 0x00808080;

		profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT] = styles.color[InternalConst.TAL_PROFILE_COLOR_SELECT];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK0] = styles.color[InternalConst.TAL_PROFILE_COLOR_MARK0];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK1] = styles.color[InternalConst.TAL_PROFILE_COLOR_MARK1];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK2] = styles.color[InternalConst.TAL_PROFILE_COLOR_MARK2];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK3] = styles.color[InternalConst.TAL_PROFILE_COLOR_MARK3];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK4] = styles.color[InternalConst.TAL_PROFILE_COLOR_MARK4];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK5] = styles.color[InternalConst.TAL_PROFILE_COLOR_MARK5];
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK6] = styles.color[InternalConst.TAL_PROFILE_COLOR_MARK6];

		profiles.multiplexer = preferences.multiplexer;
		
		profiles.classicFirstLetter = false;
		profiles.showFirstLetter = 0;
		
		if (preferences.isASRoll)
			profiles.twoColumnUsed = false;
		
		profiles.isTransparentImage = 
				(profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK] & 0xff) > 0xa0 &&
				(profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK] & 0xff00) > 0xa000 &&
				(profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK] & 0xff0000) > 0xa00000;

		preferences.vjustifyUsed = preferences.vjustifyRequest;
		if (preferences.vjustifyUsed && (profiles.twoColumnUsed || profiles.specialModeRoll))
			preferences.vjustifyUsed = false;

		calc.clearMainWidth();
		fonts.clearFontCache();

		clearPagePosition();
		
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
		
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT] =	0x00101010;
		profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK] = 0x00ffffff;

		profiles.background = null;
		profiles.style_summ = false;

		adaptProfileParameters();
	}

	/**
	 * Обновление стилей отображения в книге
	 * @param val - новые параметы стилей, если null - устанавливаются параметры по умолчанию
	 * @return результат выполнения
	 */
	public synchronized int updateBookStyles(AlBookStyles val, EngBookMyType.TAL_HYPH_LANG hyphLang) {
        if (preferences.isASRoll)
            return TAL_RESULT.ERROR;

        if (val == null) {
            initDefaultStyles();
        } else {
			styles.color[InternalConst.TAL_PROFILE_COLOR_SELECT] = val.colorSelect;
			styles.color[InternalConst.TAL_PROFILE_COLOR_MARK0] = val.colorMarkFind;
			styles.color[InternalConst.TAL_PROFILE_COLOR_MARK1] = val.colorMarkRed;
			styles.color[InternalConst.TAL_PROFILE_COLOR_MARK2] = val.colorMarkYellow;
			styles.color[InternalConst.TAL_PROFILE_COLOR_MARK3] = val.colorMarkBlue;
			styles.color[InternalConst.TAL_PROFILE_COLOR_MARK4] = val.colorMarkGreen;
			styles.color[InternalConst.TAL_PROFILE_COLOR_MARK5] = val.colorMarkPurple;
			styles.color[InternalConst.TAL_PROFILE_COLOR_MARK6] = val.colorMarkAqua;


			profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT] = styles.color[InternalConst.TAL_PROFILE_COLOR_SELECT];
			profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK0] = styles.color[InternalConst.TAL_PROFILE_COLOR_MARK0];
			profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK1] = styles.color[InternalConst.TAL_PROFILE_COLOR_MARK1];
			profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK2] = styles.color[InternalConst.TAL_PROFILE_COLOR_MARK2];
			profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK3] = styles.color[InternalConst.TAL_PROFILE_COLOR_MARK3];
			profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK4] = styles.color[InternalConst.TAL_PROFILE_COLOR_MARK4];
			profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK5] = styles.color[InternalConst.TAL_PROFILE_COLOR_MARK5];
			profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK6] = styles.color[InternalConst.TAL_PROFILE_COLOR_MARK6];
        }

		adaptProfileParameters();

		if (hyphLang != null)
			hyphen.setLang(hyphLang);

		shtamp.value++;
		return returnOkWithRedraw();
	}

	private void initDefaultStyles() {
		styles.color[InternalConst.TAL_PROFILE_COLOR_SELECT] = AlBookStyles.DEFAULT_COLOR_SELECT;
		styles.color[InternalConst.TAL_PROFILE_COLOR_MARK0] = AlBookStyles.DEFAULT_COLOR_MARKFIND;
		styles.color[InternalConst.TAL_PROFILE_COLOR_MARK1] = AlBookStyles.DEFAULT_COLOR_MARKRED;
		styles.color[InternalConst.TAL_PROFILE_COLOR_MARK2] = AlBookStyles.DEFAULT_COLOR_MARKYELLOW;
		styles.color[InternalConst.TAL_PROFILE_COLOR_MARK3] = AlBookStyles.DEFAULT_COLOR_MARKBLUE;
		styles.color[InternalConst.TAL_PROFILE_COLOR_MARK4] = AlBookStyles.DEFAULT_COLOR_MARKGREEN;
		styles.color[InternalConst.TAL_PROFILE_COLOR_MARK5] = AlBookStyles.DEFAULT_COLOR_MARKPURPLE;
		styles.color[InternalConst.TAL_PROFILE_COLOR_MARK6] = AlBookStyles.DEFAULT_COLOR_MARKAQUA;
	}


	private static final int MAX_NOTESITEMS_ON_PAGE	= 2;
	private static final int DEF_RED_LINE_VALUE =		104;	
	private static final int DEF_RED_LINEV_VALUE =		100;
	private static final int DEF_STYLEV_VALUE =		0;
	private static final int DEF_RED_PARV_VALUE =		20;
	private static final int DEF_STYLE1_VALUE =		210;
	private static final int DEF_STYLE2_VALUE =		225;
	private static final int DEF_STYLE3_VALUE =		240;
	private static final int DEF_RED_SUMMV_VALUE =		0;
	private static final boolean DEF_SCREEN_PUNCTUATION =	true;

    private static final String TESTSTRING_FOR_CALCPAGESIZE = "Ш .ангй";
	private void calcScreenParameters() {

		screen_parameters.fletter_mask0 = AlStyles.SL_MARKFIRTSTLETTER0 |
			(0x03/*styles.style[InternalConst.STYLES_STYLE_FLETTER0]*/ & (AlStyles.SL_COLOR_MASK | AlStyles.SL_SHADOW |
			AlStyles.SL_FONT_MASK | AlStyles.SL_SIZE_MASK/* | AlStyles.SL_KONTUR_MASK*/));

		if (profiles.classicFirstLetter) {
			screen_parameters.fletter_mask0 &= AlStyles.SL_MARKFIRTSTLETTER0 | AlStyles.SL_FONT_MASK | AlStyles.SL_SIZE_MASK | AlStyles.SL_COLOR_MASK;
			screen_parameters.fletter_mask0 |= 0x03/*styles.style[InternalConst.STYLES_STYLE_FLETTER1]*/ & (AlStyles.SL_SHADOW/* | AlStyles.SL_KONTUR_MASK*/);
			screen_parameters.fletter_mask1 = 0x03/*styles.style[InternalConst.STYLES_STYLE_FLETTER1]*/ & (AlStyles.STYLE_BOLD | AlStyles.STYLE_ITALIC);
		} else
			screen_parameters.fletter_mask1 = 0x03/*styles.style[InternalConst.STYLES_STYLE_FLETTER0]*/ & (AlStyles.STYLE_BOLD | AlStyles.STYLE_ITALIC);

		screen_parameters.style_notes = 80L << AlStyles.SL_SIZE_SHIFT;//styles.style[InternalConst.STYLES_STYLE_FOOTNOTES];
		//screen_parameters.style_titlenotes = styles.style[InternalConst.STYLES_STYLE_TITLE] & AlStyles.SL_COLOR_MASK;
		screen_parameters.fletter_colored = true;
				
		screen_parameters.marginL = profiles.marginL;
		screen_parameters.marginR = profiles.marginR;
		screen_parameters.marginT = profiles.marginT;
		screen_parameters.marginB = profiles.marginB;
		int min_dim = preferences.value2CalcMargins > 0 ? preferences.value2CalcMargins : Math.min(screenWidth >> 1, screenHeight);
		if (screen_parameters.marginL < 0) screen_parameters.marginL = screen_parameters.marginL * (-1) * min_dim  / (profiles.twoColumnUsed ? 100 : 100);
		if (screen_parameters.marginT < 0) screen_parameters.marginT = screen_parameters.marginT * (-1) * min_dim  / 100;
		if (screen_parameters.marginR < 0) screen_parameters.marginR = screen_parameters.marginR * (-1) * min_dim  / (profiles.twoColumnUsed ? 100 : 100);
		if (screen_parameters.marginB < 0) screen_parameters.marginB = screen_parameters.marginB * (-1) * min_dim  / 100;

		if (profiles.twoColumnUsed && screen_parameters.marginR < 30 * preferences.multiplexer)
			screen_parameters.marginR = (int) (30 * preferences.multiplexer);
			
		int tmp;

		for (int i = 0; i < InternalConst.TAL_PROFILE_FONTTYPE_COUNT; i++) {
			fonts.modifyPaint(0xffffffffffffffffL, AlStyles.SL_SIZE_NORMAL | ((long)i << AlStyles.SL_FONT_SHIFT), profiles, false);
			screen_parameters.cFontInterline[i] = profiles.font_interline[i];
			screen_parameters.cFontHeight[i] = fontParam.base_line_down + fontParam.base_line_up;
			screen_parameters.cFontLineDown[i] = fontParam.base_line_down;
			screen_parameters.cFontLineUp[i] = fontParam.base_line_up;
		}

		//
		fonts.modifyPaint(0xffffffffffffffffL, AlStyles.SL_SIZE_NORMAL, profiles, false);
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

		screen_parameters.reservHeight0 = (int) (fontParam.def_reserv * preferences.multiplexer);
			
		//int paragraphHeight = 0x65656900;//PrefManager.getInt(R.string.keyscreen_paragraph);
		
		screen_parameters.redLineV = DEF_RED_LINEV_VALUE;
		if (screen_parameters.redLineV < 10)
			screen_parameters.redLineV = 10;
		if (screen_parameters.redLineV > 200)
			screen_parameters.redLineV = 200;
		screen_parameters.redParV = DEF_RED_PARV_VALUE;
		
		/*screen_parameters.redLine = DEF_RED_LINE_VALUE;
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
			screen_parameters.redLine *= 2;*/
		
		screen_parameters.redList = (fontParam.space_width_standart * (preferences.chinezeFormatting ? 4 : 3));

		/*screen_parameters.redStyle1 = profiles.margin1Style;//DEF_STYLE1_VALUE;
		if (screen_parameters.redStyle1 >= 200) {
			screen_parameters.redStyle1 = (screen_parameters.free_picture_width * (screen_parameters.redStyle1 - 200) / 100);
		} else
		if (screen_parameters.redStyle1 >= 100) {
			screen_parameters.redStyle1 = (int) (fontParam.space_width_standart * (screen_parameters.redStyle1 - 100));
		}		
		if (screen_parameters.redStyle1 < 1) {
			screen_parameters.redStyle1 = 1;
		}*/

		//paragraphHeight = 0x656565;//PrefManager.getInt(R.string.keyscreen_parlevel);
			
		/*screen_parameters.summRedV = DEF_RED_SUMMV_VALUE;
		
		screen_parameters.redStyle2 = profiles.margin2Style;//DEF_STYLE2_VALUE;
		if (screen_parameters.redStyle2 >= 200) {
			screen_parameters.redStyle2 = (int) (screen_parameters.free_picture_width * (screen_parameters.redStyle2 - 200) / 100);
		} else
		if (screen_parameters.redStyle2 >= 100) {
			screen_parameters.redStyle2 = (int) (fontParam.space_width_standart * (screen_parameters.redStyle2 - 100));
		}			
		if (screen_parameters.redStyle2 < 1) {
			screen_parameters.redStyle2 = 1;
		}
		
		screen_parameters.redStyle3 = profiles.margin3Style;//DEF_STYLE3_VALUE;
		if (screen_parameters.redStyle3 >= 200) {
			screen_parameters.redStyle3 = (int) (screen_parameters.free_picture_width * (screen_parameters.redStyle3 - 200) / 100);
		} else
		if (screen_parameters.redStyle3 >= 100) {
			screen_parameters.redStyle3 = (int) (fontParam.space_width_standart * (screen_parameters.redStyle3 - 100));
		}		
		if (screen_parameters.redStyle3 < 1) {
			screen_parameters.redStyle3 = 1;
		}*/

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

            calc.getTextWidths(TESTSTRING_FOR_CALCPAGESIZE.toCharArray(), 0, TESTSTRING_FOR_CALCPAGESIZE.length(), testWidth, true);
            for (int i = 1; i < TESTSTRING_FOR_CALCPAGESIZE.length(); i++)
                testWidth[0] += testWidth[i];

            float charWidth = (float)(testWidth[0]) / TESTSTRING_FOR_CALCPAGESIZE.length() + 0.5f;

            int itemHeight = screen_parameters.cFontHeight[InternalConst.TAL_PROFILE_FONTTYPE_TEXT] +
                    screen_parameters.cFontHeight[InternalConst.TAL_PROFILE_FONTTYPE_TEXT] *
							screen_parameters.cFontInterline[InternalConst.TAL_PROFILE_FONTTYPE_TEXT] / 100;
            int rows = (screenHeight - screen_parameters.marginT - screen_parameters.marginB) / itemHeight;
            if (rows < 1)
                rows = 1;
            int cols = (int) (((profiles.twoColumnUsed ? (screenWidth >> 1) : screenWidth) -
                                screen_parameters.marginL - screen_parameters.marginR) / charWidth);
            if (cols < 1)
                cols = 1;


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
            if (preferences.pageSize < 1)
                preferences.pageSize = 1;

            preferences.needCalcAutoPageSize = false;
        }
	}

	private void initDefaultPreference() {
		preferences.maxNotesItemsOnPageRequest = MAX_NOTESITEMS_ON_PAGE;
		preferences.delete0xA0 = true;
		preferences.need_dialog = 0x00;
		preferences.notesAsSUP = true;
		preferences.sectionNewScreen = false;
		//preferences.styleSumm = false;
		preferences.u301mode = 0x00;		
		preferences.notesOnPage = true;
		preferences.justify = true;
		preferences.vjustifyRequest = false;
		preferences.isASRoll = false;
		preferences.useSoftHyphen = true;
		preferences.calcPagesModeRequest = TAL_SCREEN_PAGES_COUNT.SIZE;
	}

	private void drawPageFromPosition(int pos, boolean needRecalc, int activePage) {
		calc.drawBackground(screenWidth, screenHeight, profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK],
                profiles.background, profiles.backgroundMode);

		AlBitmap abmp = engOptions.externalBitmap != null ? engOptions.externalBitmap : bmp[activePage];

		notesCounter++;
		if (profiles.twoColumnUsed) {

			if (needRecalc) {
				recalcColumn(
						(screenWidth >> 1) - screen_parameters.marginR - screen_parameters.marginL,
						screenHeight - screen_parameters.marginB - screen_parameters.marginT,
                        mpage[activePage][0], pos/*, TAL_CALC_MODE.NORMAL*/);
				prepareColumn(mpage[activePage][0]);

				recalcColumn(
						(screenWidth >> 1) - screen_parameters.marginR - screen_parameters.marginL,
						screenHeight - screen_parameters.marginB - screen_parameters.marginT,
                        mpage[activePage][1], mpage[activePage][0].end_position/*, TAL_CALC_MODE.NORMAL*/);
				prepareColumn(mpage[activePage][1]);
			}

			markFindResultAndSelect(mpage[activePage][0]);
			markFindResultAndSelect(mpage[activePage][1]);

			if (activePage == 0)
				selection.clearSelectMarker();

			drawColumn(mpage[activePage][0],
                    screen_parameters.marginL,
                    screen_parameters.marginT,
                    (screenWidth >> 1) - screen_parameters.marginR,
                    screenHeight - screen_parameters.marginB);
			drawColumn(mpage[activePage][1],
					(screenWidth >> 1) + screen_parameters.marginR,
					screen_parameters.marginT,
					screenWidth - screen_parameters.marginL,
					screenHeight - screen_parameters.marginB);

			if (activePage == 0)
				drawSelectMarker();

			abmp.freeSpaceAfterPage = 0;
		} else {
			Log.e("calc0", Long.toString(System.currentTimeMillis()));
			if (needRecalc) {
				recalcColumn(
						screenWidth - screen_parameters.marginR - screen_parameters.marginL,
						screenHeight - screen_parameters.marginB - screen_parameters.marginT,
                        mpage[activePage][0], pos/*, TAL_CALC_MODE.NORMAL*/);
				prepareColumn(mpage[activePage][0]);
			}
			Log.e("calc1", Long.toString(System.currentTimeMillis()));
			markFindResultAndSelect(mpage[activePage][0]);
			Log.e("calc2", Long.toString(System.currentTimeMillis()));

			if (activePage == 0)
				selection.clearSelectMarker();

            if (profiles.specialModeMadRoll)
                calc.setViewPort(0, screen_parameters.marginT, screenWidth, screenHeight  - screen_parameters.marginB);

            drawColumn(mpage[activePage][0],
                    screen_parameters.marginL,
                    screen_parameters.marginT,
                    screenWidth - screen_parameters.marginR,
                    screenHeight - screen_parameters.marginB);
			Log.e("calc3", Long.toString(System.currentTimeMillis()));

            if (profiles.specialModeMadRoll)
                calc.setViewPort(-100, 0, screenWidth, screenHeight);

			if (activePage == 0)
				drawSelectMarker();

            if (profiles.specialModeRoll) {
				abmp.freeSpaceAfterPage = mpage[activePage][0].pageHeight - mpage[activePage][0].textHeight;
                if (abmp.freeSpaceAfterPage < 0 || mpage[activePage][0].notePresent || screen_parameters.marginT != 0 || screen_parameters.marginB != 0)
					abmp.freeSpaceAfterPage = 0;
            } else {
				abmp.freeSpaceAfterPage = 0;
            }
		}
		
		if (selection.selectMode == TAL_SCREEN_SELECTION_MODE.CLEAR)
			selection.selectMode = TAL_SCREEN_SELECTION_MODE.NONE;
	}

	private void dublicatePage(int srcPage, int dstPage, int pos) {
		if (selection.selectMode != TAL_SCREEN_SELECTION_MODE.NONE)
			return;
		if (engOptions.externalBitmap != null || engOptions.runInOneThread)
			return;
		if (bmp[dstPage].width != bmp[srcPage].width ||
			bmp[dstPage].height != bmp[srcPage].height)
			return;

		calc.beginMain(bmp[dstPage], profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK]);
		calc.drawImage(0, 0, bmp[srcPage].width, bmp[srcPage].height, bmp[srcPage], false);
		bmp[dstPage].shtamp = bmp[srcPage].shtamp;
		bmp[dstPage].position = pos;
		bmp[dstPage].freeSpaceAfterPage = bmp[srcPage].freeSpaceAfterPage;
		calc.endMain();

		mpage[dstPage][0].dublicate(mpage[srcPage][0]);
		if (profiles.twoColumnUsed)
			mpage[dstPage][1].dublicate(mpage[srcPage][1]);
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
	public synchronized AlBitmap	getPageBitmap(TAL_PAGE_INDEX index, int width, int height) {

        boolean needCalcNextPage = false;
		AlBitmap abmp = null;

		int rW = (width + 0x03) & 0xfffc;
		int rH = (height + 0x03) & 0xfffc;

        if (openState.getState() != AlBookState.OPEN && openState.getState() <= AlBookState.PROCESS0) {
            if (index == TAL_PAGE_INDEX.CURR) {
				abmp = engOptions.externalBitmap != null ? engOptions.externalBitmap : bmp[2];

                int waitposition = openState.getState() != AlBookState.NOLOAD ? -2 : -1;

                if (abmp.width != rW || abmp.height != rH)
                    EngBitmap.reCreateBookBitmap(abmp, width, height, shtamp);

                if (abmp.shtamp == shtamp.value && abmp.position == waitposition)
                    return abmp;

				abmp.shtamp = shtamp.value;
				abmp.position = waitposition;

                calc.beginMain(abmp, profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK]);
                calc.drawBackground(width, height, profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK], profiles.background, profiles.backgroundMode);
                if (openState.getState() != AlBookState.NOLOAD) {
                    int x = (width - waitBitmap.width) >> 1;
                    int y = (height - waitBitmap.height) >> 1;
                    calc.drawImage(x, y, waitBitmap.width, waitBitmap.height, waitBitmap, profiles.isTransparentImage);
                }
                calc.endMain();
				return abmp;
            } else
				return null;
        }

		if (index == TAL_PAGE_INDEX.CURR) {
			abmp = engOptions.externalBitmap != null ? engOptions.externalBitmap : bmp[0];

			if (abmp.width != rW || abmp.height != rH)
				EngBitmap.reCreateBookBitmap(abmp, width, height, shtamp);

			if (abmp.shtamp != shtamp.value || bookPosition != abmp.position) {

				abmp.shtamp = shtamp.value;
				abmp.position = bookPosition;

                calc.beginMain(abmp, profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK]);

                calcScreenParameters();
                drawPageFromPosition(bookPosition, true, 0);

                calc.endMain();
            } else
            if (selection.selectMode != EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE &&
                    selection.shtampSelectRequred != selection.shtampSelectUsed) {

                calc.beginMain(abmp, profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK]);

                calcScreenParameters();
                drawPageFromPosition(bookPosition, false, 0);

                calc.endMain();
                selection.shtampSelectUsed = selection.shtampSelectRequred;
            }

			needCalcNextPage = engOptions.externalBitmap == null /*&& profiles.specialModeRoll*/ && (cachePrevNextPoint.current != bookPosition ||
                    cachePrevNextPoint.prev == -1 || cachePrevNextPoint.shtamp != shtamp.value);

			if (!needCalcNextPage)
				return abmp;
		}

        if (preferences.isASRoll)
            return null;

        scrollPrevPagePointStop = -1;
        int addonPosition = bookPosition;
        int testPosition = addonPosition;

        if (index == TAL_PAGE_INDEX.NEXT || needCalcNextPage) {
			if (engOptions.externalBitmap != null)
				return null;

            if (testPosition >= format.getSize())
                return needCalcNextPage ? bmp[0] : null;

			if (bmp[1].width != rW || bmp[1].height != rH) {
				EngBitmap.reCreateBookBitmap(bmp[1], width, height, null);
				bmp[1].shtamp = -101;
			}

            if (bmp[1].shtamp != shtamp.value || testPosition != bmp[1].position) {

                if (cachePrevNextPoint.current != bookPosition || cachePrevNextPoint.shtamp != shtamp.value) {
                    cachePrevNextPoint.shtamp = shtamp.value;
                    cachePrevNextPoint.current = bookPosition;
                    cachePrevNextPoint.next = cachePrevNextPoint.prev = -1;
                }

                //Log.e("calc next", Boolean.toString(needCalcNextPage));
                if (cachePrevNextPoint.next != -1) {
                    addonPosition = cachePrevNextPoint.next;
                } else {
                    addonPosition = cachePrevNextPoint.next = calculateNextPagePoint(bookPosition);
                }

                if (addonPosition == bookPosition)
                    return needCalcNextPage ? bmp[0] : null;

                bmp[1].shtamp = shtamp.value;
                bmp[1].position = testPosition;

                calc.beginMain(bmp[1], profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK]);
                calcScreenParameters();
                if (index == TAL_PAGE_INDEX.PREV)
                    scrollPrevPagePointStop = bookPosition;
                drawPageFromPosition(addonPosition, true, 1);
                scrollPrevPagePointStop = -1;
                calc.endMain();
            }

            return needCalcNextPage ? bmp[0] : bmp[1];
        }

        // draw prev page
		if (engOptions.externalBitmap != null)
			return null;
        if (testPosition == 0)
            return null;
        testPosition *= -1;

		if (bmp[2].width != rW || bmp[2].height != rH) {
			EngBitmap.reCreateBookBitmap(bmp[2], width, height, null);
			bmp[2].shtamp = -101;
		}

        if (bmp[2].shtamp != shtamp.value || testPosition != bmp[2].position) {

            if (cachePrevNextPoint.current != bookPosition || cachePrevNextPoint.shtamp != shtamp.value) {
                cachePrevNextPoint.shtamp = shtamp.value;
                cachePrevNextPoint.current = bookPosition;
                cachePrevNextPoint.next = cachePrevNextPoint.prev = -1;
            }

            //Log.e("calc prev", Boolean.toString(needCalcNextPage));
            if (cachePrevNextPoint.prev != -1) {
                addonPosition = cachePrevNextPoint.prev;
            } else {
                addonPosition = cachePrevNextPoint.prev = calculatePrevPagePoint(bookPosition);
            }

            if (addonPosition == bookPosition)
                return null;

			bmp[2].shtamp = shtamp.value;
            bmp[2].position = testPosition;

            calc.beginMain(bmp[2], profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK]);
            calcScreenParameters();
            if (index == TAL_PAGE_INDEX.PREV)
                scrollPrevPagePointStop = bookPosition;
            drawPageFromPosition(addonPosition, true, 2);
            scrollPrevPagePointStop = -1;
            calc.endMain();
        }

        return bmp[2];
	}

    private int scrollPrevPagePointStop = -1;

    private class AlCachePrevNextPoint {
        int     shtamp = -100;
        int     current = -1;
        int     prev = -1;
        int     next = -1;
    }
    private final AlCachePrevNextPoint cachePrevNextPoint = new AlCachePrevNextPoint();

	private void markFindResultAndSelect(AlOnePage page) {
		AlOneItem oi;

		int spos, epos;
		if (bookmarks != null) {
			for (int i = 0; i < bookmarks.size(); i++) {
				if (bookmarks.get(i).color == EngBookMyType.TAL_BOOKMARK_COLOR.NONE)
					continue;

				spos = bookmarks.get(i).pos_start;
				epos = bookmarks.get(i).pos_end;

				if ((spos >= page.start_position && spos < page.end_position)||
					(epos >= page.start_position && epos < page.end_position)||
					(spos <  page.start_position && epos > page.end_position)){

					boolean needMark;
					for (int item = 0; item < page.countItems; item++) {
						oi = page.items.get(item);
						for (int poschar = 0; poschar < oi.count; poschar++) {
							if (oi.pos[poschar] >= spos && oi.pos[poschar] <= epos) {
								needMark = true;
							} else if (oi.pos[poschar] == SPECIAL_HYPH_POS && poschar > 0 && oi.pos[poschar - 1] >= spos && oi.pos[poschar - 1] <= epos) {
								needMark = true;
							} else
								needMark = false;

							if (needMark) {
								oi.style[poschar] &= ~(AlStyles.SL_MARK | AlStyles.SL_MARKCOLOR_MASK);
								switch (bookmarks.get(i).color) {
								case RED:
									oi.style[poschar] |= AlStyles.SL_MARK | AlStyles.SL_MARKCOLOR2;
									break;
								case YELLOW:
									oi.style[poschar] |= AlStyles.SL_MARK | AlStyles.SL_MARKCOLOR3;
									break;
								case BLUE:
									oi.style[poschar] |= AlStyles.SL_MARK | AlStyles.SL_MARKCOLOR4;
									break;
								case GREEN:
									oi.style[poschar] |= AlStyles.SL_MARK | AlStyles.SL_MARKCOLOR5;
									break;
								case PURPLE:
									oi.style[poschar] |= AlStyles.SL_MARK | AlStyles.SL_MARKCOLOR6;
									break;
								case UNDERLINE:
									oi.style[poschar] |= AlStyles.SL_MARK | AlStyles.SL_MARKCOLOR7;
									break;
								}
							}
						}
					}
				}
			}
		}

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
						} else
                        //
                            if (oi.pos[poschar] == SPECIAL_HYPH_POS && poschar > 0 && oi.pos[poschar - 1] >= selection.selectPosition.x && oi.pos[poschar - 1] <= selection.selectPosition.y) {
                                oi.style[poschar] |= AlStyles.SL_SELECT;
                            } else
                        //
                        {
							oi.style[poschar] &= ~AlStyles.SL_SELECT;
						}
					}
				}
				break;
			default:
				if (format.resfind.size() > 0) {
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
									if (oi.pos[poschar] >= spos && oi.pos[poschar] <= epos) {
                                        oi.style[poschar] |= AlStyles.SL_MARK | AlStyles.SL_MARKCOLOR1;
                                    } else
                                    if (oi.pos[poschar] == SPECIAL_HYPH_POS && poschar > 0 && oi.pos[poschar - 1] >= spos && oi.pos[poschar - 1] <= epos)
                                        oi.style[poschar] |= AlStyles.SL_MARK | AlStyles.SL_MARKCOLOR1;
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
		
		int ext_len, cnt_char, cnt_img = 0;
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
				(profiles.classicFirstLetter || oi.isEnd || ((oi.style[0] & AlStyles.SL_MARKFIRTSTLETTER0) == 0)))
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
				
				
				if (poi != null && (poi.style[0] & AlStyles.SL_MARKFIRTSTLETTER0) != 0) {

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

				// special case
				if (cnt_char == 1 && cnt_img == 1 && oi.text[1] == '-' && oi.pos[1] < 0) {
                    cnt_char = 0;
                    oilen--;
                    oi.count--;
                    oi.textWidth -= oi.width[1];
				}

				if (cnt_char == 0 && cnt_img == 1) {
					AlOneItem poi = null;
					if (oi.count == 1 && profiles.classicFirstLetter) {
						for (jj = j - 1; jj >= 0; jj--)
							if (!page.items.get(jj).isNote) {
								poi = page.items.get(jj);
								break;
							}
					}
					
					if (poi != null && (poi.style[0] & AlStyles.SL_MARKFIRTSTLETTER0) != 0) {
						if (oi.width[0] > oi.allWidth) {
							oi.width[0] = oi.allWidth;
							oi.textWidth = oi.allWidth;
						}						
					} else {
						oi.justify = AlParProperty.SL2_JUST_CENTER;
						if (oi.textWidth <= oi.allWidth) {
							oi.allWidth += oi.isRed;
							oi.isRed = 0;
						} else {
							oi.allWidth += oi.isRed + oi.isLeft + oi.isRight;
							oi.isLeft = 0;
							oi.isRed = 0;
							oi.isRight = 0;
						}
						oi.allWidth += oi.isRed + oi.isLeft + oi.isRight;
						oi.isLeft = 0;
						oi.isRed = 0;
						oi.isRight = 0;					
						if (page.countItems == 1) {
                            if (oi.isTableRow) {
								oi.height += oi.base_line_down >> 1;
								oi.base_line_down >>= 1;
							} else {
                                if (page.pageHeight > page.textHeight && (!profiles.specialModeRoll || ((oi.style[0] & AlStyles.SL_COVER) != 0))) {
                                    oi.height += (page.pageHeight - page.textHeight) >> 1;
                                    page.textHeight += page.pageHeight - page.textHeight;
                                }
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
					(oi.isEnd || oi.justify != AlParProperty.SL2_JUST_NONE || !preferences.justify)) {
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
			if (oi.count > 0 && oi.justify == AlParProperty.SL2_JUST_CENTER &&
				((oi.style[0] & (/*AlStyles.SL_MARKTITLE | */AlParProperty.SL2_JUSTIFY_POEM)) ==
					(/*AlStyles.SL_MARKTITLE | */AlParProperty.SL2_JUSTIFY_POEM)) &&
				!oi.isEnd) {
				
				count_space = 0;
				for (i = 0; i < oilen; i++) {
					if (oi.text[i] == 0x20)
						count_space++;
				}
				
				if (count_space >= 2) {
					oi.justify = AlParProperty.SL2_JUST_NONE;
					specialJust = true;
				}
			}
			
			if (oi.justify == AlParProperty.SL2_JUST_NONE) {

				if (oi.count > 0 && ((oi.style[0] & AlParProperty.SL2_UL_MASK) >> AlParProperty.SL2_UL_SHIFT) == 0) {
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

				for (i = 0; i < oilen - 1; i++) {
					ch = oi.text[i];
					//if (ch == 0x20 || (AlUnicode.isChineze(ch) && !AlUnicode.isLetterOrDigit(ch)))
					if (ch == 0x20 || (AlUnicode.isChineze(ch)/* && !AlUnicode.isLetterOrDigit(ch)*/))
						count_space++;
				}


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
					
					for (i = 0; i < oilen - 1; i++) {
						ch = oi.text[i];
						if (ch == 0x20) {
							add = ext_len / count_space;
							oi.width[i] += add;
							count_space--;
							ext_len -= add;
						} else
						//if (AlUnicode.isChineze(ch) && !AlUnicode.isLetterOrDigit(ch)) {
						if (AlUnicode.isChineze(ch)/* && !AlUnicode.isLetterOrDigit(ch)*/) {
							add = ext_len / count_space;
							oi.width[i] += add;
							count_space--;
							ext_len -= add;

							if ((oi.style[i] & AlStyles.SL_CHINEZEADJUST) == 0) {
								oi.style[i + 1] |= AlStyles.SL_CHINEZEADJUST;
							} else {
								oi.style[i + 1] &= ~AlStyles.SL_CHINEZEADJUST;
							}
						}

					}

                    if (oi.isArabic) {
                        if (oi.isStart)
                            oi.isLeft -= oi.isRed;
                    }

                } else {
					count_space = 0;

					/*for (i = 0; i < oilen - 1; i++) {
						ch = oi.text[i];
						if (!AlUnicode.isLetterOrDigit(ch) || AlUnicode.isChinezeSpecial(ch))
						count_space++;
					}

					if (count_space > 0) {
						ext_len = (int)(oi.allWidth - oi.textWidth);

						for (i = 0; i < oilen - 1; i++) {
							ch = oi.text[i];
							if (!AlUnicode.isLetterOrDigit(ch) || AlUnicode.isChinezeSpecial(ch)) {
								add = (int)(ext_len / count_space);
								count_space--;
								if (add > 0) {
									oi.width[i] += add;
									ext_len -= add;
									if (i == 0 || (oi.style[i - 1] & AlStyles.SL_CHINEZEADJUST) == 0)
										oi.style[i] |= AlStyles.SL_CHINEZEADJUST;
								}
							}
						}
					} else {*/
						for (i = 0; i < oilen - 1; i++) {
							ch = oi.text[i];
							if (AlUnicode.isChineze(ch))
								count_space++;
						}

						if (count_space > (oilen >> 1)) {
							ext_len = oi.allWidth - oi.textWidth;

							for (i = 0; i < oilen - 1; i++) {
								ch = oi.text[i];
								if (AlUnicode.isChineze(ch)) {
									add = ext_len / count_space;
									count_space--;
									if (add > 0) {
										oi.width[i] += add;
										ext_len -= add;
										if ((oi.style[i] & AlStyles.SL_CHINEZEADJUST) == 0) {
											oi.style[i + 1] |= AlStyles.SL_CHINEZEADJUST;
										} else {
											oi.style[i + 1] &= ~AlStyles.SL_CHINEZEADJUST;
										}
									}
								}
							}
						}
					//}
				}
			} else
			if (oi.justify == AlParProperty.SL2_JUST_LEFT) {

					switch (Character.getType(oi.text[0])) {
					case Character.START_PUNCTUATION:
					case Character.INITIAL_QUOTE_PUNCTUATION:
						oi.isLeft -= screen_parameters.vikluchL;
						break;
					}
			} else
			if (oi.justify == AlParProperty.SL2_JUST_CENTER) {
				ext_len = oi.allWidth - oi.textWidth;
				ext_len >>= 1;
				oi.isLeft += ext_len;				
			} else
			if (oi.justify == AlParProperty.SL2_JUST_RIGHT && oi.blockHeight == 0) {
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
				(profiles.classicFirstLetter || oi.isEnd || ((oi.style[0] & AlStyles.SL_MARKFIRTSTLETTER0) == 0)))
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
					
					if (((oi.style[0] & (/*AlStyles.SL_PREV_EMPTY_1 + */AlParProperty.SL2_EMPTY_BEFORE)) != 0) && oi.isStart) {
						jj++;
					} else
					if (oi.isStart) {
						add++;
					} 	
				}
					
				int pt = (int) (2 * preferences.multiplexer);
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
						
						if (((oi.style[0] & (/*AlStyles.SL_PREV_EMPTY_1 + */AlParProperty.SL2_EMPTY_BEFORE)) != 0) && oi.isStart) {
							
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
						
						if (((oi.style[0] & (/*AlStyles.SL_PREV_EMPTY_1 + */AlParProperty.SL2_EMPTY_BEFORE)) != 0) && oi.isStart) {
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

	private void drawImage(int pos, long style, int widthImage, int x, int y, int downLine) {
		AlOneImage ai = null;
		String link;
		int scale = (int) ((style & AlStyles.SL_COLOR_MASK) >> AlStyles.SL_COLOR_SHIFT);

        link = format.getLinkNameByPos(pos, InternalConst.TAL_LINK_TYPE.IMAGE);
		if ((style & AlStyles.SL_IMAGE_OK) != 0) {
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
                        calc.drawRect(x - 1, y - h - 1, x + w + 1, y + 1 + downLine,//y - 2 * preferences.picture_need_tuneK,
                                (profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT] & 0xffffff) | 0x80000000);
                        calc.drawLine(x, y - h, x, y, (int) preferences.multiplexer, profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT]);
                        calc.drawLine(x, y - h, x + w, y - h, (int) preferences.multiplexer, profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT]);
                        calc.drawLine(x + w, y - h, x + w, y, (int) preferences.multiplexer, profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT]);
                        calc.drawRect(x, y,// - 3 * preferences.picture_need_tuneK,
                                x + w, y + downLine, profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT]);
					} else
					if ((style & AlStyles.SL_MARK) != 0) {
						int num_color = (int) ((old_style & AlStyles.SL_MARKCOLOR_MASK) >> AlStyles.SL_MARKCOLOR_SHIFT);

						int sm;
						switch (num_color) {
							case 1: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK0]; break;
							case 2: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK1]; break;
							case 3: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK2]; break;
							case 4: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK3]; break;
							case 5: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK4]; break;
							case 6: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK5]; break;
							case 7: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK6]; break;
							default: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK]; break;
						}

						sm |= 0x80000000;
						if (num_color == 7) {
							calc.drawRect(x, y + 1, x + w, y + 2, sm);
						} else {
							calc.drawRect(x, y + 1, x + w, y + downLine, sm);
						}
					}
					
					return;
				}
			}
		}

        if (link != null) {
            if (AlFormat.LEVEL2_TABLETOTEXT_STR.contentEquals(link)){
                if (tableBitmap != null) {
                    imageParam.real_height = tableBitmap.height;
                    imageParam.real_width = tableBitmap.width;
                    calc.drawImage(x, y - imageParam.real_height, tableBitmap.width, tableBitmap.height, tableBitmap, profiles.isTransparentImage);
                    return;
                }
            }
        }
		if (errorBitmap != null) {
			imageParam.real_height = errorBitmap.height;
			imageParam.real_width = errorBitmap.width;
			calc.drawImage(x, y - imageParam.real_height, errorBitmap.width, errorBitmap.height, errorBitmap, profiles.isTransparentImage);
		}
	}


    private void drawTable(int num, long style, int x, int y, AlOneItem oi, AlOnePage page) {
        AlOneTable ai = null;
        String link, rows;

        link = format.getLinkNameByPos(oi.pos[num], InternalConst.TAL_LINK_TYPE.ROW);
        if (link == null)
            return;
        int i = link.indexOf(':');
        if (i == -1)
            return;

        rows = link.substring(i + 1);
        link = link.substring(0, i);

        int table = InternalFunc.str2int(link, 10);
        int row = InternalFunc.str2int(rows, 10);

        ai = format.getTableByNum(table);

        ////////////////////////////////////////////////////////////////////////////////

        if (ai != null) {

            for (i = 0; i < ai.rows.get(row).cell_accepted; i++) {

                if (ai.rows.get(row).cells.get(i).start == AlOneTable.LEVEL2_TABLE_CELL_COLSPANNED || ai.rows.get(row).cells.get(i).start == AlOneTable.LEVEL2_TABLE_CELL_ALIGNED)
                    continue;

                //all draw left and right line
                calc.drawLine(
                        x + ai.rows.get(row).cells.get(i).left + ai.rows.get(row).cells.get(i).width,
                        y - oi.base_line_up - oi.height,
                        x + ai.rows.get(row).cells.get(i).left + ai.rows.get(row).cells.get(i).width,
                        y + oi.base_line_down + oi.interline,
						(int) preferences.multiplexer, profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT]);
                calc.drawLine(
                        x + ai.rows.get(row).cells.get(i).left,
                        y - oi.base_line_up - oi.height,
                        x + ai.rows.get(row).cells.get(i).left,
                        y + oi.base_line_down + oi.interline,
						(int) preferences.multiplexer, profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT]);

                //draw down line if last row or bottom cell not equal -2
                if (row == ai.rows.size() - 1 || i >= ai.rows.get(row + 1).cells.size() || ai.rows.get(row + 1).cells.get(i).start != AlOneTable.LEVEL2_TABLE_CELL_ROWSPANNED) {
                    calc.drawLine(
                            x + ai.rows.get(row).cells.get(i).left,
                            y + oi.base_line_down + oi.interline,
                            x + ai.rows.get(row).cells.get(i).left + ai.rows.get(row).cells.get(i).width,
                            y + oi.base_line_down + oi.interline,
							(int) preferences.multiplexer, profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT]);
                }

                if (ai.rows.get(row).cells.get(i).start == AlOneTable.LEVEL2_TABLE_CELL_ROWSPANNED) {
                    continue;
                }

                //draw top line if not -2
                calc.drawLine(
                        x + ai.rows.get(row).cells.get(i).left,
                        y - oi.base_line_up - oi.height,
                        x + ai.rows.get(row).cells.get(i).left + ai.rows.get(row).cells.get(i).width,
                        y - oi.base_line_up - oi.height,
						(int) preferences.multiplexer, profiles.colors[InternalConst.TAL_PROFILE_COLOR_TEXT]);

                if (!ai.rows.get(row).cells.get(i).isFull) {
                    calc.drawRect(
                            x + ai.rows.get(row).cells.get(i).left,
                            y - oi.base_line_up - oi.height,
                            x + ai.rows.get(row).cells.get(i).left + ai.rows.get(row).cells.get(i).width,
                            y + oi.base_line_down + oi.interline,
                            0x30000000 | profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT]);
                }

                prepareColumn(ai.rows.get(row).pages[i]);
                markFindResultAndSelect(ai.rows.get(row).pages[i]);

                if (ai.rows.get(row).cells.get(i).width >= (fontParam.space_width_standart << 1))
                    drawColumn(ai.rows.get(row).pages[i],
                            x + ai.rows.get(row).cells.get(i).left + (fontParam.space_width_standart >> 0),
                            y - ai.rows.get(row).height,
                            x + ai.rows.get(row).cells.get(i).left + ai.rows.get(row).cells.get(i).width - (fontParam.space_width_standart >> 0),
                            y);
            }
        }
    }

    private int drawPartItem(int start, int end,
							 long old_style, int x, int y,
							 AlOneItem oi, AlOnePage page) {
		int i, k;
		int x2;
		
		int ySelect0 = y;
		if (profiles.classicFirstLetter && ((old_style & AlStyles.SL_MARKFIRTSTLETTER0) != 0)) {
			y += fontParam.base_ascent - (fontParam.height - fontParam.def_line_down);
			//y -= screen_parameters.interFI0[InternalConst.INTER_FLET];
		}
		int ySelect1 = y;

        //boolean pageForMarker = //(page == &mpage[0][0] || page == &mpage[0][1]);
        //        page != mpage[1][0] && page != mpage[1][1] && page != mpage[2][0] && page != mpage[2][1];

        if (!oi.isNote && page.mode != InternalConst.TAL_PAGE_MODE.ADDON)//pageForMarker)
			switch (selection.selectMode) {
			case DICTIONARY:
			case START:
			case END:
				x2 = x;
				for (i = start; i <= end; i++) {
					if (selection.selectPosition.x == oi.pos[i]) {
						selection.selectMarkerStart.x = x2/* - (selectStartBitmap.width >> 1)*/;
						selection.selectMarkerStart.y = ySelect0 - oi.base_line_up;
						selection.selectMarkerStart.height = oi.base_line_up + oi.base_line_down;
					}

					x2 += oi.width[i];

					if (selection.selectPosition.y >= oi.pos[i] && oi.pos[i] >= 0) {
						selection.selectMarkerEnd.x = x2/* - (selectStartBitmap.width >> 1)*/;
						selection.selectMarkerEnd.y = ySelect0 - oi.base_line_up;
						selection.selectMarkerEnd.height = oi.base_line_up + oi.base_line_down;
					}
				}
				break;
        }
		
		if ((old_style & AlStyles.SL_IMAGE) != 0) {
			for (i = start; i <= end; i++) {
                if (oi.text[i] == AlStyles.CHAR_IMAGE_E) {

                    if (oi.blockHeight != 0) {
                        y += oi.blockHeight;
                        x -= oi.isRed;
                    }

                    drawImage(oi.pos[i], oi.style[i], oi.width[i], x, y, oi.base_line_down);
                    if (engOptions.drawLinkInternal && (oi.style[i] & AlStyles.STYLE_LINK) != 0)
                        calc.drawLine(x, y + 2, x + oi.width[i] + 1, y + 2,
								(int) preferences.multiplexer, profiles.colors[InternalConst.TAL_PROFILE_COLOR_LINK]);
                    x += oi.width[i];

                    if (oi.blockHeight != 0) {
                        y -= oi.blockHeight;
                        x += oi.isRed;
                    }
                } else
                if (oi.width[i] > 0) {
                    drawTable(i, oi.style[i], x, y, oi, page);
                }
			}
		} else {

			int sm;
			if ((old_style & (AlStyles.SL_MARK | AlStyles.SL_SELECT)) != 0) {

				if ((old_style & AlStyles.SL_MARK) != 0) {
					int num_color = (int) ((old_style & AlStyles.SL_MARKCOLOR_MASK) >> AlStyles.SL_MARKCOLOR_SHIFT);

					switch (num_color) {
					case 1: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK0]; break;
					case 2: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK1]; break;
					case 3: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK2]; break;
					case 4: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK3]; break;
					case 5: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK4]; break;
					case 6: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK5]; break;
					case 7: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK6]; break;
					default: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK]; break;
					}

					sm |= 0x80000000;

					x2 = x;
					for (i = start; i <= end; i++)
						x2 += oi.width[i];

					if (num_color == 7) {
						calc.drawRect(x, ySelect0  + 1, x2, ySelect1 + 2, sm);
					} else {
						calc.drawRect(x, ySelect0 - oi.base_line_up, x2, ySelect1 + oi.base_line_down, sm);
					}
				}

				if ((old_style & AlStyles.SL_SELECT) != 0) {
					sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT];

					sm |= 0x80000000;

					x2 = x;
					for (i = start; i <= end; i++)
						x2 += oi.width[i];

					calc.drawRect(x, ySelect0 - oi.base_line_up, x2, ySelect1 + oi.base_line_down, sm);
				}
			}

			
			if ((old_style & AlStyles.STYLE_SUB) != 0) {
				y += fontParam.base_line_down / 2; 
			} else
			if ((old_style & AlStyles.STYLE_SUP) != 0) {
				y -= fontParam.base_line_up / 2; 
			}

			if ((old_style & AlStyles.STYLE_UNDER) != 0 ||
					((old_style & AlStyles.STYLE_LINK) != 0) && engOptions.drawLinkInternal) {
				x2 = x; 
				for (i = start; i <= end; i++)
					x2 += oi.width[i];
				calc.drawLine(x, y + 2, x2 + 1, y + 2,
						(int) preferences.multiplexer, fontParam.color);
			}
			
			//k = (int) ((old_style & AlStyles.SL_KONTUR_MASK) >> AlStyles.SL_KONTUR_SHIFT);
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
							calc.drawText(x + (int)preferences.multiplexer, y + (int)preferences.multiplexer,
								convertChineseText(oi.text), start, end - start + 1);
							fontParam.color = x2;
						}
						calc.drawText(x, y, convertChineseText(oi.text), i, 1);
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
						x2 = calc.fontPaint.getColor();
						calc.fontPaint.setColor(profiles.colors[InternalConst.TAL_PROFILE_COLOR_SHADOW] | 0xff000000);
						calc.drawText(x + (int)preferences.multiplexer, y + (int)preferences.multiplexer,
							convertChineseText(oi.text), start, end - start + 1);
						calc.fontPaint.setColor(x2);
					}
					calc.drawText(x, y, convertChineseText(oi.text), start, end - start + 1);

				//}
				
				for (i = start; i <= end; i++) 
					x += oi.width[i];
			}
		}
		
		return x;
	}

    private void drawSelectMarker() {
        switch (selection.selectMode) {
            case START:
            case END:
                if (selection.selectMarkerStart.x != -1 && selection.selectMarkerStart.y != -1) {
                    if (selectStartBitmap != null)
                    	calc.drawImage(selection.selectMarkerStart.x - (selectStartBitmap.width >> 1),
								selection.selectMarkerStart.y
									- (selectStartBitmap.height >> 1)
                                ,
                            selectStartBitmap.width, selectStartBitmap.height, selectStartBitmap, true);
                }
                if (selection.selectMarkerEnd.x != -1 && selection.selectMarkerEnd.y != -1) {
                    if (selectEndBitmap != null)
						calc.drawImage(selection.selectMarkerEnd.x - (selectEndBitmap.width >> 1),
							selection.selectMarkerEnd.y + selection.selectMarkerEnd.height - selectEndBitmap.height
									+ (selectStartBitmap.height >> 1)
                                ,
							selectEndBitmap.width, selectEndBitmap.height, selectEndBitmap, true);
                }
                break;
        }
    }

    public synchronized AlPoint getSelectedPoint(boolean needStart) {
		selection.selectMarkerStart.position = selection.selectPosition.x;
		selection.selectMarkerEnd.position = selection.selectPosition.y;

		return needStart ? selection.selectMarkerStart : selection.selectMarkerEnd;
    }

	private void drawColumn(AlOnePage page, int x0, int y0, int x1, int y1) {

		boolean first_notes = true;
		AlOneItem oi;
		int x;
		int col_count = page.countItems;
		
		if (preferences.isASRoll || (profiles.specialModeRoll && !profiles.twoColumnUsed)) {
			oi = page.items.get(col_count);
			if (oi.count > 0 && oi.pos[0] >= page.end_position &&
				(profiles.classicFirstLetter || oi.isEnd || ((oi.style[0] & AlStyles.SL_MARKFIRTSTLETTER0) == 0)))
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
								(int)preferences.multiplexer,
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
					
					if (oi.isStart && ((oi.justify & AlParProperty.SL2_JUST_RIGHT) == 0) && ((oi.style[0] & AlParProperty.SL2_UL_BASE) != 0)) {
						int ul = (int) ((oi.style[0] & AlParProperty.SL2_UL_MASK) >> AlParProperty.SL2_UL_SHIFT);
						long stl = oi.style[0] & (~((long)AlStyles.STYLE_MASK));
						fonts.modifyPaint(old_style, stl, profiles, true);
						old_style = stl;
						char ch = 0x2022;
						
						switch (ul) {
						case 0x01: case 0x04: case 0x07: case 0x0a: case 0x0d: ch = (char)0x2022; break;
						case 0x02: case 0x05: case 0x08: case 0x0b: case 0x0e: ch = (char)0x25E6; break;
						case 0x03: case 0x06: case 0x09: case 0x0c: case 0x0f: ch = (char)0x25AA; break;
						}
						
						calc.drawText(x - screen_parameters.redList, y, convertChineseText(ch));
					}
					
					for (i = 0; i < oi.count; i++) {
						if (oi.text[i] == 0x20) {
							if (end >= start) {
								x = drawPartItem(start, end, old_style, x, y, oi, page);
							}
							if (((old_style & AlStyles.STYLE_UNDER) != 0)
									&& (i + 1 < oi.count)
									&& ((oi.style[i + 1] & AlStyles.STYLE_UNDER) != 0)) {

                                int ysub = y;
								if ((old_style & AlStyles.STYLE_SUB) != 0)
                                    ysub += fontParam.base_line_down / 2;
								if ((old_style & AlStyles.STYLE_SUP) != 0)
                                    ysub -= fontParam.base_line_up / 2;
								calc.drawLine(x, ysub + 2, x + oi.width[i] + 1, ysub + 2,
										(int) preferences.multiplexer, fontParam.color);
							} else
							if (engOptions.drawLinkInternal && ((old_style & AlStyles.STYLE_LINK) != 0)
									&& (i + 1 < oi.count)
									&& ((oi.style[i + 1] & AlStyles.STYLE_LINK) != 0)) {

								int ysub = y;
								if ((old_style & AlStyles.STYLE_SUB) != 0)
									ysub += fontParam.base_line_down / 2;
								if ((old_style & AlStyles.STYLE_SUP) != 0)
									ysub -= fontParam.base_line_up / 2;
								calc.drawLine(x, ysub + 2, x + oi.width[i] + 1, ysub + 2,
										(int) preferences.multiplexer, fontParam.color);
							}
							if (((old_style & (AlStyles.SL_SELECT | AlStyles.SL_MARK)) != 0)
									&& (i + 1 < oi.count)
									&& ((oi.style[i + 1] & (AlStyles.SL_SELECT | AlStyles.SL_MARK)) != 0)) {

								int sm;

								if ((old_style & AlStyles.SL_MARK) != 0) {
									int num_color = (int) ((old_style & AlStyles.SL_MARKCOLOR_MASK) >> AlStyles.SL_MARKCOLOR_SHIFT);

									switch (num_color) {
										case 1: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK0]; break;
										case 2: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK1]; break;
										case 3: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK2]; break;
										case 4: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK3]; break;
										case 5: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK4]; break;
										case 6: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK5]; break;
										case 7: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_MARK6]; break;
										default: sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_BACK]; break;
									}

									sm |= 0x80000000;
									if (num_color == 7) {
										calc.drawRect(x, y + 1, x + oi.width[i], y + 2, sm);
									} else {
										calc.drawRect(x, y - oi.base_line_up, x + oi.width[i], y + oi.base_line_down, sm);
									}
								}

								if ((old_style & AlStyles.SL_SELECT) != 0) {
									sm = profiles.colors[InternalConst.TAL_PROFILE_COLOR_SELECT];

									sm |= 0x80000000;
									calc.drawRect(x, y - oi.base_line_up, x + oi.width[i], y + oi.base_line_down, sm);
								}
							}
							x += oi.width[i];

							start = i + 1;
						} else if ((oi.style[i] & AlStyles.LMASK_DRAW_STYLE) != (old_style & AlStyles.LMASK_DRAW_STYLE)) {
							if (end > start) {
								x = drawPartItem(start, end, old_style, x, y, oi, page);
							} else if (end == start && i != 0 && oi.text[start] != 0x20) {
								x = drawPartItem(start, end, old_style, x, y, oi, page);
							}
							fonts.modifyPaint(old_style, oi.style[i], profiles, true);
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

    private void resultEmptyMetadata(AlBookProperties bProp) {
        bProp.content = null;
        bProp.size = 0;

        bProp.title = null;
        bProp.authors = null;
        bProp.genres = null;
        bProp.series = null;
        bProp.coverImageData = null;
    }

    public AlBookProperties getBookProperties(){
		return bookMetaData;
	}

	//public final AlMutex _lockObj = new AlMutex();
	public final AlMutex _lockObjAddon = new AlMutex();

    public synchronized AlBookProperties scanMetaData(String fName, AlBookOptions bookOptions) {
		synchronized (_lockObjAddon) {
			return scanMetaDataReal(fName, bookOptions);
		}
	}

    private AlBookProperties scanMetaDataReal(String fName, AlBookOptions bookOptions) {

		resultEmptyMetadata(bookMetaData);
		if (fName == null)
			return null;

		String currName;
		String prevExt;
		int tmp;
		TAL_FILE_TYPE ft = TAL_FILE_TYPE.TXT;

		tmp = fName.indexOf(EngBookMyType.AL_FILENAMES_SEPARATOR);
		if (tmp == -1) {
			currName = fName;
			fName = "";
		} else {
			currName = fName.substring(0, tmp);
			fName = fName.substring(tmp + 1);
		}

		if (currName.toLowerCase().endsWith(".rar") || currName.toLowerCase().endsWith(".cbr"))
			ft = AlFilesBypassRAR.isBypassRARFile(currName);

		AlFiles activeFile = null;
		int lastInitState;
		if (ft == TAL_FILE_TYPE.RARUnk) {
			openState.decState();
			return null;
		} else if (ft == TAL_FILE_TYPE.TXT) {
			activeFile = bookOptions.decryptObj == null ?
					new AlFilesBypassNative() : new AlFilesBypassDecrypt(bookOptions.decryptObj);
			activeFile.setLoadTime1(true);

			lastInitState = activeFile.initState(currName, null, null);
		} else {
			activeFile = new AlFilesBypassRAR();
			lastInitState = activeFile.initState(currName, null, null);
		}

		while (true) {
			if (activeFile.getSize() < 1 || lastInitState != TAL_RESULT.OK) {
				activeFile = null;
				return null;
			}

			tmp = currName.lastIndexOf('.');
			if (tmp == -1) {
				prevExt = null;
			} else {
				prevExt = currName.substring(tmp);
			}

			tmp = fName.indexOf(EngBookMyType.AL_FILENAMES_SEPARATOR);
			if (tmp == -1) {
				currName = fName;
				fName = "";
			} else {
				currName = fName.substring(0, tmp);
				fName = fName.substring(tmp + 1);
			}

			AlFiles a = activeFile;

			ArrayList<AlFileZipEntry> fList = new ArrayList<>(0);
			fList.clear();

			if (a.getIdentStr().equalsIgnoreCase("bypassrar")) {
				activeFile = new AlFilesRAR();
				lastInitState = activeFile.initState(currName, a, fList);

				ft = AlFilesCBZ.isCBZFile(activeFile, prevExt, true);
				if (ft == TAL_FILE_TYPE.CBZ)
					return bookMetaData;
				continue;
			}

			ft = AlFilesZIP.isZIPFile(currName, a, fList, prevExt);
			if (ft == TAL_FILE_TYPE.ZIP) {
				activeFile = new AlFilesZIP();
				lastInitState = activeFile.initState(currName, a, fList);

				ft = AlFilesCBZ.isCBZFile(activeFile, prevExt, true);
				if (ft == TAL_FILE_TYPE.CBZ)
					return bookMetaData;
				continue;
			} else if (ft == TAL_FILE_TYPE.FB3) {
				activeFile = new AlFilesZIP();
				activeFile.initState(AlFiles.LEVEL1_ZIP_FIRSTNAME_FB3, a, fList);
				a = activeFile;
				activeFile = new AlFilesFB3();
				lastInitState = activeFile.initState(currName, a, fList);
				break;
			} else if (ft == TAL_FILE_TYPE.EPUB) {
				activeFile = new AlFilesZIP();
				activeFile.initState(AlFiles.LEVEL1_ZIP_FIRSTNAME_EPUB, a, fList);
				a = activeFile;
				activeFile = new AlFilesEPUB();
				lastInitState = activeFile.initState(currName, a, fList);
				break;
			} else if (ft == TAL_FILE_TYPE.DOCX) {
				return bookMetaData;
			} else if (ft == TAL_FILE_TYPE.ODT) {
				return bookMetaData;
			}

			ft = AlFilesPDB.isPDBFile(currName, a, fList, prevExt);
			if (ft == TAL_FILE_TYPE.MOBI) {
				activeFile = new AlFilesMOBI();
				activeFile.setOnlyScan();
				lastInitState = activeFile.initState(currName, a, fList);
				break;
			} else if (ft == TAL_FILE_TYPE.PDB) {
				return bookMetaData;
			} else if (ft == TAL_FILE_TYPE.PDBUnk) {
				activeFile = null;
				return null;
			}

			ft = AlFileDoc.isDOC(currName, a, fList, prevExt);
			if (ft == TAL_FILE_TYPE.DOC) {
				return bookMetaData;
			}

			break;
		}

		if (activeFile.getSize() < 1 || lastInitState != TAL_RESULT.OK) {
			activeFile = null;
			return null;
		}

		AlFormat formatMetaData = null;

		if (AlFormatFB3.isFB3(activeFile)) {
			formatMetaData = new AlFormatFB3();
		} else if (AlFormatMOBI.isMOBI(activeFile)) {
			formatMetaData = new AlScanMOBI();
		} else if (AlFormatEPUB.isEPUB(activeFile)) {
			formatMetaData = new AlFormatEPUB();
		} else if (AlFormatFB2.isFB2(activeFile)) {
			formatMetaData = new AlFormatFB2();
		} else {
			return bookMetaData;
		}

		bookOptions.formatOptions &= ~AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG;
		formatMetaData.initState(bookOptions, activeFile, preferences, styles);
		formatMetaData.prepareAll();

		if (formatMetaData.getSize() < 1)
			return null;

		bookMetaData.content = null;
		bookMetaData.size = 0;
		bookMetaData.title = formatMetaData.bookTitle;
		bookMetaData.authors = formatMetaData.bookAuthors;
		bookMetaData.genres = formatMetaData.bookGenres;
		bookMetaData.series = formatMetaData.bookSeries;

		bookMetaData.coverImageData = null;
		if (bookOptions.needCoverData && formatMetaData.coverName != null) {
			AlOneImage a = formatMetaData.getImageByName(AlFormat.LEVEL2_COVERTOTEXT_STR);
			if (a != null) {
				images.initWork(a, formatMetaData);
				if (a.data != null) {
					bookMetaData.coverImageData = a.data;
				}
			}
		}

		return bookMetaData;
    }

	/*public synchronized void replaceDelay2Real() {
		shtamp.value++;
		format = formatDelay;
		formatDelay = null;
	}*/


	TAL_NOTIFY_RESULT openBookInThread(String fName, AlBookOptions bookOptions) {
		resultEmptyMetadata(bookMetaData);
		String currName;
		String prevExt;
		int	tmp;
		TAL_FILE_TYPE ft = TAL_FILE_TYPE.TXT;
		bookmarks = null;

		tmp = fName.indexOf(EngBookMyType.AL_FILENAMES_SEPARATOR);
		if (tmp == -1) {
			currName = fName;
			fName = "";
		} else {
			currName = fName.substring(0, tmp);
			fName = fName.substring(tmp + 1);
		}		

		//Log.e("files open start", Long.toString(System.currentTimeMillis()));

		if (currName.toLowerCase().endsWith(".rar") || currName.toLowerCase().endsWith(".cbr"))
			ft = AlFilesBypassRAR.isBypassRARFile(currName);

		AlFiles activeFile = null;
		int lastInitState;
		if (ft == TAL_FILE_TYPE.RARUnk) {
			openState.decState();
			return TAL_NOTIFY_RESULT.ERROR;
		} else
		if (ft == TAL_FILE_TYPE.TXT) {
			activeFile = bookOptions.decryptObj == null ?
					new AlFilesBypassNative() : new AlFilesBypassDecrypt(bookOptions.decryptObj);
			activeFile.setLoadTime1(true);

			lastInitState = activeFile.initState(currName, null, null);
		} else {
			activeFile = new AlFilesBypassRAR();
			activeFile.setLoadTime1(true);

			lastInitState = activeFile.initState(currName, null, null);
		}

		while (true) {
			if (activeFile.getSize() < 1 || lastInitState != TAL_RESULT.OK) {
				activeFile = null;
				openState.decState();				
				return TAL_NOTIFY_RESULT.ERROR;
			}

			tmp = currName.lastIndexOf('.');
			if (tmp == -1) {
				prevExt = null;
			} else {
				prevExt = currName.substring(tmp);
				if (activeFile.getIdentStr().contentEquals("decrypt")) {
					String newExt = ((AlFilesBypassDecrypt)activeFile).getDecriptFileExt();
					if (newExt != null)
						prevExt = newExt;
				}
			}

			tmp = fName.indexOf(EngBookMyType.AL_FILENAMES_SEPARATOR);
			if (tmp == -1) {
				currName = fName;
				fName = "";
			} else {
				currName = fName.substring(0, tmp);
				fName = fName.substring(tmp + 1);
			}
		
			AlFiles a = activeFile; 

			ArrayList<AlFileZipEntry> fList = new ArrayList<>(0);
			fList.clear();

			if (a.getIdentStr().equalsIgnoreCase("bypassrar")) {
				activeFile = new AlFilesRAR();
				lastInitState = activeFile.initState(currName, a, fList);

				ft = AlFilesCBZ.isCBZFile(activeFile, prevExt, true);
				if (ft == TAL_FILE_TYPE.CBZ) {
					a = activeFile;
					activeFile = new AlFilesCBZ();
					lastInitState = activeFile.initState(currName, a, fList);
					break;
				}
				continue;
			}

			if (a.getParent() == null && prevExt.equalsIgnoreCase(".chm")) {
				ft = AlFilesCHM.isCHMFile(currName, a, fList, prevExt);
				if (ft == TAL_FILE_TYPE.CHM) {
					activeFile = new AlFilesCHM();
					lastInitState = activeFile.initState(currName, a, fList);
					break;
				}
			}

			ft = AlFilesZIP.isZIPFile(currName, a, fList, prevExt);		
			if (ft == TAL_FILE_TYPE.ZIP) {
				activeFile = new AlFilesZIP();
                lastInitState = activeFile.initState(currName, a, fList);

				ft = AlFilesCBZ.isCBZFile(activeFile, prevExt, false);
				if (ft == TAL_FILE_TYPE.CBZ) {
					a = activeFile;
					activeFile = new AlFilesCBZ();
					lastInitState = activeFile.initState(currName, a, fList);
					break;
				}

				continue;
			} else
			if (ft == TAL_FILE_TYPE.EPUB) {
				activeFile = new AlFilesZIP();
				activeFile.initState(AlFiles.LEVEL1_ZIP_FIRSTNAME_EPUB, a, fList);
				a = activeFile;
				activeFile = new AlFilesEPUB();
                lastInitState = activeFile.initState(currName, a, fList);
				break;
			} else
            if (ft == TAL_FILE_TYPE.FB3) {
                activeFile = new AlFilesZIP();
                activeFile.initState(AlFiles.LEVEL1_ZIP_FIRSTNAME_FB3, a, fList);
                a = activeFile;
                activeFile = new AlFilesFB3();
                lastInitState = activeFile.initState(currName, a, fList);
                break;
            } else
			if (ft == TAL_FILE_TYPE.DOCX) {
				activeFile = new AlFilesZIP();
				activeFile.initState(AlFiles.LEVEL1_ZIP_FIRSTNAME_DOCX, a, fList);
				a = activeFile;
				activeFile = new AlFilesDocx();
				lastInitState = activeFile.initState(currName, a, fList);
				break;
			} else
			if (ft == TAL_FILE_TYPE.ODT) {
				activeFile = new AlFilesZIP();
				activeFile.initState(AlFiles.LEVEL1_ZIP_FIRSTNAME_ODT, a, fList);
				a = activeFile;
				activeFile = new AlFilesODT();
				lastInitState = activeFile.initState(currName, a, fList);
				break;
			}else
			if (ft == TAL_FILE_TYPE.JEB) {
				activeFile = new JEBFilesZIP();
				activeFile.initState(AlFiles.LEVEL1_ZIP_FIRSTNAME_EPUB, a, fList);
				a = activeFile;
				activeFile = new JEBFilesEPUB();
				lastInitState = activeFile.initState(currName, a, fList);
				break;
			}


			ft = AlFilesPDB.isPDBFile(currName, a, fList, prevExt);
			if (ft == TAL_FILE_TYPE.MOBI) {
				activeFile = new AlFilesMOBI();
				lastInitState = activeFile.initState(currName, a, fList);
				break;
			} else
			if (ft == TAL_FILE_TYPE.PDB) {
				activeFile = new AlFilesPDB();
                lastInitState = activeFile.initState(currName, a, fList);
				break;
			} else
			if (ft == TAL_FILE_TYPE.PDBUnk) {
				activeFile = new AlFilesPDBUnk();
				lastInitState = activeFile.initState(currName, a, fList);
				break;
			}

			ft = AlFileDoc.isDOC(currName, a, fList, prevExt);
			if (ft == TAL_FILE_TYPE.DOC) {
				activeFile = new AlFileDoc();
                lastInitState = activeFile.initState(currName, a, fList);
				break;
			}

			break;
		}

        if (activeFile.getSize() < 1 || lastInitState != TAL_RESULT.OK) {
            activeFile = null;
            openState.decState();
            return TAL_NOTIFY_RESULT.ERROR;
        }

		{
			AlFormat fmt = null;

			if (AlFormatCHM.isCHM(activeFile)) {
				fmt = new AlFormatCHM();
			} else if (AlFormatFB3.isFB3(activeFile)) {
				fmt = new AlFormatFB3();
			} else if (AlFormatMOBI.isMOBI(activeFile)) {
				fmt = new AlFormatMOBI();
			} else if (AlFormatEPUB.isEPUB(activeFile)) {
				fmt = new AlFormatEPUB();
			} else if (AlFormatDOCX.isDOCX(activeFile) || AlFormatDOCX.isDOCX_XML(activeFile) > 0) {
				fmt = new AlFormatDOCX();
			} else if (AlFormatDOC.isDOC(activeFile)) {
				fmt = new AlFormatDOC();
			} else if (AlFormatODT.isODT(activeFile)) {
				fmt = new AlFormatODT();
			} else if (AlFormatRTF.isRTF(activeFile)) {
				fmt = new AlFormatRTF();
			} else if (AlFormatFB2.isFB2(activeFile)) {
				fmt = new AlFormatFB2();
			} else if (AlFormatHTML.isHTML(activeFile)) {
				fmt = new AlFormatHTML();
			} else if (AlFormatCOMICS.isCOMICS(activeFile) || AlFormatCOMICS.isACBF(activeFile)) {
				fmt = new AlFormatCOMICS();
			} else if (AlFormatNativeImages.isImage(activeFile, prevExt)) {
				fmt = new AlFormatNativeImages();
			} else
				fmt = new AlFormatTXT();

			if (format != null && (bookOptions.formatOptions & AlFiles.LEVEL1_BOOKOPTIONS_NEED_MULTIFILE_FULL) != 0) {
				formatDelay = fmt;
			} else {
				format = fmt;
			}

			activeFile.setLoadTime1(false);

			bookOptions.formatOptions &= ~AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG;
			//if (preferences.calcPagesModeRequest != TAL_SCREEN_PAGES_COUNT.SIZE)
			bookOptions.formatOptions |= AlFiles.LEVEL1_BOOKOPTIONS_NEED_UNPACK_FLAG;

			activeFile.setLoadTime2(true);

			//Log.e("files open end", Long.toString(System.currentTimeMillis()));
			fmt.initState(bookOptions, activeFile, preferences, styles);

			if (fmt.getSize() < 1) {
				openState.decState();
				return TAL_NOTIFY_RESULT.ERROR;
			}

			fmt.prepareAll();
			//Log.e("format open end", Long.toString(System.currentTimeMillis()));

			fmt.fullPath = activeFile.getFullRealName();

			activeFile.setLoadTime2(false);

			synchronized (this) {
				int savedPos = bookPosition;
				bookPosition = bookOptions.readPosition;
				if (fmt.multiFiles.modePart) {
					format.multiFiles.correctionPos = ((format.multiFiles.queryWaitingPosition >> 32L) & 0x7fffffff) - format.multiFiles.queryRealPosition;
					bookPosition -= format.multiFiles.correctionPos;
				} else if (fmt == formatDelay) {
					savedPos = savedPos - bookPosition;
					bookPosition += format.multiFiles.correctionPos + savedPos;

					format = formatDelay;
					formatDelay = null;
				}
			}
		}

		if (bookPosition < 0 || bookPosition >= format.getSize())
			bookPosition = 0;

		preferences.calcPagesModeUsed = preferences.calcPagesModeRequest;
		if (preferences.calcPagesModeUsed == TAL_SCREEN_PAGES_COUNT.AUTO &&
				format.getSize() < AL_FILESIZEMIN_FOR_AUTOCALC)
			preferences.calcPagesModeUsed = TAL_SCREEN_PAGES_COUNT.SCREEN;

		if (!format.isTextFormat)
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
		
		bookPosition = getCorrectScreenPagePosition(bookPosition);

		shtamp.value++;

		openState.incState();
		if(JEBFormatEPUB.isJEB(activeFile)){
			bookMetaData.content = null;
			bookMetaData.size = 0;
			bookMetaData.title = format.bookTitle;
			bookMetaData.authors = format.bookAuthors;
			bookMetaData.genres = format.bookGenres;
			bookMetaData.series = format.bookSeries;

			bookMetaData.coverImageData = null;
			if (bookOptions.needCoverData && format.coverName != null) {
				AlOneImage a = format.getImageByName(AlFormat.LEVEL2_COVERTOTEXT_STR);
				if (a != null) {
					images.initWork(a, format);
					if (a.data != null) {
						bookMetaData.coverImageData = a.data;
					}
				}
			}
		}
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
			clearPagePosition();
            return TAL_RESULT.OK;
        }

		if (preferences.calcPagesModeUsed == TAL_SCREEN_PAGES_COUNT.SCREEN && !format.isTextFormat) {
			calcCountPages();
			return TAL_RESULT.OK;
		}

		openState.decState();		
		
		if (preferences.calcPagesModeRequest == TAL_SCREEN_PAGES_COUNT.SCREEN &&
			preferences.calcPagesModeUsed == TAL_SCREEN_PAGES_COUNT.AUTO)
			preferences.calcPagesModeUsed = TAL_SCREEN_PAGES_COUNT.SCREEN;

		AlThreadData.startThread(threadData, TAL_THREAD_TASK.NEWCALCPAGES, engOptions.runInOneThread);
		return TAL_RESULT.OK;
	}

	private ArrayList<AlOneBookmark> bookmarks = null;

	public synchronized int updateBookmarks(ArrayList<AlOneBookmark> bmks) {
		if (openState.getState() != AlBookState.OPEN)
			return TAL_RESULT.ERROR;
		bookmarks = bmks;
		shtamp.value++;
		return returnOkWithRedraw();
	}

    /**
     * Отладочный метод. Практического смысла в приложении для конечно пользователя не имеет
     * @param path - путь к каталогу, в котором будут созданы отладочные файлы
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public synchronized int createDebugFile(String path) {
        if (preferences.isASRoll)
            return TAL_RESULT.ERROR;

		if (openState.getState() != AlBookState.OPEN) 
			return TAL_RESULT.ERROR;
		
		openState.incState();		

		threadData.param_char1 = path;
		AlThreadData.startThread(threadData, TAL_THREAD_TASK.CREATEDEBUG, engOptions.runInOneThread);
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
        if (preferences.isASRoll)
            return TAL_RESULT.ERROR;

		while (threadData.getObjOpen()) ;

		synchronized (this) {
			switch (openState.getState()) {
				case AlBookState.OPEN:
					closeBook();
				case AlBookState.NOLOAD:
					openState.incState();
					break;
				default:
					return TAL_RESULT.ERROR;
			}


			threadData.param_void1 = bookOptions;
			threadData.param_char1 = fName;
			AlThreadData.startThread(threadData, TAL_THREAD_TASK.OPENBOOK, engOptions.runInOneThread);
			return TAL_RESULT.OK;
		}
	}

	protected void closeBookReal() {
		while (openState.getState() < AlBookState.LOAD)
			openState.incState();
		while (openState.getState() > AlBookState.LOAD)
			openState.decState();

		/*openState.decState();
		openState.decState();*/
		format = null;
		images.resetStoredImages();
		openState.decState();
	}

    /**
     * Закрытие книги
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public int closeBook() {
		while (threadData.getObjOpen()) ;

		synchronized (this) {
			while (threadData.getWork0()) ;

			if (preferences.isASRoll)
				return TAL_RESULT.ERROR;

			if (openState.getState() != AlBookState.OPEN)
				return TAL_RESULT.ERROR;

			closeBookReal();

			return returnOkWithRedraw();
		}
	}

    /**
     * получение информации о книге
     * @return null или AlBookProperties
     */
    public synchronized AlBookProperties getBookProperties(boolean needCalcPage4Content) {


        if (!isBookOpened())
            return null;

        resultEmptyMetadata(bookProperties);

        bookProperties.title = format.bookTitle;
        bookProperties.authors = format.bookAuthors;
		bookProperties.isTextFormat = format.isTextFormat;
		bookProperties.fullPath = format.fullPath;
		bookProperties.content = format.ttl;
        if (needCalcPage4Content && format.ttl.size() > 0) {
            for (AlOneContent x : format.ttl) {
                switch (preferences.calcPagesModeUsed) {
                    case SCREEN:
                        x.pageNum  = getCorrectScreenPagePosition(x.positionS);
                        break;
                    case AUTO:
                    case SIZE:
                        x.pageNum = /*(int)*/(/*0.5f + */(x.positionS / preferences.pageSize)) + 1;
                        break;
                }
            }
        }

        bookProperties.size = format.getSize();
        bookProperties.genres = format.bookGenres;
        bookProperties.series = format.bookSeries;

		bookProperties.coverImageData = null;
		if (format.coverName != null) {
			AlOneImage a = format.getImageByName(AlFormat.LEVEL2_COVERTOTEXT_STR);
			if (a != null) {
                if (a.needScan) {
					images.initWork(a, format);
					images.scanImage(a);
				}
				if (a.data != null) {
					bookProperties.coverImageData = a.data;
				}
			}
		}

        return bookProperties;
    }

    /**
     * получение результатов поиска заданной строки. Результатом является список, содержащий позиции
     найденных слов (фраз) в тексте.
     * @return null если ничего не было найдено или сам список
     */
	public synchronized ArrayList<AlOneSearchResult> getFindTextResult() {
        if (preferences.isASRoll)
            return null;

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
	public synchronized int findText(String find) {
        if (preferences.isASRoll)
            return TAL_RESULT.ERROR;

		if (openState.getState() != AlBookState.OPEN)
			return TAL_RESULT.ERROR;
		
		openState.incState();

		threadData.param_char1 = find;
		AlThreadData.startThread(threadData, TAL_THREAD_TASK.FIND, engOptions.runInOneThread);
		return TAL_RESULT.OK;
	}

    /**
     * установка картинок, которые использует библиотека в процессе генерации страниц
     * @param errorImage - картинка, которая будет оборажена в случае невозможности чтения реальной картинки из книги. задавать обязательно
     * @param tableImage - картинка, которая оборазиться  для таблиц, заданных в формате фб2. При клике на картинку образиться тело таблицы.
    Задавать обязательно
     * @param waitImage - картинка-заставка, выводится при осуществлении долговременных операций, например, загрузке книги или поиске строки.
    Задавать обязательно
     * @param selectStart - картинка маркер начала выделения.
    Задавать не обязательно
     * @param selectEnd - картинка маркер окончания выделения.
    Задавать не обязательно
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public synchronized int setServiceBitmap(AlBitmap errorImage, AlBitmap tableImage, AlBitmap waitImage, AlBitmap selectStart, AlBitmap selectEnd) {
        if (preferences.isASRoll)
            return TAL_RESULT.ERROR;

		errorBitmap = errorImage;
		tableBitmap = tableImage;
		waitBitmap = waitImage;
        selectStartBitmap = selectStart;
        selectEndBitmap = selectEnd;
		return TAL_RESULT.OK;
	}

    /**
     * Установка новых размеров рабочей области для вывода страницы
     * @param width - ширина страницы текста
     * @param height - высота страницы текста
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public synchronized int setNewScreenSize(int width, int height) {

		if (screenWidth == width && screenHeight == height)
			return TAL_RESULT.OK;

		if (width <= 0 || height <= 0)
			return TAL_RESULT.ERROR;

        if (preferences.isASRoll) {
            startStopAS();
        }

		if (engOptions.externalBitmap != null) {
			screenWidth = width;
			screenHeight = height;
			return TAL_RESULT.OK;
		}

		AlBitmap abmp = /*engOptions.externalBitmap != null ? engOptions.externalBitmap : */bmp[0];

		switch (openState.getState()) {
		case AlBookState.NOLOAD:
			screenWidth = width;
			screenHeight = height;			
			EngBitmap.reCreateBookBitmap(abmp, screenWidth, screenHeight, shtamp);
			/*if (engOptions.externalBitmap != null) {
				EngBitmap.reCreateBookBitmap(bmp[1], screenWidth, screenHeight, null);
				EngBitmap.reCreateBookBitmap(bmp[2], screenWidth, screenHeight, null);
			}*/
			shtamp.value++;
			break;
		case AlBookState.OPEN:
			screenWidth = width;
			screenHeight = height;			
			EngBitmap.reCreateBookBitmap(abmp, screenWidth, screenHeight, shtamp);
			/*if (engOptions.externalBitmap != null) {
				EngBitmap.reCreateBookBitmap(bmp[1], screenWidth, screenHeight, null);
				EngBitmap.reCreateBookBitmap(bmp[2], screenWidth, screenHeight, null);
			}*/
			shtamp.value++;			
			needNewCalcPageCount();
			break;
		} 

		return abmp.bmp != null ? TAL_RESULT.OK : TAL_RESULT.ERROR;
	}

	private static void addW2I(AlOneItem oi, AlOneWord tword, int cnt) {
		boolean use4text;
		for (int wcurr = 0; wcurr < cnt; wcurr++) {
			use4text = true;
			if ((tword.style[wcurr] & AlStyles.SL_IMAGE) != 0) {
				if (oi.needHeihtImage0 && oi.interline < 0) {
					oi.height -= oi.interline; 
					oi.needHeihtImage0 = false;
				}
				use4text = false;
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
			if (use4text && oi.base_line_up4text < tword.base_line_up[wcurr])
				oi.base_line_up4text = tword.base_line_up[wcurr];

			oi.count++;
			if (oi.count >= oi.realLength) 
				AlOneItem.incItemLength(oi);
		}

	}

	private static void addC2I0(AlOneItem oi, char ch, int need_width, int specialPos) {
		oi.text[oi.count] = ch;
		oi.style[oi.count] = oi.style[oi.count - 1];
		if ((oi.style[oi.count] & AlStyles.SL_IMAGE) != 0)
			oi.style[oi.count] &= AlStyles.LMASK_SPECIALHYHP;
		oi.pos[oi.count] = specialPos;
		oi.width[oi.count] = need_width;
				
		oi.count++;
		if (oi.count >= oi.realLength) 
			AlOneItem.incItemLength(oi);
	}

	private void initOneItem(AlOneItem oi, AlOneItem poi, long style,
							 int pos, int width, boolean addEmptyLine, TAL_CALC_MODE calcMode, AlOnePage page) {

		long v;

        if (profiles.specialModeRoll)
            addEmptyLine = true;

		oi.num = (int) ((style & AlStyles.SL3_NUMBER_MASK) >> AlStyles.SL3_NUMBER_SHIFT);
        oi.table_start = oi.table_row = -1;
		oi.isTableRow = calcMode == TAL_CALC_MODE.ROWS;
		if (oi.isTableRow) {
			oi.prop = AlParProperty.DEFALULT_TABLE;
		} else
		if (calcMode == TAL_CALC_MODE.NOTES) {
			oi.prop = AlParProperty.DEFALULT_NOTE;
		} else {
			oi.prop = format.par0.get(oi.num).prop;
			if (format.par0.get(oi.num).table_start != -1)
				oi.prop = AlParProperty.DEFALULT_TABLE;
		}
        oi.blockHeight = 0;
		oi.allWidth = width;
		oi.textWidth = 0;		
		oi.height = 0;
		oi.needHeihtImage0 = addEmptyLine;
		oi.cntImage = 0;
		oi.isEnd = oi.isStart = false;		
		oi.isRed = oi.isLeft = oi.isRight = 0;
		oi.start_pos = pos;
		oi.justify = oi.prop & AlParProperty.SL2_JUST_MASK;
		oi.isArabic = false;
        oi.yDrawPosition = -1;


        oi.base_line_down = 2;//screen_parameters.cFontLineDown[(int) ((style & AlStyles.SL_FONT_MASK) >> AlStyles.SL_FONT_SHIFT)];
		oi.base_line_up = 2;//screen_parameters.cFontLineUp[(int) ((style & AlStyles.SL_FONT_MASK) >> AlStyles.SL_FONT_SHIFT)];
		/*if (oi.base_line_down < 2)
			oi.base_line_down = 2;
		if (oi.base_line_up < 2)
			oi.base_line_up = 2;*/
		oi.base_line_up4text = oi.base_line_up;

		oi.isNote = false;
		oi.isPrepare = false;
		oi.spaceAfterHyph0 = 0;		
		
		switch ((int) (oi.prop & AlParProperty.SL2_INTER_MASK >> 32L)) {
		case (int)(AlParProperty.SL2_INTER_100_ >> 32L):
			oi.interline = 0;
			break;
		case (int)(AlParProperty.SL2_INTER_TEXT1 >> 32L):
			oi.interline = screen_parameters.cFontInterline[InternalConst.TAL_PROFILE_FONTTYPE_TEXT] *
				screen_parameters.cFontHeight[(int) ((style & AlStyles.SL_FONT_MASK) >> AlStyles.SL_FONT_SHIFT)] / 100;
			break;	
		case (int)(AlParProperty.SL2_INTER_NOTES >> 32L):
			oi.interline = screen_parameters.cFontInterline[InternalConst.TAL_PROFILE_FONTTYPE_NOTE] *
				screen_parameters.cFontHeight[InternalConst.TAL_PROFILE_FONTTYPE_NOTE] / 100;
			break;	
		case (int)(AlParProperty.SL2_INTER_FONT >> 32L):
			oi.interline = screen_parameters.cFontInterline[(int) ((style & AlStyles.SL_FONT_MASK) >> AlStyles.SL_FONT_SHIFT)] *
				screen_parameters.cFontHeight[(int) ((style & AlStyles.SL_FONT_MASK) >> AlStyles.SL_FONT_SHIFT)] / 100;
			break;		
		}

        if (oi.isTableRow && oi.interline > 0) {
            oi.interline = 0;
        }

        if (calcMode == TAL_CALC_MODE.NOTES) {
			oi.justify = 0;
			oi.isNote = true;
			oi.prop = AlParProperty.SL2_INTER_NOTES;
			if (oi.interline > 0)
				oi.interline = 0;
			return;
		}
		
		if ((style & AlStyles.SL_PAR) != 0) {	
			oi.isStart = true;

			if (preferences.chinezeFormatting && oi.justify == AlParProperty.SL2_JUST_NONE) {
				v = 8 * 3;
			} else {
				v = (oi.prop & (AlParProperty.SL2_INDENT_MASK)) >> AlParProperty.SL2_INDENT_SHIFT;
				if (preferences.chinezeFormatting) {
					v *= 2;
				}
			}

			if (v > 0 && ((oi.prop & AlParProperty.SL2_UL_BASE) == 0)) {
				if (!profiles.classicFirstLetter || (style & AlStyles.SL_MARKFIRTSTLETTER0) == 0) {
					//oi.isRed = (int)(((double)width) * v / 300.0);
					oi.isRed = (int)(fontParam.space_width * v / 3);
					oi.allWidth -= oi.isRed;
				}
			}
			
			if (addEmptyLine || preferences.isASRoll) {

				v = (oi.prop & (AlParProperty.SL2_MARGT_MASK/* - AlParProperty::SL2_MARGT_MASK_EM*/)) >> AlParProperty.SL2_MARGT_SHIFT;
				if (v != 0) {
					//v = (int32_t)(((double)page->pageHeight) * v / 100) * profiles.multiplexer;
					v = (int)(((double)width) * v / 300.0);
					if (v > (page.pageHeight >> 1))
						v = page.pageHeight >> 1;
					oi.height += v;
				}

				/*if ((poi == null && (style & AlStyles.SL_STANZA) != 0) || (oi.isTableRow) ||
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
				}*/

				if ((oi.prop & (/*AlStyles::SL_PREV_EMPTY_1 + */AlParProperty.SL2_EMPTY_BEFORE)) != 0)
					oi.height += fontParam.height * screen_parameters.redLineV / 100.0f;
				
				if (!preferences.isASRoll) {
					if ((oi.prop & AlParProperty.SL2_BREAK_BEFORE) != 0)
						oi.height += InternalConst.BREAK_HEIGHT;
					if (poi != null && poi.count == 1 && ((poi.style[0] & AlStyles.SL_IMAGE) != 0) && 
							((poi.style[0] & AlStyles.SL_COVER) != 0)) {
						oi.height += InternalConst.BREAK_HEIGHT;
					}
				}

			}
		} else {
			if ((oi.prop & AlParProperty.SL2_JUSTIFY_POEM) != 0) {

					if (oi.justify == AlParProperty.SL2_JUST_NONE || oi.justify == AlParProperty.SL2_JUST_LEFT) {
						oi.justify = AlParProperty.SL2_JUST_RIGHT;
						/*oi.isRed = screen_parameters.redLine;
						oi.allWidth -= oi.isRed;*/
					} else 
					if (oi.justify == AlParProperty.SL2_JUST_RIGHT) {
						oi.justify = AlParProperty.SL2_JUST_LEFT;
						/*oi.isRed = screen_parameters.redLine;
						oi.allWidth -= oi.isRed;*/
					}

			}
			
			if (profiles.classicFirstLetter) {
				if (poi != null && poi.count > 0 && (poi.style[0] & AlStyles.SL_MARKFIRTSTLETTER0) != 0) {
					oi.isRed = poi.isRed + poi.width[0];
					oi.isLeft = poi.isLeft;
					oi.allWidth -= oi.isLeft + oi.isRed;					
					oi.height -= fontParam.height + (float)screen_parameters.cFontInterline[InternalConst.TAL_PROFILE_FONTTYPE_TEXT] *
							screen_parameters.cFontHeight[InternalConst.TAL_PROFILE_FONTTYPE_TEXT] / 100.0;
					
					for (int j = 1; j < poi.count; j++) {
						if ((poi.style[j] & AlStyles.SL_MARKFIRTSTLETTER0) == 0)
							break;
						oi.isRed += poi.width[j];
						oi.allWidth -= poi.width[j];
					}					
				}
			}
		}

		v = (oi.prop & (AlParProperty.SL2_MARGL_MASK/* - AlParProperty::SL2_MARGL_MASK_EM*/)) >> AlParProperty.SL2_MARGL_SHIFT;
		if (v != 0) {
			//oi.isLeft = (int)(((double)width) * v / 300.0);
			oi.isLeft = (int)(fontParam.space_width * v / 3);
			if (oi.isLeft > oi.allWidth * 0.8)
				oi.isLeft = (int)(oi.allWidth * 0.8);

			oi.allWidth -= oi.isLeft;
		}

		v = (oi.prop & (AlParProperty.SL2_MARGR_MASK/* - AlParProperty::SL2_MARGR_MASK_EM*/)) >> AlParProperty.SL2_MARGR_SHIFT;
		if (v != 0) {
			//oi.isRight = (int)(((double)width) * v / 300.0);
			oi.isRight = (int)(fontParam.space_width * v / 3);
			if (oi.isRight > oi.allWidth * 0.8)
				oi.isRight = (int)(oi.allWidth * 0.8);

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

		if (((oi.justify & AlParProperty.SL2_JUST_RIGHT) == 0) && ((oi.prop & AlParProperty.SL2_UL_BASE) != 0)) {
			int ul = (int) ((oi.prop & AlParProperty.SL2_UL_MASK) >> AlParProperty.SL2_UL_SHIFT);
			if (ul > 0) {
				ul *= screen_parameters.redList; 
				while (ul > (oi.allWidth / 2)) 
					ul -= screen_parameters.redList;
				
				oi.isLeft += ul;
				oi.allWidth -= ul;
			}			
		}

		if (page.block.use) {
			if (oi.isNote ) {

			} else {
				int diff = page.block.left - oi.isLeft;
				oi.allWidth -= diff;
				oi.isLeft += diff;
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

        oi.style[num] &= AlStyles.SL_COLOR_IMASK - AlStyles.STYLE_SUB - AlStyles.STYLE_SUP;
        oi.style[num] |= AlStyles.SL_COLOR_LINK | AlStyles.STYLE_LINK;
        oi.pos[num] = pos;
        oi.text[num] = 0x2026;
	}

    private boolean addCellToPage(int width, int height, AlOnePage page,
                                  int start_point, int end_point) {

        page.start_position = start_point;
        page.countItems = 0;
        page.items.get(0).count = 0;
        page.selectStart = page.selectEnd = -1;
        page.pageHeight = height;
        page.block.use = false;
        page.topMarg = 0;
		page.textHeight = 0;

        page.textHeightWONotes = page.textHeight;
        page.notePresent = false;
        page.notesShift = (int) (screen_parameters.cFontHeight[InternalConst.TAL_PROFILE_FONTTYPE_TEXT] * 0.6f/* >> 1*/);

        note_word.need_flags = 0;
        note_word.count = 0;
        note_word.hyph[0] = InternalConst.TAL_HYPH_INPLACE_DISABLE;

        int start = start_point, i, j;
        char ch;
        boolean noFirstAdd = false;
        while (start < end_point) {
            j = format.getNoteBuffer(start, format_note_and_style, shtamp.value, profiles);
            i = start - (start & AlFiles.LEVEL1_FILE_BUF_MASK);

            for (; i < j; i++, start++) {

                if (start >= end_point)
                    break;
                if ((ch = format_note_and_style.txt[i]) == 0x00)
                    continue;
                if ((format_note_and_style.stl[i] & AlStyles.STYLE_HIDDEN) != 0)
                    continue;

                //
                if ((format_note_and_style.stl[i] & AlStyles.SL_PAR) != 0) {
                    if (note_word.count > 0) {
                        //note_word.style[note_word.count - 1] |= AlStyles.SL2_ENDPARAGRAPH;
                        if (addWord(note_word, page, width, TAL_CALC_MODE.ROWS))
                            return false;
                        noFirstAdd = true;
                    }
                    if (noFirstAdd) {
                        if (addWord(note_word, page, width, TAL_CALC_MODE.ROWS))
                            return false;
                        noFirstAdd = false;
                    }
                }

                if (ch == 0x20) {
                    if (note_word.count != 0) {
                        if (addWord(note_word, page, width, TAL_CALC_MODE.ROWS))
                        return false;
                        noFirstAdd = true;
                    }
                } else {
                    if (ch == 0xad) {
                        note_word.hyph[note_word.count] = InternalConst.TAL_HYPH_INPLACE_DISABLE;
                    } else
                    if (false && 0x301 == ch && note_word.count > 0 && preferences.u301mode != 0) {
                        if (2 == preferences.u301mode)
                            continue;
                        note_word.style[note_word.count - 1] ^= 0x03;
                    } else {
                        note_word.text[note_word.count] = ch;
                        note_word.style[note_word.count] = format_note_and_style.stl[i];
                        note_word.pos[note_word.count] = start;

                        note_word.count++;
                        note_word.hyph[note_word.count] = InternalConst.TAL_HYPH_INPLACE_DISABLE;
                        if (note_word.count >= EngBookMyType.AL_WORD_LEN) {
                            note_word.need_flags |= InternalConst.AL_ONEWORD_FLAG_NOINSERTALL;
                            if (addWord(note_word, page, width, TAL_CALC_MODE.ROWS))
                            	return false;
                            note_word.need_flags &= ~InternalConst.AL_ONEWORD_FLAG_NOINSERTALL;
                            noFirstAdd = true;
                        }
                    }
                }

            }
        }
        boolean res = true;

        if (note_word.count != 0)
            res = !addWord(note_word, page, width, TAL_CALC_MODE.ROWS);
        res = res && (!addWord(note_word, page, width, TAL_CALC_MODE.ROWS));

		page.end_position = end_point;

        return res;
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
					/*if (note_word.count + 3 < EngBookMyType.AL_WORD_LEN &&
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
					} else*/
					if (note_word.count != 0) {
						if (addWord(note_word, page, width, TAL_CALC_MODE.NOTES))
							return false;
					}
				}
				
				if (ch == 0x20 || ch == 0x3000) {// || AlUnicode::isChineze(ch)) {
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

		int savedTextHeight = page.textHeight;

		if (!page.block.use)
			if (!profiles.specialModeRoll && oi.isStart && !oi.isNote && oi.cntImage > 0 && oi.count > oi.cntImage) {
				if (!page.block.use && oi.base_line_up > oi.base_line_up4text * 2) {

					boolean only_image = true;
					int block_width = 0;
					for (int i = 0; i < oi.count; i++) {
						switch (oi.text[i]) {
							case 0x20:
							case 0xa0:
								if (only_image) {
									block_width += oi.width[i];
								}
								break;
							case 0x03:
								if (only_image) {
									block_width += oi.width[i];
								} else {
									block_width = 0;
								}
								break;
							default:
								only_image = false;
								break;
						}
					}

					if (block_width > 0 && oi.allWidth - block_width > (fontParam.space_width_standart << 4)) {
						page.block.use = true;

						page.block.left = oi.isLeft + block_width + oi.isRed;
						if (fontParam.space_width_standart >= oi.isRed)
							page.block.left += fontParam.space_width_standart << 1;
						page.block.height = 0;

						oi.blockHeight = oi.base_line_up - oi.base_line_up4text;
						oi.base_line_up = oi.base_line_up4text;
					}
				}
			}

		int old_h = page.textHeight;

        page.textHeight += oi.height + oi.base_line_down + oi.base_line_up;
		if (calcMode == TAL_CALC_MODE.NOTES) {
			if (page.notePresent && oi.interline < 0) {
				page.textHeight += oi.interline;				
			}
			
			if (!page.notePresent) {
				page.textHeight += page.notesShift;
			}			
			
			page.notePresent = true;
		} else {
			page.textHeight += oi.interline;
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
					(page.items.get(page.countItems - 1).style[0] & AlStyles.SL_MARKFIRTSTLETTER0) != 0)) {
				old_h = InternalConst.MIN_ITEM_HEIGHT - page.textHeight + old_h;
				oi.base_line_down += old_h;
				page.textHeight += old_h;
			}
		}

		if (!oi.isNote)
			page.textHeightWONotes += page.textHeight - savedTextHeight;

		if (page.block.use ) {
			if (page.block.height == 0)
				page.block.height = page.textHeightWONotes + oi.blockHeight + oi.base_line_down;


			if ((oi.isEnd && !oi.isNote) || page.block.height <= page.textHeightWONotes) {
				int diff = page.block.height - page.textHeightWONotes;

				if (diff > 0) {
					oi.base_line_down += diff;
					page.textHeightWONotes += diff;
					page.textHeight += diff;
				}

				page.block.use = false;
			}
		}

		int test_item = page.countItems; 

		page.countItems++;
		if (page.countItems >= page.realLength)
			AlOnePage.addItem(page);
		page.items.get(page.countItems).count = 0;

		if (calcMode == TAL_CALC_MODE.NORMAL && preferences.notesOnPage && format.haveNotesOnPage()) {
			int k;					
			for (k = 0; k < page.items.get(test_item).count; k++) {
				if ((page.items.get(test_item).style[k] & AlStyles.SL_MARKNOTE) != 0) {
					AlOneLink al = null;
					String link = format.getLinkNameByPos(page.items.get(test_item).pos[k], InternalConst.TAL_LINK_TYPE.LINK);
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

				/*if (tword.count == 1 && tword.text[0] == AlStyles.CHAR_ROWS_E)
					tword.style[0] &= 0xffffffffffffffffL - AlStyles.SL_UL_BASE;*/
				initOneItem(oi, poi, tword.style[0], tword.pos[0], width, page.countItems != 0, calcMode, page);
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
						if (tword.text[0] >= 0x3000 && ((tword.need_flags & InternalConst.AL_ONEWORD_FLAG_NOINSERTALL) != 0)) {
							int wwlen = word_len, wchina = tword.count;
							while ((--wchina) > 8 && wwlen > oi.allWidth + (fontParam.space_width_standart << 4)) {
								tword.hyph[wchina] = '8';
								wwlen -= tword.width[wchina];
							}
							hyphen.getHyph(tword.text, tword.hyph, wchina + 1, hyphFlag);
						} else {
							hyphen.getHyph(tword.text, tword.hyph, tword.count, hyphFlag);
						}
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
					
					if (wlen <= oi.allWidth && tword.hyph[tword.complete] == 'D') {
						//if () {
							oi.textWidth += wlen;
							addW2I(oi, tword, tword.complete);
							
							oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
							oi.textWidth += oi.spaceAfterHyph0;
							return false;						
						//}
					}			
					if (tword.complete == 1)
						break;
				} while (true);
				
				if ((tword.style[0] & AlStyles.SL_NOHYPH) == 0) {
					tword.complete = tword.count;
					wlen = word_len;			
					do  {
						tword.complete--;
						wlen -= tword.width[tword.complete];

						if (wlen > oi.allWidth) {

						} else
						switch (tword.hyph[tword.complete]) {
						case 'B':
							//if (wlen <= oi.allWidth) {
								oi.textWidth += wlen;
								addW2I(oi, tword, tword.complete);
								
								oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
								oi.textWidth += oi.spaceAfterHyph0;
								return false;						
							//}
							//break;
						case '-':
							if (tword.width[tword.complete] != 0) {
								if (wlen + fontParam.hyph_width_current <= oi.allWidth) {
									oi.textWidth += wlen + fontParam.hyph_width_current;
									
									addW2I(oi, tword, tword.complete);
									addC2I0(oi, '-', fontParam.hyph_width_current, SPECIAL_HYPH_POS);
									
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
						
						if (wlen <= oi.allWidth && tword.hyph[tword.complete] == 'B') {
							//if (wlen <= oi.allWidth) {
								oi.textWidth += wlen;
								addW2I(oi, tword, tword.complete);
								
								oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
								oi.textWidth += oi.spaceAfterHyph0;
								return false;						
							//}
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
								addC2I0(oi, '-', fontParam.hyph_width_current, SPECIAL_HYPH_POS);
								
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
							addC2I0(oi, '-', fontParam.hyph_width_current, SPECIAL_HYPH_POS);
							
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
					addC2I0(oi, '-', fontParam.hyph_width_current, SPECIAL_HYPH_POS);
					
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
				
				addC2I0(oi, ' ', need_space_len, -1);
				addW2I(oi, tword, tword.count);
				return false;
			} 

			if (oi.textWidth + need_space_len >= oi.allWidth) {
				word_len++;
			} else
			if (tword.count > 3) {
				int china_end_char = 0xffff;

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
				
				/*boolean hyph_last_word = true;
				if ((tword.style[tword.count - 1] & AlStyles.SL2_ENDPARAGRAPH) != 0) {
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
				}*/
				//
				
				if ((tword.style[0] & AlStyles.SL_NOHYPH) == 0/* && hyph_last_word*/) {

					

					tword.complete = hyph_end_position;//tword.count;
					wlen = word_len2;			
					do {
						tword.complete--;
						wlen -= tword.width[tword.complete];
						
						if (tword.complete != tword.count - 1 && tword.hyph[tword.complete] == 'D') {
							if (oi.textWidth + wlen + need_space_len <= oi.allWidth) {
								oi.textWidth += need_space_len + wlen;
								
								addC2I0(oi, ' ', need_space_len, -1);
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
									
									addC2I0(oi, ' ', need_space_len, -1);
									addW2I(oi, tword, tword.complete);
									addC2I0(oi, '-', fontParam.hyph_width_current, SPECIAL_HYPH_POS);
									
									oi.spaceAfterHyph0 = oi.allWidth - oi.textWidth;
									oi.textWidth += oi.spaceAfterHyph0;
									return false;
								}
							}
							break;							
						case 'B':
							if (tword.complete != tword.count - 1 && oi.textWidth + wlen + need_space_len <= oi.allWidth) {
								oi.textWidth += need_space_len + wlen;
								
								addC2I0(oi, ' ', need_space_len, -1);
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
								
								addC2I0(oi, ' ', need_space_len, -1);
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
								
								addC2I0(oi, ' ', need_space_len, -1);
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

            int rowspanDiff = 0;
            if (oi.count == 1 && oi.text[0] == AlStyles.CHAR_ROWS_E)
                rowspanDiff = verifyRowSpan(page, oi, false);

            if ((calcMode != TAL_CALC_MODE.NOTES || notesItemsOnPage < preferences.maxNotesItemsOnPageUsed) && (
				
					(page.textHeight + oi.height + oi.base_line_down + oi.base_line_up + (oi.interline > 0 ? oi.interline : 0) +
					((calcMode == TAL_CALC_MODE.NOTES && !page.notePresent) ? page.notesShift : 0) -
					((preferences.isASRoll ? 0 : screen_parameters.reservHeight0)) <= page.pageHeight
                            + rowspanDiff
                    ) ||

					(page.countItems == 0))
				) {

                if (rowspanDiff > 0)
                    verifyRowSpan(page, oi, true);

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

        int devDiff = screen_parameters.cFontLineDown[(int) ((tword.style[pos_in_word] & AlStyles.SL_FONT_MASK) >> AlStyles.SL_FONT_SHIFT)];
        if (devDiff < 2)
            devDiff = 2;
		int maxHeight = screen_parameters.free_picture_height -
                devDiff;
		int maxWight = screen_parameters.free_picture_width;
        if (maxWight > width)
            maxWight = width;

        if (calcMode == TAL_CALC_MODE.NOTES) {
			maxHeight >>= 4;
		}
			
		tword.style[pos_in_word] &= AlStyles.SL_COLOR_IMASK/* & AlStyles.SL_IMAGE_IMASK*/;
		AlOneImage ai = null;
		String link = format.getLinkNameByPos(pos, InternalConst.TAL_LINK_TYPE.IMAGE);
		if (link != null) {
            if (AlFormat.LEVEL2_TABLETOTEXT_STR.contentEquals(link)) {
                if (tableBitmap != null) {
                    imageParam.height = tableBitmap.height;
                    imageParam.width = tableBitmap.width;
                    return;
                }
            } else {
                ai = format.getImageByName(link);
            }
        }

		if (ai != null && (ai.iType != AlOneImage.NOT_EXTERNAL_IMAGE)) {
			if (ai.needScan) {
				images.initWork(ai, format);
				if (format.isTextFormat) {
					images.gc(format.getAllImages());
				} else {
					images.gc(format.getAllImages(), 2);
				}
				images.scanImage(ai);
			}  
			if (ai.width != -1) {
				ai.tm = System.currentTimeMillis();
				imageParam.real_height = ai.height;
				imageParam.real_width = ai.width;
				tword.style[pos_in_word] |= AlStyles.SL_IMAGE_OK;
			}		
		}
		
		if ((tword.style[pos_in_word] & AlStyles.SL_IMAGE_OK) != 0) {
			int scale = 0;
			imageParam.height = imageParam.real_height;
			imageParam.width = imageParam.real_width;

			//if (format.isTextFormat)
				while ((imageParam.height > maxHeight || imageParam.width > maxWight) && scale < 31) {
					imageParam.height >>= 1;
					imageParam.width >>= 1;
					scale++;
				}

			if ((tword.style[pos_in_word] & AlStyles.SL_COVER) != 0) {
				while (imageParam.height < maxHeight && imageParam.width < maxWight) {
					imageParam.height <<= 1;
					imageParam.width <<= 1;
					if (scale > 0)
						scale--;
				}
			} else if (preferences.multiplexer > 1) {
				int k = (int) (preferences.multiplexer - 1);
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
				if (page.block.use && calcMode == TAL_CALC_MODE.NORMAL) {
					if (page.block.left + imageParam.width >= width) {
						AlOneItem oi = page.items.get(page.countItems);

						if (oi.count > 0) {
							endBlockOnPrevItem(page, oi);
						} else {
							endBlockOnPrevItem(page, null);
						}
					}
				}
				return;
			}
			
			tword.style[pos_in_word] &= AlStyles.SL_IMAGE_OK;
		}

		if (errorBitmap != null) {
			imageParam.height = errorBitmap.height;
			imageParam.width = errorBitmap.width;
		} else {
			imageParam.height = 16;
			imageParam.width = 16;
		}
	}

    private int verifyRowSpan(AlOnePage page, AlOneItem oi, boolean writeMode) {

        if (oi.table_start == -1) {
            String link, rows;
            link = format.getLinkNameByPos(oi.pos[0], InternalConst.TAL_LINK_TYPE.ROW);
            if (link == null)
                return 0;
            int i = link.indexOf(':');
            if (i == -1)
                return 0;
            rows = link.substring(i + 1);
            link = link.substring(0, i);
            oi.table_start = InternalFunc.str2int(link, 10);
            oi.table_row = InternalFunc.str2int(rows, 10);
        }

        AlOneTable t = format.getTableByNum(oi.table_start);
        if (t == null || oi.table_row < 0 || oi.table_row >= t.rows.size())
            return 0;

        if (page.countItems < 1)
            return 0;

        if (page.items.get(page.countItems - 1).table_start != oi.table_start ||
                page.items.get(page.countItems - 1).table_row + 1 != oi.table_row)
            return 0;

        int parentDataHeight = 0, parentOtherHeight = 0, availableHeight = 0;

        for (int i = 0; i < t.rows.get(oi.table_row).cells.size(); i++) {
            if (t.rows.get(oi.table_row).cells.get(i).start == AlOneTable.LEVEL2_TABLE_CELL_ROWSPANNED) {

                if (t.rows.get(oi.table_row - 1).cells.get(i).start >= 0) {
                    if (parentDataHeight < t.rows.get(oi.table_row - 1).cells.get(i).height)
                        parentDataHeight = t.rows.get(oi.table_row - 1).cells.get(i).height;
                }

            } else {
                if (availableHeight < t.rows.get(oi.table_row).cells.get(i).height)
                    availableHeight = t.rows.get(oi.table_row).cells.get(i).height;

                //if (t.rows[oi.table_row - 1].cells[i].start >= 0) {
                if (parentOtherHeight < t.rows.get(oi.table_row - 1).cells.get(i).height)
                    parentOtherHeight = t.rows.get(oi.table_row - 1).cells.get(i).height;
                //}
            }
        }

        int diff = parentDataHeight - parentOtherHeight;
        if (diff > availableHeight)
            diff = availableHeight;

        if (diff > 0) {
            if (writeMode) {
                for (int i = 0; i < t.rows.get(oi.table_row).cells.size(); i++) {
                    if (t.rows.get(oi.table_row).cells.get(i).start == AlOneTable.LEVEL2_TABLE_CELL_ROWSPANNED) {
                        if (t.rows.get(oi.table_row - 1).cells.get(i).start >= 0) {
                            t.rows.get(oi.table_row - 1).cells.get(i).height -= diff;
                            t.rows.get(oi.table_row).cells.get(i).height += diff;
                        }
                    }
                }

                t.rows.get(oi.table_row - 1).height -= diff;
                t.rows.get(oi.table_row - 1).shtamp--;
                t.rows.get(oi.table_row).shtamp--;

                page.items.get(page.countItems - 1).base_line_up -= diff;

                page.textHeight -= diff;
            }

            return diff;
        }
        return 0;
    }

    private int getTableSize(int pos, AlOnePage page, int width, TAL_CALC_MODE calcMode) {

        AlOneTable ai = null;
        String link, rows;

        link = format.getLinkNameByPos(pos, InternalConst.TAL_LINK_TYPE.ROW);
        if (link == null)
            return AlOneTable.LEVEL2_TABLE_ROW_HEIGHT_IFERROR;
        int i = link.indexOf(':'), j;
        if (i == -1)
            return AlOneTable.LEVEL2_TABLE_ROW_HEIGHT_IFERROR;

        rows = link.substring(i + 1);
        link = link.substring(0, i);

        int table = InternalFunc.str2int(link, 10);
        int row = InternalFunc.str2int(rows, 10);

        ai = format.getTableByNum(table);

        int height = screen_parameters.free_picture_height - 2;

        ////////////////////////////////////////////////////////////////////////////////

        if (ai != null) {
            if (ai.rows.get(row).shtamp != shtamp.value) {
                ai.rows.get(row).cell_accepted = ai.rows.get(row).cells.size();

                if (ai.rows.get(row).pages == null)
                    ai.rows.get(row).addAllPages();

                ai.rows.get(row).height = 2;

                int fullW = width, left = 0;
                fullW -= (ai.rows.get(row).cells.size() - ai.rows.get(row).cell_accepted) * 3;

                ai.rows.get(row).cells.get(0).left = 0;
                for (i = 0; i < ai.rows.get(row).cell_accepted; i++) {
                    ai.rows.get(row).cells.get(i).width = fullW / (ai.rows.get(row).cell_accepted - i);
                    ai.rows.get(row).cells.get(i).left = left;
                    left += ai.rows.get(row).cells.get(i).width;
                    fullW -= ai.rows.get(row).cells.get(i).width;

                    if (ai.rows.get(row).cells.get(i).start == AlOneTable.LEVEL2_TABLE_CELL_COLSPANNED) {

                        for (j = i - 1; j >= 0; j--) {
                            if (ai.rows.get(row).cells.get(j).start == AlOneTable.LEVEL2_TABLE_CELL_COLSPANNED)
                                continue;

                            ai.rows.get(row).cells.get(j).width += ai.rows.get(row).cells.get(i).width;
                            break;
                        }

                        ai.rows.get(row).cells.get(i).width = 0;
                        ai.rows.get(row).cells.get(i).left = -1000;
                    }
                }

                for (i = 0; i < ai.rows.get(row).cell_accepted; i++) {
                    if (ai.rows.get(row).cells.get(i).start == AlOneTable.LEVEL2_TABLE_CELL_COLSPANNED)
                        continue;

                    if (ai.rows.get(row).cells.get(i).start < 0) {
                        ai.rows.get(row).cells.get(i).height = 2;
                    } else {
                        ai.rows.get(row).cells.get(i).isFull =
                            addCellToPage(ai.rows.get(row).cells.get(i).width - (fontParam.space_width_standart << 1),
                                height,
                                ai.rows.get(row).pages[i],
                                ai.rows.get(row).cells.get(i).start,
                                ai.rows.get(row).cells.get(i).stop);
                        ai.rows.get(row).cells.get(i).height = ai.rows.get(row).pages[i].textHeight;
                    }

                    if (ai.rows.get(row).height < ai.rows.get(row).cells.get(i).height)
                        ai.rows.get(row).height = ai.rows.get(row).cells.get(i).height;
                }

                ai.rows.get(row).shtamp = shtamp.value;
            }

            return ai.rows.get(row).height;
        }

        return AlOneTable.LEVEL2_TABLE_ROW_HEIGHT_IFERROR;
    }

    private void endBlockOnPrevItem(AlOnePage page, AlOneItem oi) {
		int diff = page.block.height - page.textHeightWONotes;

		if (oi == null) {
			for (int i = page.countItems - 1; i > 0; i--) {
				if (!page.items.get(i).isNote) {
					page.items.get(i).base_line_down += diff;
					break;
				}
			}
		} else {
			oi.base_line_down += diff;
		}

		page.textHeightWONotes += diff;
		page.textHeight += diff;

		page.block.use = false;
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
							calc.getTextWidths(tword.text, start, t, tword.width, false);
							
							if ((old_style & AlStyles.SL_SHADOW) != 0
								&& (tword.style[i] & AlStyles.SL_SHADOW) == 0) {
									tword.width[t] += preferences.multiplexer;
							}
	
							if ((old_style & AlStyles.STYLE_RAZR) != 0) {
								for (j = 0; j < t; j++) {
									tword.width[start + j] += fontParam.space_width_current;											
								}
							}
							
							//if (preferences.chinezeFormatting) {
								/*for (j = 0; j < t; j++) {
									if (AlUnicode.isChinezeSpecial(tword.text[j])) {
										tword.width[j] *= 0.7f;
										if (j == 0 || (tword.style[j - 1] & AlStyles.SL_CHINEZEADJUST) == 0)
											tword.style[j] |= AlStyles.SL_CHINEZEADJUST;
									}
									*//*switch (tword.text[j]) {
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
									}*//*
								}*/
							//}
						}
					}

				}

				int old_correctitalic = fontParam.correct_italic;
					
				fonts.modifyPaint(old_style, tword.style[i], profiles, true);
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

				calc.getTextWidths(tword.text, start, t, tword.width, false);
				
				if ((old_style & AlStyles.STYLE_RAZR) != 0) {
					for (j = 0; j < t; j++) {
						tword.width[start + j] += fontParam.space_width_current;						
					}
				}

				//if (preferences.chinezeFormatting) {
					/*for (j = 0; j < t; j++) {
						if (AlUnicode.isChinezeSpecial(tword.text[j])) {
							tword.width[j] *= 0.7;
							if (j == 0 || (tword.style[j - 1] & AlStyles.SL_CHINEZEADJUST) == 0)
								tword.style[j] |= AlStyles.SL_CHINEZEADJUST;
						}
						*//*switch (tword.text[j]) {
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
						}*//*
					}*/
				//}
			}
			
			if (fontParam.correct_italic != 0)
				tword.width[end] += fontParam.correct_italic;

			if ((old_style & AlStyles.SL_SHADOW) != 0)
				tword.width[end] += preferences.multiplexer;
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
                            if (tword.text[start + j] == AlStyles.CHAR_IMAGE_E) {

                                getImageSize(tword, start + j, page, width, page.countItems, calcMode);
                                tword.width[start + j] = imageParam.width;
                                tword.base_line_up[start + j] = imageParam.height;
                                tword.base_line_down[start + j] = 0;
                            } else
                            if (tword.text[start + j] == AlStyles.CHAR_ROWS_E) {
                                if (calcMode == TAL_CALC_MODE.NOTES) {
                                    tword.width[start + j] = 0;
                                    tword.base_line_up[start + j] = 2;
                                } else {
                                    tword.width[start + j] = width;
                                    tword.base_line_up[start + j] = getTableSize(tword.pos[start + j], page, width, calcMode);
                                }
                                tword.base_line_down[start + j] = 2;
                            }
                        }
					} else {
						int t = end - start + 1;

						//calc.getTextWidths(fontParam, tword.text, start, t, tword.width, modeCalcLight);
						if (isArabic) {
							calc.getTextWidthsArabic(tword.text, start, t, tword.width, modeCalcLight);
						} else {
							if (fontParam.style == 0) {
								char ch;
								for (j = 0; j < t; j++) {
									ch = tword.text[start + j];
									if (calc.mainWidth[ch] == AlCalc.UNKNOWNWIDTH) {
										tword.width[start + j] = calc.getOneMainTextCharWidth(ch);
									} else {
										tword.width[start + j] = calc.mainWidth[ch];
									}
								}
							} else {
								calc.getTextWidths(tword.text, start, t, tword.width, modeCalcLight);
							}
						}
						
						if ((old_style & AlStyles.SL_SHADOW) != 0
							&& (tword.style[i] & AlStyles.SL_SHADOW) == 0) {
								tword.width[t] += preferences.multiplexer;
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
							/*for (j = 0; j < t; j++) {
								if (AlUnicode.isChinezeSpecial(tword.text[j])) {
									tword.width[j] *= 0.7f;
									if (j == 0 || (tword.style[j - 1] & AlStyles.SL_CHINEZEADJUST) == 0)
										tword.style[j] |= AlStyles.SL_CHINEZEADJUST;
								}
								*//*switch (tword.text[j]) {
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
								}*//*
							}*/
						//}
					}

				}

				int old_correctitalic = fontParam.correct_italic;
					
				fonts.modifyPaint(old_style, tword.style[i], profiles, true);
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
                    if (tword.text[start + j] == AlStyles.CHAR_IMAGE_E) {
                        getImageSize(tword, start + j, page, width, page.countItems, calcMode);
                        tword.width[start + j] = imageParam.width;
                        tword.base_line_up[start + j] = imageParam.height;
                        tword.base_line_down[start + j] = 0;
                    } else
                    if (tword.text[start + j] == AlStyles.CHAR_ROWS_E) {
                        if (calcMode == TAL_CALC_MODE.NOTES) {
                            tword.width[start + j] = 0;
                            tword.base_line_up[start + j] = 2;
                        } else {
                            tword.width[start + j] = width;
                            tword.base_line_up[start + j] = getTableSize(tword.pos[start + j], page, width, calcMode);
                        }
                        tword.base_line_down[start + j] = 2;
                    }
				}
			} else {
				int t = end - start + 1;

				//calc.getTextWidths(fontParam, tword.text, start, t, tword.width, modeCalcLight);
// inc speed???
				if (isArabic) {
					calc.getTextWidthsArabic(tword.text, start, t, tword.width, modeCalcLight);
				} else {
					if (fontParam.style == 0) {
						char ch;
						for (j = 0; j < t; j++) {
							ch = tword.text[start + j];
							if (calc.mainWidth[ch] == AlCalc.UNKNOWNWIDTH) {
								tword.width[start + j] = calc.getOneMainTextCharWidth(ch);
							} else {
								tword.width[start + j] = calc.mainWidth[ch];
							}
						}
					} else {
						calc.getTextWidths(tword.text, start, t, tword.width, modeCalcLight);
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
					/*for (j = 0; j < t; j++) {
						if (AlUnicode.isChinezeSpecial(tword.text[j])) {
							tword.width[j] *= 0.7;
							if (j == 0 || (tword.style[j - 1] & AlStyles.SL_CHINEZEADJUST) == 0)
								tword.style[j] |= AlStyles.SL_CHINEZEADJUST;
						}
						*//*switch (tword.text[j]) {
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
						}*//*
					}*/
				//}
			}

            if (fontParam.correct_italic != 0 && !(tword.count == 1 && tword.text[0] < 0x20))
				tword.width[end] += fontParam.correct_italic;

			if ((old_style & AlStyles.SL_SHADOW) != 0)
				tword.width[end] += preferences.multiplexer;
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
						if ((tword.style[i] & AlStyles.STYLE_CODE) == 0) {
							tword.style[i] &= AlStyles.SL_COLOR_MASK | AlStyles.SL_FONT_MASK | AlStyles.STYLE_MASK | AlStyles.SL_PAR | AlStyles.SL_IMAGE;
						} else {
							tword.style[i] &= AlStyles.SL_COLOR_MASK | AlStyles.STYLE_MASK | AlStyles.SL_PAR | AlStyles.SL_IMAGE;
						}
						tword.style[i] |= (((long)80L) << AlStyles.SL_SIZE_SHIFT);
					}
					/*for (i = 0; i < tword.count; i++) {
						if ((tword.style[i] & AlStyles.STYLE_CODE) != 0) {
							tword.style[i] &= AlStyles.SL_COLOR_MASK | AlStyles.SL_FONT_MASK | AlStyles.STYLE_MASK | AlStyles.SL_PAR | AlStyles.SL_IMAGE;
							if (preferences.styleSumm) 
								tword.style[i] |= screen_parameters.style_notes & AlStyles.SL_FONT_IMASK; else 
								tword.style[i] ^= screen_parameters.style_notes & AlStyles.SL_FONT_IMASK;
						}*//* else
						if ((tword.style[i] & AlStyles.STYLE_HIDDEN) != 0 && (tword.style[i] & AlStyles.SL_REMAPFONT) != 0 ) {
							tword.style[i] &= AlStyles.SL_FONT_MASK | 
								AlStyles.SL_MARKTITLE | AlStyles.STYLE_BOLD | AlStyles.SL_MASKFORLINK |
								AlStyles.STYLE_ITALIC | AlStyles.STYLE_SUB | AlStyles.STYLE_SUP | AlStyles.STYLE_LINK | AlStyles.SL_IMAGE |
								AlStyles.STYLE_HIDDEN | AlStyles.STYLE_STRIKE | AlStyles.STYLE_UNDER;
							if (preferences.styleSumm) 
								tword.style[i] |= screen_parameters.style_notes & AlStyles.SL_FONT_IMASK; else 
								tword.style[i] ^= screen_parameters.style_notes & AlStyles.SL_FONT_IMASK;
						}*//* else{
							tword.style[i] &= AlStyles.SL_MARKTITLE | AlStyles.STYLE_BOLD | AlStyles.SL_MASKFORLINK |
								AlStyles.STYLE_ITALIC | AlStyles.STYLE_SUB | AlStyles.STYLE_SUP | AlStyles.STYLE_LINK | AlStyles.SL_IMAGE |
								AlStyles.STYLE_HIDDEN | AlStyles.STYLE_STRIKE | AlStyles.STYLE_UNDER;
							if (preferences.styleSumm) tword.style[i] |= 
								screen_parameters.style_notes; else tword.style[i] ^= screen_parameters.style_notes;
						}
						
						if ((tword.style[i] & AlStyles.STYLE_LINK) != 0x00) {
							tword.style[i] &= AlStyles.SL_COLOR_IMASK;
							tword.style[i] |= AlStyles.SL_COLOR_LINK;
						} else						
						if ((tword.style[i] & AlStyles.SL_MARKTITLE) != 0x00) {
							tword.style[i] &= AlStyles.SL_COLOR_IMASK & 0xfffffffffffffffcL;
							tword.style[i] |= screen_parameters.style_titlenotes + AlStyles.STYLE_BOLD;
						}
						
						tword.style[i] |= (tword.style[i] & AlStyles.SL_FONT_MASK) >> 8;
					}*/
				} else {
					for (i = 0; i < tword.count; i++) {
						if ((tword.style[i] & AlStyles.SL_MARKFIRTSTLETTER0) != 0) {
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
					if (oi.count == 0)
						return false;

                    int rowspanDiff = 0;
                    if (oi.count == 1 && oi.text[0] == AlStyles.CHAR_ROWS_E)
                        rowspanDiff = verifyRowSpan(page, oi, false);
					
					if ((calcMode != TAL_CALC_MODE.NOTES || notesItemsOnPage < preferences.maxNotesItemsOnPageUsed) && (
						
							(page.textHeight + oi.height + oi.base_line_down + oi.base_line_up + (oi.interline > 0 ? oi.interline : 0) +
							(calcMode == TAL_CALC_MODE.NOTES && !page.notePresent ? page.notesShift : 0) -
							((preferences.isASRoll ? 0 : screen_parameters.reservHeight0)) <= page.pageHeight
                                    + rowspanDiff
                            ) ||
							(page.countItems == 0))
						) {

                        if (rowspanDiff > 0)
                            verifyRowSpan(page, oi, true);

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
					if ((tword.style[0] & AlStyles.SL_MARKFIRTSTLETTER0) != 0) {
						if (profiles.classicFirstLetter) {
							tword.base_line_down[0] = fontParam.def_line_down;
							tword.base_line_up[0] = fontParam.height - fontParam.base_line_down;
							tword.base_line_down[0] += fontParam.height +
									screen_parameters.cFontInterline[InternalConst.TAL_PROFILE_FONTTYPE_TEXT] *
									screen_parameters.cFontHeight[0] / 100;
						} else {
							if (tword.base_line_down[0] > fontParam.def_line_down)
								tword.base_line_down[0] = fontParam.def_line_down;
						}
						int t;
						for (t = 1; t < tword.count; t++) {
							if ((tword.style[t] & AlStyles.SL_MARKFIRTSTLETTER0) == 0)
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

	private int	getCorrectSizePosition(int pos) {
		int i = format.getNumParagraphByPoint(pos);
		int len = format.getLengthPragarphByNum(i);
		i = format.getStartPragarphByNum(i);
		if (i + 128 >= pos)
			return i;

		int start = pos, j;
		int end = format.getSize();

		if (pos + 128 > i + len && pos + 128 < end)
			return i + len;

		while (start < end) {
			j = format.getTextBuffer(start, format_text_and_style, shtamp.value, profiles);
			i = start - (start & AlFiles.LEVEL1_FILE_BUF_MASK);

			for (; i < j; i++, start++) {
				if (format_text_and_style.txt[i] == 0x00)
					continue;

				if (format_text_and_style.txt[i] == 0x20)
					return start;

				if ((format_text_and_style.stl[i] & AlStyles.SL_PAR) != 0)
					return start;
			}
		}

		return pos;
	}

	private void recalcColumn(int width, int height, AlOnePage page, int start_point/*, TAL_CALC_MODE calc_mode*/) {

		page.start_position = page.end_position = start_point;
		page.countItems = 0;
		page.items.get(0).count = 0;
		page.selectStart = page.selectEnd = -1;
		page.pageHeight = height;
		page.block.use = false;
		if (preferences.isASRoll) {
			page.topMarg = -page.overhead;
			page.textHeight = -page.overhead;
		} else {
			page.topMarg = 0;
			page.textHeight = 0;
		}
		page.textHeightWONotes = page.textHeight;
		page.notePresent = false;
		page.notesShift = (int) (screen_parameters.cFontHeight[InternalConst.TAL_PROFILE_FONTTYPE_TEXT] * 0.6f/* >> 1*/);
		/*if (screen_parameters.interFI0[0] < 0)
			page.notesShift -= screen_parameters.interFI0[0];*/

		tmp_word.need_flags = 0;
		tmp_word.count = 0;
		tmp_word.hyph[0]	= InternalConst.TAL_HYPH_INPLACE_DISABLE;
			
		int start = start_point, i, j = 0;
		int end = format.getSize();
		boolean	noFirstAdd = false;
		while (start < end) {
			
			j = format.getTextBuffer(start, format_text_and_style, shtamp.value, profiles);		
			i = start - (start & AlFiles.LEVEL1_FILE_BUF_MASK);
			
			char ch;
			for (; i < j; i++, start++) {
				
				if ((ch = format_text_and_style.txt[i]) == 0x00)
					continue;
                if ((format_text_and_style.stl[i] & (AlStyles.STYLE_HIDDEN | AlStyles.SL_TABLE)) != 0)
                    continue;

                    if ((format_text_and_style.stl[i] & AlStyles.SL_PAR) != 0) {
					if (tmp_word.count > 0) {
						//tmp_word.style[tmp_word.count - 1] |= AlStyles.SL2_ENDPARAGRAPH;
						if (addWord(tmp_word, page, width, TAL_CALC_MODE.NORMAL/*calc_mode*/))
							return;
						noFirstAdd = true;
					}
					if (noFirstAdd) {						
						if (addWord(tmp_word, page, width, TAL_CALC_MODE.NORMAL/*calc_mode*/))
							return;
						noFirstAdd = false;
					}
				}

				if (ch == 0x20 ) {
					if (tmp_word.count != 0) {
						if (addWord(tmp_word, page, width, TAL_CALC_MODE.NORMAL/*calc_mode*/))
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
							if (addWord(tmp_word, page, width, TAL_CALC_MODE.NORMAL/*calc_mode*/))
								return;
							tmp_word.need_flags &= ~InternalConst.AL_ONEWORD_FLAG_NOINSERTALL;
							noFirstAdd = true;
						}
					}
				}
			}
		}

		int verifed_pos = -1;

		if (tmp_word.count > 0) {
			addWord(tmp_word, page, width, TAL_CALC_MODE.NORMAL);
		}

        addWord(tmp_word, page, width, TAL_CALC_MODE.NORMAL);

		if (page.countItems > 0) {
			i = page.items.get(page.countItems - 1).count;
			while ((i--) > 0) {
				verifed_pos = page.items.get(page.countItems - 1).pos[i];
				if (verifed_pos >= 0)
					break;
			}
		}

		if (verifed_pos >= 0) {
			int last_text = end - 1;
			while (--j > 0) {
				if (format_text_and_style.txt[j] > 0x20 ||
						format_text_and_style.txt[j] == AlStyles.CHAR_IMAGE_E ||
						format_text_and_style.txt[j] == AlStyles.CHAR_ROWS_E)
						break;
				last_text--;
			}
			if (last_text == verifed_pos) {
				page.end_position = end;
			} else {
				page.end_position = verifed_pos + 1;
			}
		} else
			page.end_position = end;

    }

	private void calcCountPages() {

		format.lastCalcTime = System.currentTimeMillis();
		clearPagePosition();

		int start_point = 0;
		int end_point = format.getSize();

		if (!format.isTextFormat) {

			while (start_point < format.getCountPages()) {
				pagePositionPointer.add(AlPagePositionStack.add(format.getPageStart(start_point), 0));
				if (profiles.twoColumnUsed)
					start_point++;
				start_point++;
			}

			format.lastCalcTime = System.currentTimeMillis() - format.lastCalcTime;
			format.lastPageCount = pagePositionPointer.size();
			return;
		}


		//calc.beginMain();
		calcScreenParameters();

		calcWordLenForPages = true;


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
                        mpage[0][0], start_point/*, TAL_CALC_MODE.NORMAL*/);
				recalcColumn(
					(screenWidth >> 1) - screen_parameters.marginR - screen_parameters.marginL, 
					screenHeight - screen_parameters.marginB - screen_parameters.marginT,
                        mpage[0][1], mpage[0][0].end_position/*, TAL_CALC_MODE.NORMAL*/);
				start_point = mpage[0][1].end_position;
			} else {
				recalcColumn(
					screenWidth - screen_parameters.marginR - screen_parameters.marginL, 
					screenHeight - screen_parameters.marginB - screen_parameters.marginT,
                        mpage[0][0], start_point/*, TAL_CALC_MODE.NORMAL*/);
				start_point = mpage[0][0].end_position;
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
		
		Log.e("last open/calc time", Long.toString(AlFiles.time_load1 + AlFiles.time_load2) + '/' + Long.toString(format.lastCalcTime));

		bookPosition = getCorrectScreenPagePosition(bookPosition);
		if (preferences.calcPagesModeUsed == TAL_SCREEN_PAGES_COUNT.SCREEN) {
			bookPosition = pagePositionPointer.get(bookPosition).start;			
		} else {
			clearPagePosition();
		}

		//calc.endMain();
	}

	private int	getCorrectScreenPagePosition(int pos) {
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
     * @param position - обьект, содержащий минимально необходимую информацию о положении чтения
     * @return TAL_RESULT.OK если все успешно и TAL_RESULT.ERROR если есть ошибка
     */
	public synchronized int	getPageCount(AlCurrentPosition position) {
		position.noNeedSave = true;

		if (isBookOpened()) {
			position.noNeedSave = format.getNoNeedSave();
			position.haveProblem = format.haveProblem;

            position.readPositionStart = bookPosition;
			position.readPositionEnd = (profiles.twoColumnUsed ? mpage[0][1] : mpage[0][0]).end_position;

			position.readPositionAddon = format.getPositionAddon(bookPosition);

			position.isFirstPage = bookPosition == 0;
            position.isLastPage = position.readPositionEnd >= format.getSize();

			switch (preferences.calcPagesModeUsed) {
				case SCREEN:
                    position.pageCurrent = getCorrectScreenPagePosition(bookPosition) + 1;
                    position.pageCount = pagePositionPointer.size();
					position.pageSize = -1;

					return TAL_RESULT.OK;
				case AUTO:
				case SIZE:
                    position.pageCurrent = /*(int)*/(/*0.5f + */(bookPosition / preferences.pageSize)) + 1;
                    position.pageCount = /*(int)*/(/*0.5f + */(format.getSize() / preferences.pageSize)) + 1;
					if (position.pageCurrent > position.pageCount || position.isLastPage)
						position.pageCurrent = position.pageCount;

					position.pageSize = preferences.pageSize;

					return TAL_RESULT.OK;
			}
		}
        position.readPositionStart = -1;
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
		int end = format.getSize() - 1;
		int tmp, start0 = format.getStartPragarphByNum(num_par);
			
		if (start0 == start_point) {
			if (num_par == 0) {
				recalcColumn(width, height, page, 0/*, TAL_CALC_MODE.NORMAL*/);
				return 0;			
			}
			start0 = format.getStartPragarphByNum(--num_par);
		}

        int nextStart, nextEnd;

        while (true) {
            recalcColumn(width, height, page, start0/*, TAL_CALC_MODE.NORMAL*/);

            if (page.realLength > page.countItems && page.items.get(page.countItems).pos[0] >= page.end_position) {
                nextEnd = getOverItemEndPos(page.items.get(page.countItems));
                nextStart = getOverItemStartPos(page.items.get(page.countItems));

                if (nextStart <= start_point) {
                    if (nextEnd >= start_point)
                        return start0;
                    break;
                }
            }
           /* if (start_point >= end*//*specialLast*//* && page.end_position >= end) {

            } else {

                if (page.realLength > page.countItems && page.items.get(page.countItems).pos[0] >= page.end_position) {
                    nextEnd = getOverItemEndPos(page.items.get(page.countItems));
                    nextStart = getOverItemStartPos(page.items.get(page.countItems));
                } else {
                    nextEnd = getOverItemEndPos(page.items.get(page.countItems - 1));
                    nextStart = Math.min(getOverItemStartPos(page.items.get(page.countItems - 1)), start0);
                }

                if (nextStart <= start_point) {
                    if (nextEnd >= start_point)
                        return start0;
                    break;
                }
            }*/
            if (num_par == 0)
                return 0;

            //if ((format.getStylePragarphByNum(num_par) & AlStyles.PAR_BREAKPAGE) != 0
			if ((format.par0.get(num_par).prop & AlParProperty.SL2_BREAK_BEFORE) != 0
                    && preferences.sectionNewScreen)
                return start0;

            start0 = format.getStartPragarphByNum(--num_par);
        }

        int /*nextStart1, nextEnd1,*/ start1;

        while (true) {

			start1 = 1;
			while (page.items.get(start1).isNote && start1 < page.countItems) start1++;

            start1 = page.items.get(start1).start_pos;
            if (start1 == start_point)
                return start0;

            recalcColumn(width, height, page, start1/*, TAL_CALC_MODE.NORMAL*/);

            if (start_point >= end/*specialLast*/) {
                if (page.end_position >= end)
                    return start1;
            } else
            if (page.realLength > page.countItems && page.items.get(page.countItems).pos[0] >= page.end_position) {
                nextEnd = getOverItemEndPos(page.items.get(page.countItems));
            } else {
                nextEnd = getOverItemEndPos(page.items.get(page.countItems - 1));
            }

            if (nextEnd >= start_point) {
                return start1;
            }

            start0 = start1;
        }

	}

	public synchronized int getPageOfPosition(int pos) {
		if (openState.getState() == AlBookState.OPEN) {
			switch (preferences.calcPagesModeUsed) {
			case SCREEN:
				return getCorrectScreenPagePosition(pos) + 1;
			case AUTO:
				case SIZE:
				return /*(int) */(/*0.5f + */(pos / preferences.pageSize)) + 1;
			default:
				return -1;
			}
		}
		return -1;
	}

	public synchronized int getPositionOfPage(int pageNum) {
		if (preferences.isASRoll)
			return -1;

		if (openState.getState() != AlBookState.OPEN)
			return -1;

		switch (preferences.calcPagesModeUsed) {
		case SCREEN:
			if (pageNum - 1 >= 0 && pageNum - 1 < pagePositionPointer.size()) {
				return pagePositionPointer.get(pageNum - 1).start;
				}
			break;
		case AUTO:
			case SIZE:
			if ((pageNum - 1) * preferences.pageSize >= 0 && (pageNum - 1) * preferences.pageSize < format.getSize()) {
				return getCorrectSizePosition((pageNum - 1) * preferences.pageSize);
			}
			break;
		}
		return -1;
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
                    mpage[2][0], res);
			res = calcPrevStartPoint(
					(screenWidth >> 1) - screen_parameters.marginR - screen_parameters.marginL, 
					screenHeight - screen_parameters.marginB - screen_parameters.marginT,
                    mpage[2][0], res);
		} else {
			res = calcPrevStartPoint(
					screenWidth - screen_parameters.marginR - screen_parameters.marginL,
					screenHeight - screen_parameters.marginB - screen_parameters.marginT,
                    mpage[2][0], res);
		}

		//calc.endMain();
		calcWordLenForPages = false;

		return res;
	}

	private void updatePageItems(AlOnePage page, int x0, int y0, int x1, int y1) {
		boolean first_notes = true;
		AlOneItem oi;
		int x;
		int col_count = page.countItems;

		if (preferences.isASRoll || (profiles.specialModeRoll && !profiles.twoColumnUsed)) {
			oi = page.items.get(col_count);
			if (oi.count > 0 && oi.pos[0] >= page.end_position &&
					(profiles.classicFirstLetter || oi.isEnd || ((oi.style[0] & AlStyles.SL_MARKFIRTSTLETTER0) == 0)))
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
						first_notes = false;
					}
				}


				y += oi.height + oi.base_line_up;
				oi.yDrawPosition = y;

				y += oi.base_line_down;
				y += oi.interline;
			}
		}
	}

	private void recalcAndPrepareColumn() {
		if (openState.getState() != AlBookState.OPEN) {
			return;
		}
		calcScreenParameters();
		recalcColumn(screenWidth - screen_parameters.marginR - screen_parameters.marginL,
				screenHeight - screen_parameters.marginB - screen_parameters.marginT,
				mpage[0][0], bookPosition);
		prepareColumn(mpage[0][0]);
		updatePageItems(mpage[0][0],
				screen_parameters.marginL,
				screen_parameters.marginT,
				(screenWidth >> 1) - screen_parameters.marginR,
				screenHeight - screen_parameters.marginB);
	}

	private int returnOkWithRedraw() {
		recalcAndPrepareColumn();
		threadData.sendNotifyForUIThread(TAL_NOTIFY_ID.NEEDREDRAW, TAL_NOTIFY_RESULT.OK);
		return TAL_RESULT.OK;
	}

    private int	calculateNextPagePoint(int pos) {
        int current_page;
        switch (preferences.calcPagesModeUsed) {
            case SCREEN:
                current_page = getCorrectScreenPagePosition(bookPosition);
                if (current_page < pagePositionPointer.size() - 1)
                    return pagePositionPointer.get(current_page + 1).start;
                break;
            case AUTO:
            case SIZE:
                current_page = pos;
                if (profiles.twoColumnUsed) {
                    current_page = mpage[0][1].end_position;
                    if (mpage[0][0].end_position >= format.getSize())
                        current_page = mpage[0][0].end_position;
                } else {
                    current_page = mpage[0][0].end_position;
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
                current_page = getCorrectScreenPagePosition(bookPosition);
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

	public synchronized int gotoPage(int pageNum) {
		if (preferences.isASRoll)
			return TAL_RESULT.ERROR;

		if (openState.getState() != AlBookState.OPEN)
			return TAL_RESULT.ERROR;

		switch (preferences.calcPagesModeUsed) {
			case SCREEN:
                if (pageNum - 1 >= 0 && pageNum - 1 < pagePositionPointer.size()) {
                    bookPosition = pagePositionPointer.get(pageNum - 1).start;
                    return returnOkWithRedraw();
                }
			    break;
			case AUTO:
			case SIZE:
                if ((pageNum - 1) * preferences.pageSize >= 0 && (pageNum - 1) * preferences.pageSize < format.getSize()) {
					clearPagePosition();
                    bookPosition = getCorrectSizePosition((pageNum - 1) * preferences.pageSize);
                    return returnOkWithRedraw();
                }
                break;
		}

		return TAL_RESULT.ERROR;
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
	public synchronized int	gotoPosition(TAL_GOTOCOMMAND mode, int pos) {
        if (preferences.isASRoll)
            return TAL_RESULT.ERROR;

		if (openState.getState() > AlBookState.PROCESS0) {
			switch (mode) {
				case NEXTPAGE:
				case PREVPAGE:
					break;
				default:
					// commented by joy@onyx, to work around the issue of opening large doc file
					//return TAL_RESULT.ERROR;
					break;
			}
		}

		if (openState.getState() != AlBookState.OPEN && openState.getState() < AlBookState.PROCESS0)
			return TAL_RESULT.ERROR;

		if ((mode == TAL_GOTOCOMMAND.POSITION || mode == TAL_GOTOCOMMAND.POSITION_WITH_CORRECT)&& pos > format.getSize())
			mode = TAL_GOTOCOMMAND.LASTPAGE;

		int current_page, oldbookPosition;
		switch (preferences.calcPagesModeUsed) {
		case SCREEN: {
				if (mode == TAL_GOTOCOMMAND.POSITION) {
					current_page = getCorrectScreenPagePosition(pos);
					bookPosition = pagePositionPointer.get(current_page).start;
					return returnOkWithRedraw();
				} else {
					current_page = getCorrectScreenPagePosition(bookPosition);
					switch (mode) {
						case NEXTPAGE:
							if (current_page < pagePositionPointer.size() - 1) {
								oldbookPosition = bookPosition;
								bookPosition = pagePositionPointer.get(current_page + 1).start;
								if (AL_SUPPORT_COPYPAGE) {
									dublicatePage(0, 2, -bookPosition);
									if (bmp[1].shtamp == shtamp.value && oldbookPosition == bmp[1].position)
										dublicatePage(1, 0, bookPosition);
								}
								return returnOkWithRedraw();
							}
							break;
						case PREVPAGE:
							if (current_page > 0) {
								oldbookPosition = bookPosition;
								bookPosition = pagePositionPointer.get(current_page - 1).start;
								if (AL_SUPPORT_COPYPAGE) {
									if (bmp[2].shtamp == shtamp.value && oldbookPosition == -bmp[2].position)
										dublicatePage(2, 0, bookPosition);
								}
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
			}
			break;
		case AUTO:
		case SIZE: {
				switch (mode) {
				case NEXTPAGE:
					current_page = bookPosition;
					if (profiles.twoColumnUsed) {
						current_page = mpage[0][1].end_position;
						if (mpage[0][0].end_position >= format.getSize())
							current_page = mpage[0][0].end_position;
					} else {
						current_page = mpage[0][0].end_position;
					}
					if (current_page < format.getSize()) {
						// add to stack position
						AlPagePositionStack.addBackPage(pagePositionPointer, current_page, bookPosition);
						//
						oldbookPosition = bookPosition;
						bookPosition = current_page;
						if (AL_SUPPORT_COPYPAGE) {
							dublicatePage(0, 2, -bookPosition);
							if (bmp[1].shtamp == shtamp.value && oldbookPosition == bmp[1].position)
								dublicatePage(1, 0, bookPosition);
						}
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
                    bookPosition = format.getSize() - 1;
					clearPagePosition();
                    current_page = calculatePrevPagesPoint(bookPosition);
                    if (bookPosition != current_page) {
                        bookPosition = current_page;
                        return returnOkWithRedraw();
                    }
                    break;
				case PREVPAGE:
					current_page = AlPagePositionStack.getBackPage(pagePositionPointer, bookPosition);
					if (current_page == -1) {
						clearPagePosition();
						current_page = calculatePrevPagesPoint(bookPosition);
					}
					if (bookPosition != current_page) {
						oldbookPosition = bookPosition;
						bookPosition = current_page;
						if (AL_SUPPORT_COPYPAGE) {
							if (bmp[2].shtamp == shtamp.value && oldbookPosition == -bmp[2].position)
								dublicatePage(2, 0, bookPosition);
						}
						return returnOkWithRedraw();
					}
					break;
				case POSITION_WITH_CORRECT:
					pos = getCorrectSizePosition(pos);
				case POSITION:
					if (AlPagePositionStack.getBackPage(pagePositionPointer, pos) == -1)
						clearPagePosition();
					bookPosition = pos;
					return returnOkWithRedraw();
				}
			}
			break;
		}

		return TAL_RESULT.ERROR;
	}

	private final AlTapInfo tapInfo = new AlTapInfo();

	private void clearPagePosition() {
		bmp[1].shtamp = bmp[2].shtamp = 0;
		pagePositionPointer.clear();
	}

    /**
     * Получение информации о месте в книге, по которому "тапнули" на экране. Результат зависит от текущего режима выделения.
     * @param x - координата x тапа
     * @param y - координата y тапа
     * @param initialSelectMode - по умолчанию должен быть NONE. В любом другом случае - указание режима в который перейдет библиотека при тапе
     * @return AlTapInfo
     */
    public synchronized AlTapInfo getInfoByTap(int x, int y, TAL_SCREEN_SELECTION_MODE initialSelectMode) {
    	tapInfo.clearInfo();

        if (preferences.isASRoll)
            return null;
    	
    	tapInfo.x = x;
    	tapInfo.y = y;

		if (openState.getState() != AlBookState.OPEN || !getPositionByXY(initialSelectMode)) {

			if (selection.selectMode == TAL_SCREEN_SELECTION_MODE.NONE) {
				switch (initialSelectMode) {
					case START:
					case END:
					case DICTIONARY:
						setSelectionMode(initialSelectMode);
						break;
				}
			}
            return null;
        }

		//
		// scan bookmark Id
		if (tapInfo.pos != AlTapInfo.TAP_ON_CLEAR_SPACE && bookmarks != null) {
			AlOneBookmark bmk = null;
			for (int i = 0; i < bookmarks.size(); i++) {
				bmk = bookmarks.get(i);
				if (bmk.pos_start <= tapInfo.pos && bmk.pos_end >= tapInfo.pos) {
					tapInfo.bookmarkId = bmk.id;
					break;
				}
			}
		}
		//

        int start, stop;

		switch (selection.selectMode) {
		case NONE:
			if (!tapInfo.isNote && (initialSelectMode == TAL_SCREEN_SELECTION_MODE.START || initialSelectMode == TAL_SCREEN_SELECTION_MODE.END)) {

				if (engOptions.selectCorrecter != null) {
					fillTextOnScreen(true, tapInfo.tapWordStart, true, tapInfo.tapWordStop);
					if (engOptions.selectCorrecter.correct(textOnScreen)) {
						tapInfo.tapWordStart = textOnScreen.correctedPositionStart;
						tapInfo.tapWordStop = textOnScreen.correctedPositionEnd;
					}
				}

                setSelection(initialSelectMode, tapInfo.tapWordStart, tapInfo.tapWordStop);
			} else
			if (!tapInfo.isNote && (initialSelectMode == TAL_SCREEN_SELECTION_MODE.DICTIONARY)) {

				if (engOptions.selectCorrecter != null) {
					fillTextOnScreen(true, tapInfo.tapWordStart, true, tapInfo.tapWordStop);
					if (engOptions.selectCorrecter.correct(textOnScreen)) {
						tapInfo.tapWordStart = textOnScreen.correctedPositionStart;
						tapInfo.tapWordStop = textOnScreen.correctedPositionEnd;
					}
				}

				setSelection(EngBookMyType.TAL_SCREEN_SELECTION_MODE.DICTIONARY, tapInfo.tapWordStart, tapInfo.tapWordStop);
			} else {
				if (tapInfo.isImage) {
					AlOneImage ai;
					String link = format.getLinkNameByPos(tapInfo.pos, InternalConst.TAL_LINK_TYPE.IMAGE);
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
					String link = format.getLinkNameByPos(tapInfo.pos, InternalConst.TAL_LINK_TYPE.LINK);
					if (link != null) {
						al = format.getLinkByName(link, true);
						if (al != null) {
							tapInfo.linkLocalPosition = al.positionS;
                            tapInfo.isFootNote = (al.iType == 1) && (!tapInfo.isNote);
                            tapInfo.link.append(link);
                        } else {
							AlOneTable at = format.getTableByName(link);
							if (at != null) {
								tapInfo.isLocalLink = false;
								tapInfo.isTableLink = true;
								tapInfo.linkLocalPosition = at.start;
							} else {
								tapInfo.isLocalLink = false;
								tapInfo.isExtLink = true;
								tapInfo.link.append(link);
							}
						}
					} else {
						tapInfo.isLocalLink = false;
					}
				}
			}		
			break;
		case START:
			if (engOptions.selectCorrecter != null) {
				fillTextOnScreen(true, tapInfo.tapWordStart, false, -1);
				if (engOptions.selectCorrecter.correct(textOnScreen))
					tapInfo.tapWordStart = textOnScreen.correctedPositionStart;
			}

			start = tapInfo.tapWordStart;
            stop = selection.selectPosition.y;
            if (stop < start)
                stop = tapInfo.tapWordStop;

            setSelection(selection.selectMode, start, stop);
			break;
		case END:
			if (engOptions.selectCorrecter != null) {
				fillTextOnScreen(false, -1, true, tapInfo.tapWordStop);
				if (engOptions.selectCorrecter.correct(textOnScreen))
					tapInfo.tapWordStop = textOnScreen.correctedPositionEnd;
			}

			start = selection.selectPosition.x;
            stop = tapInfo.tapWordStop;
			if (stop < start)
                start = tapInfo.tapWordStart;

            setSelection(selection.selectMode, start, stop);
			break;
		case DICTIONARY:
			if (engOptions.selectCorrecter != null) {
				fillTextOnScreen(true, tapInfo.tapWordStart, true, tapInfo.tapWordStop);
				if (engOptions.selectCorrecter.correct(textOnScreen)) {
					tapInfo.tapWordStart = textOnScreen.correctedPositionStart;
					tapInfo.tapWordStop = textOnScreen.correctedPositionEnd;
				}
			}

			setSelection(EngBookMyType.TAL_SCREEN_SELECTION_MODE.DICTIONARY, tapInfo.tapWordStart, tapInfo.tapWordStop);
			break;
		}

        return tapInfo;
    }

    private boolean getPositionByXY(TAL_SCREEN_SELECTION_MODE initialSelectMode) {

        if (profiles.twoColumnUsed && tapInfo.x >= (screenWidth >> 1))
            return getPositionInPageByXY(mpage[0][1], (screenWidth >> 1) + screen_parameters.marginR, initialSelectMode);
        return getPositionInPageByXY(mpage[0][0], screen_parameters.marginL, initialSelectMode);
    }

    private static final int SPECIAL_HYPH_POS = -2;

	private AlOneTable getTableAndRowByPos(int sellpos, AlIntHolder row) {
		String link, rows;

		link = format.getLinkNameByPos(sellpos, InternalConst.TAL_LINK_TYPE.ROW);
		if (link == null)
			return null;

		int i = link.indexOf(':');
		if (i == -1)
			return null;

		rows = link.substring(i + 1);
		link = link.substring(0, i);

		int table = InternalFunc.str2int(link, 10);
		row.value = InternalFunc.str2int(rows, 10);

		return format.getTableByNum(table);
	}

    private boolean getPositionInTableByXY(AlTapInfo tapInfo, TAL_SCREEN_SELECTION_MODE initialSelectMode, int left, int top) {
        int sellPos = tapInfo.pos, SymbolFound = -1, i;
        tapInfo.pos = AlTapInfo.TAP_ON_CLEAR_SPACE;
        tapInfo.isTableTap = true;

        // get Table
       /* AlOneTable ai = null;
        String link, rows;

        link = format.getLinkNameByPos(sellPos, InternalConst.TAL_LINK_TYPE.ROW);
        if (link == null)
            return true;
        int i = link.indexOf(':');
        if (i == -1)
            return true;

        rows = link.substring(i + 1);
        link = link.substring(0, i);

        int table = InternalFunc.str2int(link, 10);
        int row = InternalFunc.str2int(rows, 10);

        ai = format.getTableByNum(table);*/

		AlIntHolder rowHolde = new AlIntHolder(0);
		AlOneTable ai = getTableAndRowByPos(sellPos, rowHolde);
		int row = rowHolde.value;

		if (ai != null) {
            // get Cell
            AlOneTableCell cell = null;
            AlOnePage page = null;
            for (i = 0; i < ai.rows.get(row).cell_accepted; i++) {
                if (tapInfo.x >= left + ai.rows.get(row).cells.get(i).left && tapInfo.x < left + ai.rows.get(row).cells.get(i).left + ai.rows.get(row).cells.get(i).width) {
                    cell = ai.rows.get(row).cells.get(i);
                    page = ai.rows.get(row).pages[i];

					if (cell.start < 0) {
						for (int j = row - 1; j >= 0; j--) {
							if (ai.rows.get(j).cells.get(i).start >= 0) {
								cell = ai.rows.get(j).cells.get(i);
								page = ai.rows.get(j).pages[i];
								break;
							}
						}
					}

					break;
                }
            }

            if (cell != null) {
                /////////////////////////////////////////
                int x, y, areal, arealY;
                for (int z = 0; z < 2; z++) {

                    areal = (z != 0) ? (int) (EngBookMyType.AL_DEFAULT_TAP_AREAL * preferences.multiplexer) : 0;

                    for (int j = 0; j < page.countItems; j++) {
                        AlOneItem oi = page.items.get(j);

                        if (oi.isNote && (selection.selectMode != TAL_SCREEN_SELECTION_MODE.NONE || initialSelectMode != TAL_SCREEN_SELECTION_MODE.NONE))
                            continue;

                        y = oi.yDrawPosition;

                        arealY = areal;
                        if (j < page.countItems - 1) {
                            AlOneItem oi2 = page.items.get(j + 1);
                            if (y + oi.base_line_down + arealY > oi2.yDrawPosition + oi2.base_line_down)
                                arealY = oi2.yDrawPosition + oi2.base_line_down - y - oi.base_line_down;
                        }

                        if (y - oi.base_line_up - areal <= tapInfo.y &&
                                y + oi.base_line_down + arealY >= tapInfo.y) {

                            x = left + cell.left + oi.isLeft + oi.isRed;

                            tapInfo.tapWordStart = tapInfo.tapWordStop = -1;

                            for (i = 0; i < oi.count; i++) {

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
                                    tapInfo.isLocalLink = (oi.style[i] & AlStyles.STYLE_LINK) != 0;
                                    tapInfo.isImage = oi.text[i] == AlStyles.CHAR_IMAGE_E;
                                    tapInfo.pos = oi.pos[i];

                                    SymbolFound = i;

                                    if (AlUnicode.isChineze(oi.text[i]))
                                        break;
                                }

                                x += oi.width[i];
                            }

                            //
                            if (tapInfo.pos != AlTapInfo.TAP_ON_CLEAR_SPACE &&
                                    (selection.selectMode != TAL_SCREEN_SELECTION_MODE.NONE ||
                                            initialSelectMode != TAL_SCREEN_SELECTION_MODE.NONE)) {

                                if (selection.selectMode == TAL_SCREEN_SELECTION_MODE.END) {
                                    if (selection.selectPosition.x > tapInfo.tapWordStart && selection.selectPosition.x <= tapInfo.pos)
                                        selection.selectPosition.x = tapInfo.tapWordStart;
                                } else if (selection.selectMode == TAL_SCREEN_SELECTION_MODE.START) {
                                    if (selection.selectPosition.y < tapInfo.tapWordStop && selection.selectPosition.y >= tapInfo.pos)
                                        selection.selectPosition.y = tapInfo.tapWordStop;
                                }

                                int rt, rc, newPos = -1, ItemFound = j;

                                // calc start

                                boolean needContinueCalc;


                                if (tapInfo.tapWordStart != -1) {

                                    needContinueCalc = !oi.isArabic;
                                    if (needContinueCalc) {
                                        for (rc = SymbolFound - 1; rc >= 0; rc--) {
                                            if (oi.pos[rc] < 0) {
                                                needContinueCalc = false;
                                                break;
                                            }
                                        }
                                    }



                                    if (needContinueCalc) {
                                        for (rt = j - 1; rt >= 0; rt--) {
                                            oi = page.items.get(rt);

                                            if (oi.isArabic) {
                                                needContinueCalc = false;
                                                break;
                                            }

                                            if (oi.isNote)
                                                continue;

                                            if (oi.count > 0) {
                                                if (oi.pos[oi.count - 1] != SPECIAL_HYPH_POS) {
                                                    needContinueCalc = false;
                                                    break;
                                                }

                                                for (rc = oi.count - 2; rc >= 0; rc--) {
                                                    if (oi.pos[rc] == -1) {
                                                        needContinueCalc = false;
                                                        break;
                                                    }
                                                    newPos = oi.pos[rc];
                                                }
                                            }

                                            if (newPos != -1)
                                                tapInfo.tapWordStart = newPos;
                                            break;
                                        }
                                    }

                                }

                                // calc stop

                                newPos = -1;
                                if (tapInfo.tapWordStop != -1) {

                                    oi = page.items.get(ItemFound);
                                    needContinueCalc = !oi.isArabic;

                                    if (needContinueCalc) {
                                        for (rc = SymbolFound + 1; rc < oi.count; rc++) {
                                            if (oi.pos[rc] == -1) {
                                                needContinueCalc = false;
                                                break;
                                            }
                                        }
                                        needContinueCalc &= oi.count > 0 && oi.pos[oi.count - 1] == SPECIAL_HYPH_POS;
                                    }

                                    if (needContinueCalc) {
                                        for (rt = ItemFound + 1; rt < page.countItems; rt++) {

                                            oi = page.items.get(rt);

                                            if (oi.isArabic) {
                                                needContinueCalc = false;
                                                break;
                                            }

                                            if (oi.isNote)
                                                continue;

                                            for (rc = 0; rc < oi.count; rc++) {
                                                if (oi.pos[rc] < 0) {
                                                    needContinueCalc = false;
                                                    break;
                                                }
                                                newPos = oi.pos[rc];
                                            }

                                            if (newPos != -1)
                                                tapInfo.tapWordStop = newPos;
                                            break;
                                        }
                                    }
                                }
                            }
                            //
                            return tapInfo.pos != AlTapInfo.TAP_ON_CLEAR_SPACE;
                        }
                    }
                }

                /////////////////////////////////////////
            }

        }
        return true;
    }

	public synchronized AlTapInfo getInfoByLinkPos(int pos) {
		tapInfo.clearInfo();

		if (openState.getState() == AlBookState.OPEN) {
			tapInfo.pos = pos;
			tapInfo.isLocalLink = true;
			AlOneLink al;
			String link = format.getLinkNameByPos(tapInfo.pos, InternalConst.TAL_LINK_TYPE.LINK);
			if (link != null) {
				al = format.getLinkByName(link, true);
				if (al != null) {
					tapInfo.linkLocalPosition = al.positionS;
					tapInfo.isFootNote = (al.iType == 1) && (!tapInfo.isNote);
					tapInfo.link.append(link);
				} else {
					AlOneTable at = format.getTableByName(link);
					if (at != null) {
						tapInfo.isLocalLink = false;
						tapInfo.isTableLink = true;
						tapInfo.linkLocalPosition = at.start;
					} else {
						tapInfo.isLocalLink = false;
						tapInfo.isExtLink = true;
						tapInfo.link.append(link);
					}
				}
			} else {
				tapInfo.isLocalLink = false;
			}
		}

		return tapInfo;
	}

	public synchronized AlTextOnScreen getTextOnScreen() {
		textOnScreen.clear();

		if (openState.getState() == AlBookState.OPEN) {
			fillTextOnScreenOnePage(mpage[0][0], screen_parameters.marginL);
			if (profiles.twoColumnUsed)
				fillTextOnScreenOnePage(mpage[0][1], (screenWidth >> 1) + screen_parameters.marginR);
		}

		textOnScreen.needCorrectStart = false;
		textOnScreen.defaultResultForStart = -1;
		textOnScreen.needCorrectEnd = false;
		textOnScreen.defaultResultForEnd = -1;
		textOnScreen.numWordWithStartSelection = -1;
		textOnScreen.numWordWithEndSelection = -1;

		textOnScreen.clearBeforeNormalCall();

		return textOnScreen;
	}

	private void fillTextOnScreen(boolean s, int ps, boolean e, int pe) {
		textOnScreen.clear();

		s = textOnScreen.verifyStart(s, ps);
		e = textOnScreen.verifyEnd(e, pe);

		if (s && ps < 0)
			s = false;
		if (e && pe < 0)
			e = false;

		if (!s && !e)
			return;

		fillTextOnScreenOnePage(mpage[0][0], screen_parameters.marginL);
		if (profiles.twoColumnUsed)
			fillTextOnScreenOnePage(mpage[0][1], (screenWidth >> 1) + screen_parameters.marginR);

		textOnScreen.prepareBeforeCorrect0(s, ps, e, pe);
	}

	private void fillTextOnScreenOnePage(AlOnePage page, int margLeft) {
		int x, y, pos = -1;

		StringBuilder word_text = new StringBuilder();
		word_text.setLength(0);

		AlRect word_rect = new AlRect();
		AlRect link_rect = new AlRect();
		ArrayList<Integer> word_pos = new ArrayList<>();

		for (int j = 0; j < page.countItems; j++) {
			AlOneItem oi = page.items.get(j);

			if (oi.isNote)
				continue;

			y = oi.yDrawPosition;
			x = margLeft + oi.isLeft + oi.isRed;

			for (int i = 0; i < oi.count; i++) {
				if (pos == -1) {
					if ((oi.style[i] & AlStyles.STYLE_LINK) != 0) {
						pos = oi.pos[i];

						link_rect.x0 = x;
						link_rect.x1 = link_rect.x0 + oi.width[i];
						link_rect.y0 = y - oi.base_line_up;
						link_rect.y1 = link_rect.y0 + oi.base_line_down + oi.base_line_up;
					}
				} else {
					if ((oi.style[i] & AlStyles.STYLE_LINK) == 0) {
						textOnScreen.addLink(pos, link_rect);
						pos = -1;
					} else {
						link_rect.x1 += oi.width[i];
					}
				}

				if ((oi.style[i] & AlStyles.SL_IMAGE) != 0) {
					long style = oi.style[i];
					int widthImage = oi.width[i];
					AlOneImage ai = null;
					String link;
					int scale = (int) ((style & AlStyles.SL_COLOR_MASK) >> AlStyles.SL_COLOR_SHIFT);

					link = format.getLinkNameByPos(oi.pos[i], InternalConst.TAL_LINK_TYPE.IMAGE);
					if ((style & AlStyles.SL_IMAGE_OK) != 0) {
						if (link != null)
							ai = format.getImageByName(link);

						if (ai != null) {
							AlBitmap b = images.getImage(ai, scale);
							if (b != null) {
								int th = ai.height;
								int tw = ai.width;
								for (int k = 0; k < scale; k++) {
									th >>= 1;
									tw >>= 1;
								}

								final int w;
								final int h;
								final float f = (float) widthImage / tw;
								if (f <= 1.02f && f >= 0.99f) {
									w = tw;
									h = th;
								} else {
									w = (int) (tw * f);
									h = (int) (th * f);
								}

								AlRect rect = new AlRect();
								rect.set(x, y - h, x + w, y);
								textOnScreen.addImage(oi.pos[i], rect, b.bmp.copy(Bitmap.Config.ARGB_8888, false));
							}
						}
					}
				}

				if (oi.text[i] <= 0x20 || oi.pos[i] < 0) {
					textOnScreen.addText(word_text, word_rect, word_pos);

					if (oi.text[i] == AlStyles.CHAR_ROWS_E) {
						if (pos != -1) {
							textOnScreen.addLink(pos, link_rect);
							pos = -1;
						}
						fillTextOnScreenTable(oi.pos[i], margLeft);
						continue;
					}
				} else {

					if ( AlUnicode.isChineze(oi.text[i]) || (word_text.length() == 1 && AlUnicode.isChineze(word_text.charAt(0))))
						textOnScreen.addText(word_text, word_rect, word_pos);

					if (word_text.length() == 0) {
						word_rect.x0 = word_rect.x1 = x;
						word_rect.y0 = y - oi.base_line_up;
						word_rect.y1 = word_rect.y0 + oi.base_line_down + oi.base_line_up;

						word_pos.clear();
					}

					word_rect.x1 += oi.width[i];
					word_text.append(oi.text[i]);
					word_pos.add(oi.pos[i]);
				}

				x += oi.width[i];
			}

			textOnScreen.addText(word_text, word_rect, word_pos);
			if (pos != -1) {
				textOnScreen.addLink(pos, link_rect);
				pos = -1;
			}
		}
	}

	private void fillTextOnScreenTable(int sellPos, int margLeft) {
		// get Table
		AlOneTable ai = null;
		String link, rows;

		link = format.getLinkNameByPos(sellPos, InternalConst.TAL_LINK_TYPE.ROW);
		if (link == null)
			return;
		int i = link.indexOf(':');
		if (i == -1)
			return;

		rows = link.substring(i + 1);
		link = link.substring(0, i);

		int table = InternalFunc.str2int(link, 10);
		int row = InternalFunc.str2int(rows, 10);

		ai = format.getTableByNum(table);
		if (ai != null) {
			// get Cell
			AlOneTableCell cell = null;
			AlOnePage page = null;
			for (i = 0; i < ai.rows.get(row).cell_accepted; i++) {
				cell = ai.rows.get(row).cells.get(i);
				page = ai.rows.get(row).pages[i];
				if (cell != null && page != null)
					fillTextOnScreenOnePage(page, margLeft + cell.left);
			}
		}
	}

    private boolean getPositionInPageByXY(AlOnePage page, int margLeft, TAL_SCREEN_SELECTION_MODE initialSelectMode) {

        int areal = (int) (EngBookMyType.AL_DEFAULT_TAP_AREAL * preferences.multiplexer);
		int arealY = areal, x, y, SymbolFound = -1;

		if (selection.selectMode == TAL_SCREEN_SELECTION_MODE.NONE && initialSelectMode == TAL_SCREEN_SELECTION_MODE.NONE) {
			for (int j = 0; j < page.countItems; j++) {
				AlOneItem oi = page.items.get(j);

				y = oi.yDrawPosition;

				if (y - oi.base_line_up - areal <= tapInfo.y &&
						y + oi.base_line_down /*+ arealY*/ >= tapInfo.y) {

					x = margLeft + oi.isLeft + oi.isRed;

					tapInfo.tapWordStart = tapInfo.tapWordStop = -1;

					for (int i = 0; i < oi.count; i++) {
						if ((oi.style[i] & AlStyles.STYLE_LINK) != 0 &&
							oi.pos[i] >= 0 &&
							x - areal <= tapInfo.x &&
							x + oi.width[i] + areal >= tapInfo.x) {

							tapInfo.isNote = oi.isNote;
							tapInfo.isLocalLink = (oi.style[i] & AlStyles.STYLE_LINK) != 0;
							tapInfo.isImage = oi.text[i] == AlStyles.CHAR_IMAGE_E;
							tapInfo.pos = oi.pos[i];
							tapInfo.tapWordStart = tapInfo.tapWordStop = oi.pos[i];

							return true;
						}

						x += oi.width[i];
					}
				}
			}
		}

        for (int z = 0; z < 2; z++) {

            areal = (z != 0) ? (int) (EngBookMyType.AL_DEFAULT_TAP_AREAL * preferences.multiplexer) : 0;

            for (int j = 0; j < page.countItems; j++) {
            	AlOneItem oi = page.items.get(j);

                if (oi.isNote && (selection.selectMode != TAL_SCREEN_SELECTION_MODE.NONE || initialSelectMode != TAL_SCREEN_SELECTION_MODE.NONE))
                    continue;

            	y = oi.yDrawPosition;

				arealY = areal;
				if (j < page.countItems - 1) {
					AlOneItem oi2 = page.items.get(j + 1);
					if (y + oi.base_line_down + arealY > oi2.yDrawPosition + oi2.base_line_down)
						arealY = oi2.yDrawPosition + oi2.base_line_down - y - oi.base_line_down;
				}

            	if (y - oi.base_line_up - areal <= tapInfo.y && 
            		y + oi.base_line_down + arealY >= tapInfo.y) {
            		
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
                            tapInfo.isLocalLink = (oi.style[i] & AlStyles.STYLE_LINK) != 0;
                            tapInfo.isImage = oi.text[i] == AlStyles.CHAR_IMAGE_E;
                            tapInfo.pos = oi.pos[i];

                            if (oi.text[i] == AlStyles.CHAR_ROWS_E) {
                                if (areal > 0) {
                                    tapInfo.pos = AlTapInfo.TAP_ON_CLEAR_SPACE;
                                } else
                                    getPositionInTableByXY(tapInfo, initialSelectMode, x, y);

                                return tapInfo.pos != AlTapInfo.TAP_ON_CLEAR_SPACE;
                            }

							SymbolFound = i;

							if (AlUnicode.isChineze(oi.text[i]))
								break;
            			}
            			
            			x += oi.width[i];
            		}

					//
					if (tapInfo.pos != AlTapInfo.TAP_ON_CLEAR_SPACE &&
							(selection.selectMode != TAL_SCREEN_SELECTION_MODE.NONE ||
							 initialSelectMode != TAL_SCREEN_SELECTION_MODE.NONE)) {

						if (selection.selectMode == TAL_SCREEN_SELECTION_MODE.END) {
							if (selection.selectPosition.x > tapInfo.tapWordStart && selection.selectPosition.x <= tapInfo.pos)
								selection.selectPosition.x = tapInfo.tapWordStart;
						} else
                        if (selection.selectMode == TAL_SCREEN_SELECTION_MODE.START) {
							if (selection.selectPosition.y < tapInfo.tapWordStop && selection.selectPosition.y >= tapInfo.pos)
								selection.selectPosition.y = tapInfo.tapWordStop;
						}

						int rt, rc, newPos = -1, ItemFound = j;

						// calc start

						boolean	needContinueCalc;


						if (tapInfo.tapWordStart != -1) {

							needContinueCalc = !oi.isArabic;
							if (needContinueCalc) {
								for (rc = SymbolFound - 1; rc >= 0; rc--) {
									if (oi.pos[rc] < 0) {
										needContinueCalc = false;
										break;
									}
								}
							}

							for (int zrev = 0; zrev < 2; zrev++) {
								if (zrev == 1) {
									if (!profiles.twoColumnUsed || page != mpage[0][1])
										break;
									j = mpage[0][0].countItems;
								}

								if (needContinueCalc) {
									for (rt = j - 1; rt >= 0; rt--) {
										oi = zrev == 1 ? mpage[0][0].items.get(rt) : page.items.get(rt);

										if (oi.isArabic) {
											needContinueCalc = false;
											break;
										}

										if (oi.isNote)
											continue;

										if (oi.count > 0) {
											if (oi.pos[oi.count - 1] != SPECIAL_HYPH_POS) {
												needContinueCalc = false;
												break;
											}

											for (rc = oi.count - 2; rc >= 0; rc--) {
												if (oi.pos[rc] == -1) {
													needContinueCalc = false;
													break;
												}
												newPos = oi.pos[rc];
											}
										}

										if (newPos != -1)
											tapInfo.tapWordStart = newPos;
										break;
									}
								}
							}
						}

						// calc stop

						newPos = -1;
						if (tapInfo.tapWordStop != -1) {

							oi = page.items.get(ItemFound);
							needContinueCalc = !oi.isArabic;

							if (needContinueCalc) {
								for (rc = SymbolFound + 1; rc < oi.count; rc++) {
									if (oi.pos[rc] == -1) {
										needContinueCalc = false;
										break;
									}
								}
								needContinueCalc &= oi.count > 0 && oi.pos[oi.count - 1] == SPECIAL_HYPH_POS;
							}

							if (needContinueCalc) {
								for (rt = ItemFound + 1; rt < page.countItems; rt++) {

									oi = page.items.get(rt);

									if (oi.isArabic) {
										needContinueCalc = false;
										break;
									}

									if (oi.isNote)
										continue;

									for (rc = 0; rc < oi.count; rc++) {
										if (oi.pos[rc] < 0) {
											needContinueCalc = false;
											break;
										}
										newPos = oi.pos[rc];
									}

									if (newPos != -1)
										tapInfo.tapWordStop = newPos;
									break;
								}

								if (needContinueCalc) {
									if (profiles.twoColumnUsed && page == mpage[0][0]) {
										oi = mpage[0][1].items.get(0);
										if (!oi.isArabic) {
											for (rc = 0; rc < oi.count; rc++) {
												if (oi.pos[rc] < 0)
													break;
												newPos = oi.pos[rc];
											}
											if (newPos != -1)
												tapInfo.tapWordStop = newPos;
										}
									}
								}
							}
						}
					}
					//
            		return tapInfo.pos != AlTapInfo.TAP_ON_CLEAR_SPACE;
            	}
            }
        }

        return false;
    }
	/**
	 * Получение исходника изображения, по которому был тап.
	 * @return null если ошибка
	 */
	public synchronized AlSourceImage getImageSource(String imageName) {
		if (preferences.isASRoll)
			return null;

		if (openState.getState() == AlBookState.OPEN) {
			AlOneImage a = format.getImageByName(imageName);
			if (a != null) {
				if (a.needScan) {
					images.initWork(a, format);
					images.scanImage(a);
				}

				AlSourceImage srcImage = new AlSourceImage();
				srcImage.width = a.width;
				srcImage.height = a.height;
				srcImage.data = a.data;
				return srcImage;
			}
		}
		return null;
	}

    /**
	 * Получение исходника таблицы, по которой был тап.
	 * @return null если ошибка или выделенный текст
	 */
	public synchronized String getTableSource(int address) {
        if (preferences.isASRoll)
            return null;

		if (openState.getState() == AlBookState.OPEN) {
			return format.getTableSource(address);
		}
		return null;
	}
    /**
     * Получение выделенного на странице (страницах) текста.
     * @forDictionary - нужно ли обрабатывать строку выделеннного текста для отправки в словарь.
     * Если режим выделения DICTIONARY - значение параметра не имеет значения
     * @return null если ошибка или выделенный текст
     */
	public synchronized String getSelectedText(boolean forDictionary) {
        if (preferences.isASRoll)
            return null;

        if (openState.getState() == AlBookState.OPEN) {
            switch (selection.selectMode) {
                case DICTIONARY:
                    return format.getDictWordByPos(selection.selectPosition.x, selection.selectPosition.y);
                case END:
                case START:
                	if (!selection.tooManySelect)
                		return format.getTextByPos(selection.selectPosition.x, selection.selectPosition.y, forDictionary);
                	break;
            }
        }
        return null;
	}

	public synchronized String getTextByPosition(int start, int end, boolean forDictionary) {
		if (openState.getState() == AlBookState.OPEN) {
			return format.getTextByPos(start, end, forDictionary);
		}
		return null;
	}

	/**
     * Получение текста сноски.
	 * @link - имя сноски
     * @return null если ошибка или выделенный текст
     */
    public synchronized String getFootNoteText(String link) {
        if (preferences.isASRoll)
            return null;

        if (openState.getState() == AlBookState.OPEN) {
            AlOneLink al;
            if (link != null) {
                al = format.getLinkByName(link, true);
                if (al != null && al.iType == 1)
                    return format.getTextByPos(al.positionS, al.positionE, false);
            }
        }
        return null;
    }

    /**
     * Определение в каком режиме выделения находится библиотека в текущий момент
     * @return текущий режим выделения
     */
	public synchronized EngBookMyType.TAL_SCREEN_SELECTION_MODE getSelectionMode() {

        return selection.selectMode;
	}

	public synchronized AlPoint getSelectionRange() {
		if (openState.getState() == AlBookState.OPEN) {
			switch (selection.selectMode) {
				case START:
				case END:
				case DICTIONARY:
					return selection.selectPosition;
			}
		}
		return null;
	}

	public synchronized int setSelectionRange(int selStart, int selStop) {
		if (openState.getState() == AlBookState.OPEN) {
			switch (selection.selectMode) {
				case START:
				case END:
				case DICTIONARY:
					if (selStart < 0 || selStop >= format.getSize())
						return TAL_RESULT.ERROR;

					boolean isUpdate = (selection.selectPosition.x != selStart || selection.selectPosition.y != selStop);
					if (isUpdate) {
						selection.shtampSelectRequred++;
						selection.selectPosition.x = selStart;
						selection.selectPosition.y = selStop;
						return returnOkWithRedraw();
					}
					return TAL_RESULT.OK;
			}
		}
		return TAL_RESULT.ERROR;
	}

	private void getTextRectInPage(AlOnePage page, int textStart, int textStop, AlRect rect, int margLeft) {
		if (page.start_position > textStop || page.end_position < textStart)
			return;

		int x, y;
		for (int j = 0; j < page.countItems; j++) {
			AlOneItem oi = page.items.get(j);
			if (oi.isNote)
				continue;

			y = oi.yDrawPosition;
			x = margLeft + oi.isLeft + oi.isRed;

			for (int i = 0; i < oi.count; i++) {
				if (rect.x0 == -1 && oi.pos[i] >= textStart) {
					rect.x0 = x;
					rect.y0 = y - oi.base_line_up;
				} else
				if (rect.x0 != -1 && oi.pos[i] >= 0 && oi.pos[i] <= textStop) {
					rect.x1 = x;
					rect.y1 = y + oi.base_line_down;
				} else
				if (oi.pos[i] > textStop)
					return;

				x += oi.width[i];
			}
		}
	}

	public synchronized int getTextRect(int textStart, int textStop, AlRect rect) {
		if (openState.getState() == AlBookState.OPEN || rect == null ||
				textStart > textStop) {

			rect.x0 = rect.y0 = -1;
			rect.x1 = rect.y1 = -1;

			getTextRectInPage(mpage[0][0], textStart, textStop, rect, screen_parameters.marginL);
			if (profiles.twoColumnUsed)
				getTextRectInPage(mpage[0][1], textStart, textStop, rect, (screenWidth >> 1) + screen_parameters.marginR);

			return TAL_RESULT.OK;
		}
		return TAL_RESULT.ERROR;
	}

    /**
     * установка нового режима выделения. В случае, если логика работы не позволяет инициировать заданный режим выделения -
     новый режим устанвлен не будет. Т.е. при вызове необходимо проверять результат работы с методом. Если
     задаваемое и возвращаемое значения не совпадают - значит уставка не прошла.
     * @param newMode - задаваемый режим выделения @see TAL_SCREEN_SELECTION_MODE
     * @return - текущий режим выделения. В случае успешного вызова - должен быть равен newMode
     */
	public synchronized EngBookMyType.TAL_SCREEN_SELECTION_MODE setSelectionMode(EngBookMyType.TAL_SCREEN_SELECTION_MODE newMode) {
        if (preferences.isASRoll)
            return getSelectionMode();

		if (openState.getState() == AlBookState.OPEN) {
			
			if (newMode == EngBookMyType.TAL_SCREEN_SELECTION_MODE.CLEAR)
				newMode = EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE;
			
			if (newMode == selection.selectMode)
				return selection.selectMode;
			
			if (newMode == EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE && 
					selection.selectMode == EngBookMyType.TAL_SCREEN_SELECTION_MODE.CLEAR)
				return EngBookMyType.TAL_SCREEN_SELECTION_MODE.NONE;
			
			int start, stop;
            int NO_SELECTED = -100;
			switch (selection.selectMode) {
			case NONE:
				switch (newMode) {
				case DICTIONARY:
					start = stop = mpage[0][0].start_position;

					AlOneItem oi = null;
					//
					if (mpage[0][0].countItems > 0 && mpage[0][0].items.get(0).count == 1 && mpage[0][0].items.get(0).text[0] == AlStyles.CHAR_ROWS_E) {
						int t = mpage[0][0].items.get(0).table_start;
						if (t != -1) {
							AlOneTable table = format.getTableByNum(t);
							if (table != null) {
								t = table.rows.get(mpage[0][0].items.get(0).table_row).start;
								if (t < start) {
									start = stop = t;

									boolean flagFound = false;
									for (int i = mpage[0][0].items.get(0).table_row; i < table.rows.size(); i++) {
										if (flagFound)
											break;
										for (int j = 0; j < table.rows.get(i).cells.size(); j++) {
											if (table.rows.get(i).cells.get(j).start >= 0) {
												if (table.rows.get(i).pages[j].countItems >= 0 && table.rows.get(i).pages[j].items.get(0).count > 0) {
													oi = table.rows.get(i).pages[j].items.get(0);
													flagFound = true;
													break;
												}
											}
										}
									}
								}
							}
						}
					}

					if (oi == null)
						oi = mpage[0][0].items.get(0);

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
					start = mpage[0][0].start_position;
					if (start < mpage[0][0].items.get(0).pos[0])
						start = mpage[0][0].items.get(0).pos[0];
					//
					if (mpage[0][0].countItems > 0 && mpage[0][0].items.get(0).count == 1 && mpage[0][0].items.get(0).text[0] == AlStyles.CHAR_ROWS_E) {
						int t = mpage[0][0].items.get(0).table_start;
						if (t != -1) {
							AlOneTable table = format.getTableByNum(t);
							if (table != null) {
								t = table.rows.get(mpage[0][0].items.get(0).table_row).start;
								if (t < start)
									start = t;
							}
						}
					}
					//
					stop = mpage[0][0].end_position - 1;
					if (profiles.twoColumnUsed && mpage[0][1].countItems > 0)
						stop = mpage[0][1].end_position - 1;
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
			selection.selectPosition.set(start, stop, -1);
			selection.tooManySelect = stop - start > EngBookMyType.AL_MAXIMUM_SELECT_BLOCK_SIZE;
			returnOkWithRedraw();
		}
	}

	public synchronized void getScrollShift(final boolean fromCurrentPage, int shift, AlIntHolder outShift, AlIntHolder outPos, boolean nearest) {
		if (engOptions.externalBitmap != null)
			return;

		AlOnePage page = fromCurrentPage ? mpage[0][0] : mpage[2][0];

		int resultPos = fromCurrentPage ? page.end_position : page.start_position;
		int tmpDiff = Math.abs(outShift.value - shift);
		int tmpShift, resultShift = outShift.value;

		if (!fromCurrentPage) {
			if (shift > outShift.value - bmp[2].freeSpaceAfterPage ) {
				outPos.value = resultPos;
				outShift.value = shift;
				return;
			}

			shift = outShift.value - bmp[2].freeSpaceAfterPage - shift;
			tmpDiff = outShift.value - bmp[2].freeSpaceAfterPage;
		}

		for (int i = 0; i < page.countItems; i++) {
			AlOneItem oi = page.items.get(i);

			if (!oi.isNote) {
				tmpShift = oi.yDrawPosition - oi.base_line_up - oi.height - screen_parameters.marginT;
				if (tmpShift < 0)
					tmpShift = 0;

				int t = Math.abs(shift - tmpShift);
                if (nearest) {
                    if (t < tmpDiff) {
                        tmpDiff = t;
                        resultShift = tmpShift;
                        resultPos = (i == 0) ? page.start_position : getOverItemStartPos(oi);
                    }
                } else
                if (fromCurrentPage) {
                    if (tmpShift > shift && t < tmpDiff) {
                        tmpDiff = t;
                        resultShift = tmpShift;
                        resultPos = (i == 0) ? page.start_position : getOverItemStartPos(oi);
                    }
                } else {
                    if (tmpShift < shift && t < tmpDiff) {
                        tmpDiff = t;
                        resultShift = tmpShift;
                        resultPos = (i == 0) ? page.start_position : getOverItemStartPos(oi);
                    }
                }
			}
		}

		if (!fromCurrentPage) {
			outShift.value = outShift.value - bmp[2].freeSpaceAfterPage - resultShift;
		} else {
			outShift.value = resultShift;
		}

		outPos.value = resultPos;
	}

    private int startStopAS() {
        if (preferences.isASRoll) {

		} else {

		}

        preferences.isASRoll = !preferences.isASRoll;
        shtamp.value++;

		return returnOkWithRedraw();
	}

    public synchronized int setAutoScrollMode(boolean mode) {
        if (openState.getState() != AlBookState.OPEN) {
            return TAL_RESULT.ERROR;
        }

        if (mode == preferences.isASRoll)
            return TAL_RESULT.OK;

        startStopAS();

        return returnOkWithRedraw();
    }

	private char convertChineseText(char text) {
		switch (chineseConvert) {
			case SIMPLIFIED_TO_TRADITIONAL:
				return ChineseTextUtils.convertToTraditional(text);
			case TRADITIONAL_TO_SIMPLIFIED:
				return ChineseTextUtils.convertToSimplified(text);
			default:
				return text;
		}
	}

	private char[] convertChineseText(char[] text) {
		char[] result = text;
		switch (chineseConvert) {
			case SIMPLIFIED_TO_TRADITIONAL:
				result = text.clone();
				ChineseTextUtils.convertToTraditional(result);
				break;
			case TRADITIONAL_TO_SIMPLIFIED:
				result = text.clone();
				ChineseTextUtils.convertToSimplified(result);
			default:
				break;
		}
		return result;
	}

	private boolean isBookOpened() {
		return openState.getState() == AlBookState.OPEN || openState.getState() >= AlBookState.PROCESS0;
	}

}
