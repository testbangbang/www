package com.onyx.android.eschool.action;

import android.util.Log;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentRefreshRequest;
import com.onyx.android.sdk.data.request.cloud.v2.PushNotificationLoadRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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
                QueryResult<Metadata> result = refreshRequest.getProductResult();
                if (e != null || result == null || result.hasException()) {
                    ToastUtils.showToast(request.getContext(), R.string.refresh_fail);
                    return;
                }
                if (result.isContentEmpty()) {
                    ToastUtils.showToast(request.getContext(), R.string.refresh_content_empty);
                }
                libraryDataModel = LibraryDataModel.create(result, null, refreshRequest.getThumbnailMap());
                if (libraryDataModel == null || CollectionUtils.isNullOrEmpty(libraryDataModel.visibleBookList)) {
                    BaseCallback.invoke(baseCallback, request, null);
                    return;
                }
                loadPushNotification(dataHolder, baseCallback);
            }
        });
        showLoadingDialog(dataHolder, R.string.refreshing);
    }

    private void loadPushNotification(LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        List<String> idList = new ArrayList<>();
        for (Metadata metadata : libraryDataModel.visibleBookList) {
            idList.add(metadata.getCloudId());
        }
        final PushNotificationLoadRequest loadRequest = new PushNotificationLoadRequest(idList, true);
        dataHolder.getCloudManager().submitRequest(dataHolder.getContext(), loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    libraryDataModel.notificationMap = loadRequest.getNotificationMap();
                }
                BaseCallback.invoke(baseCallback, request, null);
            }
        });
    }

    public LibraryDataModel getLibraryDataModel() {
        return libraryDataModel;
    }
}
