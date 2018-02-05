package com.onyx.jdread.reader.event;

import com.onyx.android.sdk.data.model.Annotation;

/**
 * Created by huxiaomao on 2018/1/20.
 */

public class AnnotationCopyEvent {
    private Annotation annotation;

    public AnnotationCopyEvent(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {
        return annotation;
    }
}
