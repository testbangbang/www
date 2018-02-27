package com.onyx.android.note.note.menu;

import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.NoteUIBundle;
import com.onyx.android.note.R;
import com.onyx.android.note.common.base.BaseFragment;
import com.onyx.android.note.databinding.FragmentNoteMenuBinding;
import com.onyx.android.sdk.note.event.SetDrawExcludeRectEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2018/2/2.
 */

public class NoteMenuFragment extends BaseFragment {

    private FragmentNoteMenuBinding binding;
    private NoteMenuHandler noteMenuHandler;

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
        binding.setModel(getUIBundle().getNoteMenuModel());
        noteMenuHandler = new NoteMenuHandler(getNoteBundle().getEventBus());
        noteMenuHandler.subscribe();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMenu();
    }

    private void initMenu() {
        binding.menuLayout.post(new Runnable() {
            @Override
            public void run() {
                setDrawMenuExcludeRect();
            }
        });
    }

    private void setDrawMenuExcludeRect() {
        Rect menu1 = new Rect();
        Rect menu2 = new Rect();
        Rect expand = new Rect();
        binding.expand.getGlobalVisibleRect(expand);
        binding.menu1Layout.noteMenu1.getGlobalVisibleRect(menu1);
        binding.menu2Layout.noteMenu2.getGlobalVisibleRect(menu2);
        List<Rect> rectList = new ArrayList<>();
        rectList.add(menu1);
        rectList.add(menu2);
        rectList.add(expand);
        getNoteBundle().post(new SetDrawExcludeRectEvent(rectList));
    }

    @Override
    public void onDestroy() {
        noteMenuHandler.unSubscribe();
        super.onDestroy();
    }

    private NoteDataBundle getNoteBundle() {
        return NoteDataBundle.getInstance();
    }

    private NoteUIBundle getUIBundle() {
        return NoteUIBundle.getInstance();
    }
}
