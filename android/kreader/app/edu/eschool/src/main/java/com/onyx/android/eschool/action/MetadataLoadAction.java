package com.onyx.android.eschool.action;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.request.data.db.LibraryLoadRequest;
import com.onyx.android.sdk.utils.CollectionUtils;

/**
 * Created by suicheng on 2017/4/15.
 */

public class MetadataLoadAction extends BaseAction<LibraryDataHolder> {
    private boolean showDialog = true;
    private boolean loadFromCache = true;

    private LibraryDataModel libraryDataModel;
    private QueryArgs queryArgs;

    public MetadataLoadAction(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    public MetadataLoadAction(QueryArgs queryArgs, boolean showDialog) {
        this.queryArgs = queryArgs;
        this.showDialog = showDialog;
    }

    public void setLoadFromCache(boolean loadFromCache) {
        this.loadFromCache = loadFromCache;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        final LibraryLoadRequest libraryRequest = new LibraryLoadRequest(queryArgs);
        libraryRequest.setLoadFromCache(loadFromCache);
        dataHolder.getDataManager().submit(dataHolder.getContext(), libraryRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
                if (e == null) {
                    libraryDataModel = new LibraryDataModel();
                    libraryDataModel.visibleLibraryList = libraryRequest.getLibraryList();
                    libraryDataModel.visibleBookList = libraryRequest.getBookList();
                    libraryDataModel.bookCount = (int) libraryRequest.getTotalCount();
                    libraryDataModel.thumbnailMap = libraryRequest.getThumbnailMap();
                    libraryDataModel.libraryCount = CollectionUtils.getSize(libraryRequest.getLibraryList());
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
        if (showDialog) {
            showLoadingDialog(dataHolder, R.string.loading);
        }
    }

    public LibraryDataModel getLibraryDataModel() {
        return libraryDataModel;
    }
}
