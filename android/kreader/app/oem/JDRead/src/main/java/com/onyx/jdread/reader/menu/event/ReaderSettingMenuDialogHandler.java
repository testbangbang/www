package com.onyx.jdread.reader.menu.event;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.databinding.ReaderSettingMenuBinding;
import com.onyx.jdread.main.common.ViewConfig;
import com.onyx.jdread.reader.actions.NextPageAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.common.ToastMessage;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.CloseDocumentEvent;
import com.onyx.jdread.reader.event.PageViewUpdateEvent;
import com.onyx.jdread.reader.menu.actions.ReaderSettingShowMenuAction;
import com.onyx.jdread.reader.menu.actions.SettingFontSizeAction;
import com.onyx.jdread.reader.menu.actions.SettingTypefaceAction;
import com.onyx.jdread.reader.menu.actions.UpdatePageInfoAction;
import com.onyx.jdread.reader.menu.dialog.ReaderSettingViewBack;
import com.onyx.jdread.reader.menu.model.ReaderSettingModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderSettingMenuDialogHandler {
    private ReaderSettingViewBack readerSettingViewBack;
    private ReaderSettingMenuBinding binding;
    private ReaderDataHolder readerDataHolder;

    public ReaderSettingMenuDialogHandler(ReaderDataHolder readerDataHolder,ReaderSettingViewBack readerSettingViewBack) {
        this.readerDataHolder = readerDataHolder;
        this.readerSettingViewBack = readerSettingViewBack;
    }

    public void setBinding(ReaderSettingMenuBinding binding) {
        this.binding = binding;
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
        new NextPageAction().execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingMenuItemPreviousChapterEvent(ReaderSettingMenuItemPreviousChapterEvent event) {
        new PrevPageAction().execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderFunctionItemCatalogEvent(ReaderFunctionItemCatalogEvent event){
        ToastMessage.showMessage(JDReadApplication.getInstance().getApplicationContext(),"Catalog");
        //start activity
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderFunctionItemBackEvent(ReaderFunctionItemBackEvent event){
        EventBus.getDefault().post(new CloseDocumentEvent());
        readerSettingViewBack.getContent().dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderFunctionItemProgressEvent(ReaderFunctionItemProgressEvent event){
        binding.readerSettingFunctionBar.getFunctionBarModel().changeTabSelection(ViewConfig.FunctionModule.SHOP);
        //show system,title,progress,function, menu
        new ReaderSettingShowMenuAction(binding, ReaderSettingModel.ReaderSystemMenuGroup.progressMenuGroup).execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderFunctionItemBrightnessEvent(ReaderFunctionItemBrightnessEvent event){
        binding.readerSettingFunctionBar.getFunctionBarModel().changeTabSelection(ViewConfig.FunctionModule.SETTING);
        //show system,title,brightness,function, menu
        new ReaderSettingShowMenuAction(binding, ReaderSettingModel.ReaderSystemMenuGroup.brightnessMenuGroup).execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderFunctionItemSettingEvent(ReaderFunctionItemSettingEvent event){
        if(readerDataHolder.getReader().getReaderHelper().getTextStyleManager().getStyle() != null) {
            //epub show text
            new ReaderSettingShowMenuAction(binding, ReaderSettingModel.ReaderSystemMenuGroup.textMenuGroup).execute(readerDataHolder);
        }else {
            //pdf show text
            new ReaderSettingShowMenuAction(binding, ReaderSettingModel.ReaderSystemMenuGroup.imageMenuGroup).execute(readerDataHolder);
        }
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onReaderSettingMenuItemBackPdfEvent(ReaderSettingMenuItemBackPdfEvent event){
        ToastMessage.showMessage(JDReadApplication.getInstance().getApplicationContext(),"BackPdf");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingMenuItemCustomizeEvent(ReaderSettingMenuItemCustomizeEvent event){
        new ReaderSettingShowMenuAction(binding, ReaderSettingModel.ReaderSystemMenuGroup.customMenuGroup).execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPageViewUpdateEvent(PageViewUpdateEvent event){
        new UpdatePageInfoAction(binding).execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingFontSizeEvent(ReaderSettingFontSizeEvent event){
        new SettingFontSizeAction(readerDataHolder.getReader().getReaderHelper().getStyle(),event.fontSize).execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingTypefaceEvent(ReaderSettingTypefaceEvent event){
        new SettingTypefaceAction(readerDataHolder.getReader().getReaderHelper().getStyle(),event.typeFace).execute(readerDataHolder);
    }
}
