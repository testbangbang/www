package com.onyx.kreader.scribble.request;

import com.onyx.kreader.scribble.ShapeViewHelper;
import com.onyx.kreader.scribble.data.ShapeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 12/25/15.
 */
public class RemoveByUniqueIdListRequest extends BaseScribbleRequest {

    private String documentIndex;
    private List<ShapeModel> list = new ArrayList<ShapeModel>();


    public RemoveByUniqueIdListRequest(final String doc, final List<ShapeModel> l) {
        documentIndex = doc;
        list.addAll(l);
    }

    @Override
    public void execute(final ShapeViewHelper parent) throws Exception {
        // ask scribble provider to removeShape them.
    }
}
