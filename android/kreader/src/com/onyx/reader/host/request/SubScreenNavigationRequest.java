package com.onyx.reader.host.request;

import android.graphics.RectF;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.navigation.NavigationList;
import com.onyx.reader.host.navigation.NavigationManager;
import com.onyx.reader.host.wrapper.Reader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/17/15.
 */
public class SubScreenNavigationRequest extends BaseRequest {

    private NavigationManager.Type type;
    private float scale;
    private NavigationList list;

    public SubScreenNavigationRequest(final NavigationManager.Type t, final NavigationList l) {
        type = t;
        list = l;
    }

    // check page at first. and then goto the location.
    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().setSubScreenNavigation(type, list);
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }

}
