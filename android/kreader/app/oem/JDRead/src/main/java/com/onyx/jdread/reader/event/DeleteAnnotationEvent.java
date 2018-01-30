package com.onyx.jdread.reader.event;

import com.onyx.android.sdk.data.model.Annotation;

/**
 * Created by huxiaomao on 2018/1/29.
 */

public class DeleteAnnotationEvent {
    public Annotation annotation;

    public DeleteAnnotationEvent(Annotation annotation) {
        this.annotation = annotation;
    }
}
