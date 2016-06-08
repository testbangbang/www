package com.onyx.kreader.scribble.request;


import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.scribble.ShapeManager;
import com.onyx.kreader.scribble.data.ShapeModel;
import com.onyx.kreader.scribble.data.ShapeDataProvider;

import java.util.List;

/**
 * Created by zengzhu on 4/18/16.
 */
public class LoadShapesRequest extends BaseScribbleRequest {

    private List<ShapeModel> list;

    public LoadShapesRequest(final String id, final List<PageInfo> pages) {
        setDocUniqueId(id);
        setVisiblePages(pages);
    }

    public void execute(final ShapeManager parent) throws Exception {
        loadShapeData();
    }

    public final List<ShapeModel> getList() {
        return list;
    }

    public void loadShapeData() {
        getShapeDataInfo().loadUserShape(getContext(), getDocUniqueId(), getVisiblePages());
    }

}
