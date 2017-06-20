package com.onyx.android.sdk.data.db;

import android.net.Uri;

import com.raizlabs.android.dbflow.annotation.provider.ContentProvider;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;

/**
 * Created by ming on 2017/2/8.
 */

@ContentProvider(authority = OnyxStatisticsContentProvider.AUTHORITY,
        database = OnyxStatisticsDatabase.class,
        baseContentUri = OnyxStatisticsContentProvider.BASE_CONTENT_URI)
public class OnyxStatisticsContentProvider {

    public static final String AUTHORITY = "com.onyx.kreader.statistics.provider";

    public static final String BASE_CONTENT_URI = "content://";

    @TableEndpoint(name = OnyxStatisticsProvider.ENDPOINT, contentProvider = OnyxStatisticsContentProvider.class)
    public static class OnyxStatisticsProvider {

        public static final String ENDPOINT = "OnyxStatisticsModel";

        private static Uri buildUri(String... paths) {
            Uri.Builder builder = Uri.parse(BASE_CONTENT_URI + AUTHORITY).buildUpon();
            for (String path : paths) {
                builder.appendPath(path);
            }
            return builder.build();
        }

        @ContentUri(path = OnyxStatisticsProvider.ENDPOINT,
                type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)
        public static Uri CONTENT_URI = buildUri(ENDPOINT);

    }
}
