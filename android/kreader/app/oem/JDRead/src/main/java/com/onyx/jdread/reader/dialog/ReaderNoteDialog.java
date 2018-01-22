package com.onyx.jdread.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivityNoteBinding;
import com.onyx.jdread.reader.actions.CreateNoteAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.NoteActivityEventHandler;
import com.onyx.jdread.reader.model.NoteViewModel;


/**
 * Created by huxiaomao on 17/5/10.
 */

public class ReaderNoteDialog extends Dialog implements ReaderNoteViewBack {
    private static final String TAG = ReaderNoteDialog.class.getSimpleName();
    private ReaderDataHolder readerDataHolder;
    private ActivityNoteBinding binding;
    private NoteViewModel noteViewModel;
    private NoteActivityEventHandler noteActivityEventHandler;

    public ReaderNoteDialog(ReaderDataHolder readerDataHolder, @NonNull Activity activity) {
        super(activity, android.R.style.Theme_NoTitleBar);
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        initView();
        registerListener();
        initData();
    }

    private void initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.activity_note, null, false);
        setContentView(binding.getRoot());

        noteViewModel = new NoteViewModel();
        noteViewModel.setReaderDataHolder(readerDataHolder);
        binding.setNoteViewModel(noteViewModel);
        noteActivityEventHandler = new NoteActivityEventHandler(noteViewModel, this);
    }

    private void initData() {
        final CreateNoteAction createNoteAction = new CreateNoteAction();
        createNoteAction.execute(readerDataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                noteViewModel.setNoteInfo(readerDataHolder.getAppContext(), createNoteAction.getNoteInfo());
            }
        });
    }

    private void registerListener() {
        noteActivityEventHandler.registerListener();
    }

    @Override
    public void dismiss() {
        noteActivityEventHandler.unregisterListener();
        super.dismiss();
    }

    @Override
    public Dialog getContent() {
        return this;
    }
}
