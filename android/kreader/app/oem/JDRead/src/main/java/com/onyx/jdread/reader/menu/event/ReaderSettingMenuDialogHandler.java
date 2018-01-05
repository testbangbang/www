package com.onyx.jdread.reader.menu.event;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.ViewConfig;
import com.onyx.jdread.main.model.FunctionBarModel;
import com.onyx.jdread.reader.common.ToastMessage;
import com.onyx.jdread.reader.event.CloseDocumentEvent;
import com.onyx.jdread.reader.menu.dialog.ReaderSettingViewBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderSettingMenuDialogHandler {
    private ReaderSettingViewBack readerSettingViewBack;
    private FunctionBarModel functionBarModel;

    public ReaderSettingMenuDialogHandler(ReaderSettingViewBack readerSettingViewBack) {
        this.readerSettingViewBack = readerSettingViewBack;
    }

    public void setFunctionBarModel(FunctionBarModel functionBarModel) {
        this.functionBarModel = functionBarModel;
    }

    public void registerListener() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void unregisterListener() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseReaderSettingMenuEvent(CloseReaderSettingMenuEvent event) {
        readerSettingViewBack.getContent().dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBuyBookClickEvent(BuyBookClickEvent event) {
        ToastMessage.showMessage(JDReadApplication.getInstance().getApplicationContext(), "buyBook");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingMenuItemNextChapterEvent(ReaderSettingMenuItemNextChapterEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingMenuItemPreviousChapterEvent(ReaderSettingMenuItemPreviousChapterEvent event) {

    }

    @Subscribe
    public void onReaderFunctionItemCatalogEvent(ReaderFunctionItemCatalogEvent event){
        ToastMessage.showMessage(JDReadApplication.getInstance().getApplicationContext(),"Catalog");
    }

    @Subscribe
    public void onReaderFunctionItemProgressEvent(ReaderFunctionItemProgressEvent event){
        functionBarModel.changeTabSelection(ViewConfig.FunctionModule.SHOP);
    }

    @Subscribe
    public void onReaderFunctionItemBackEvent(ReaderFunctionItemBackEvent event){
        EventBus.getDefault().post(new CloseDocumentEvent());
        readerSettingViewBack.getContent().dismiss();
    }

    @Subscribe
    public void onReaderFunctionItemBrightnessEvent(ReaderFunctionItemBrightnessEvent event){
        functionBarModel.changeTabSelection(ViewConfig.FunctionModule.SETTING);
    }

    @Subscribe
    public void onReaderFunctionItemSettingEvent(ReaderFunctionItemSettingEvent event){
        ToastMessage.showMessage(JDReadApplication.getInstance().getApplicationContext(),"Setting");
    }
}
