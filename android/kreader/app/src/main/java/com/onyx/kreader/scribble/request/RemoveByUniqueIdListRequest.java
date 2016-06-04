package com.onyx.kreader.scribble.request;

import com.onyx.kreader.scribble.ScribbleManager;
import com.onyx.kreader.scribble.data.Scribble;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 12/25/15.
 */
public class RemoveByUniqueIdListRequest extends BaseScribbleRequest {

    private String documentIndex;
    private List<Scribble> list = new ArrayList<Scribble>();


    public RemoveByUniqueIdListRequest(final String doc, final List<Scribble> l) {
        documentIndex = doc;
        list.addAll(l);
    }

    @Override
    public void execute(final ScribbleManager parent) throws Exception {
        // ask scribble provider to remove them.
    }
}
