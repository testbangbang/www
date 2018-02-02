package com.onyx.android.note.note;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onyx.android.note.common.base.BaseFragment;

/**
 * Created by lxm on 2018/2/2.
 */

public class NoteFragment extends BaseFragment {

    public static final String NOTE_UI_BUNDLE = "NoteUIBundle";

    private NoteUIBundle uiBundle;

    public static NoteFragment newInstance() {
        return new NoteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiBundle = new NoteUIBundle();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
