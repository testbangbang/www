package com.onyx.android.note.activity;

import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.ui.view.ContentView;

import java.util.List;
import java.util.Map;

/**
 * Created by solskjaer49 on 16/8/1 17:59.
 */

public interface ManagerInterface extends BaseInterface {
    void loadNoteList();

    void updateCurLibID(String curLibID);

    void updateCurLibName(String curLibName);

    void updateCurLibPath(String curLibPath);

    void updateUIWithNewNoteList(List<NoteModel> curLibSubContList);

    ContentView getContentView();

    Map<String, Integer> getLookupTable();

    void showMovableFolderDialog(List<NoteModel> curLibSubContList);
}
