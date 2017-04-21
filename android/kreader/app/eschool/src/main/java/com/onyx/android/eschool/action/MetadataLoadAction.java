package com.onyx.android.eschool.action;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.request.data.db.LibraryLoadRequest;

/**
 * Created by suicheng on 2017/4/15.
 */

public class MetadataLoadAction extends BaseAction<LibraryDataHolder> {
    private boolean loadMore = false;
    private QueryArgs queryArgs;

    public MetadataLoadAction(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    public MetadataLoadAction(QueryArgs queryArgs, boolean loadMore) {
        this.queryArgs = queryArgs;
        this.loadMore = loadMore;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        final LibraryLoadRequest libraryRequest = new LibraryLoadRequest(queryArgs);
        dataHolder.getDataManager().submit(dataHolder.getContext(), libraryRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
                if (e == null) {
                    if (loadMore) {
                        dataHolder.getBookList().addAll(libraryRequest.getBookList());
                    } else {
                        dataHolder.setBookList(libraryRequest.getBookList());
                    }
                    dataHolder.setLibraryList(libraryRequest.getLibraryList());
                    dataHolder.setBookCount(libraryRequest.getTotalCount());
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
        showLoadingDialog(dataHolder, R.string.loading);
    }
}
