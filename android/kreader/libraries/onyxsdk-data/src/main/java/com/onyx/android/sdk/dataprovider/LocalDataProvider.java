package com.onyx.android.sdk.dataprovider;

import android.content.Context;
import android.util.Log;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.QueryBuilder;
import com.raizlabs.android.dbflow.sql.language.*;
import com.raizlabs.android.dbflow.sql.language.property.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by zhuzeng on 8/27/16.
 */
public class LocalDataProvider implements DataProviderBase {

    public void clearMetadata() {
        Delete.table(Metadata.class);
    }

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

    public List<Metadata> findMetadata(final Context context, final QueryCriteria queryCriteria) {
        final ConditionGroup conditionGroup = queryCriteriaCondition(queryCriteria);
        if (conditionGroup.size() > 0) {
            return new Select().from(Metadata.class).where(conditionGroup).queryList();
        }
        return new ArrayList<>();
    }

    private static ConditionGroup queryCriteriaCondition(final QueryCriteria queryCriteria) {
        ConditionGroup group = ConditionGroup.clause();
        andWith(group, matchSet(Metadata_Table.authors, queryCriteria.author));
        andWith(group, matchSet(Metadata_Table.tags, queryCriteria.tags));
        andWith(group, matchSet(Metadata_Table.series, queryCriteria.series));
        andWith(group, matchSet(Metadata_Table.title, queryCriteria.title));
        andWith(group, matchSet(Metadata_Table.type, queryCriteria.fileType));
        return group;
    }

    private static void andWith(final ConditionGroup parent, final ConditionGroup child) {
        if (parent != null && child != null) {
            parent.and(child);
        }
    }

    private static ConditionGroup matchSet(final Property<String> property, final Set<String> set) {
        if (set == null || set.size() <= 0) {
            return null;
        }
        final ConditionGroup conditionGroup = ConditionGroup.clause();
        for (String string : set) {
            conditionGroup.or(property.like("%" + string + "%"));
        }
        return conditionGroup;
    }

    public void saveMetadata(final Context context, final Metadata metadata) {
        metadata.save();
    }

    public void removeMetadata(final Context context, final Metadata metadata) {
        metadata.delete();
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
