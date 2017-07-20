package com.onyx.android.dr.reader.event;

import com.onyx.android.sdk.data.model.Annotation;

/**
 * Created by huxiaomao on 17/5/18.
 */

public class UpdateAnnotationResultEvent {
    private int position;
    private Annotation annotation;

    public int getPosition() {
        return position;
    }

    public UpdateAnnotationResultEvent setPosition(int position) {
        this.position = position;
        return this;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public UpdateAnnotationResultEvent setAnnotation(Annotation annotation) {
        this.annotation = annotation;
        return this;
    }
}
