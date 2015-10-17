package com.onyx.reader.host.request;

import android.graphics.RectF;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.wrapper.Reader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/17/15.
 */
public class SubScreenNavigationRequest extends BaseRequest {

    private float scale;
    private List<RectF> list = new ArrayList();

    public SubScreenNavigationRequest(final float s, final List<RectF> l) {
        list.addAll(l);
        scale = s;
    }

    // check page at first. and then goto the location.
    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().setSubScreenNavigation(scale, list);
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }

}
