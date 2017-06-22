package com.onyx.android.sdk.data.db.table;

import android.net.Uri;

import com.onyx.android.sdk.data.db.OnyxSystemContentProvider;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;

/**
 * Created by suicheng on 2017/6/19.
 */
@TableEndpoint(name = OnyxSystemConfigProvider.ENDPOINT,
            contentProvider = OnyxSystemContentProvider.class)
public class OnyxSystemConfigProvider {
    public static final String ENDPOINT = "SystemKeyValueItem";

    private static Uri buildUri(String... paths) {
        return OnyxSystemContentProvider.buildCommonUri(paths);
    }

    @ContentUri(path = OnyxSystemConfigProvider.ENDPOINT,
            type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)
    public static Uri CONTENT_URI = buildUri(ENDPOINT);
}
