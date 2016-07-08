package com.onyx.kreader.dataprovider;

import android.content.Context;
import android.graphics.Rect;
import com.onyx.kreader.dataprovider.request.BaseDataProviderRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class AnnotationProvider {


    public static final List<Annotation> loadAnnotations(final String application, final String md5, final String position) {
        return new Select().from(Annotation.class).where(Bookmark_Table.md5.eq(md5))
                .and(Bookmark_Table.application.eq(application))
                .and(Bookmark_Table.position.eq(position))
                .queryList();
    }

    public static void addAnnotation(final Annotation annotation){
        annotation.save();
    }

    public static void updateAnnotation(final Annotation annotation){
        annotation.save();
    }

    public static void deleteAnnotation(final Annotation annotation) {
        annotation.delete();
    }

}
