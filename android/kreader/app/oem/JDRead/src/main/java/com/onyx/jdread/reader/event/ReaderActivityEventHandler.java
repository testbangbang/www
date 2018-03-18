package com.onyx.jdread.reader.event;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.api.device.epd.UpdateScheme;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.dialog.DialogMessage;
import com.onyx.jdread.R;
import com.onyx.jdread.main.activity.MainActivity;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.event.SystemBarBackToSettingEvent;
import com.onyx.jdread.main.event.SystemBarClickedEvent;
import com.onyx.jdread.main.view.SystemBarPopupWindow;
import com.onyx.jdread.manager.ManagerActivityUtils;
import com.onyx.jdread.personal.dialog.ExportDialog;
import com.onyx.jdread.personal.event.ExportToEmailEvent;
import com.onyx.jdread.personal.event.ExportToImpressionEvent;
import com.onyx.jdread.personal.event.ExportToNativeEvent;
import com.onyx.jdread.reader.actions.AddAnnotationAction;
import com.onyx.jdread.reader.actions.AnnotationCopyToClipboardAction;
import com.onyx.jdread.reader.actions.CheckAnnotationAction;
import com.onyx.jdread.reader.actions.CloseDocumentAction;
import com.onyx.jdread.reader.actions.DeleteAnnotationAction;
import com.onyx.jdread.reader.actions.GetViewSettingAction;
import com.onyx.jdread.reader.actions.GotoPositionAction;
import com.onyx.jdread.reader.actions.NextPageAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.actions.SelectTextCopyToClipboardAction;
import com.onyx.jdread.reader.actions.ShowSettingMenuAction;
import com.onyx.jdread.reader.actions.ToggleBookmarkAction;
import com.onyx.jdread.reader.actions.UpdateViewPageAction;
import com.onyx.jdread.reader.catalog.dialog.ReaderBookInfoDialog;
import com.onyx.jdread.reader.catalog.event.AnnotationItemClickEvent;
import com.onyx.jdread.reader.catalog.event.ExportReadNoteEvent;
import com.onyx.jdread.reader.common.ReaderViewBack;
import com.onyx.jdread.reader.common.ToastMessage;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.dialog.DialogBaiduBaiKe;
import com.onyx.jdread.reader.dialog.ReaderNoteDialog;
import com.onyx.jdread.reader.dialog.SingleLineDialog;
import com.onyx.jdread.reader.dialog.TranslateDialog;
import com.onyx.jdread.reader.menu.common.ReaderBookInfoDialogConfig;
import com.onyx.jdread.reader.menu.common.ReaderConfig;
import com.onyx.jdread.reader.menu.dialog.CloseDocumentDialog;
import com.onyx.jdread.reader.menu.dialog.DialogSearch;
import com.onyx.jdread.reader.menu.dialog.ReaderSettingMenuDialog;
import com.onyx.jdread.reader.menu.event.CloseReaderSettingMenuEvent;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.menu.event.SearchContentEvent;
import com.onyx.jdread.reader.menu.event.ToggleBookmarkSuccessEvent;
import com.onyx.jdread.reader.menu.model.ReaderPageInfoModel;
import com.onyx.jdread.reader.model.ReaderViewModel;
import com.onyx.jdread.reader.request.ReaderBaseRequest;
import com.onyx.jdread.setting.common.AssociateDialogHelper;
import com.onyx.jdread.setting.common.ExportHelper;
import com.onyx.jdread.setting.event.BindEmailEvent;
import com.onyx.jdread.setting.view.OnyxDigitalClock;
import com.onyx.jdread.util.BroadcastHelper;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huxiaomao on 2017/12/26.
 */

public class ReaderActivityEventHandler {
    private ReaderViewModel readerViewModel;
    private ReaderViewBack readerViewBack;
    private ReaderSettingMenuDialog readerSettingMenuDialog;
    private ReaderNoteDialog readerNoteDialog;
    private CloseDocumentDialog closeDocumentDialog;
    private ReaderBookInfoDialog readerBookInfoDialog;
    private ExportHelper exportHelper;
    private SystemBarPopupWindow.SystemBarPopupModel systemBarPopupWindowModel;

    public ReaderActivityEventHandler(ReaderViewModel readerViewModel, ReaderViewBack readerViewBack) {
        this.readerViewModel = readerViewModel;
        this.readerViewBack = readerViewBack;
        ReaderPageInfoModel.setHasChapterInfo(true);
        exportHelper = new ExportHelper(readerViewBack.getContext(), readerViewModel.getEventBus());
    }

    public void registerListener() {
        if (!readerViewModel.getEventBus().isRegistered(this)) {
            readerViewModel.getEventBus().register(this);
        }
    }

    public void unregisterListener() {
        if (readerViewModel.getEventBus().isRegistered(this)) {
            readerViewModel.getEventBus().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenDocumentFailResultEvent(OpenDocumentFailResultEvent event) {
        if (event.getThrowable() != null) {
            BroadcastHelper.sendFeedbackBroadcast(readerViewBack.getContext(), event.getThrowable());
        }
        DialogMessage dlg = new DialogMessage(readerViewBack.getContext(), event.getMessage());
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                readerViewBack.getContext().finish();
            }
        });
        dlg.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenDocumentSuccessResultEvent(OpenDocumentSuccessResultEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDocumentLoadSuccessResultEvent(DocumentLoadSuccessEvent event) {
        Log.d(getClass().getSimpleName(), "onDocumentLoadSuccessResultEvent");
        if(readerViewModel.getReaderDataHolder().isInitViewPage() && !readerViewModel.getReaderDataHolder().isPreload()){
            new UpdateViewPageAction().execute(readerViewModel.getReaderDataHolder(),null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDocumentLoadFailedResultEvent(DocumentLoadFailedEvent event) {
        Log.d(getClass().getSimpleName(), "onDocumentLoadFailedResultEvent");
    }

    @Subscribe
    public void onMenuAreaEvent(MenuAreaEvent event) {
        new ShowSettingMenuAction().execute(readerViewModel.getReaderDataHolder(), null);
    }

    @Subscribe
    public void onPrevPageEvent(PrevPageEvent event) {
        new PrevPageAction().execute(readerViewModel.getReaderDataHolder(), null);
    }

    @Subscribe
    public void onNextPageEvent(NextPageEvent event) {
        new NextPageAction().execute(readerViewModel.getReaderDataHolder(), null);
    }

    @Subscribe
    public void onShowLastPageEvent(ShowLastPageEvent event) {
        readerViewModel.setIsShowLastPage(true);
    }

    @Subscribe
    public void onCloseDocumentEvent(CloseDocumentEvent event) {
        new CloseDocumentAction().execute(readerViewModel.getReaderDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerViewModel.getEventBus().post(new FinishEvent());
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable, this.getClass().getSimpleName(), readerViewModel.getReaderDataHolder().getEventBus());
            }
        });

    }

    @Subscribe
    public void onFinishEvent(FinishEvent event) {
        readerViewBack.getContext().finish();
    }

    @Subscribe
    public void onShowReaderSettingMenuEvent(ShowReaderSettingMenuEvent event) {
        if (readerViewBack != null) {
            Activity activity = readerViewBack.getContext();
            if (activity == null) {
                return;
            }
            readerSettingMenuDialog = new ReaderSettingMenuDialog(readerViewModel.getReaderDataHolder(), activity);
            readerSettingMenuDialog.show();
        }
    }

    @Subscribe
    public void onShowReaderCatalogMenuEvent(ShowReaderCatalogMenuEvent event) {
        Activity activity = readerViewBack.getContext();
        if (activity == null) {
            return;
        }
        readerBookInfoDialog = new ReaderBookInfoDialog(activity, readerViewModel.getReaderDataHolder(),
                ReaderBookInfoDialogConfig.CATALOG_MODE);
        readerBookInfoDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInitPageViewInfoEvent(InitPageViewInfoEvent event) {
        if(readerViewModel.getReaderDataHolder().isPreload()){
            startMainActivity();
            readerViewBack.getContext().finish();
        }else {
            if (JDPreferenceManager.getBooleanValue(R.string.speed_refresh_key,false)) {
                EpdController.setSystemUpdateModeAndScheme(UpdateMode.ANIMATION, UpdateScheme.QUEUE_AND_MERGE, Integer.MAX_VALUE);
            }
            new GetViewSettingAction(event.getReaderViewInfo()).execute(readerViewModel.getReaderDataHolder(), null);
        }
    }

    private void startMainActivity(){
        Intent intent = new Intent(readerViewBack.getContext(), MainActivity.class);
        readerViewBack.getContext().startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateReaderViewInfoEvent(UpdateReaderViewInfoEvent event) {
        readerViewModel.getReaderDataHolder().setReaderViewInfo(event.getReaderViewInfo());
        readerViewModel.getReaderDataHolder().setStyle(event.getStyle());
        if (event.getSettings() != null) {
            readerViewModel.getReaderDataHolder().setSettings(event.getSettings());
        }
        readerViewModel.getReaderDataHolder().setReaderUserDataInfo(event.getReaderUserDataInfo());
        if (readerSettingMenuDialog != null && readerSettingMenuDialog.isShowing()) {
            readerSettingMenuDialog.updateBookmarkState();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchContentEvent(SearchContentEvent event) {
        readerViewModel.getEventBus().post(new CloseReaderSettingMenuEvent());
        DialogSearch dialog = new DialogSearch(readerViewBack.getContext(), readerViewModel.getReaderDataHolder());
        dialog.show();
    }

    public static void updateReaderViewInfo(ReaderDataHolder readerDataHolder, ReaderBaseRequest request) {
        UpdateReaderViewInfoEvent event = new UpdateReaderViewInfoEvent();
        event.setReaderViewInfo(request.getReaderViewInfo());
        event.setReaderUserDataInfo(request.getReaderUserDataInfo());
        event.setSettings(request.getSettings());
        event.setStyle(request.getStyle());
        readerDataHolder.getEventBus().post(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopupLineationClickEvent(PopupLineationClickEvent event) {
        final CheckAnnotationAction action = new CheckAnnotationAction();
        action.execute(readerViewModel.getReaderDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                AddAnnotation(action.isEquals,action.userNote);
            }
        });
    }

    private void AddAnnotation(boolean isEquals,String userNote){
        if(isEquals){
            updatePageView();
            ToastMessage.showMessageCenter(readerViewModel.getReaderDataHolder().getAppContext(), ResManager.getString(R.string.annotation_repeat));
            return;
        }
        new AddAnnotationAction(userNote,"", ReaderConfig.QUOTE_STATE_NOT_CHANGED).execute(readerViewModel.getReaderDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                updatePageView();
            }
        });
    }

    private void updatePageView(){
        UpdateViewPageAction action =new UpdateViewPageAction();
        action.execute(readerViewModel.getReaderDataHolder(),null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopupNoteClickEvent(PopupNoteClickEvent event) {
        final CheckAnnotationAction action = new CheckAnnotationAction();
        action.execute(readerViewModel.getReaderDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                showReaderNoteDialog();
            }
        });
    }

    private void showReaderNoteDialog(){
        Activity activity = readerViewBack.getContext();
        if (activity == null) {
            return;
        }
        readerNoteDialog = new ReaderNoteDialog(readerViewModel.getReaderDataHolder(), activity,null);
        readerNoteDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopupNoteClickEvent(EditNoteClickEvent event) {
        Activity activity = readerViewBack.getContext();
        if (activity == null) {
            return;
        }
        readerNoteDialog = new ReaderNoteDialog(readerViewModel.getReaderDataHolder(), activity,event.getAnnotation());
        readerNoteDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnnotationItemClickEvent(AnnotationItemClickEvent event){
        new GotoPositionAction(event.getAnnotation().getPosition()).execute(readerViewModel.getReaderDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                hideBookInfoDialog();
            }
        });
    }

    private void hideBookInfoDialog(){
        if(readerNoteDialog != null && readerNoteDialog.isShowing()){
            readerNoteDialog.dismiss();
        }
        if(readerBookInfoDialog != null && readerBookInfoDialog.isShowing()){
            readerBookInfoDialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportReadNoteEvent(ExportReadNoteEvent event) {
        Activity activity = readerViewBack.getContext();
        if (activity == null) {
            return;
        }
        hideBookInfoDialog();
        ExportDialog dialog = new ExportDialog();
        dialog.setEventBus(readerViewModel.getEventBus());
        dialog.show(activity.getFragmentManager(), "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBindEmailEvent(BindEmailEvent event) {
        AssociateDialogHelper.dismissEmailDialog();
        ToastUtil.showToast(R.string.bind_email_success);
        onExportReadNoteEvent(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportToNativeEvent(ExportToNativeEvent event) {
        // TODO: 2018/3/8 ExportAction

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportToEmailEvent(ExportToEmailEvent event) {
        // TODO: 2018/3/8 ExportAction
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExportToImpressionEvent(ExportToImpressionEvent event) {
        // TODO: 2018/3/8 ExportAction
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopupCopyClickEvent(PopupCopyClickEvent event) {
        new SelectTextCopyToClipboardAction().execute(readerViewModel.getReaderDataHolder(), null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnnotationCopyEvent(AnnotationCopyEvent event) {
        new AnnotationCopyToClipboardAction(event.getAnnotation()).execute(readerViewModel.getReaderDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                updatePageView();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopupTranslationClickEvent(PopupTranslationClickEvent event) {
        String text = readerViewModel.getReaderDataHolder().getReaderSelectionInfo().getSelectText();
        showTranslateDialog(text);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnnotationTranslationEvent(AnnotationTranslationEvent event) {
        String text = event.getAnnotation().getQuote();
        showTranslateDialog(text);
    }

    private void showTranslateDialog(String text){
        Activity activity = readerViewBack.getContext();
        if (activity == null) {
            return;
        }
        float x = readerViewModel.getReaderDataHolder().getSelectMenuModel().getLastX();
        float y = readerViewModel.getReaderDataHolder().getSelectMenuModel().getLastY();
        TranslateDialog translateDialog = new TranslateDialog(activity, text, readerViewModel.getEventBus(), x, y);
        translateDialog.show();
        translateDialog.setCanceledOnTouchOutside(true);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopupBaidupediaClickEvent(PopupBaidupediaClickEvent event) {
        String text = readerViewModel.getReaderDataHolder().getReaderSelectionInfo().getSelectText();
        showDialogDict(text);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnnotationBaidupediaEvent(AnnotationBaidupediaEvent event) {
        String text = event.getAnnotation().getQuote();
        showDialogDict(text);
    }

    private void showDialogDict(String text){
        Activity activity = readerViewBack.getContext();
        if (activity == null) {
            return;
        }
        if(!Utils.isNetworkConnected(activity)){
            ToastUtil.showToast(R.string.reader_check_network);
            return;
        }
        DialogBaiduBaiKe dialogDict = new DialogBaiduBaiKe(activity, text,readerViewModel.getEventBus());
        dialogDict.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTranslateResultEvent(WordTranslateResultEvent event) {
        readerViewModel.getReaderDataHolder().getSelectMenuModel().updateTranslateResult(event.getTranslateResult());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReaderErrorEvent(ReaderErrorEvent event) {
        String[] errors = ReaderErrorEvent.getThrowableStringRep(event.throwable);
        ReaderErrorEvent.printThrowable(errors);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToggleBookmarkSuccessEvent(ToggleBookmarkSuccessEvent event) {
        int messageId = R.string.reader_bookmark_add_success;
        if (event.getToggleSwitch() == ToggleBookmarkAction.ToggleSwitch.Off) {
            messageId = R.string.reader_bookmark_delete_success;
        }
        ToastMessage.showMessageCenter(readerViewModel.getReaderDataHolder().getAppContext(),readerViewModel.getReaderDataHolder().getAppContext().getResources().getString(messageId));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenDocumentSuccessEvent(OpenDocumentSuccessEvent event) {
        readerViewModel.getReaderDataHolder().setDocumentOpenState();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteAnnotationEvent(DeleteAnnotationEvent event) {
        DeleteAnnotationAction action = new DeleteAnnotationAction(event.annotation);
        action.execute(readerViewModel.getReaderDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowCloseDocumentDialogEvent(ShowCloseDocumentDialogEvent event){
        if(getCloseDocumentDialog().isShowing()){
            return;
        }
        getCloseDocumentDialog().show();
    }

    public CloseDocumentDialog getCloseDocumentDialog(){
        if(closeDocumentDialog == null){
            closeDocumentDialog = new CloseDocumentDialog(readerViewModel.getReaderDataHolder(),readerViewBack.getContext());
        }
        return closeDocumentDialog;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateViewPageEvent(UpdateViewPageEvent event){
        UpdateViewPageAction action = new UpdateViewPageAction();
        action.execute(readerViewModel.getReaderDataHolder(),null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowSignMessageEvent(ShowSignMessageEvent event){
        Activity activity = readerViewBack.getContext();
        if (activity == null) {
            return;
        }

        showSingleLineDialog(event,activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSystemBarClickedEvent(SystemBarClickedEvent event) {
        if (systemBarPopupWindowModel == null) {
            systemBarPopupWindowModel = new SystemBarPopupWindow.SystemBarPopupModel();
        } else {
            systemBarPopupWindowModel.brightnessModel.updateLight();
            systemBarPopupWindowModel.updateRefreshMode();
        }
        SystemBarPopupWindow systemBarPopupWindow = new SystemBarPopupWindow(readerViewBack.getContext(), systemBarPopupWindowModel);
        systemBarPopupWindow.show(readerSettingMenuDialog.findViewById(R.id.reader_setting_system_bar));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSystemBarBackToSettingEvent(SystemBarBackToSettingEvent event) {
        ManagerActivityUtils.startSettingsActivity(readerViewBack.getContext());
    }

    private void showSingleLineDialog(ShowSignMessageEvent event,Activity activity){
        SingleLineDialog singleLineDialog = new SingleLineDialog(activity, event.signNoteInfo.note,
                readerViewModel.getEventBus(),event.signNoteInfo.rect,
                readerViewModel.getReaderDataHolder().getReaderTouchHelper().getContentHeight(),
                readerViewModel.getReaderDataHolder().getReaderTouchHelper().getContentWidth());
        singleLineDialog.show();
        singleLineDialog.setCanceledOnTouchOutside(true);
    }

    public void updateTimeFormat() {
        if (readerSettingMenuDialog != null && readerSettingMenuDialog.findViewById(R.id.reader_setting_system_bar) != null) {
            OnyxDigitalClock onyxDigitalClock = (OnyxDigitalClock) readerSettingMenuDialog.findViewById(R.id.reader_setting_system_bar).findViewById(R.id.onyx_digital_clock);
            if (onyxDigitalClock != null) {
                onyxDigitalClock.setFormat();
            }
        }
    }
}
