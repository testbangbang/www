package com.neverland.engbook.forpublic;

/**
 * параметры отображения текста книги
 */
public class AlPublicProfileOptions {
	public static final int DEF_MARGIN = 5;
	public static final int MIN_MARGIN = 0;
	public static final int MAX_MARGIN = 30;

	public static final int	BACK_TILE_NONE = 0;
	public static final int	BACK_TILE_X = 1;
	public static final int	BACK_TILE_Y = 2;
    public static final int	BACK_RESERVED = 0xf0;

	/**
	 * использовать две колонки текста
	 */
	public boolean				twoColumn = false;
	/**
	 * использовать "жирный" текст
	 */
	public boolean				bold = false;
	/**
	 * размер шрифта. В случае отправки некорректного значения - функция setNewProfile заполнит поле правильным вариантом
	 */
	public int					font_size = 18;
	/**
	 * имя шрифта для основного текста
	 */
	public String				font_name = null;
	/**
	 * имя шрифта для участков кода, если null - используется шрифт текста
	 */
	public String				font_monospace = null;
	public String				font_title = null;
	/**
	 * картинка для фона
	 */
	public AlBitmap				background = null;
	/**
	 * режим растягивания картинки для фона
	 */
	public int					backgroundMode = BACK_TILE_X + BACK_TILE_Y;
	/**
	 * левый отступ страницы в процентах
	 */
	public int					marginLeft = DEF_MARGIN;
	/**
	 * правый отступ страницы в процентах
	 */
	public int					marginRight = DEF_MARGIN;
	/**
	 * верхний отступ страницы в процентах
	 */
	public int					marginTop = DEF_MARGIN;
	/**
	 * нижний отступ страницы в процентах
	 */
	public int					marginBottom = DEF_MARGIN;
	/**
	 * цвет текста
	 */
	public int					colorText = 0xffffff;
	/**
	 * цвет заголовков
	 */
	public boolean				overrideColorTitle = false;
	public int					colorTitle = 0xffffff;
	/**
	 * цвет фона (особенно следует учитывать в случае если картинка фона имеет полупрозрачность)
	 */
	public int					colorBack = 0x000000;

	public boolean				overrideColorBold = false;
	public int					colorBold = 0xffffff;
	public boolean				overrideColorItalic = false;
	public int					colorItalic = 0xffffff;
	public boolean				overrideColorBoldItalic = false;
	public int					colorBoldItalic = 0xffffff;
	public boolean				overrideColorCode = false;
	public int					colorCode = 0xffffff;

	/**
	 * межстрочное расстояние. В случае отправки некорректного значения - функция setNewProfile заполнит поле правильным вариантом
	 */
	public int					interline = 0;

	public boolean				indentParagraph = true;

	/**
	 * режим свитка. Работает только в случае, если AlEngineOptions.useScreenPages == TAL_SCREEN_PAGES_COUNT.SIZE
	 */
	public boolean				specialModeRoll = false;
	public boolean				specialModeMadRoll = false;
    /**
     * секции (главы) с новой страницы. Не работает в режиме свитка
     */
    public boolean		        sectionNewScreen = false;
    /**
     * выравнивание простого текста по ширине
     */
    public boolean		        justify = true;
    /**
     * отображение сносок внизу страницы. Не работает в режиме свитка и в случае, если AlEngineOptions.useScreenPages != TAL_SCREEN_PAGES_COUNT.SIZE
     */
    public boolean		        notesOnPage = false;

	/**
	 * значение 3 отступов для стилей
	 * 0..99 - в пикселях
	 * 100..199 - количество пробелов стандартного шрифта - 100
	 * 200..255 - значение - 200 в процентах от ширины страницы
	 */
	/*public int		       		margin1Style = 210;
	public int		        	margin2Style = 225;
	public int		        	margin3Style = 240;*/

	/*
	support [0..15] Em
	*/
	public int					textIndentDefaultEm = 2;
	public boolean				textIndentOverrideFromCSS = false;

	/**
	 * метод задает значение левого отсупа на странице
	 * В случае двухколоночного режима отступы используется по следующему принципу:
	 * левый отступ первой колонки и правый отступ второй колонки - используют значение marginLeft,
	 * правый отступ первой колонки и левый отступ второй колонки - marginRight
	 * @param v - значение отступа в процентах
	 */
	public void setMarginLeft(int v) {
		marginLeft = v;
	}

	/**
	 * метод задает значение правого отсупа на странице
	 * В случае двухколоночного режима отступы используется по следующему принципу:
	 * левый отступ первой колонки и правый отступ второй колонки - используют значение marginLeft,
	 * правый отступ первой колонки и левый отступ второй колонки - marginRight
	 * @param v - значение отступа в процентах
	 */
	public void setMarginRight(int v) {
		marginRight = v;
	}

	/**
	 * метод задает значение верхнего отступа на странице
	 * @param v - значение отступа в процентах
	 */
	public void setMarginTop(int v) {
		marginTop = v;
	}

	/**
	 * метод задает значение нижнего отступа на странице
	 * @param v - значение отступа в процентах
	 */
	public void setMarginBottom(int v) {
		marginBottom = v;
	}

	/**
	 * метод задает значение правого и левого отсупов на странице
	 * В случае двухколоночного режима отступы используется по следующему принципу:
	 * левый отступ первой колонки и правый отступ второй колонки - используют значение marginLeft,
	 * правый отступ первой колонки и левый отступ второй колонки - marginRight
	 * @param v - значение отступа в процентах
	 */
	public void setMarginHorizontal(int v) {
		setMarginLeft(v);
		setMarginRight(v);
	}

	/**
	 * метод задает значение верхнего и нижнего отступов на странице
	 * @param v - значение отступа в процентах
	 */
	public void setMarginVertical(int v) {
		setMarginTop(v);
		setMarginBottom(v);
	}

	/**
	 * Метод задает одинаковое значение для всех отступов
	 * В случае двухколоночного режима отступы используется по следующему принципу:
	 * левый отступ первой колонки и правый отступ второй колонки - используют значение marginLeft,
	 * правый отступ первой колонки и левый отступ второй колонки - marginRight
	 * @param v - значение отступа в процентах
	 */
	public void setMargins(int v) {
		setMarginHorizontal(v);
		setMarginVertical(v);
	}

	/**
	 * проверка корректности значения отступа
	 * @param v - значение отступа в процентах
	 * @return - корректное значение, если что-то не правильно. Для внутреннего использования
	 */
	public int validateMargin(int v) {
		if (v >= MIN_MARGIN && v <= MAX_MARGIN)
			return v;
		return DEF_MARGIN;
	}
}
