package com.onyx.kreader.host.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.api.ReaderSentence;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.layout.LayoutProviderUtils;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.PagePositionUtils;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class GetSentenceRequest extends BaseReaderRequest {

    private int page;
    private String sentenceStartPosition;
    private ReaderSentence sentenceResult;

    public GetSentenceRequest(int page, final String sentenceStartPosition) {
        this.page = page;
        this.sentenceStartPosition = sentenceStartPosition;
    }

    public void execute(final Reader reader) throws Exception {
        createReaderViewInfo();
        String pageName = PagePositionUtils.fromPageNumber(page);
        PageInfo pageInfo = reader.getReaderLayoutManager().getPageManager().getPageInfo(pageName);
        sentenceResult = reader.getDocument().getSentence(pageName, sentenceStartPosition);
        LayoutProviderUtils.updateReaderViewInfo(getReaderViewInfo(), reader.getReaderLayoutManager());
        if (sentenceResult != null && sentenceResult.getReaderSelection().getRectangles().size() > 0) {
            getReaderUserDataInfo().saveHighlightResult(translateToScreen(pageInfo, sentenceResult.getReaderSelection()));
        }
    }

    public ReaderSentence getSentenceResult() {
        return sentenceResult;
    }

    private ReaderSelection translateToScreen(PageInfo pageInfo, ReaderSelection selection) {
        for (int i = 0; i < selection.getRectangles().size(); i++) {
            PageUtils.translate(pageInfo.getDisplayRect().left,
                    pageInfo.getDisplayRect().top,
                    pageInfo.getActualScale(),
                    selection.getRectangles().get(i));
        }
        return selection;
    }
}
