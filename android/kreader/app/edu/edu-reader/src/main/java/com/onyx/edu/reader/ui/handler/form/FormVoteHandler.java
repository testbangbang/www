package com.onyx.edu.reader.ui.handler.form;

import com.onyx.edu.reader.ui.handler.HandlerManager;

/**
 * Created by ming on 2017/7/26.
 */

public class FormVoteHandler extends FormBaseHandler {

    public FormVoteHandler(HandlerManager parent) {
        super(parent);
    }

    @Override
    public boolean isEnableNoteDrawing() {
        return false;
    }
}
