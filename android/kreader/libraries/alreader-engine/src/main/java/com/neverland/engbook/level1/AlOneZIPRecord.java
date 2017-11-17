package com.neverland.engbook.level1;

public class AlOneZIPRecord {
    public static final int	SPECIAL_NONE = 0;
    public static final int	SPECIAL_FIRST = 1;
    public static final int	SPECIAL_CONTENT = 2;
    public static final int	SPECIAL_TOC = 3;
    public static final int	SPECIAL_IMAGE = 4;
    public static final int	SPECIAL_NOTE = 5;
    public static final int	SPECIAL_ENDNOTE = 6;
    public static final int	SPECIAL_FOOTNOTE = 7;
    public static final int	SPECIAL_STYLE = 8;
    public static final int	SPECIAL_CHM_HHC = 9;
    public static final int	SPECIAL_UNKNOWN = 32;
/*
    public static final String SPECIAL_NONE_STR = Integer.toString(SPECIAL_NONE);
    public static final String SPECIAL_FIRST_STR = Integer.toString(SPECIAL_FIRST);
    public static final String SPECIAL_CONTENT_STR = Integer.toString(SPECIAL_CONTENT);
    public static final String SPECIAL_TOC_STR = Integer.toString(SPECIAL_TOC);
    public static final String SPECIAL_IMAGE_STR = Integer.toString(SPECIAL_IMAGE);
    public static final String SPECIAL_NOTE_STR = Integer.toString(SPECIAL_NOTE);
    public static final String SPECIAL_ENDNOTE_STR = Integer.toString(SPECIAL_ENDNOTE);
    public static final String SPECIAL_FOOTNOTE_STR = Integer.toString(SPECIAL_FOOTNOTE);
    public static final String SPECIAL_STYLE_STR = Integer.toString(SPECIAL_STYLE);*/


    String		    file = null;
    int			    num = -1;
    int			    size = 0;
    int			    startSize = 0;
    int 		    endSize = 0;
    int			    special = 0;
    byte[]			startStr = null;
    byte[]			endStr = null;

}
