package com.onyx.jdread.reader.event;

import android.app.Activity;
import android.content.Context;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.reader.actions.NextPageAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.actions.ShowSettingMenuAction;
import com.onyx.jdread.reader.common.ReaderViewBack;
import com.onyx.jdread.reader.menu.dialog.ReaderSettingMenuDialog;
import com.onyx.jdread.reader.model.ReaderViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2017/12/26.
 */

public class ReaderActivityEventHandler {
    private ReaderViewModel readerViewModel;
    private ReaderViewBack readerViewBack;

    public ReaderActivityEventHandler(ReaderViewModel readerViewModel,ReaderViewBack readerViewBack) {
        this.readerViewModel = readerViewModel;
        this.readerViewBack = readerViewBack;
    }

    public void registeredListener() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void unregisteredListener() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenDocumentFailResultEvent(OpenDocumentFailResultEvent event) {
        readerViewModel.setTipMessage(event.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenDocumentSuccessResultEvent(OpenDocumentSuccessResultEvent event) {

    }

    @Subscribe
    public void onMenuAreaEvent(MenuAreaEvent event) {
        new ShowSettingMenuAction().execute(readerViewModel.getReaderDataHolder());
    }

    @Subscribe
    public void onPrevPageEvent(PrevPageEvent event) {
        new PrevPageAction().execute(readerViewModel.getReaderDataHolder());
    }

    @Subscribe
    public void onNextPageEvent(NextPageEvent event) {
        new NextPageAction().execute(readerViewModel.getReaderDataHolder());
    }

    @Subscribe
    public void onShowReaderSettingMenuEvent(ShowReaderSettingMenuEvent event) {
        if(readerViewBack != null){
            Activity activity = readerViewBack.getContext();
            if(activity == null){
                return;
            }
            ReaderSettingMenuDialog readerSettingMenuDialog = new ReaderSettingMenuDialog(readerViewModel.getReaderDataHolder(), activity);
            readerSettingMenuDialog.show();
        }
    }

}
