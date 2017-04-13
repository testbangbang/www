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

    @TableEndpoint(name = OnyxDataBaseContentProvider.OnyxMetadataProvider.ENDPOINT,
            contentProvider = OnyxDataBaseContentProvider.class)
    public static class OnyxMetadataProvider {

        public static final String ENDPOINT = "Metadata";

        private static Uri buildUri(String... paths) {
            return buildCommonUri(paths);
        }

        @ContentUri(path = OnyxDataBaseContentProvider.OnyxMetadataProvider.ENDPOINT,
                type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)
        public static Uri CONTENT_URI = buildUri(ENDPOINT);
    }

    @TableEndpoint(name = OnyxDataBaseContentProvider.OnyxMetadataCollectionProvider.ENDPOINT,
            contentProvider = OnyxDataBaseContentProvider.class)
    public static class OnyxMetadataCollectionProvider {

        public static final String ENDPOINT = "MetadataCollection";

        private static Uri buildUri(String... paths) {
            return buildCommonUri(paths);
        }

        @ContentUri(path = OnyxDataBaseContentProvider.OnyxMetadataCollectionProvider.ENDPOINT,
                type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)
        public static Uri CONTENT_URI = buildUri(ENDPOINT);
    }

    @TableEndpoint(name = OnyxDataBaseContentProvider.OnyxLibraryProvider.ENDPOINT,
            contentProvider = OnyxDataBaseContentProvider.class)
    public static class OnyxLibraryProvider {

        public static final String ENDPOINT = "Library";

        private static Uri buildUri(String... paths) {
            return buildCommonUri(paths);
        }

        @ContentUri(path = OnyxDataBaseContentProvider.OnyxLibraryProvider.ENDPOINT,
                type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)
        public static Uri CONTENT_URI = buildUri(ENDPOINT);
    }

    @TableEndpoint(name = OnyxDataBaseContentProvider.OnyxAnnotationProvider.ENDPOINT,
            contentProvider = OnyxDataBaseContentProvider.class)
    public static class OnyxAnnotationProvider {

        public static final String ENDPOINT = "Annotation";

        private static Uri buildUri(String... paths) {
            return buildCommonUri(paths);
        }

        @ContentUri(path = OnyxDataBaseContentProvider.OnyxAnnotationProvider.ENDPOINT,
                type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)
        public static Uri CONTENT_URI = buildUri(ENDPOINT);
    }

    @TableEndpoint(name = OnyxDataBaseContentProvider.OnyxBookmarkProvider.ENDPOINT,
            contentProvider = OnyxDataBaseContentProvider.class)
    public static class OnyxBookmarkProvider {

        public static final String ENDPOINT = "Bookmark";

        private static Uri buildUri(String... paths) {
            return buildCommonUri(paths);
        }

        @ContentUri(path = OnyxDataBaseContentProvider.OnyxBookmarkProvider.ENDPOINT,
                type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)
        public static Uri CONTENT_URI = buildUri(ENDPOINT);
    }

    @TableEndpoint(name = OnyxDataBaseContentProvider.OnyxThumbnailProvider.ENDPOINT,
            contentProvider = OnyxDataBaseContentProvider.class)
    public static class OnyxThumbnailProvider {

        public static final String ENDPOINT = "Thumbnail";

        private static Uri buildUri(String... paths) {
            return buildCommonUri(paths);
        }

        @ContentUri(path = OnyxDataBaseContentProvider.OnyxThumbnailProvider.ENDPOINT,
                type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)
        public static Uri CONTENT_URI = buildUri(ENDPOINT);
    }

    private static Uri buildCommonUri(String... paths) {
        Uri.Builder builder = Uri.parse(BASE_CONTENT_URI + AUTHORITY).buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }
}

