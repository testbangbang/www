package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.requests.ExportAnnotationRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by ming on 16/10/17.
 */

public class ExportAnnotationAction extends BaseAction {

    private List<Annotation> annotations;
    private boolean append = false;
    private boolean showLoading = false;

    public ExportAnnotationAction(final List<Annotation> annotations, final boolean append, final boolean showLoading) {
        this.annotations = annotations;
        this.append = append;
        this.showLoading = showLoading;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        if (showLoading) {
            showLoadingDialog(readerDataHolder, R.string.exporting);
        }
        final ExportAnnotationRequest request = new ExportAnnotationRequest(annotations, append);
        readerDataHolder.submitNonRenderRequest(request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
                if (baseCallback != null) {
                    baseCallback.done(request, e);
                }
            }
        });
    }
}
