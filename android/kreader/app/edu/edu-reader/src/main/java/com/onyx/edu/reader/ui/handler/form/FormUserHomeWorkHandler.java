package com.onyx.edu.reader.ui.handler.form;

import android.widget.Toast;

import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.edu.reader.R;
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

    @Override
    public boolean isEnableNoteDrawing() {
        return super.isEnableNoteDrawing() && !getReaderDataHolder().getNoteManager().getNoteDocument().isLock();
    }

    @Override
    public boolean onMenuClicked(ReaderMenuAction action) {
        if (getReaderDataHolder().getNoteManager().getNoteDocument().isLock()) {
            switch (action) {
                case SUBMIT:
                case TOGGLE_FORM_SCRIBBLE:
                case SCRIBBLE_SHAPE:
                case SCRIBBLE_WIDTH:
                case SCRIBBLE_ERASER:
                case SCRIBBLE_UNDO:
                case SCRIBBLE_REDO:
                    Toast.makeText(getContext(), R.string.can_not_modify, Toast.LENGTH_SHORT).show();
                    return true;
            }
        }
        return super.onMenuClicked(action);
    }

    @Override
    protected boolean lockShapeByDocumentStatus() {
        return true;
    }

    @Override
    protected boolean lockShapeByRevision() {
        return true;
    }
}
