package com.onyx.jdread.reader.event;

import android.app.Activity;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.jdread.reader.actions.GetViewSettingAction;
import com.onyx.jdread.reader.actions.NextPageAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.actions.ShowSettingMenuAction;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.common.ReaderViewBack;
import com.onyx.jdread.reader.menu.common.ReaderBookInfoDialogConfig;
import com.onyx.jdread.reader.catalog.dialog.ReaderBookInfoDialog;
import com.onyx.jdread.reader.menu.dialog.ReaderSettingMenuDialog;
import com.onyx.jdread.reader.model.ReaderViewModel;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2017/12/26.
 */

public class ReaderActivityEventHandler {
    private ReaderViewModel readerViewModel;
    private ReaderViewBack readerViewBack;
    private ReaderTextStyle style;
    private ImageReflowSettings settings;
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    private ReaderSettingMenuDialog readerSettingMenuDialog;

    public ReaderActivityEventHandler(ReaderViewModel readerViewModel, ReaderViewBack readerViewBack) {
        this.readerViewModel = readerViewModel;
        this.readerViewBack = readerViewBack;
    }

    public ReaderTextStyle getStyle() {
        return style;
    }

    public void setStyle(ReaderTextStyle style) {
        this.style = style;
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
    public void onCloseDocumentEvent(CloseDocumentEvent event) {
        readerViewBack.getContext().finish();
    }

    @Subscribe
    public void onShowReaderSettingMenuEvent(ShowReaderSettingMenuEvent event) {
        if (readerViewBack != null) {
            Activity activity = readerViewBack.getContext();
            if (activity == null) {
                return;
            }
            readerSettingMenuDialog = new ReaderSettingMenuDialog(readerViewModel.getReaderDataHolder(), activity,
                    style, settings,readerViewInfo,readerUserDataInfo);
            readerSettingMenuDialog.show();
        }
    }

    @Subscribe
    public void onShowReaderCatalogMenuEvent(ShowReaderCatalogMenuEvent event){
        Activity activity = readerViewBack.getContext();
        if (activity == null) {
            return;
        }
        ReaderBookInfoDialog readerBookInfoDialog = new ReaderBookInfoDialog(activity,readerViewModel.getReaderDataHolder(),
                readerViewInfo,
                readerUserDataInfo,
                ReaderBookInfoDialogConfig.CATALOG_MODE);
        readerBookInfoDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPageViewUpdateEvent(PageViewUpdateEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInitPageViewInfoEvent(InitPageViewInfoEvent event) {
        new GetViewSettingAction(event.getReaderViewInfo()).execute(readerViewModel.getReaderDataHolder());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateViewSettingEvent(UpdateViewSettingEvent event){
        style = event.getStyle();
        settings = event.getSettings();
        readerUserDataInfo = event.getReaderUserDataInfo();
        if(readerSettingMenuDialog != null && readerSettingMenuDialog.isShowing()){
            readerSettingMenuDialog.getReaderSettingMenuDialogHandler().setReaderUserDataInfo(readerUserDataInfo);
            readerSettingMenuDialog.getReaderSettingMenuDialogHandler().setSettings(settings);
            readerSettingMenuDialog.getReaderSettingMenuDialogHandler().setStyle(style);
            readerSettingMenuDialog.updateBookmarkState();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateReaderViewInfoEvent(UpdateReaderViewInfoEvent event){
        readerViewInfo = event.getReaderViewInfo();
    }

    public static void updateReaderViewInfo(ReaderBaseRequest request){
        UpdateReaderViewInfoEvent event = new UpdateReaderViewInfoEvent();
        event.setReaderViewInfo(request.getReaderViewInfo());
        EventBus.getDefault().post(event);
    }
}
