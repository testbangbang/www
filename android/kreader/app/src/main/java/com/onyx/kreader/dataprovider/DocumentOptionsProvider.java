package com.onyx.kreader.dataprovider;

import android.content.Context;
import com.onyx.kreader.host.options.BaseOptions;

/**
 * Created by zhuzeng on 5/27/16.
 * serves as proxy between request and function provider. it may forward request to real impl provider like
 * onyx android sdk or new sdk.
 */
public class DocumentOptionsProvider {

    public static BaseOptions loadDocumentOptions(final Context context, final String path) {
        return new BaseOptions();
    }

    public static void saveDocumentOptions(final Context context, final String path, final BaseOptions options) {

    }

}
