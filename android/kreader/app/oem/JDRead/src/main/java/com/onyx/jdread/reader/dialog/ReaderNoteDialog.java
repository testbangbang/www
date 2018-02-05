package com.onyx.jdread.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.DeviceUtils;
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
    private Annotation annotation;

    public ReaderNoteDialog(ReaderDataHolder readerDataHolder, @NonNull Activity activity,Annotation annotation) {
        super(activity, android.R.style.Theme_NoTitleBar);
        this.readerDataHolder = readerDataHolder;
        this.annotation = annotation;
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
        noteViewModel.setReaderDataHolder(readerDataHolder.getEventBus());
        binding.setNoteViewModel(noteViewModel);
        noteActivityEventHandler = new NoteActivityEventHandler(readerDataHolder,noteViewModel, this);
    }

    private void initData() {
        final CreateNoteAction createNoteAction = new CreateNoteAction(annotation);
        createNoteAction.execute(readerDataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                noteViewModel.setNoteInfo(readerDataHolder.getAppContext(), createNoteAction.getNoteInfo(),annotation);
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

    @Override
    public void show() {
        super.show();
        DeviceUtils.adjustFullScreenStatus(this.getWindow(),true);
    }
}
