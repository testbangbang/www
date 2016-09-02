package com.onyx.kreader.ui.actions;

import android.graphics.PointF;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.api.ReaderHitTestOptions;
import com.onyx.kreader.host.impl.ReaderHitTestOptionsImpl;
import com.onyx.kreader.host.request.SelectWordRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by Joy on 2016/6/3.
 */
public class SelectWordAction{

    public static void selectWord(final ReaderDataHolder readerDataHolder, final String pageName, final PointF startPoint, final PointF endPoint, final BaseCallback baseCallback){
        execute(readerDataHolder, pageName, startPoint, endPoint, ReaderHitTestOptionsImpl.create(true), baseCallback);
    }

    public static void select(final ReaderDataHolder readerDataHolder, final String pageName, final PointF startPoint, final PointF endPoint, final BaseCallback baseCallback){
        execute(readerDataHolder, pageName, startPoint, endPoint, ReaderHitTestOptionsImpl.create(false), baseCallback);
    }

    private static void execute(final ReaderDataHolder readerDataHolder, final String pageName, final PointF startPoint, final PointF endPoint, final ReaderHitTestOptions hitTestOptions, final BaseCallback baseCallback){
        final SelectWordRequest selectWordRequest = new SelectWordRequest(pageName, startPoint, endPoint, hitTestOptions);
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