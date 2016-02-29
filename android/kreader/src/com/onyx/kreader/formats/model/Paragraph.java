package com.onyx.kreader.formats.model;

/**
 * Created by zengzhu on 2/28/16.
 */
public class Paragraph {

    private long streamPosition;
    private long streamLength;


    static public Paragraph create() {
        return new Paragraph();
    }

}
