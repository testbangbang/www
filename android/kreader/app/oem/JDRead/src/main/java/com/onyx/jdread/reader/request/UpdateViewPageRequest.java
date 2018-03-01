package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.ReaderViewHelper;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class UpdateViewPageRequest extends ReaderBaseRequest {
    private Reader reader;

    public UpdateViewPageRequest(Reader reader) {
        this.reader = reader;
    }

    @Override
    public UpdateViewPageRequest call() throws Exception {
        reader.getReaderSelectionHelper().clear();
        updatePageView();
        updateSetting(reader);
        reloadAnnotation();
        return this;
    }

    public void updatePageView() {
        reader.getReaderViewHelper().updatePageView(reader, getReaderUserDataInfo(),getReaderViewInfo());
    }

    private void reloadAnnotation(){
        String displayName = reader.getReaderHelper().getPlugin().displayName();
        String md5 = reader.getReaderHelper().getDocumentMd5();

        getReaderUserDataInfo().loadDocumentAnnotations(reader.getReaderHelper().getContext(), displayName, md5);
    }
}
