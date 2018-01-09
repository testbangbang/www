package com.onyx.jdread.reader.menu.event;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.databinding.ReaderSettingMenuBinding;
import com.onyx.jdread.main.common.ViewConfig;
import com.onyx.jdread.reader.actions.NextPageAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.common.GammaInfo;
import com.onyx.jdread.reader.common.ToastMessage;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.CloseDocumentEvent;
import com.onyx.jdread.reader.event.PageViewUpdateEvent;
import com.onyx.jdread.reader.menu.actions.ChangeChineseConvertTypeAction;
import com.onyx.jdread.reader.menu.actions.GammaCorrectionAction;
import com.onyx.jdread.reader.menu.actions.ImageReflowAction;
import com.onyx.jdread.reader.menu.actions.ReaderSettingShowMenuAction;
import com.onyx.jdread.reader.menu.actions.ResetNavigationAction;
import com.onyx.jdread.reader.menu.actions.ScaleToPageCropAction;
import com.onyx.jdread.reader.menu.actions.SettingFontSizeAction;
import com.onyx.jdread.reader.menu.actions.SettingLeftAndRightSpacingAction;
import com.onyx.jdread.reader.menu.actions.SettingLineSpacingAction;
import com.onyx.jdread.reader.menu.actions.SettingParagraphSpacingAction;
import com.onyx.jdread.reader.menu.actions.SettingTypefaceAction;
import com.onyx.jdread.reader.menu.actions.SettingUpAndDownSpacingAction;
import com.onyx.jdread.reader.menu.actions.SwitchNavigationToComicModeAction;
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
    private ReaderTextStyle style;
    private ImageReflowSettings settings;

    public ReaderSettingMenuDialogHandler(ReaderDataHolder readerDataHolder,ReaderSettingViewBack readerSettingViewBack,ReaderTextStyle style,ImageReflowSettings settings) {
        this.readerDataHolder = readerDataHolder;
        this.readerSettingViewBack = readerSettingViewBack;
        this.style = style;
        this.settings = settings;
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
        new SettingFontSizeAction(style,event.fontSize).execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingTypefaceEvent(ReaderSettingTypefaceEvent event){
        new SettingTypefaceAction(style,event.typeFace).execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateImageShowModeEvent(SwitchNavigationToComicModeEvent event){
        new SwitchNavigationToComicModeAction().execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResetNavigationEvent(ResetNavigationEvent event){
        new ResetNavigationAction().execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGammaCorrectionEvent(GammaCorrectionEvent event){
        GammaInfo gammaInfo = new GammaInfo();
        gammaInfo.setTextGamma(event.textGamma);
        new GammaCorrectionAction(gammaInfo).execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScaleToPageCropEvent(ScaleToPageCropEvent event){
        new ScaleToPageCropAction().execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImageReflowEvent(ImageReflowEvent event){
        new ImageReflowAction(settings).execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeChineseConvertTypeEvent(ChangeChineseConvertTypeEvent event){
        new ChangeChineseConvertTypeAction(event.convertType).execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingParagraphSpacingEvent(SettingParagraphSpacingEvent event){
        new SettingParagraphSpacingAction().execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingLeftAndRightSpacingEvent(SettingLeftAndRightSpacingEvent event){
        new SettingLeftAndRightSpacingAction(style,event.margin).execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingLineSpacingEvent(SettingLineSpacingEvent event){
        new SettingLineSpacingAction(style,event.margin).execute(readerDataHolder);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingUpAndDownSpacingEvent(SettingUpAndDownSpacingEvent event){
        new SettingUpAndDownSpacingAction(style,event.margin).execute(readerDataHolder);
    }
}
