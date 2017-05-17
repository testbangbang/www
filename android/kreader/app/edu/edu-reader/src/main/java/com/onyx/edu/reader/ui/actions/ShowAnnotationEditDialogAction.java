package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.dialog.DialogAnnotation;

/**
 * Created by joy on 7/11/16.
 */
public class ShowAnnotationEditDialogAction extends BaseAction {

    public interface OnEditListener{
        void onUpdateFinished(Annotation annotation);
        void onDeleteFinished();
    }

    private Annotation annotation;

    public ShowAnnotationEditDialogAction(final Annotation annotation) {
        this.annotation = annotation;
    }

    private OnEditListener mOnEditListener;

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        editAnnotationWithDialog(readerDataHolder, annotation);
        BaseCallback.invoke(callback, null, null);
    }

    private void editAnnotationWithDialog(final ReaderDataHolder readerDataHolder, final Annotation annotation) {
        DialogAnnotation dlg = new DialogAnnotation(readerDataHolder.getContext(), DialogAnnotation.AnnotationAction.update, annotation.getNote(), new DialogAnnotation.Callback() {
            @Override
            public void onAddAnnotation(String note) {
            }

            @Override
            public void onUpdateAnnotation(String note) {
                updateAnnotation(readerDataHolder, annotation, note);
            }

            @Override
            public void onRemoveAnnotation() {
                deleteAnnotation(readerDataHolder, annotation);
            }
        });
        dlg.show();
    }

    private void updateAnnotation(final ReaderDataHolder readerDataHolder, final Annotation annotation, final String note) {
        new UpdateAnnotationAction(annotation, note).execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (mOnEditListener != null){
                    annotation.setNote(note);
                    mOnEditListener.onUpdateFinished(annotation);
                }
            }
        });
    }

    private void deleteAnnotation(final ReaderDataHolder readerDataHolder, final Annotation annotation) {
        new DeleteAnnotationAction(annotation).execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (mOnEditListener != null){
                    mOnEditListener.onDeleteFinished();
                }
            }
        });
    }

    public void setOnEditListener(OnEditListener onEditListener) {
        mOnEditListener = onEditListener;
    }
}
