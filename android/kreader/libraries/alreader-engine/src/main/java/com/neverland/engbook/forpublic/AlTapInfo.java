package com.neverland.engbook.forpublic;

/**
* структура - содержит в себе информацию о том, куда "тапнул" пользователь
* значения полей заполняются в зависимости от того, в каком режиме выделения находится библиотека в момент тапа
*/
public class AlTapInfo {
    public static final int TAP_ON_CLEAR_SPACE = -1;
    ///////////////////// input
    /**
     * входные параметры - координаты тапа
     */
    public int                  x;
    public int                  y;
    
    ///////////////////// output
    /**
     * координаты начала слова, по которому тапнули
     */
    public int                  pos = TAP_ON_CLEAR_SPACE;
    /**
     * тапнули по слову, которое является частью подстраничной сноски
     */
    public boolean              isNote = false;
    /**
     * тапнули по локальной ссылке - поле link не заполняется. Переход можно осуществить
     gotoPosition(TAL_GOTOCOMMAND.POSITION, linkLocalPosition)
     */
    public boolean              isLocalLink = false;
    /**
     * тапнули по сноске - устанавливается вместе с isLocalLink. Можно осуществить локальный переход
     * gotoPosition(TAL_GOTOCOMMAND.POSITION, linkLocalPosition)
     * можно получить текст сноски - getFootNoteText(link)
     */
    public boolean              isFootNote = false;
    /**
     * тапнули по внешней ссылке - link содержит адрес этой ссылки
     */
    public boolean              isExtLink = false;
    /**
     * тапнули по картинке - image содержит адрес картинки
     */
    public boolean              isImage = false;
    /**
     * тапнули по сноске на полноценный просмотр таблицы - linkLocalPosition содержит адрес таблицы
     */
    public boolean				isTableLink = false;
    /**
     * тап был по содержимому таблицы
     */
    public boolean				isTableTap = false;


    public int                  tapWordStart = TAP_ON_CLEAR_SPACE;
    public int                  tapWordStop = TAP_ON_CLEAR_SPACE;


    /**
     * при тапе по локальной ссылке поле содержит позицию перехода
     * при тапе по таблице - адрес таблицы
     */
    public int                  linkLocalPosition = -1;
    /**
     * адрес внешней ссылки
     */
    public final StringBuilder  link = new StringBuilder();
    /**
     * адрес картинки
     */
    public final StringBuilder  image = new StringBuilder();

    /*
	тап был по области, которая отмечена как заметка. Id  этой самой заметки
	*/
    public int bookmarkId = -1;


    public void clearInfo() {
        pos = TAP_ON_CLEAR_SPACE;
        isNote = false;
        isLocalLink = false;
        isFootNote = false;
        isExtLink = false;
        isImage = false;
        isTableLink = false;
        isTableTap = false;
        link.setLength(0);
        image.setLength(0);
        bookmarkId = -1;
        tapWordStart = TAP_ON_CLEAR_SPACE;
        tapWordStop = TAP_ON_CLEAR_SPACE;
    }

}
