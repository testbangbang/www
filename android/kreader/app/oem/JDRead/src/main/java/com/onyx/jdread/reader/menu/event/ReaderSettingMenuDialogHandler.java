package com.onyx.jdread.reader.menu.event;

import android.util.Log;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ReaderSettingMenuBinding;
import com.onyx.jdread.main.common.ViewConfig;
import com.onyx.jdread.main.event.ShowBackTabEvent;
import com.onyx.jdread.main.event.SystemBarBackToSettingEvent;
import com.onyx.jdread.main.event.TabLongClickedEvent;
import com.onyx.jdread.manager.ManagerActivityUtils;
import com.onyx.jdread.reader.actions.GotoPageAction;
import com.onyx.jdread.reader.actions.NextPageAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.common.GammaInfo;
import com.onyx.jdread.reader.common.ToastMessage;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.CloseDocumentEvent;
import com.onyx.jdread.reader.event.OpenBookDetailEvent;
import com.onyx.jdread.reader.event.ShowReaderCatalogMenuEvent;
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
import com.onyx.jdread.reader.menu.common.BookmarkHandle;
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
    private BookmarkHandle bookmarkHandle;

    public ReaderSettingMenuDialogHandler(ReaderDataHolder readerDataHolder, ReaderSettingViewBack readerSettingViewBack) {
        this.readerDataHolder = readerDataHolder;
        this.readerSettingViewBack = readerSettingViewBack;
        bookmarkHandle = new BookmarkHandle();
    }

    public void setBinding(ReaderSettingMenuBinding binding) {
        this.binding = binding;
    }

    public void registerListener() {
        if (!readerDataHolder.getEventBus().isRegistered(this)) {
            readerDataHolder.getEventBus().register(this);
        }
    }

    public void unregisterListener() {
        if (readerDataHolder.getEventBus().isRegistered(this)) {
            readerDataHolder.getEventBus().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseReaderSettingMenuEvent(CloseReaderSettingMenuEvent event) {
        closeDialog();
    }

    private void closeDialog(){
        readerSettingViewBack.getContent().dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBuyBookClickEvent(BuyBookClickEvent event) {
        readerDataHolder.getEventBus().post(new OpenBookDetailEvent());
        closeDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingMenuItemNextChapterEvent(ReaderSettingMenuItemNextChapterEvent event) {
        new NextPageAction().execute(readerDataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerDataHolder.getEventBus().post(new UpdateProgressEvent());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateProgressEvent(UpdateProgressEvent event){
        new UpdatePageInfoAction(binding, readerDataHolder.getReaderViewInfo(),true).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingMenuItemPreviousChapterEvent(ReaderSettingMenuItemPreviousChapterEvent event) {
        new PrevPageAction().execute(readerDataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerDataHolder.getEventBus().post(new UpdateProgressEvent());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderFunctionItemCatalogEvent(ReaderFunctionItemCatalogEvent event) {
        //catalog
        closeDialog();
        readerDataHolder.getEventBus().post(new ShowReaderCatalogMenuEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderFunctionItemBackEvent(ReaderFunctionItemBackEvent event) {
        readerDataHolder.getEventBus().post(new CloseDocumentEvent());
        readerSettingViewBack.getContent().dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderFunctionItemProgressEvent(ReaderFunctionItemProgressEvent event) {
        binding.readerSettingFunctionBar.getFunctionBarModel().changeTabSelection(ViewConfig.FunctionModule.SHOP);
        //show system,title,progress,function, menu
        new ReaderSettingShowMenuAction(binding, ReaderSettingModel.ReaderSystemMenuGroup.progressMenuGroup).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderFunctionItemBrightnessEvent(ReaderFunctionItemBrightnessEvent event) {
        binding.readerSettingFunctionBar.getFunctionBarModel().changeTabSelection(ViewConfig.FunctionModule.SETTING);
        //show system,title,brightness,function, menu
        new ReaderSettingShowMenuAction(binding, ReaderSettingModel.ReaderSystemMenuGroup.brightnessMenuGroup).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderFunctionItemSettingEvent(ReaderFunctionItemSettingEvent event) {
        if(readerDataHolder.supportFontSizeAdjustment()){
            //epub show text
            new ReaderSettingShowMenuAction(binding, ReaderSettingModel.ReaderSystemMenuGroup.textMenuGroup).execute(readerDataHolder,null);
        } else {
            //pdf show text
            new ReaderSettingShowMenuAction(binding, ReaderSettingModel.ReaderSystemMenuGroup.imageMenuGroup).execute(readerDataHolder,null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingMenuItemBackPdfEvent(ReaderSettingMenuItemBackPdfEvent event) {
        readerDataHolder.getEventBus().post(new SystemBarBackToSettingEvent());
        closeDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingMenuItemCustomizeEvent(ReaderSettingMenuItemCustomizeEvent event) {
        new ReaderSettingShowMenuAction(binding, ReaderSettingModel.ReaderSystemMenuGroup.customMenuGroup).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingFontSizeEvent(ReaderSettingFontSizeEvent event) {
        new SettingFontSizeAction(readerDataHolder.getStyleCopy(),
                event.styleIndex,event.settingType).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingTypefaceEvent(ReaderSettingTypefaceEvent event) {
        new SettingTypefaceAction(readerDataHolder.getStyleCopy(),
                event.typeFace,event.styleIndex,event.settingType
        ,event.currentLineSpacing).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateImageShowModeEvent(SwitchNavigationToComicModeEvent event) {
        new SwitchNavigationToComicModeAction().execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResetNavigationEvent(ResetNavigationEvent event) {
        new ResetNavigationAction().execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGammaCorrectionEvent(GammaCorrectionEvent event) {
        readerDataHolder.getGammaInfo().setEmboldenLevel(event.textGamma);
        new GammaCorrectionAction(readerDataHolder.getGammaInfo()).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScaleToPageCropEvent(ScaleToPageCropEvent event) {
        new ScaleToPageCropAction(readerDataHolder.getReaderViewInfo()).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImageReflowEvent(ImageReflowEvent event) {
        new ImageReflowAction(readerDataHolder.getSettingsCopy()).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeChineseConvertTypeEvent(ChangeChineseConvertTypeEvent event) {
        new ChangeChineseConvertTypeAction(event.convertType).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingParagraphSpacingEvent(SettingParagraphSpacingEvent event) {
        new SettingParagraphSpacingAction(readerDataHolder.getStyleCopy(),event.spacing).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingLeftAndRightSpacingEvent(SettingLeftAndRightSpacingEvent event) {
        new SettingLeftAndRightSpacingAction(readerDataHolder.getStyleCopy(), event.margin).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingLineSpacingEvent(SettingLineSpacingEvent event) {
        new SettingLineSpacingAction(readerDataHolder.getStyleCopy(), event.margin,event.styleIndex,
                event.settingType).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingUpAndDownSpacingEvent(SettingUpAndDownSpacingEvent event) {
        new SettingUpAndDownSpacingAction(readerDataHolder.getStyleCopy(), event.top,event.bottom).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotoPageEvent(GotoPageEvent event) {
        new GotoPageAction(event.page).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToggleBookmarkEvent(ToggleBookmarkEvent event) {
        bookmarkHandle.toggleBookmarkEvent(readerDataHolder);
        closeDialog();
    }

    public boolean hasBookmark(){
        return bookmarkHandle.hasBookmark(readerDataHolder);
    }

    public void updatePageInfo(){
        new UpdatePageInfoAction(binding, readerDataHolder.getReaderViewInfo(),false).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTabLongClickedEvent(TabLongClickedEvent event) {
        if (ViewConfig.FunctionModule.isBackModule(event.functionItem.getFunctionModule())) {
            EventBus.getDefault().post(new ShowBackTabEvent(false));
        }
    }

    @Subscribe
    public void onShowBackTabEvent(ShowBackTabEvent event) {
        isShowBackTab(event.isShow());
    }

    private void isShowBackTab(boolean show) {
        PreferenceManager.setBooleanValue(JDReadApplication.getInstance(), R.string.show_back_tab_key, show);
        readerSettingViewBack.setFunction();
    }
}
