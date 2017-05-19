package com.onyx.android.eschool.action;

import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.CloudLibrary;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.request.cloud.CloudLibraryListLoadRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;

import java.util.List;

/**
 * Created by suicheng on 2017/5/18.
 */

public class CloudLibraryListLoadAction extends BaseAction<LibraryDataHolder> {

    private List<CloudLibrary> libraryList;

    @Override
    public void execute(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        CloudManager cloudManager = dataHolder.getCloudManager();
        final CloudLibraryListLoadRequest loadRequest = new CloudLibraryListLoadRequest();
        cloudManager.submitRequest(dataHolder.getContext(), loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    ToastUtils.showToast(dataHolder.getContext(), "获取书库异常");
                    return;
                }
                libraryList = loadRequest.getLibraryList();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public List<CloudLibrary> getLibraryList() {
        return libraryList;
    }
}
