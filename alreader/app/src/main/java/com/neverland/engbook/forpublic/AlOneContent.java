package com.neverland.engbook.forpublic;


/**
 * структура, описывающая один элемент содержания
 */
public class AlOneContent {

	/**
	 * название главы
	 */
	public String		name;
	/**
	 * позиция начала главы
	 */
	public int			positionS;
	/**
	 * уровень содержания
	 */
	public int			iType;
	/**
	 * пока не используется
	 */
	public boolean		isBookmark;

	public static AlOneContent add(String s, int content_start, int level) {
		AlOneContent a = new AlOneContent();
		a.name = s;
		a.positionS = content_start;
		a.iType = level;
		return a;
	}

	@Override
	public String toString() {
		return String.valueOf(iType) + '/' + name + '/' + positionS;
	}

}
