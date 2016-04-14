package com.neverland.engbook.forpublic;

import android.graphics.Point;

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
     * тапнули по внешней ссылке - link содержит адрес этой ссылки
     */
    public boolean              isExtLink = false;
    /**
     * тапнули по картинке - image содержит адрес картинки
     */
    public boolean              isImage = false;
    
    public int                  tapWordStart = TAP_ON_CLEAR_SPACE;
    public int                  tapWordStop = TAP_ON_CLEAR_SPACE;
    /*public final Point          markerSelectStart = new Point();
    public final Point          markerSelectStop = new Point();*/

    /**
     * при тапе по локальной ссылке поле содержит позицию перехода
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

    public void clearInfo() {
        pos = TAP_ON_CLEAR_SPACE;
        isNote = false;
        isLocalLink = false;
        isExtLink = false;
        isImage = false;
        link.setLength(0);
        image.setLength(0);
        tapWordStart = TAP_ON_CLEAR_SPACE;
        tapWordStop = TAP_ON_CLEAR_SPACE;  
    }

}
