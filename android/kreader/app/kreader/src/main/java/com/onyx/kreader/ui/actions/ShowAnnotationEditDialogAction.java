package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.dataprovider.Annotation;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.dialog.DialogAnnotation;

/**
 * Created by joy on 7/11/16.
 */
public class ShowAnnotationEditDialogAction extends BaseAction {

    private Annotation annotation;

    public ShowAnnotationEditDialogAction(final Annotation annotation) {
        this.annotation = annotation;
    }

    @Override
    public void execute(ReaderActivity readerActivity) {
        editAnnotationWithDialog(readerActivity, annotation);
    }

    private void editAnnotationWithDialog(final ReaderActivity readerActivity, final Annotation annotation) {
        DialogAnnotation dlg = new DialogAnnotation(readerActivity, DialogAnnotation.AnnotationAction.update, annotation.getNote(), new DialogAnnotation.Callback() {
            @Override
            public void onAddAnnotation(String note) {
            }

            @Override
            public void onUpdateAnnotation(String note) {
                updateAnnotation(readerActivity, annotation, note);
            }

            @Override
            public void onRemoveAnnotation() {
                deleteAnnotation(readerActivity, annotation);
            }
        });
        dlg.show();
    }

    private void updateAnnotation(final ReaderActivity readerActivity, final Annotation annotation, final String note) {
        PageInfo pageInfo = readerActivity.getReaderViewInfo().getPageInfo(annotation.getPosition());
        new UpdateAnnotationAction(pageInfo, annotation, note).execute(readerActivity);
    }

    private void deleteAnnotation(final ReaderActivity readerActivity, final Annotation annotation) {
        new DeleteAnnotationAction(annotation).execute(readerActivity);
    }
}
