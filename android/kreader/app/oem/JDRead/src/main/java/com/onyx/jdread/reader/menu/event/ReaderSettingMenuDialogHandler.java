package com.onyx.jdread.reader.menu.event;

import android.graphics.RectF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.databinding.ReaderSettingMenuBinding;
import com.onyx.jdread.main.common.ViewConfig;
import com.onyx.jdread.reader.actions.GotoPageAction;
import com.onyx.jdread.reader.actions.NextPageAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.actions.ToggleBookmarkAction;
import com.onyx.jdread.reader.common.GammaInfo;
import com.onyx.jdread.reader.common.ToastMessage;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.CloseDocumentEvent;
import com.onyx.jdread.reader.event.PageViewUpdateEvent;
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
import com.onyx.jdread.reader.menu.dialog.ReaderSettingViewBack;
import com.onyx.jdread.reader.menu.model.ReaderSettingModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderSettingMenuDialogHandler {
    private ReaderSettingViewBack readerSettingViewBack;
    private ReaderSettingMenuBinding binding;
    private ReaderDataHolder readerDataHolder;
    private boolean sideNoting = false;
    private int sideNotePage = 0;

    public ReaderSettingMenuDialogHandler(ReaderDataHolder readerDataHolder, ReaderSettingViewBack readerSettingViewBack) {
        this.readerDataHolder = readerDataHolder;
        this.readerSettingViewBack = readerSettingViewBack;
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
        ToastMessage.showMessage(JDReadApplication.getInstance().getApplicationContext(), "buyBook");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingMenuItemNextChapterEvent(ReaderSettingMenuItemNextChapterEvent event) {
        new NextPageAction().execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingMenuItemPreviousChapterEvent(ReaderSettingMenuItemPreviousChapterEvent event) {
        new PrevPageAction().execute(readerDataHolder,null);
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
        if (readerDataHolder.getReader().getReaderHelper().getTextStyleManager().getStyle() != null) {
            //epub show text
            new ReaderSettingShowMenuAction(binding, ReaderSettingModel.ReaderSystemMenuGroup.textMenuGroup).execute(readerDataHolder,null);
        } else {
            //pdf show text
            new ReaderSettingShowMenuAction(binding, ReaderSettingModel.ReaderSystemMenuGroup.imageMenuGroup).execute(readerDataHolder,null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingMenuItemBackPdfEvent(ReaderSettingMenuItemBackPdfEvent event) {
        ToastMessage.showMessage(JDReadApplication.getInstance().getApplicationContext(), "BackPdf");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingMenuItemCustomizeEvent(ReaderSettingMenuItemCustomizeEvent event) {
        new ReaderSettingShowMenuAction(binding, ReaderSettingModel.ReaderSystemMenuGroup.customMenuGroup).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPageViewUpdateEvent(PageViewUpdateEvent event) {
        new UpdatePageInfoAction(binding, readerDataHolder.getReaderViewInfo()).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingFontSizeEvent(ReaderSettingFontSizeEvent event) {
        new SettingFontSizeAction(readerDataHolder.getStyleCopy(), event.fontSize).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderSettingTypefaceEvent(ReaderSettingTypefaceEvent event) {
        new SettingTypefaceAction(readerDataHolder.getStyleCopy(), event.typeFace).execute(readerDataHolder,null);
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
        GammaInfo gammaInfo = new GammaInfo();
        gammaInfo.setTextGamma(event.textGamma);
        new GammaCorrectionAction(gammaInfo).execute(readerDataHolder,null);
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
        new SettingParagraphSpacingAction().execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingLeftAndRightSpacingEvent(SettingLeftAndRightSpacingEvent event) {
        new SettingLeftAndRightSpacingAction(readerDataHolder.getStyleCopy(), event.margin).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingLineSpacingEvent(SettingLineSpacingEvent event) {
        new SettingLineSpacingAction(readerDataHolder.getStyleCopy(), event.margin).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSettingUpAndDownSpacingEvent(SettingUpAndDownSpacingEvent event) {
        new SettingUpAndDownSpacingAction(readerDataHolder.getStyleCopy(), event.margin).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGotoPageEvent(GotoPageEvent event) {
        new GotoPageAction(event.page).execute(readerDataHolder,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToggleBookmarkEvent(ToggleBookmarkEvent event) {
        if(hasBookmark()){
            removeBookmark();
        }else{
            addBookmark();
        }
    }

    private void removeBookmark() {
        new ToggleBookmarkAction(ToggleBookmarkAction.ToggleSwitch.Off,readerDataHolder.getReaderUserDataInfo(),getFirstVisiblePageWithBookmark()).execute(readerDataHolder,null);
    }

    private void addBookmark() {
        new ToggleBookmarkAction(ToggleBookmarkAction.ToggleSwitch.On,readerDataHolder.getReaderUserDataInfo(),getFirstPageInfo()).execute(readerDataHolder,null);
    }

    public boolean hasBookmark() {
        return getFirstVisiblePageWithBookmark() != null;
    }

    public PageInfo getFirstVisiblePageWithBookmark() {
        for (PageInfo pageInfo : getVisiblePages()) {
            if(readerDataHolder.getReaderUserDataInfo() == null){
                continue;
            }
            if (readerDataHolder.getReaderUserDataInfo().hasBookmark(pageInfo)) {
                return pageInfo;
            }
        }
        return null;
    }

    public final PageInfo getFirstPageInfo() {
        return readerDataHolder.getReaderViewInfo().getFirstVisiblePage();
    }

    public final List<PageInfo> getVisiblePages() {
        ArrayList<PageInfo> pages = new ArrayList<>();

        PageInfo firstPage = getFirstPageInfo();
        if (firstPage == null) {
            return pages;
        }
        if (!supportScalable()) {
            firstPage.setSubPage(-1);
        }

        pages.add(firstPage);

        if (sideNoting) {
            PageInfo subNotePage = new PageInfo(firstPage.getName(),
                    firstPage.getRange().startPosition,
                    firstPage.getRange().endPosition,
                    firstPage.getOriginWidth(),
                    firstPage.getOriginHeight());

            RectF pageRect = new RectF(0, 0, subNotePage.getOriginWidth(),
                    subNotePage.getOriginHeight());
            int displayWidth = readerDataHolder.getReader().getReaderViewHelper().getContentWidth();
            int displayHeight = readerDataHolder.getReader().getReaderViewHelper().getContentHeight();
            RectF viewportRect = new RectF(displayWidth / 2, 0, displayWidth, displayHeight);
            float scale = PageUtils.scaleToFitRect(pageRect, viewportRect);

            subNotePage.setScale(scale);
            subNotePage.updateDisplayRect(pageRect);

            PageUtils.updateVisibleRect(subNotePage, viewportRect);

            subNotePage.setSubPage(getSubPageIndex());
            pages.add(subNotePage);
        }

        return pages;
    }

    public boolean supportScalable() {
        return readerDataHolder.getReaderViewInfo() != null && readerDataHolder.getReaderViewInfo().supportScalable;
    }

    public int getSubPageIndex() {
        return supportScalable() ? sideNotePage + 1 : sideNotePage;
    }
}
