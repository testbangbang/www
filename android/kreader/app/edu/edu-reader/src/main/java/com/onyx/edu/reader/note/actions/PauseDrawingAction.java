package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.request.PauseDrawingRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/6/9.
 */

public class PauseDrawingAction extends BaseAction {

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        NoteManager noteManager = readerDataHolder.getNoteManager();
        PauseDrawingRequest pauseDrawingRequest = new PauseDrawingRequest(readerDataHolder.getVisiblePages());
        noteManager.submit(readerDataHolder.getContext(), pauseDrawingRequest, baseCallback);
    }
}
