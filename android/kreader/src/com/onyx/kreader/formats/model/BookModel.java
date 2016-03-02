package com.onyx.kreader.formats.model;

/**
 * Created by zengzhu on 2/28/16.
 */
public class BookModel {

    private BookModelHelper modelHelper;
    private TextModel textModel;


    public BookModel(final String path) {
        modelHelper = new BookModelHelper(path);
    }

    public final BookModelHelper getModelHelper() {
        return modelHelper;
    }

    public final TextModel getTextModel() {
        if (textModel == null) {
            textModel = new TextModel();
        }
        return textModel;
    }
}
