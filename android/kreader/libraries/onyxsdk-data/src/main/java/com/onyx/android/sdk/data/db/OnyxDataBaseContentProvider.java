package com.onyx.android.sdk.data.db;

import android.net.Uri;

import com.raizlabs.android.dbflow.annotation.provider.ContentProvider;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;

/**
 * Created by suicheng on 2017/4/12.
 */
@ContentProvider(authority = OnyxDataBaseContentProvider.AUTHORITY,
        database = ContentDatabase.class,
        baseContentUri = OnyxDataBaseContentProvider.BASE_CONTENT_URI)
public class OnyxDataBaseContentProvider {
    public static final String AUTHORITY = "com.onyx.content.database.ContentProvider";

    public static final String BASE_CONTENT_URI = "content://";

    public static Uri buildCommonUri(String... paths) {
        Uri.Builder builder = Uri.parse(BASE_CONTENT_URI + AUTHORITY).buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }
}

