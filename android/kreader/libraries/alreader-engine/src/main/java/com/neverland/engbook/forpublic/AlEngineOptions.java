package com.neverland.engbook.forpublic;

import android.content.Context;

import com.neverland.engbook.forpublic.EngBookMyType.TAL_HYPH_LANG;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_SCREEN_PAGES_COUNT;

/**
 * параметры инициализации библиотеки. Все (кроме языка переносов) перечисленные здесь значения не могут быть изменены ПОСЛЕ инициализации
 */
public class AlEngineOptions {
	public static final int AL_DEFAULT_PAGESIZE0 =	1024;
	public static final int AL_USEAUTO_PAGESIZE  = 	-1;
	public static final int AL_USEDEF_PAGESIZE	 = 	0;

	public static final int CSS_SUPPORT_ALL = 0x0fffffff;

	// always use
	// visibility hyphens decoratio-line decoration page-break-before page-break-after vertical-align font-style font-weight white-space

	// font-size
	public static final int CSS_SUPPORT_FONT_SIZE = 				0x00000002;
	// font-family [monospace, fantasy, all other]
	public static final int CSS_SUPPORT_FONT_FAMILY = 				0x00000004;
	// margin-top margi-bottom margin
    public static final int CSS_SUPPORT_VERTICAL_MARGINS = 			0x00000100;
	// margin-right margin-left margin
	public static final int CSS_SUPPORT_HORIZONTAL_MARGINS = 		0x00000200;
	// text-indent
	public static final int CSS_SUPPORT_TEXT_INDENT = 				0x00000400;
	// align alreader-align-poem
	public static final int CSS_SUPPORT_JUSTIFY = 					0x00001000;
	// text-shadow letter-spacing
	public static final int CSS_SUPPORT_ANY_JUNK = 					0x00100000;



	public int									magic = 0x1812;
	/**
	 * способ подсчета количества страниц
	 */
	public TAL_SCREEN_PAGES_COUNT				useScreenPages = TAL_SCREEN_PAGES_COUNT.SIZE;
	/**
	 * коэффициент масштабирования для текста
	 */
	public float								multiplexer = 1.0f; //

	/**
	 * каталог, откуда библиотека считывает шрифты
	 */
	public String								font_catalog = null; // not to use for win32
	/**
	 * каталог, откуда библиотека считывает шрифты
	 */
	public String[]								font_catalogs_addon = null; // not to use for win32
	/*
	 * список шрифтов из ресурсов
	 */
	public AlResourceFont						font_resource[] = null;
	/**
	 * язык переносов, который использует библиотека по умолчанию
	 */
	public TAL_HYPH_LANG						hyph_lang = TAL_HYPH_LANG.NONE;
	/**
	 *
	 */
	public Context								appInstance;
	/**
	 * некоторые ньюансы форматирования текста на странице - устанавливать в TRUE только если локаль устройства - китайская
	 */
	public boolean								chinezeSpecial = false;
	/**
	 * используемый размер странцы в случае если useScreenPages равно TAL_SCREEN_PAGES_COUNT_BY_SIZE
	 pageSize4Use = AL_USEAUTO_PAGESIZE - вариант автоматического подсчета размера страницы
	 */
	public int									pageSize4Use = AL_USEDEF_PAGESIZE;

	public int									value2CalcMargins = 0;
	/**
	 * если сноски показываются только во всплывающем окошке - опция скрывает на странице сноски (ASIDE epub)
	 */
	public boolean 								onlyPopupFootnote = false;
	/**
	 * по умолчанию линки подчеркиваются
	 */
	public boolean 								drawLinkInternal = true;

	public EngBookMyType.TAL_TABLEMODE			tableMode = EngBookMyType.TAL_TABLEMODE.INTERNAL;


	public boolean 								runInOneThread = false;
	public boolean 								syncLoading = true;

	public EngSelectionCorrecter				selectCorrecter = null;
	public AlBitmap 							externalBitmap = null;

	public int									notesItemsOnPageCount = 0;

	public String								defaultFB2 = null;
	public String								defaultFB3 = null;
	public String								defaultHTML = null;
	public String								defaultMOBI = null;
	public String								defaultEPUB = null;

	public int									cssSupportLevel = CSS_SUPPORT_ALL;
	public String								defaultAllCSS = null;
}
