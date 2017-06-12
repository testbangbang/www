package com.onyx.android.sdk.data.db;

import android.net.Uri;

import com.raizlabs.android.dbflow.annotation.provider.ContentProvider;

/**
 * Created by suicheng on 2017/5/31.
 */
@ContentProvider(authority = OnyxAccountContentProvider.AUTHORITY,
        database = AccountDatabase.class,
        baseContentUri = OnyxAccountContentProvider.BASE_CONTENT_URI)
public class OnyxAccountContentProvider {

    public static final String AUTHORITY = "com.onyx.account.database.ContentProvider";

    public static final String BASE_CONTENT_URI = "content://";

    public static Uri buildCommonUri(String... paths) {
        Uri.Builder builder = Uri.parse(BASE_CONTENT_URI + AUTHORITY).buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }
}
