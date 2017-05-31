package com.onyx.android.sdk.data.db;

import android.net.Uri;

import com.raizlabs.android.dbflow.annotation.provider.ContentProvider;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;

/**
 * Created by Onyx-lw on 2017/5/31.
 */
@ContentProvider(authority = OnyxDataBaseContentProvider.AUTHORITY,
        database = ContentDatabase.class,
        baseContentUri = OnyxDataBaseContentProvider.BASE_CONTENT_URI)
public class OnyxDataBaseContentProvider {
    public static final String AUTHORITY = "com.onyx.content.database.ContentProvider";
    public static final String BASE_CONTENT_URI = "content://";

    @TableEndpoint(name = AnnotationProvider.ENDPOINT, contentProvider = OnyxDataBaseContentProvider.class)
    public static class AnnotationProvider {

        public static final String ENDPOINT = "Annotation";

        private static Uri buildUri(String... paths) {
            Uri.Builder builder = Uri.parse(BASE_CONTENT_URI + AUTHORITY).buildUpon();
            for (String path : paths) {
                builder.appendPath(path);
            }
            return builder.build();
        }

        @ContentUri(path = AnnotationProvider.ENDPOINT,
                type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)
        public static Uri CONTENT_URI = buildUri(ENDPOINT);

    }

}
