package com.onyx.jdread.reader.event;

import android.app.Activity;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.reader.actions.AddAnnotationAction;
import com.onyx.jdread.reader.actions.CloseDocumentAction;
import com.onyx.jdread.reader.actions.DeleteAnnotationAction;
import com.onyx.jdread.reader.actions.GetViewSettingAction;
import com.onyx.jdread.reader.actions.NextPageAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.actions.SelectTextCopyToClipboardAction;
import com.onyx.jdread.reader.actions.ShowSettingMenuAction;
import com.onyx.jdread.reader.actions.ToggleBookmarkAction;
import com.onyx.jdread.reader.catalog.dialog.ReaderBookInfoDialog;
import com.onyx.jdread.reader.common.ReaderViewBack;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.dialog.DialogDict;
import com.onyx.jdread.reader.dialog.ReaderNoteDialog;
import com.onyx.jdread.reader.dialog.TranslateDialog;
import com.onyx.jdread.reader.menu.common.ReaderBookInfoDialogConfig;
import com.onyx.jdread.reader.menu.dialog.ReadSearchDialog;
import com.onyx.jdread.reader.menu.dialog.ReaderSettingMenuDialog;
import com.onyx.jdread.reader.menu.event.CloseReaderSettingMenuEvent;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.menu.event.SearchContentEvent;
import com.onyx.jdread.reader.menu.event.ToggleBookmarkSuccessEvent;
import com.onyx.jdread.reader.menu.model.ReaderPageInfoModel;
import com.onyx.jdread.reader.model.ReaderViewModel;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

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

    public ReaderActivityEventHandler(ReaderViewModel readerViewModel, ReaderViewBack readerViewBack) {
        this.readerViewModel = readerViewModel;
        this.readerViewBack = readerViewBack;
        ReaderPageInfoModel.setHasChapterInfo(true);
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
        readerViewModel.setTipMessage(event.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenDocumentSuccessResultEvent(OpenDocumentSuccessResultEvent event) {

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
    public void onCloseDocumentEvent(CloseDocumentEvent event) {
        new CloseDocumentAction().execute(readerViewModel.getReaderDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerViewModel.getEventBus().post(new FinishEvent());
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerViewModel.getReaderDataHolder().getEventBus());
            }
        });

    }

    @Subscribe
    public void onFinishEvent(FinishEvent event){
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
        ReaderBookInfoDialog readerBookInfoDialog = new ReaderBookInfoDialog(activity, readerViewModel.getReaderDataHolder(),
                ReaderBookInfoDialogConfig.CATALOG_MODE);
        readerBookInfoDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPageViewUpdateEvent(PageViewUpdateEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInitPageViewInfoEvent(InitPageViewInfoEvent event) {
        new GetViewSettingAction(event.getReaderViewInfo()).execute(readerViewModel.getReaderDataHolder(), null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateReaderViewInfoEvent(UpdateReaderViewInfoEvent event) {
        readerViewModel.getReaderDataHolder().setReaderViewInfo(event.getReaderViewInfo());
        readerViewModel.getReaderDataHolder().setStyle(event.getStyle());
        readerViewModel.getReaderDataHolder().setSettings(event.getSettings());
        readerViewModel.getReaderDataHolder().setReaderUserDataInfo(event.getReaderUserDataInfo());
        if (readerSettingMenuDialog != null && readerSettingMenuDialog.isShowing()) {
            readerSettingMenuDialog.updateBookmarkState();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchContentEvent(SearchContentEvent event) {
        readerViewModel.getEventBus().post(new CloseReaderSettingMenuEvent());
        ReadSearchDialog dialog = new ReadSearchDialog();
        dialog.setReaderDataHolder(readerViewModel.getEventBus());
        dialog.show(readerViewBack.getContext().getFragmentManager(), "");
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
        new AddAnnotationAction().execute(readerViewModel.getReaderDataHolder(), null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopupNoteClickEvent(PopupNoteClickEvent event) {
        Activity activity = readerViewBack.getContext();
        if (activity == null) {
            return;
        }
        readerNoteDialog = new ReaderNoteDialog(readerViewModel.getReaderDataHolder(), activity);
        readerNoteDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopupCopyClickEvent(PopupCopyClickEvent event) {
        new SelectTextCopyToClipboardAction().execute(readerViewModel.getReaderDataHolder(), null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopupTranslationClickEvent(PopupTranslationClickEvent event) {
        Activity activity = readerViewBack.getContext();
        if (activity == null) {
            return;
        }
        String text = readerViewModel.getReaderDataHolder().getReaderSelectionInfo().getSelectText();
        float x = readerViewModel.getReaderDataHolder().getSelectMenuModel().getLastX();
        float y = readerViewModel.getReaderDataHolder().getSelectMenuModel().getLastY();
        TranslateDialog translateDialog = new TranslateDialog(activity, text, readerViewModel.getEventBus(), x, y);
        translateDialog.show();
        translateDialog.setCanceledOnTouchOutside(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopupBaidupediaClickEvent(PopupBaidupediaClickEvent event) {
        Activity activity = readerViewBack.getContext();
        if (activity == null) {
            return;
        }
        String text = readerViewModel.getReaderDataHolder().getReaderSelectionInfo().getSelectText();
        DialogDict dialogDict = new DialogDict(activity, text);
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
        ToastUtil.showToast(readerViewModel.getReaderDataHolder().getAppContext(), messageId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenDocumentSuccessEvent(OpenDocumentSuccessEvent event){
        readerViewModel.getReaderDataHolder().setDocumentOpenState();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteAnnotationEvent(DeleteAnnotationEvent event){
        DeleteAnnotationAction action = new DeleteAnnotationAction(event.annotation);
        action.execute(readerViewModel.getReaderDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }
}
