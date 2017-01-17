package com.onyx.android.sdk.reader.host.request;

import android.graphics.RectF;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class ScaleRequest extends BaseReaderRequest {

    private String pageName;
    private float scale;
    private float x, y;

    public ScaleRequest(final String name, float s, float viewportX, float viewportY) {
        scale = s;
        x = viewportX;
        y = viewportY;
        pageName = name;
    }

    public void execute(final Reader reader) throws Exception {
        final RectF page = reader.getDocument().getPageOriginSize(pageName);
        final float toPageScale = PageUtils.scaleToPage(page.width(), page.height(), reader.getViewOptions().getViewWidth(), reader.getViewOptions().getViewHeight());
        scale = Math.max(scale, toPageScale);

        setSaveOptions(true);
        reader.getReaderLayoutManager().setSavePosition(true);
        reader.getReaderLayoutManager().setCurrentLayout(PageConstants.SINGLE_PAGE, new NavigationArgs());
        reader.getReaderLayoutManager().setScale(pageName, scale, x, y);
        drawVisiblePages(reader);
    }
}
