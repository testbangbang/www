package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.MirrorType;

/**
 * Created by solskjaer49 on 2017/8/11 12:08.
 */

public class MirrorSelectedShapeRequest extends AsyncBaseNoteRequest {
    private static final String TAG = MirrorSelectedShapeRequest.class.getSimpleName();

    public MirrorSelectedShapeRequest(MirrorType type, boolean isAddToHistory) {
        this.mirrorType = type;
        this.isAddToHistory = isAddToHistory;
        setPauseInputProcessor(true);
    }

    private MirrorType mirrorType;
    private volatile boolean isAddToHistory = false;

    @Override
    public void execute(NoteManager noteManager) throws Exception {
        setResumeInputProcessor(noteManager.useDFBForCurrentState());
        benchmarkStart();
        noteManager.getNoteDocument().getCurrentPage(getContext()).saveCurrentSelectShape();
        int translateDistance = 0;
        switch (mirrorType){
            case XAxisMirror:
                translateDistance = 2 * (int) noteManager.getNoteDocument().getCurrentPage(getContext()).getSelectedRect().centerX();
                break;
            case YAxisMirror:
                translateDistance = 2 * (int) noteManager.getNoteDocument().getCurrentPage(getContext()).getSelectedRect().centerY();
                break;
        }
        noteManager.getNoteDocument().getCurrentPage(getContext()).setMirrorEffectToSelectShapeList(mirrorType, translateDistance,isAddToHistory);
        renderCurrentPageInBitmap(noteManager);
        updateShapeDataInfo(noteManager);
    }
}
