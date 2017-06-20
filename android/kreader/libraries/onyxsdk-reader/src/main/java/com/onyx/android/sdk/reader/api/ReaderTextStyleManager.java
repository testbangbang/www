package com.onyx.android.sdk.reader.api;

import com.onyx.android.sdk.data.ReaderTextStyle;

/**
 * Created by zhuzeng on 10/3/15.
 * Defined in host and used by plugin.
 */
public interface ReaderTextStyleManager {

    public ReaderTextStyle getStyle();
    public void setStyle(final ReaderTextStyle style);


}
