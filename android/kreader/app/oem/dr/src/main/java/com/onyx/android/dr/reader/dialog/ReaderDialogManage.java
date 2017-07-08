package com.onyx.android.dr.reader.dialog;


import com.onyx.android.dr.reader.event.DisplayStatusBarEvent;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 17/5/8.
 */

public class ReaderDialogManage {

    public static void onShowMainMenu(ReaderPresenter readerPresenter) {
        EventBus.getDefault().post(new DisplayStatusBarEvent(false));
        ReaderMainMenuDialog readerMainMenuDialog = new ReaderMainMenuDialog(readerPresenter,
                readerPresenter.getReaderView().getViewContext(), -1, null);
        readerMainMenuDialog.show();
    }

    public static void onShowBookInfoDialog(ReaderPresenter readerPresenter, int mode) {
        ReaderBookInfoDialog readerBookInfoDialog = new ReaderBookInfoDialog(readerPresenter,
                readerPresenter.getReaderView().getViewContext(), mode);
        readerBookInfoDialog.show();
    }
}
