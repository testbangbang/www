package com.onyx.android.sdk.dataprovider;

import android.content.Context;
import android.util.Log;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
import java.util.List;

/**
 * Created by zhuzeng on 8/27/16.
 */
public class LocalDataProvider implements DataProviderBase {

    public Metadata findMetadata(final Context context, final String path, String md5) {
        Metadata metadata = null;
        try {
            if (StringUtils.isNullOrEmpty(md5)) {
                md5 = FileUtils.computeMD5(new File(path));
            }
            metadata = new Select().from(Metadata.class).where(Metadata_Table.uniqueId.eq(md5)).querySingle();
            return metadata;
        } catch (Exception e) {
        }
        return metadata;
    }

    public Metadata loadMetadata(final Context context, final String path, String md5) {
        Metadata metadata = findMetadata(context, path, md5);
        if (metadata == null) {
            metadata = new Metadata();
        }
        return metadata;
    }

    public boolean saveDocumentOptions(final Context context, final String path, String md5, final String json) {
        try {
            Metadata document;
            final Metadata options = findMetadata(context, path, md5);
            if (options == null) {
                document = new Metadata();
                document.setUniqueId(md5);
            } else {
                document = options;
            }
            document.setExtraAttributes(json);
            if (options == null) {
                document.save();
            } else {
                document.update();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public final List<Annotation> loadAnnotations(final String application, final String md5, final String position) {
        return new Select().from(Annotation.class).where(Annotation_Table.uniqueId.eq(md5))
                .and(Annotation_Table.application.eq(application))
                .and(Annotation_Table.position.eq(position))
                .queryList();
    }

    public final List<Annotation> loadAnnotations(final String application, final String md5) {
        return new Select().from(Annotation.class).where(Annotation_Table.uniqueId.eq(md5))
                .and(Annotation_Table.application.eq(application))
                .orderBy(Annotation_Table.pageNumber, true)
                .queryList();
    }

    public void addAnnotation(final Annotation annotation){
        annotation.save();
    }

    public void updateAnnotation(final Annotation annotation){
        annotation.save();
    }

    public void deleteAnnotation(final Annotation annotation) {
        annotation.delete();
    }

    public final Bookmark loadBookmark(final String application, final String md5, final String position) {
        return new Select().from(Bookmark.class).where(Bookmark_Table.uniqueId.eq(md5))
                .and(Bookmark_Table.application.eq(application))
                .and(Bookmark_Table.position.eq(position))
                .querySingle();
    }

    public final List<Bookmark> loadBookmarks(final String application, final String md5) {
        return new Select().from(Bookmark.class).where(Bookmark_Table.uniqueId.eq(md5))
                .and(Bookmark_Table.application.eq(application))
                .orderBy(Bookmark_Table.pageNumber, true)
                .queryList();
    }

    public void addBookmark(final Bookmark bookmark) {
        bookmark.save();
    }

    public void deleteBookmark(final Bookmark bookmark) {
        bookmark.delete();
    }

}
