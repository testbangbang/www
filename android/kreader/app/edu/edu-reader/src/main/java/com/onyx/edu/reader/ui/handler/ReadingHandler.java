package com.onyx.edu.reader.ui.handler;


import android.content.DialogInterface;

import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.data.SingletonSharedPreference;
import com.onyx.edu.reader.ui.events.ConfirmCloseDialogEvent;
import com.onyx.edu.reader.ui.events.QuitEvent;
import com.onyx.edu.reader.device.DeviceConfig;


/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 3/19/14
 * Time: 8:42 PM
 * Basic event handler.
 */
public class ReadingHandler extends BaseHandler {

    @SuppressWarnings("unused")
    private static final String TAG = ReadingHandler.class.getSimpleName();


    public ReadingHandler(HandlerManager p) {
        super(p);
    }


    @Override
    public void close(final ReaderDataHolder readerDataHolder) {
        final DeviceConfig deviceConfig = DeviceConfig.sharedInstance(readerDataHolder.getContext());
        if (SingletonSharedPreference.isShowQuitDialog(readerDataHolder.getContext()) || deviceConfig.isAskForClose()) {
            readerDataHolder.getEventBus().post(new ConfirmCloseDialogEvent(true));
            OnyxCustomDialog.getConfirmDialog(readerDataHolder.getContext(), readerDataHolder.getContext().getString(R.string.sure_exit),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            postQuitEvent(readerDataHolder);
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            postConfirmDialogOpenEvent(readerDataHolder, false);
                        }
                    }).show();
        } else {
            postQuitEvent(readerDataHolder);
        }
    }

    private void postQuitEvent(final ReaderDataHolder readerDataHolder) {
        readerDataHolder.getEventBus().post(new QuitEvent());
    }

    private void postConfirmDialogOpenEvent(final ReaderDataHolder readerDataHolder, boolean open) {
        readerDataHolder.getEventBus().post(new ConfirmCloseDialogEvent(open));
    }
}
