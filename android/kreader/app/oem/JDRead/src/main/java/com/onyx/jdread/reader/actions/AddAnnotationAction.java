package com.onyx.jdread.reader.actions;

import android.graphics.RectF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.highlight.SelectionInfo;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.AddAnnotationRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huxiaomao on 2018/1/20.
 */

public class AddAnnotationAction extends BaseReaderAction {
    private String note;

    public AddAnnotationAction(String note) {
        this.note = note;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        Map<String,SelectionInfo> readerSelectionInfos = new HashMap<>();
        for (HashMap.Entry<String, SelectionInfo> entry : readerDataHolder.getReaderSelectionInfo().getReaderSelectionInfos().entrySet()) {
            SelectionInfo copy = entry.getValue().clone();
            translateToDocument(copy.pageInfo, copy.getCurrentSelection().getRectangles());
            readerSelectionInfos.put(entry.getKey(), copy);
        }

        final AddAnnotationRequest request = new AddAnnotationRequest(readerDataHolder.getReader(),readerSelectionInfos,note);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder, request);
                if(baseCallback != null){
                    baseCallback.onNext(o);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
            }
        });
    }

    private List<RectF> translateToDocument(PageInfo pageInfo, List<RectF> rects) {
        for (RectF rect : rects) {
            PageUtils.translateToDocument(pageInfo, rect);
        }
        return rects;
    }
}
