package com.onyx.android.dr.reader.event;

import com.onyx.android.sdk.data.model.Annotation;

/**
 * Created by hehai on 17-7-26.
 */

public class AnnotationsChangeEvent {
    private Annotation annotation;

    public AnnotationsChangeEvent(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {
        return annotation;
    }
}
