package com.onyx.android.sdk.data.provider;

import android.content.Context;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryCriteria;
import com.onyx.android.sdk.data.model.*;
import com.onyx.android.sdk.data.utils.MetadataQueryArgsBuilder;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
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
            metadata = new Select().from(Metadata.class).where(Metadata_Table.idString.eq(md5)).querySingle();
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
        final ConditionGroup conditionGroup = MetadataQueryArgsBuilder.queryCriteriaCondition(queryCriteria);
        if (conditionGroup.size() > 0) {
            return new Select().from(Metadata.class).where(conditionGroup).orderBy(queryCriteria.orderBy).offset(queryCriteria.offset).limit(queryCriteria.limit).queryList();
        }
        return new ArrayList<>();
    }

    public List<Metadata> findMetadata(final Context context, final QueryArgs queryArgs) {
        if (queryArgs.conditionGroup != null && queryArgs.conditionGroup.size() > 0) {
            Where<Metadata> where = new Select().from(Metadata.class).where(queryArgs.conditionGroup);
            if (queryArgs.orderByList != null && queryArgs.orderByList.size() > 0) {
                for (OrderBy orderBy : queryArgs.orderByList) {
                    where.orderBy(orderBy);
                }
            }
            return where.offset(queryArgs.offset).limit(queryArgs.limit).queryList();
        }
        return new ArrayList<>();
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
                document.setIdString(md5);
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


    public final List<Annotation> loadAnnotations(final String application, final String md5, final String position, final OrderBy orderBy) {
        return new Select().from(Annotation.class).where(Annotation_Table.idString.eq(md5))
                .and(Annotation_Table.application.eq(application))
                .and(Annotation_Table.position.eq(position))
                .orderBy(orderBy)
                .queryList();
    }

    public final List<Annotation> loadAnnotations(final String application, final String md5, final OrderBy orderBy) {
        return new Select().from(Annotation.class).where(Annotation_Table.idString.eq(md5))
                .and(Annotation_Table.application.eq(application))
                .orderBy(orderBy)
                .queryList();
    }

    public void addAnnotation(final Annotation annotation) {
        annotation.save();
    }

    public void updateAnnotation(final Annotation annotation) {
        annotation.save();
    }

    public void deleteAnnotation(final Annotation annotation) {
        annotation.delete();
    }

    public final Bookmark loadBookmark(final String application, final String md5, final String position) {
        return new Select().from(Bookmark.class).where(Bookmark_Table.idString.eq(md5))
                .and(Bookmark_Table.application.eq(application))
                .and(Bookmark_Table.position.eq(position))
                .querySingle();
    }

    public final List<Bookmark> loadBookmarks(final String application, final String md5, final OrderBy orderBy) {
        return new Select().from(Bookmark.class).where(Bookmark_Table.idString.eq(md5))
                .and(Bookmark_Table.application.eq(application))
                .orderBy(orderBy)
                .queryList();
    }

    public void addBookmark(final Bookmark bookmark) {
        bookmark.save();
    }

    public void deleteBookmark(final Bookmark bookmark) {
        bookmark.delete();
    }

}
