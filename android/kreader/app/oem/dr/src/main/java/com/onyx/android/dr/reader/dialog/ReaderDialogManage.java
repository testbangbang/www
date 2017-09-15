package com.onyx.android.dr.reader.dialog;


import com.onyx.android.dr.reader.activity.ReaderActivity;
import com.onyx.android.dr.reader.event.DisplayStatusBarEvent;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 17/5/8.
 */

public class ReaderDialogManage {
    public static void onShowMainMenu(ReaderPresenter readerPresenter) {
        EventBus.getDefault().post(new DisplayStatusBarEvent(false));
        ReaderActivity readerActivity = (ReaderActivity) readerPresenter.getReaderView().getViewContext();
        ReaderBottomDialog readerBottomDialog = new ReaderBottomDialog(readerPresenter, readerActivity, -1, null);
        readerBottomDialog.show();
    }

    public static void onShowBookInfoDialog(ReaderPresenter readerPresenter, int mode) {
        ReaderBookInfoDialog readerBookInfoDialog = new ReaderBookInfoDialog(readerPresenter,
                readerPresenter.getReaderView().getViewContext(), mode);
        readerBookInfoDialog.show();
    }

    public static void onShowPostilMangeDialog(ReaderPresenter readerPresenter) {
        PostilManageDialog postilManageDialog = new PostilManageDialog(readerPresenter, readerPresenter.getReaderView().getViewContext());
        postilManageDialog.show();
    }

    public static void onShowSettingMenu(ReaderPresenter readerPresenter) {
        ReaderMainMenuDialog readerMainMenuDialog = new ReaderMainMenuDialog(readerPresenter,
                readerPresenter.getReaderView().getViewContext(), -1, null);
        readerMainMenuDialog.show();
    }

    public static void onShowAfterReadingMenu(ReaderPresenter readerPresenter){
        AfterReadingDialog readerMainMenuDialog = new AfterReadingDialog(readerPresenter, readerPresenter.getReaderView().getViewContext());
        readerMainMenuDialog.show();
    }
}
