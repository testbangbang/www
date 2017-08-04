package com.onyx.edu.reader.ui.handler.form;

import com.onyx.edu.reader.ui.handler.HandlerManager;

/**
 * Created by ming on 2017/8/1.
 */

public class FormMeetingHandler extends FormBaseHandler {

    public FormMeetingHandler(HandlerManager parent) {
        super(parent);
    }

    @Override
    public boolean isEnableNoteDrawing() {
        return true;
    }

}
