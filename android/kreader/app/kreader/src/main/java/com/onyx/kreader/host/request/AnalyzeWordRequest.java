package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.impl.ReaderTextSplitterImpl;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by ming on 16/9/30.
 */
public class AnalyzeWordRequest extends BaseReaderRequest{

    private String text;
    private boolean isWord;

    public AnalyzeWordRequest(String text) {
        this.text = text;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        isWord = ReaderTextSplitterImpl.sharedInstance().isWord(text);
    }

    public boolean isWord() {
        return isWord;
    }
}
