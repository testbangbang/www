package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class UpdateViewPageRequest extends ReaderBaseRequest {

    public UpdateViewPageRequest(Reader reader) {
        super(reader);
    }

    @Override
    public UpdateViewPageRequest call() throws Exception {
        getReader().getReaderSelectionHelper().clear();
        updatePageView();
        updateSetting(getReader());
        reloadAnnotation();
        return this;
    }

    public void updatePageView() {
        getReader().getReaderViewHelper().updatePageView(getReader(), getReaderUserDataInfo(),getReaderViewInfo());
    }

    private void reloadAnnotation(){
        String displayName = getReader().getReaderHelper().getPlugin().displayName();
        String md5 = getReader().getReaderHelper().getDocumentMd5();

        getReaderUserDataInfo().loadDocumentAnnotations(getReader().getReaderHelper().getContext(), displayName, md5);
    }
}
