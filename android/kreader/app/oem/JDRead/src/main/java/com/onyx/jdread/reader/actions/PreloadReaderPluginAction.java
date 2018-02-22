package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.AddAnnotationRequest;
import com.onyx.jdread.reader.request.PreloadReaderPluginRequest;

/**
 * Created by john on 18/2/2018.
 */

public class PreloadReaderPluginAction extends BaseReaderAction {

    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        PreloadReaderPluginRequest request = new PreloadReaderPluginRequest();
        request.execute(null);
    }

}
