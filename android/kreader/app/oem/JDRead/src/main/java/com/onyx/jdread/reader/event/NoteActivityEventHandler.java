package com.onyx.jdread.reader.event;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.UpdateAnnotationAction;
import com.onyx.jdread.reader.actions.UpdateViewPageAction;
import com.onyx.jdread.reader.dialog.ReaderNoteViewBack;
import com.onyx.jdread.reader.model.NoteViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class NoteActivityEventHandler {
    private NoteViewModel noteViewModel;
    private ReaderNoteViewBack noteViewBack;

    public NoteActivityEventHandler(NoteViewModel noteViewModel, ReaderNoteViewBack noteViewBack) {
        this.noteViewModel = noteViewModel;
        this.noteViewBack = noteViewBack;
    }

    public void registerListener() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void unregisterListener() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSaveNoteEvent(SaveNoteEvent event) {
        UpdateAnnotationAction action = new UpdateAnnotationAction(noteViewModel.getNoteInfo(), noteViewModel.getPagePosition());
        action.execute(noteViewModel.getReaderDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                noteViewBack.getContent().dismiss();
                new UpdateViewPageAction().execute(noteViewModel.getReaderDataHolder(), null);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoteBackEvent(NoteBackEvent event) {
        noteViewBack.getContent().dismiss();
    }
}
