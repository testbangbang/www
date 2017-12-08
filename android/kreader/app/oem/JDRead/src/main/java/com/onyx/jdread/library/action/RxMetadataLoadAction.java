package com.onyx.jdread.library.action;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryLoadRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.library.model.LibraryViewDataModel;

/**
 * Created by suicheng on 2017/4/15.
 */

public class RxMetadataLoadAction extends BaseAction<DataBundle> {
    private boolean showDialog = true;
    private boolean loadFromCache = false;

    private QueryArgs queryArgs;
    private LibraryViewDataModel dataModel;
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

    @Override
    public void execute(final DataBundle dataHolder, final RxCallback baseCallback) {
        dataModel = dataHolder.getLibraryViewDataModel();
        final RxLibraryLoadRequest libraryRequest = new RxLibraryLoadRequest(dataHolder.getDataManager(), queryArgs, dataModel.getListSelected(), dataHolder.getEventBus(), loadMetadata);
        libraryRequest.setLoadFromCache(loadFromCache);
        RxLibraryLoadRequest.setAppContext(dataHolder.getAppContext());
        libraryRequest.execute(new RxCallback<RxLibraryLoadRequest>() {
            @Override
            public void onNext(RxLibraryLoadRequest rxLibraryLoadRequest) {
                hideLoadingDialog(dataHolder);
                if (baseCallback != null) {
                    dataModel.count.set((int) libraryRequest.getTotalCount() + CollectionUtils.getSize(libraryRequest.getLibraryList()));
                    dataModel.items.clear();
                    dataModel.items.addAll(libraryRequest.getModels());
                    dataModel.libraryCount.set(CollectionUtils.getSize(libraryRequest.getLibraryList()));
                    dataModel.getPageLibraryDataModel();
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
        });

        if (showDialog) {
            showLoadingDialog(dataHolder, R.string.loading);
        }
    }
}
