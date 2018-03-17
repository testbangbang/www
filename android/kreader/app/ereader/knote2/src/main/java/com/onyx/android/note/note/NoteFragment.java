package com.onyx.android.note.note;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.R;
import com.onyx.android.note.common.base.BaseFragment;
import com.onyx.android.note.databinding.FragmentNoteBinding;
import com.onyx.android.note.note.menu.NoteMenuFragment;
import com.onyx.android.note.note.scribble.ScribbleFragment;
import com.onyx.android.sdk.note.NoteManager;

/**
 * Created by lxm on 2018/2/2.
 */

public class NoteFragment extends BaseFragment {

    private NoteMenuFragment menuFragment;
    private ScribbleFragment scribbleFragment;

    private FragmentNoteBinding binding;

    public static NoteFragment newInstance() {
        return new NoteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNoteBundle().getPenEventHandler().subscribe();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note, container, false);
        initChildFragment();
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        getNoteBundle().getHandlerManager().quit();
        getNoteBundle().getPenEventHandler().unSubscribe();
        super.onDestroy();
    }

    private void initChildFragment() {
        scribbleFragment = ScribbleFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.scribble_layout, scribbleFragment).commit();
    }

    private NoteDataBundle getNoteBundle() {
        return NoteDataBundle.getInstance();
    }

    private NoteManager getNoteManager() {
        return getNoteBundle().getNoteManager();
    }
}
