package com.onyx.jdread.library.action;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryLoadRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.library.model.LibraryViewDataModel;
import com.onyx.jdread.main.action.BaseAction;

/**
 * Created by suicheng on 2017/4/15.
 */

public class RxMetadataLoadAction extends BaseAction<LibraryDataBundle> {
    private boolean showDialog = true;
    private boolean loadFromCache = false;
    private boolean clearLibraryCache = false;

    private QueryArgs queryArgs;
    private boolean loadMetadata = true;

    public RxMetadataLoadAction(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    public RxMetadataLoadAction(QueryArgs queryArgs, boolean showDialog) {
        this.queryArgs = queryArgs;
        this.showDialog = showDialog;
    }

    public void setLoadFromCache(boolean loadFromCache) {
        this.loadFromCache = loadFromCache;
    }

    public void setLoadMetadata(boolean loadMetadata) {
        this.loadMetadata = loadMetadata;
    }

    public void setClearLibraryCache(boolean clearLibraryCache) {
        this.clearLibraryCache = clearLibraryCache;
    }

    @Override
    public void execute(final LibraryDataBundle dataHolder, final RxCallback baseCallback) {
        if (clearLibraryCache) {
            dataHolder.getDataManager().getCacheManager().clearMetadataCache();
            dataHolder.getDataManager().getCacheManager().clearLibraryCache();
        }
        final LibraryViewDataModel dataModel = dataHolder.getLibraryViewDataModel();
        queryArgs.limit = dataModel.getQueryLimit();
        final RxLibraryLoadRequest libraryRequest = new RxLibraryLoadRequest(dataHolder.getDataManager(), queryArgs, dataModel.getLibrarySelectedModel().getSelectedList(), dataModel.getLibrarySelectedModel().isSelectedAll(), dataHolder.getEventBus(), loadMetadata);
        libraryRequest.setLoadFromCache(loadFromCache);
        RxLibraryLoadRequest.setAppContext(dataHolder.getAppContext());
        libraryRequest.execute(new RxCallback<RxLibraryLoadRequest>() {
            @Override
            public void onNext(RxLibraryLoadRequest rxLibraryLoadRequest) {
                if (baseCallback != null) {
                    dataModel.count.set((int) (libraryRequest.getTotalCount()));
                    dataModel.bookCount.set((int) (dataModel.libraryPathList.size() == 0 ? libraryRequest.getAllBookCount() : libraryRequest.getTotalCount()));
                    dataModel.libraryCount.set((int) libraryRequest.getLibraryCount());
                    dataModel.getLibrarySelectedModel().setCount(libraryRequest.getMetaDataCount());
                    dataModel.setPageData(libraryRequest.getModels());
                    baseCallback.onNext(rxLibraryLoadRequest);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (baseCallback != null) {
                    baseCallback.onError(throwable);
                }
            }

            @Override
            public void onFinally() {
                super.onFinally();
                hideLoadingDialog(dataHolder);
            }
        });

        if (showDialog) {
            showLoadingDialog(dataHolder, R.string.loading);
        }
    }
}
