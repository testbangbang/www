package com.onyx.android.dr.reader.action;

import android.graphics.PointF;

import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.api.ReaderHitTestOptions;
import com.onyx.android.sdk.reader.host.impl.ReaderHitTestOptionsImpl;
import com.onyx.android.sdk.reader.host.request.SelectWordRequest;

/**
 * Created by Joy on 2016/6/3.
 */
public class SelectWordAction {

    public static void selectWord(final ReaderPresenter readerPresenter,
                                  final String pagePosition,
                                  final PointF startPoint,
                                  final PointF endPoint,
                                  final PointF touchPoint,
                                  final BaseCallback baseCallback) {
        execute(readerPresenter,
                pagePosition,
                startPoint,
                endPoint,
                touchPoint,
                ReaderHitTestOptionsImpl.create(true),
                baseCallback);
    }

    public static void selectText(final ReaderPresenter readerPresenter,
                                  final String pagePosition,
                                  final PointF startPoint,
                                  final PointF endPoint,
                                  final PointF touchPoint,
                                  final BaseCallback baseCallback) {
        execute(readerPresenter,
                pagePosition,
                startPoint,
                endPoint,
                touchPoint,
                ReaderHitTestOptionsImpl.create(false),
                baseCallback);
    }

    private static void execute(final ReaderPresenter readerPresenter,
                                final String pagePosition,
                                final PointF startPoint,
                                final PointF endPoint,
                                final PointF touchPoint,
                                final ReaderHitTestOptions hitTestOptions,
                                final BaseCallback baseCallback) {
        final SelectWordRequest selectWordRequest = new SelectWordRequest(pagePosition, startPoint, endPoint, touchPoint, hitTestOptions);
        selectWordRequest.setContext(readerPresenter.getReaderView().getViewContext().getApplicationContext());
        readerPresenter.getReader().submitRequest(readerPresenter.getReaderView().getViewContext(), selectWordRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (baseCallback != null) {
                    baseCallback.done(request, e);
                }
            }
        });
    }
}