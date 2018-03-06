package com.onyx.jdread.reader.event;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.AddAnnotationAction;
import com.onyx.jdread.reader.actions.UpdateAnnotationAction;
import com.onyx.jdread.reader.actions.UpdateViewPageAction;
import com.onyx.jdread.reader.catalog.event.UpdateAnnotationEvent;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.dialog.ReaderNoteViewBack;
import com.onyx.jdread.reader.model.NoteViewModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class NoteActivityEventHandler {
    private NoteViewModel noteViewModel;
    private ReaderNoteViewBack noteViewBack;
    private ReaderDataHolder readerDataHolder;

    public NoteActivityEventHandler(ReaderDataHolder readerDataHolder,NoteViewModel noteViewModel, ReaderNoteViewBack noteViewBack) {
        this.noteViewModel = noteViewModel;
        this.noteViewBack = noteViewBack;
        this.readerDataHolder = readerDataHolder;
    }

    public void registerListener() {
        if (!readerDataHolder.getEventBus().isRegistered(this)) {
            readerDataHolder.getEventBus().register(this);
        }
    }

    public void unregisterListener() {
        if (readerDataHolder.getEventBus().isRegistered(this)) {
            readerDataHolder.getEventBus().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddNoteEvent(AddNoteEvent event) {
        AddAnnotationAction action = new AddAnnotationAction(event.newNote,event.srcNote,event.srcNoteState);
        action.execute(readerDataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                noteViewBack.getContent().dismiss();
                notifyUpdateAnnotationList();
                new UpdateViewPageAction().execute(readerDataHolder, null);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateNoteEvent(UpdateNoteEvent event) {
        UpdateAnnotationAction action = new UpdateAnnotationAction(event.annotation,event.newNote,event.srcNote,event.srcNoteState);
        action.execute(readerDataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                noteViewBack.getContent().dismiss();
                notifyUpdateAnnotationList();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoteBackEvent(NoteBackEvent event) {
        noteViewBack.getContent().dismiss();
    }

    private void notifyUpdateAnnotationList(){
        readerDataHolder.getEventBus().post(new UpdateAnnotationEvent());
    }
}
