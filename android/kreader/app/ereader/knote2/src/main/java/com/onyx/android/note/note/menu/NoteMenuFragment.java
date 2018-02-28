package com.onyx.android.note.note.menu;

import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.NoteUIBundle;
import com.onyx.android.note.R;
import com.onyx.android.note.common.base.BaseFragment;
import com.onyx.android.note.databinding.FragmentNoteMenuBinding;
import com.onyx.android.note.event.menu.CheckMenuRectEvent;
import com.onyx.android.sdk.note.event.SetDrawExcludeRectEvent;
import com.onyx.android.sdk.utils.TreeObserverUtils;

import org.greenrobot.eventbus.Subscribe;

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
        getNoteBundle().getEventBus().register(this);
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
                postDrawMenuExcludeRect();
            }
        });
    }

    private void CheckMenuRect() {
        binding.menuLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                TreeObserverUtils.removeGlobalOnLayoutListener(binding.menuLayout.getViewTreeObserver(), this);
                postDrawMenuExcludeRect();
            }
        });
    }

    private void postDrawMenuExcludeRect() {
        List<Rect> rectList = new ArrayList<>();
        addExcludeRect(rectList, binding.expand);
        addExcludeRect(rectList, binding.menu1Layout.noteMenu1);
        addExcludeRect(rectList, binding.menu2Layout.noteMenu2);
        getNoteBundle().post(new SetDrawExcludeRectEvent(rectList));
    }

    private void addExcludeRect(List<Rect> rectList, View view) {
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        rectList.add(rect);
    }

    @Subscribe
    public void onCheckMenuRectEvent(CheckMenuRectEvent event) {
        CheckMenuRect();
    }

    @Override
    public void onDestroy() {
        noteMenuHandler.unSubscribe();
        getNoteBundle().getEventBus().unregister(this);
        super.onDestroy();
    }

    private NoteDataBundle getNoteBundle() {
        return NoteDataBundle.getInstance();
    }

    private NoteUIBundle getUIBundle() {
        return NoteUIBundle.getInstance();
    }
}
