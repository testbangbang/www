package com.onyx.kreader.dataprovider;

import android.content.Context;
import com.onyx.kreader.host.options.BaseOptions;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class DocumentOptionsProvider {

    static public BaseOptions loadDocumentOptions(final Context context, final String path) {
        return new BaseOptions();
    }

    static public void saveDocumentOptions(final Context context, final String path, final BaseOptions options) {

    }

}
