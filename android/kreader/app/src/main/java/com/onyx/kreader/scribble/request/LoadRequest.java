package com.onyx.kreader.scribble.request;


import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.scribble.ShapeManager;
import com.onyx.kreader.scribble.data.ShapeModel;
import com.onyx.kreader.scribble.data.ShapeDataProvider;

import java.util.List;

/**
 * Created by zengzhu on 4/18/16.
 */
public class LoadRequest extends BaseScribbleRequest {

    private List<ShapeModel> list;

    public LoadRequest(final List<PageInfo> pages) {
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
