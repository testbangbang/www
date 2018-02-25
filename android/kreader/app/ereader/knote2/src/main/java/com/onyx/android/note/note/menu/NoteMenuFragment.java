package com.onyx.android.note.note.menu;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.R;
import com.onyx.android.note.common.base.BaseFragment;
import com.onyx.android.note.databinding.FragmentNoteMenuBinding;

/**
 * Created by lxm on 2018/2/2.
 */

public class NoteMenuFragment extends BaseFragment {

    private FragmentNoteMenuBinding binding;

    public static NoteMenuFragment newInstance() {
        return new NoteMenuFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note_menu, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private NoteDataBundle getNoteBundle() {
        return NoteDataBundle.getInstance();
    }
}
