package com.onyx.android.sdk.data.db.table;

import android.net.Uri;

import com.onyx.android.sdk.data.db.OnyxDataBaseContentProvider;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;

/**
 * Created by suicheng on 2017/4/13.
 */

@TableEndpoint(name = OnyxAnnotationProvider.ENDPOINT,
        contentProvider = OnyxDataBaseContentProvider.class)
public class OnyxAnnotationProvider {

    public static final String ENDPOINT = "Annotation";

    private static Uri buildUri(String... paths) {
        return OnyxDataBaseContentProvider.buildCommonUri(paths);
    }

    @ContentUri(path = OnyxAnnotationProvider.ENDPOINT,
            type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)
    public static Uri CONTENT_URI = buildUri(ENDPOINT);
}
