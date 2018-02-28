package com.onyx.android.note.note.menu;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.action.SaveDocumentAction;
import com.onyx.android.note.common.base.BaseViewHandler;
import com.onyx.android.note.event.SaveDocumentEvent;
import com.onyx.android.sdk.note.NoteManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lxm on 2018/2/26.
 */

public class NoteMenuHandler extends BaseViewHandler {

    public NoteMenuHandler(EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void onSaveDocument(SaveDocumentEvent event) {
        new SaveDocumentAction(getNoteManager()).execute(null);
    }

    private NoteDataBundle getNoteBundle() {
        return NoteDataBundle.getInstance();
    }

    private NoteManager getNoteManager() {
        return getNoteBundle().getNoteManager();
    }
}
