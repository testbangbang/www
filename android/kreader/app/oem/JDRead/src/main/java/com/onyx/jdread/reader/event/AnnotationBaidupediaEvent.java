package com.onyx.jdread.reader.event;

import com.onyx.android.sdk.data.model.Annotation;

/**
 * Created by huxiaomao on 2018/1/20.
 */

public class AnnotationBaidupediaEvent {
    private Annotation annotation;

    public AnnotationBaidupediaEvent(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {
        return annotation;
    }
}
