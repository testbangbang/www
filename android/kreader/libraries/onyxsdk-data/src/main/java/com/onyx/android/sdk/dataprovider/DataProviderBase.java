package com.onyx.android.sdk.dataprovider;

import android.content.Context;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhuzeng on 8/27/16.
 */
public interface DataProviderBase {

    Metadata findMetadata(final Context context, final String path, String md5);

    Metadata loadMetadata(final Context context, final String path, String md5);

    boolean saveDocumentOptions(final Context context, final String path, String md5, final String json);


    List<Annotation> loadAnnotations(final String application, final String md5, final String position);

    List<Annotation> loadAnnotations(final String application, final String md5);

    void addAnnotation(final Annotation annotation);

    void updateAnnotation(final Annotation annotation);

    void deleteAnnotation(final Annotation annotation);


    Bookmark loadBookmark(final String application, final String md5, final String position);

    List<Bookmark> loadBookmarks(final String application, final String md5);

    void addBookmark(final Bookmark bookmark);

    void deleteBookmark(final Bookmark bookmark);
}
