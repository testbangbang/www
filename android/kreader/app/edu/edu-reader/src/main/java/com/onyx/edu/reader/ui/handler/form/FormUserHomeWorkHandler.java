package com.onyx.edu.reader.ui.handler.form;

import com.onyx.edu.reader.ui.handler.HandlerManager;

/**
 * Created by ming on 2017/8/1.
 */
public class FormUserHomeWorkHandler extends FormBaseHandler {

    public FormUserHomeWorkHandler(HandlerManager parent) {
        super(parent);
        setEnableNoteDrawing(true);
    }

    @Override
    public boolean isEnableNoteInScribbleForm() {
        return false;
    }
}
