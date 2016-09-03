package com.onyx.android.sdk.data.provider;

import android.content.Context;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryCriteria;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.data.model.Metadata;
import com.raizlabs.android.dbflow.sql.language.OrderBy;

import java.util.List;

/**
 * Created by zhuzeng on 8/27/16.
 */
public interface DataProviderBase {


    void clearMetadata();

    void saveMetadata(final Context context, final Metadata metadata);

    Metadata findMetadata(final Context context, final String path, String md5);

    Metadata loadMetadata(final Context context, final String path, String md5);

    List<Metadata> findMetadata(final Context context, final QueryCriteria queryCriteria);

    List<Metadata> findMetadata(final Context context, final QueryArgs queryArgs);

    void removeMetadata(final Context context, final Metadata metadata);

    boolean saveDocumentOptions(final Context context, final String path, String md5, final String json);


    List<Annotation> loadAnnotations(final String application, final String md5, final String position, final OrderBy orderBy);

    List<Annotation> loadAnnotations(final String application, final String md5, final OrderBy orderBy);

    void addAnnotation(final Annotation annotation);

    void updateAnnotation(final Annotation annotation);

    void deleteAnnotation(final Annotation annotation);


    Bookmark loadBookmark(final String application, final String md5, final String position);

    List<Bookmark> loadBookmarks(final String application, final String md5, final OrderBy orderBy);

    void addBookmark(final Bookmark bookmark);

    void deleteBookmark(final Bookmark bookmark);
}
