package com.onyx.jdread.reader.event;

import com.onyx.android.sdk.data.model.Annotation;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class UpdateNoteEvent {
    private Annotation annotation;

    public UpdateNoteEvent(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {
        return annotation;
    }
}
