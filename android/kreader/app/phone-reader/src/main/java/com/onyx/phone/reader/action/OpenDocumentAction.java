package com.onyx.phone.reader.action;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.request.CreateViewRequest;
import com.onyx.android.sdk.reader.host.request.LoadDocumentOptionsRequest;
import com.onyx.android.sdk.reader.host.request.OpenRequest;
import com.onyx.android.sdk.reader.host.request.RestoreRequest;
import com.onyx.android.sdk.reader.host.request.SaveDocumentOptionsRequest;
import com.onyx.phone.reader.reader.data.ReaderDataHolder;

/**
 * Created by ming on 2017/4/25.
 */

public class OpenDocumentAction extends BaseAction {

    private String documentPath;

    public OpenDocumentAction(String path) {
        this.documentPath = path;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        loadDocumentOptions(readerDataHolder);
    }

    private void loadDocumentOptions(final ReaderDataHolder readerDataHolder) {
        final LoadDocumentOptionsRequest loadDocumentOptionsRequest = new LoadDocumentOptionsRequest(documentPath,
                readerDataHolder.getReader().getDocumentMd5());
        readerDataHolder.getDataManager().submit(readerDataHolder.getContext(), loadDocumentOptionsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                openDocumentWithOptions(readerDataHolder, loadDocumentOptionsRequest.getDocumentOptions());
            }
        });
    }

    private void openDocumentWithOptions(final ReaderDataHolder readerDataHolder, final BaseOptions baseOptions) {
        OpenRequest openRequest = new OpenRequest(documentPath, baseOptions, null, false);
        readerDataHolder.submitNonRenderRequest(openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                createDocumentView(readerDataHolder, baseOptions);
            }
        });
    }

    private void createDocumentView(final ReaderDataHolder readerDataHolder, final BaseOptions baseOptions) {
        CreateViewRequest createViewRequest = new CreateViewRequest(readerDataHolder.getDisplayWidth(), readerDataHolder.getDisplayHeight());
        readerDataHolder.submitNonRenderRequest(createViewRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable throwable) {
                restoreWithOptions(readerDataHolder, baseOptions);
            }
        });
    }

    private void restoreWithOptions(final ReaderDataHolder readerDataHolder, final BaseOptions options) {
        final RestoreRequest restoreRequest = new RestoreRequest(options);
        readerDataHolder.submitRenderRequest(restoreRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                readerDataHolder.getHandlerManager().setEnable(true);
                readerDataHolder.submitNonRenderRequest(new SaveDocumentOptionsRequest());
                readerDataHolder.onDocumentInitRendered();
            }
        });
    }
}
