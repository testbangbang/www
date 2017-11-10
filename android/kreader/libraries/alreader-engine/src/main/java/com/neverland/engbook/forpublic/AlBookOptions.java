package com.neverland.engbook.forpublic;

/**
 * параметры открытия книги
 */
public class AlBookOptions {
	/*!
	не использовать поле со сторны клиентского приложения
	*/
	private boolean				blocked = false;
	/**
	 * кодовая страница, используя которую необходимо открыть книгу.
	 */
	public int					codePage = -1; // -1 for auto
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
	public long					readPositionAddon = 0;
	/**
 	* пока - резерв
 	*/
	public long					reserved = 0;
	/**
	 * объект расшифровщик файла книги
	 */
	public AlFileDecrypt		decryptObj = null;
	/**
	 * необходимость получить ковер книги - используется только в scanMetaData
	 */
	public boolean				needCoverData = false;
	/**
	 * вывод обложки на первой странице книги - отключен
	 */
	public boolean				noUseCover = false;

	public synchronized void setBlocked() {
		blocked = true;
	}

	public synchronized void clearBlocked() {
		blocked = false;
	}

	public synchronized boolean isBlocked() {
		return blocked;
	}
}
