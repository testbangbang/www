package com.onyx.kreader.scribble.request;


import com.onyx.kreader.scribble.ShapeManager;
import com.onyx.kreader.scribble.data.ShapeModel;
import com.onyx.kreader.scribble.data.ShapeDataProvider;

import java.util.List;

/**
 * Created by zengzhu on 4/18/16.
 */
public class LoadRequest extends BaseScribbleRequest {

    private String documentMd5;
    private String pageName;
    private String subPageName;
    private List<ShapeModel> list;

    public LoadRequest(final String md5, final String pn, final String spn) {
        documentMd5 = md5;
        pageName = pn;
        subPageName = spn;
    }

    public void execute(final ShapeManager parent) throws Exception {
        list = ShapeDataProvider.loadShapeList(getContext(), documentMd5, pageName, subPageName);
    }

    public final List<ShapeModel> getList() {
        return list;
    }

}
