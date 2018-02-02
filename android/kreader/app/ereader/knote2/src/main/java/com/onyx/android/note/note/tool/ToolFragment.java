package com.onyx.android.note.note.tool;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.common.base.BaseFragment;
import com.onyx.android.note.note.NoteFragment;
import com.onyx.android.note.note.NoteUIBundle;

/**
 * Created by lxm on 2018/2/2.
 */

public class ToolFragment extends BaseFragment {

    private ToolProcessor processor;
    private NoteUIBundle uiBundle;

    public static ToolFragment newInstance(NoteUIBundle uiBundle) {
        ToolFragment fragment = new ToolFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NoteFragment.NOTE_UI_BUNDLE, uiBundle);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiBundle = (NoteUIBundle) getArguments().getSerializable(NoteFragment.NOTE_UI_BUNDLE);
        processor = new ToolProcessor(getNoteBundle().getEventBus(), uiBundle);
        processor.subscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        processor.unsubscribe();
    }

    private NoteDataBundle getNoteBundle() {
        return NoteDataBundle.getInstance();
    }
}
