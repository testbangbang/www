package com.onyx.android.note.action.menu;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.rx.RxCallback;

/**
 * Created by lxm on 2018/3/9.
 */

public class ChangeShapeAction extends BaseNoteAction {

    private int shapeType;

    public ChangeShapeAction(NoteManager noteManager) {
        super(noteManager);
    }

    @Override
    public void execute(RxCallback rxCallback) {
        NoteDataBundle.getInstance().getDrawingArgs().setCurrentShapeType(shapeType);

    }
}
