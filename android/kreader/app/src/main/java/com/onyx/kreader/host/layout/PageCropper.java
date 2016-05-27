package com.onyx.kreader.host.layout;

import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.math.PageManager;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.host.wrapper.ReaderHelper;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class PageCropper extends PageManager.PageCropProvider {

    private ReaderHelper helper;
    public PageCropper(final ReaderHelper h) {
        helper = h;
    }

    public float cropPage(final float displayWidth, final float displayHeight, final PageInfo pageInfo) {
        return 0;
    }


}
