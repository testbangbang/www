package com.onyx.jdread.reader.actions;

import android.content.Context;
import android.content.res.AssetManager;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.CheckPreloadBookStateRequest;
import com.onyx.jdread.reader.utils.ReaderViewUtil;

import java.io.File;

/**
 * Created by huxiaomao on 2018/2/12.
 */

public class CheckPreloadBookStateAction extends BaseReaderAction {
    private String preloadBook;


    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        AssetManager am = readerDataHolder.getAppContext().getAssets();
        String applicationPath = readerDataHolder.getAppContext().getFilesDir().getAbsolutePath() + File.separator;
        final CheckPreloadBookStateRequest request = new CheckPreloadBookStateRequest(null,am,applicationPath);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                preloadBook = request.getPreloadBook();
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

    public String getPreloadBook() {
        return preloadBook;
    }
}
