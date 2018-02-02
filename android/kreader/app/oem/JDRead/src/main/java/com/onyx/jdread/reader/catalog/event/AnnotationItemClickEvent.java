package com.onyx.jdread.reader.catalog.event;

import com.onyx.android.sdk.data.model.Annotation;

/**
 * Created by huxiaomao on 2018/1/31.
 */

public class AnnotationItemClickEvent {
    private Annotation annotation;

    public AnnotationItemClickEvent(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {
        return annotation;
    }
}
