package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.utils.TocUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.layout.LayoutProviderUtils;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

import java.util.List;

/**
 * Created by huxiaomao on 2018/1/30.
 */

public class GetTableOfContentRequest extends ReaderBaseRequest {
    private Reader reader;
    private boolean hasToc;
    private List<Integer> readTocChapterNodeList;

    public GetTableOfContentRequest(Reader reader) {
        this.reader = reader;
    }

    @Override
    public GetTableOfContentRequest call() throws Exception {
        LayoutProviderUtils.updateReaderViewInfo(reader,getReaderViewInfo(),reader.getReaderHelper().getReaderLayoutManager());
        getReaderUserDataInfo().loadDocumentTableOfContent(reader.getReaderHelper().getContext(), reader.getReaderHelper().getDocument());

        ReaderDocumentTableOfContent toc = getReaderUserDataInfo().getTableOfContent();
        hasToc = toc != null && !toc.isEmpty();
        if (!hasToc) {
            return this;
        }
        readTocChapterNodeList = TocUtils.buildChapterNodeList(toc);
        updateSetting(reader);
        return this;
    }

    public boolean isHasToc() {
        return hasToc;
    }

    public List<Integer> getReadTocChapterNodeList() {
        return readTocChapterNodeList;
    }
}
