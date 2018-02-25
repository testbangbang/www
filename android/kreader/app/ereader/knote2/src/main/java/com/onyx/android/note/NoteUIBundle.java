package com.onyx.android.note;

import com.onyx.android.note.note.scribble.ScribbleViewModel;
import com.onyx.android.note.note.menu.NoteMenuModel;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by lxm on 2018/2/2.
 */

public class NoteUIBundle {

    private static final NoteUIBundle ourInstance = new NoteUIBundle();

    public static NoteUIBundle getInstance() {
        return ourInstance;
    }

    private NoteUIBundle() {
    }

    private EventBus getEventBus() {
        return NoteDataBundle.getInstance().getEventBus();
    }

    private NoteMenuModel noteMenuModel;
    private ScribbleViewModel scribbleViewModel;

    public NoteMenuModel getNoteMenuModel() {
        if (noteMenuModel == null) {
            noteMenuModel = new NoteMenuModel(getEventBus());
        }
        return noteMenuModel;
    }

    public ScribbleViewModel getScribbleViewModel() {
        if (scribbleViewModel == null) {
            scribbleViewModel = new ScribbleViewModel(getEventBus());
        }
        return scribbleViewModel;
    }
}
