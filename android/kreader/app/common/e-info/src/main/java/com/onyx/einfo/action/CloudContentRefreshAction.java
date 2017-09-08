package com.onyx.einfo.action;

import android.graphics.Bitmap;
import android.util.Log;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.einfo.R;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentRefreshRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;

import java.util.Map;

/**
 * Created by suicheng on 2017/5/24.
 */
public class CloudContentRefreshAction extends BaseAction<LibraryDataHolder> {

    private LibraryDataModel libraryDataModel;

    @Override
    public void execute(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        LibraryViewInfo libraryViewInfo = dataHolder.getCloudViewInfo();
        final QueryArgs queryArgs = libraryViewInfo.buildLibraryQuery(libraryViewInfo.getCurrentQueryArgs().libraryUniqueId);
        queryArgs.resetOffset();
        queryArgs.useCloudOnlyPolicy();
        final CloudContentRefreshRequest refreshRequest = new CloudContentRefreshRequest(queryArgs);
        dataHolder.getCloudManager().submitRequest(dataHolder.getContext(), refreshRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
                QueryResult<Metadata> metadataResult = refreshRequest.getProductResult();
                QueryResult<Library> libraryResult = refreshRequest.getLibraryResult();
                if (e != null || metadataResult == null || metadataResult.hasException() || libraryResult == null) {
                    ToastUtils.showToast(request.getContext(), R.string.refresh_fail);
                    return;
                }
                if (!QueryResult.isValidQueryResult(libraryResult) && !QueryResult.isValidQueryResult(metadataResult)) {
                    ToastUtils.showToast(request.getContext(), R.string.refresh_content_empty);
                }
                libraryDataModel = getLibraryDataModel(libraryResult, metadataResult, refreshRequest.getThumbnailMap());
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
        showLoadingDialog(dataHolder, R.string.refreshing);
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Library> libraryResult,
                                                 QueryResult<Metadata> result,
                                                 Map<String, CloseableReference<Bitmap>> map) {
        LibraryDataModel libraryDataModel = LibraryViewInfo.buildLibraryDataModel(result, map);
        libraryDataModel.libraryCount = (int) libraryResult.count;
        libraryDataModel.visibleLibraryList = libraryResult.list;
        return libraryDataModel;
    }

    public LibraryDataModel getLibraryDataModel() {
        return libraryDataModel;
    }
}
