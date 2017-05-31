package com.onyx.android.sdk.data.db.table;

import android.net.Uri;

import com.onyx.android.sdk.data.db.OnyxAccountContentProvider;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;

/**
 * Created by suicheng on 2017/5/31.
 */
@TableEndpoint(name = EduAccountProvider.ENDPOINT,
        contentProvider = OnyxAccountContentProvider.class)
public class EduAccountProvider {

    public static final String ENDPOINT = "EduAccount";

    private static Uri buildUri(String... paths) {
        return OnyxAccountContentProvider.buildCommonUri(paths);
    }

    @ContentUri(path = EduAccountProvider.ENDPOINT,
            type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)
    public static Uri CONTENT_URI = buildUri(ENDPOINT);
}
