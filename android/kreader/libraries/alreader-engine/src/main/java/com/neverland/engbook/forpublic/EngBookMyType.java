package com.neverland.engbook.forpublic;

public class EngBookMyType {

	/**
	@warning - не изменять
	*/
	public static final int AL_WORD_LEN =	384;
	
	public static final char AL_ROOT_WRONGPATH	= '\\';
	public static final char AL_ROOT_RIGHTPATH = '/';
	public static final String AL_ROOT_RIGHTPATH_STR = "/";
	public static final int  AL_MAX_FILENAME_LENGTH = 255;
	public static final char AL_FILENAMES_SEPARATOR = '\u0001';
	public static final int  AL_MAX_PARAGRAPH_LEN = 16384;

	//public static final int  AL_DEFAULT_PAGESIZE = 2048;

	/**
	граница "неоднозначности" при тапе, когда палец несовсем попадает в слово или ссылку.
	Подобрано эмпирически, корректировка по дпи проведится внутри библиотеки, значение задается для дпи 160
	*/
    public static final int  AL_DEFAULT_TAP_AREAL = 15;

	/**
	максимальный блок текста, который будет возвращен при выделении. Если на экране выделено больше символов - функция
	tal_getSelectedText вернет NULL
	*/
	public static final int 	AL_MAXIMUM_SELECT_BLOCK_SIZE = 32768;




    //////////////////////////////////////////////

	/*!
	способ отображения таблиц в fb3, fb3, epub, mobi
	*/
	public enum TAL_TABLEMODE {
		INTERNAL,
		EXTERNAL,
		BOTH,
		NONE,
	}

	public enum TAL_BOOKMARK_TYPE {
		MARK,
		BOOKMARK,
		CITE,
	}

	public enum TAL_BOOKMARK_COLOR{
		NONE(0),
		RED(2),
		YELLOW(3),
		BLUE(4),
		GREEN(5),
		PURPLE(6),
		UNDERLINE(7);

		public int numVal;
		TAL_BOOKMARK_COLOR(int numVal) {
			this.numVal = numVal;
		}
	}

	/**
	 * Используется в методе @see gotoPosition для определения необходмого способа перемещения позиции чтения в
	 текущей книге
	 */
	public enum TAL_GOTOCOMMAND {
		NEXTPAGE(1),
		PREVPAGE(2),
		LASTPAGE(3),
		FIRSTPAGE(4),
		POSITION(5),
		POSITION_WITH_CORRECT(6);
		
		private int numVal;
		TAL_GOTOCOMMAND(int numVal) {
	        this.numVal = numVal;
	    }
	}

	/**
	 * используется в методе @see getPageBitmap. Параметр говорит какую страницу текста необходимо сгенерировать
	 в текущий момент
	 */
	public enum TAL_PAGE_INDEX { 
		PREV,
		CURR,
		NEXT,		
	}

	/**
	 * Один из параметров инициализации библиотеки (@see AlEngineOptions). Конкретные значения базируются
	 на принятой в андроиде градации разрешений экранов. В других системах -
	 TAL_SCREEN_DPI_120, TAL_SCREEN_DPI_160, TAL_SCREEN_DPI_240 - в этих режимах картинки подаются на экран "как есть"
	 TAL_SCREEN_DPI_320 - картинки на экране увеличиваются в два раза, естественно при наличии места на экране. Т.е. картинка
	 размером 100 на 10 для этого значения дпи будет отображена на экране с размерами 200 на 20
	 TAL_SCREEN_DPI_480  - увеличение в 3 раза (картинка из предыдущего примера - 300 на 30)
	 TAL_SCREEN_DPI_640  - увеличение в 4 раза (картинка из предыдущего примера - 400 на 40)
	 */
	public enum TAL_SCREEN_DPI { 
		TAL_SCREEN_DPI_120(120),
		TAL_SCREEN_DPI_160(160),
		TAL_SCREEN_DPI_240(240),
		TAL_SCREEN_DPI_320(320),
		TAL_SCREEN_DPI_480(480),
		TAL_SCREEN_DPI_640(640);
		
		private int numVal;
		TAL_SCREEN_DPI(int numVal) {
	        this.numVal = numVal;
	    }
	}

	/**
	 * режим подсчета страниц в книге
	 */
	public enum TAL_SCREEN_PAGES_COUNT {
		/**
		страница - это заданный объем символов. В этом режиме возможно использование
		размера страницы по умолчанию, произвольного размера и автоматического размера страницы, основанного на
		приблизительном количества символов, помещающихся на страницы. Задается  значением pageSize4Use структуры AlEngineOptions при
		инициализации параметров работы библиотеки
		Самые быстрый и эффективный метод подсчета - не требует дополнительных временных затрат
		@warning - в этом режиме возможен вариант автоматического подбора размера страницы
		*/
		SIZE,
		/**
		for internal only
		*/
		SCREEN,
		/**
		Второй вариант автоматического приблизительного подсчета количества страниц в книге. после открытия книги просчитываются первые N
		реальных страниц в книге, после чего высчитывается средний размер страницы, который и используется в дальнейшем. Просчет страниц запускается при
		любом изменениии параметров страницы (изменение размеров текста, полей, размеров окна и т.д.)
		*/
		AUTO,
	}

	/**
	 * Возможные режимы выделения текста на странице. TAL_SCREEN_SELECTION_MODE_CLEAR - не должен
     * вызываться из основной программы, это внутреннее значение
	 */
	public enum TAL_SCREEN_SELECTION_MODE {
        /**
	    режим выделения выключен
	    */
		NONE,
        /**
        изменяется начало области выделения
        */
		START,
        /**
        изменяется конец области выделения
        */
		END,
        /**
        режим словаря
        */
		DICTIONARY,

		CLEAR,
	}

	public enum TAL_THREAD_TASK {
		OPENBOOK(10),
		OPENBOOK_FULLAFTERPARTIAL(11),
		CREATEDEBUG(12),
		FIND(13),
		NEWCALCPAGES(14);
		
		private int numVal;
		TAL_THREAD_TASK(int numVal) {
	        this.numVal = numVal;
	    }
	}

	/**
	 * параметр используемый для оповещения приложения о окончании фоновой операции или об изменении
     состояния библиотеки и, как правило, требующий инициализации перерисовки основной страницы книги
	 */
	public enum TAL_NOTIFY_ID {
		NEEDREDRAW(1),
		STARTTHREAD(2),
		STOPTHREAD(3),
		OPENBOOK(10),
		OPENBOOK_FULLAFTERPARTIAL(11),
		CREATEDEBUG(12),
		FIND(13),
		NEWCALCPAGES(14);
		
		private int numVal;
		TAL_NOTIFY_ID(int numVal) {
	        this.numVal = numVal;
	    }
	}

	/**
	 * результат работы фоновой операции и оповещении основного окна программы
	 */
	public enum TAL_NOTIFY_RESULT {
		OK,
		ERROR,
		EXCEPT
	}

	/**
	 * перечисление языков для работы модуля переносов в тексте.
	 */
	public enum TAL_HYPH_LANG {
		NONE,
		ENGLISH,
		RUSSIAN,
		ENGRUS,		
	}

    /**
    перечисление не имеет отношения к управляющему приложению
    */
	public enum TAL_FILE_TYPE {
		TXT,
		ZIP,
		DOC,
		EPUB,
		PDB,
		MOBI,
		PDBUnk,
		DOCX,
		ODT,
		FB3,
		CBZ,
		RAR,
		RARUnk,
		JEB,
		CHM,
	}
	
}
