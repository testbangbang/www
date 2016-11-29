package com.onyx.kreader.ui.handler;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.ui.utils.DialogHelp;
import com.onyx.kreader.R;
import com.onyx.kreader.common.PageAnnotation;
import com.onyx.kreader.ui.actions.PanAction;
import com.onyx.kreader.ui.actions.PinchZoomAction;
import com.onyx.kreader.ui.actions.ShowAnnotationEditDialogAction;
import com.onyx.kreader.ui.actions.ShowReaderMenuAction;
import com.onyx.kreader.ui.data.BookmarkIconFactory;
import com.onyx.kreader.ui.data.PageTurningDetector;
import com.onyx.kreader.ui.data.PageTurningDirection;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.events.QuitEvent;
import com.onyx.kreader.utils.DeviceConfig;

import java.util.List;


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
            DialogHelp.getConfirmDialog(readerDataHolder.getContext(), readerDataHolder.getContext().getString(R.string.sure_exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    postQuitEvent(readerDataHolder);
                }
            }).show();
        } else {
            postQuitEvent(readerDataHolder);
        }
    }

    private void postQuitEvent(final ReaderDataHolder readerDataHolder) {
        readerDataHolder.getEventBus().post(new QuitEvent());
    }

}
