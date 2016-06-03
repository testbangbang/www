package com.onyx.kreader.scribble.request;


import com.onyx.kreader.scribble.ScribbleManager;
import com.onyx.kreader.scribble.data.Scribble;
import com.onyx.kreader.scribble.data.ScribbleDataProvider;

import java.util.List;

/**
 * Created by zengzhu on 4/18/16.
 */
public class LoadRequest extends BaseScribbleRequest {

    private String documentMd5;
    private String pageName;
    private String subPageName;
    private List<Scribble> list;

    public LoadRequest(final String md5, final String pn, final String spn) {
        documentMd5 = md5;
        pageName = pn;
        subPageName = spn;
    }

    public void execute(final ScribbleManager parent) throws Exception {
        list = ScribbleDataProvider.loadScribblePage(getContext(), documentMd5, pageName, subPageName);

    }

    public final List<Scribble> getList() {
        return list;
    }

}
