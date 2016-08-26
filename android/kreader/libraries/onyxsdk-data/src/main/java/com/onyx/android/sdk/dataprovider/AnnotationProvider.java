package com.onyx.android.sdk.dataprovider;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class AnnotationProvider {


    public static final List<Annotation> loadAnnotations(final String application, final String md5, final String position) {
        return new Select().from(Annotation.class).where(Annotation_Table.md5.eq(md5))
                .and(Annotation_Table.application.eq(application))
                .and(Annotation_Table.position.eq(position))
                .queryList();
    }

    public static final List<Annotation> loadAnnotations(final String application, final String md5) {
        return new Select().from(Annotation.class).where(Annotation_Table.md5.eq(md5))
                .and(Annotation_Table.application.eq(application))
                .orderBy(Annotation_Table.pageNumber, true)
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
