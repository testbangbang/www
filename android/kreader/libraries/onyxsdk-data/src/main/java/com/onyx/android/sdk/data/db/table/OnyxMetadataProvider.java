package com.onyx.android.sdk.data.db.table;

import android.net.Uri;

import com.onyx.android.sdk.data.db.OnyxDataBaseContentProvider;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;

/**
 * Created by suicheng on 2017/4/13.
 */
@TableEndpoint(name = OnyxMetadataProvider.ENDPOINT,
        contentProvider = OnyxDataBaseContentProvider.class)
public class OnyxMetadataProvider {
    public static final String ENDPOINT = "Metadata";

    private static Uri buildUri(String... paths) {
        return OnyxDataBaseContentProvider.buildCommonUri(paths);
    }

    @ContentUri(path = OnyxMetadataProvider.ENDPOINT,
            type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)
    public static Uri CONTENT_URI = OnyxDataBaseContentProvider.buildCommonUri((ENDPOINT));
}
