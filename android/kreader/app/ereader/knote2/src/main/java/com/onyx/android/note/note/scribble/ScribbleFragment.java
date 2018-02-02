package com.onyx.android.note.note.scribble;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.common.base.BaseFragment;
import com.onyx.android.note.note.NoteFragment;
import com.onyx.android.note.note.NoteUIBundle;

/**
 * Created by lxm on 2018/2/2.
 */

public class ScribbleFragment extends BaseFragment {

    private NoteUIBundle uiBundle;
    private ScribbleProcessor processor;

    public static ScribbleFragment newInstance(NoteUIBundle uiBundle) {
        ScribbleFragment fragment = new ScribbleFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NoteFragment.NOTE_UI_BUNDLE, uiBundle);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiBundle = (NoteUIBundle) getArguments().getSerializable(NoteFragment.NOTE_UI_BUNDLE);
        processor = new ScribbleProcessor(getNoteBundle().getEventBus(), uiBundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private NoteDataBundle getNoteBundle() {
        return NoteDataBundle.getInstance();
    }
}
