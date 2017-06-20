package com.onyx.android.sdk.reader.host.request;

import android.graphics.RectF;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ScaleByRectRequest extends BaseReaderRequest {

    private RectF childInDocument;
    private String pageName;

    public ScaleByRectRequest(final String name, final RectF c) {
        pageName = name;
        childInDocument = c;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getReaderLayoutManager().setSavePosition(true);
        reader.getReaderLayoutManager().setCurrentLayout(PageConstants.SINGLE_PAGE, new NavigationArgs());
        reader.getReaderLayoutManager().scaleByRect(pageName, childInDocument);
        drawVisiblePages(reader);
    }

    public static RectF rectInDocument(final PageInfo pageInfo, final RectF rectInScreen) {
        PageUtils.translateCoordinates(rectInScreen, pageInfo.getDisplayRect());
        rectInScreen.offset(pageInfo.getPositionRect().left, pageInfo.getPositionRect().top);
        return rectInScreen;
    }
}
