package com.onyx.android.note.note;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.note.scribble.ScribbleViewModel;
import com.onyx.android.note.note.tool.ToolMenuModel;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

/**
 * Created by lxm on 2018/2/2.
 */

public class NoteUIBundle implements Serializable {

    private EventBus getEventBus() {
        return NoteDataBundle.getInstance().getEventBus();
    }

    private ToolMenuModel toolMenuModel;
    private ScribbleViewModel scribbleViewModel;

    public ToolMenuModel getToolMenuModel() {
        if (toolMenuModel == null) {
            toolMenuModel = new ToolMenuModel(getEventBus());
        }
        return toolMenuModel;
    }

    public ScribbleViewModel getScribbleViewModel() {
        if (scribbleViewModel == null) {
            scribbleViewModel = new ScribbleViewModel(getEventBus());
        }
        return scribbleViewModel;
    }
}
