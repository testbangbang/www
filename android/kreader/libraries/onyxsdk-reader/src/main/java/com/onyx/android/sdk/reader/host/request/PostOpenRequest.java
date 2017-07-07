package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class PostOpenRequest extends BaseReaderRequest {

    public PostOpenRequest() {
        setAbortPendingTasks(true);
    }

    public void execute(final Reader reader) throws Exception {
        saveReaderOptions(reader);
        reader.getReaderHelper().saveMetadata(getContext(), reader.getDocumentPath());
        reader.getReaderHelper().saveThumbnail(getContext(), reader.getDocumentPath());
        reader.getReaderHelper().initWordAnalyzerInBackground(getContext());
    }
}
