package com.onyx.edu.reader.ui.actions;

import android.graphics.PointF;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.api.ReaderHitTestOptions;
import com.onyx.android.sdk.reader.host.impl.ReaderHitTestOptionsImpl;
import com.onyx.android.sdk.reader.host.request.SelectWordRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by Joy on 2016/6/3.
 */
public class SelectWordAction{

    public static void selectWord(final ReaderDataHolder readerDataHolder,
                                  final String pagePosition,
                                  final PointF startPoint,
                                  final PointF endPoint,
                                  final PointF touchPoint,
                                  final BaseCallback baseCallback) {
        execute(readerDataHolder, pagePosition, startPoint, endPoint, touchPoint, ReaderHitTestOptionsImpl.create(true), baseCallback);
    }

    public static void selectText(final ReaderDataHolder readerDataHolder,
                                  final String pagePosition,
                                  final PointF startPoint,
                                  final PointF endPoint,
                                  final PointF touchPoint,
                                  final BaseCallback baseCallback){
        execute(readerDataHolder, pagePosition, startPoint, endPoint, touchPoint, ReaderHitTestOptionsImpl.create(false), baseCallback);
    }

    private static void execute(final ReaderDataHolder readerDataHolder, final String pagePosition, final PointF startPoint, final PointF endPoint, final PointF touchPoint, final ReaderHitTestOptions hitTestOptions, final BaseCallback baseCallback){
        final SelectWordRequest selectWordRequest = new SelectWordRequest(pagePosition, startPoint, endPoint, touchPoint, hitTestOptions);
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), selectWordRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if(baseCallback != null){
                    baseCallback.done(request,e);
                }
            }
        });
    }
}