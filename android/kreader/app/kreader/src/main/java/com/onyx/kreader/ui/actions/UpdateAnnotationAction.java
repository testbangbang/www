package com.onyx.kreader.ui.actions;

import android.graphics.RectF;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.dataprovider.Annotation;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.request.AddAnnotationRequest;
import com.onyx.kreader.host.request.UpdateAnnotationRequest;
import com.onyx.kreader.ui.ReaderActivity;

import java.util.Date;
import java.util.List;

/**
 * Created by joy on 7/7/16.
 */
public class UpdateAnnotationAction extends BaseAction {

    private Annotation annotation;
    private String note;

    public UpdateAnnotationAction(Annotation annotation, String note) {
        this.annotation = annotation;
        this.note = note;
    }

    @Override
    public void execute(ReaderActivity readerActivity) {
        annotation.setNote(note);
        readerActivity.submitRequest(new UpdateAnnotationRequest(annotation));
    }
}
