package com.neverland.engbook.forpublic;

/**
 * параметры открытия книги
 */
public class AlBookOptions {
	/**
	 * кодовая страница, используя которую необходимо открыть книгу.
	 */
	public int					codePage = 0; // -1 for auto
	/**
	 * кодовая страница по умолчанию. Значение используется, если codePage равно TAL_CODE_PAGES_AUTO и библиотека не смогла
	 определить реальную кодовую страницу
	 */
	public int					codePageDefault = 1251;
	/**
	 * параметры, зависящие от формата книги
	 */
	public int					formatOptions = 0; // default 0
	/**
	 * позиция чтения, куда будет осуществлен переход после открытия книги
	 */
	public int					readPosition = 0;
	/**
 	* пока - резерв
 	*/
	public long					reserved = 0;
}
