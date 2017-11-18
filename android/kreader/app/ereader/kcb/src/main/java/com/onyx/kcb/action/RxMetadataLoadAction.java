package com.onyx.kcb.action;

import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryLoadRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.kcb.R;
import com.onyx.kcb.holder.LibraryDataHolder;
import com.onyx.kcb.model.DataModel;
import com.onyx.kcb.model.LibraryViewDataModel;

/**
 * Created by suicheng on 2017/4/15.
 */

public class RxMetadataLoadAction extends BaseAction<LibraryDataHolder> {
    private boolean showDialog = true;
    private boolean loadFromCache = false;

    private LibraryDataModel libraryDataModel;
    private QueryArgs queryArgs;
    private LibraryViewDataModel dataModel;

    public RxMetadataLoadAction(LibraryViewDataModel dataModel, QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
        this.dataModel = dataModel;
    }

    public RxMetadataLoadAction(LibraryViewDataModel dataModel, QueryArgs queryArgs, boolean showDialog) {
        this.queryArgs = queryArgs;
        this.showDialog = showDialog;
        this.dataModel = dataModel;
    }

    public void setLoadFromCache(boolean loadFromCache) {
        this.loadFromCache = loadFromCache;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, final RxCallback baseCallback) {
        final RxLibraryLoadRequest libraryRequest = new RxLibraryLoadRequest(dataHolder.getDataManager(), queryArgs);
        libraryRequest.setLoadFromCache(loadFromCache);
        libraryRequest.execute(new RxCallback<RxLibraryLoadRequest>() {
            @Override
            public void onNext(RxLibraryLoadRequest rxLibraryLoadRequest) {
                hideLoadingDialog();
                libraryDataModel = new LibraryDataModel();
                libraryDataModel.visibleLibraryList = libraryRequest.getLibraryList();
                libraryDataModel.visibleBookList = libraryRequest.getBookList();
                libraryDataModel.bookCount = (int) libraryRequest.getTotalCount();
                libraryDataModel.thumbnailMap = libraryRequest.getThumbnailMap();
                libraryDataModel.libraryCount = CollectionUtils.getSize(libraryRequest.getLibraryList());

                dataModel.items.clear();
                dataModel.count.set(libraryDataModel.libraryCount + libraryDataModel.bookCount);
                LibraryDataModel pageLibraryDataModel = dataHolder.getLibraryViewInfo().getPageLibraryDataModel(libraryDataModel);
                LibraryViewDataModel.libraryToDataModel(dataModel.items, pageLibraryDataModel.visibleLibraryList);
                LibraryViewDataModel.metadataToDataModel(dataModel.items, pageLibraryDataModel.visibleBookList, libraryRequest.getThumbnailMap());
                if (baseCallback != null) {
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
