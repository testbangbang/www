package com.onyx.einfo.action;

import android.content.Context;

import com.onyx.einfo.R;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.einfo.manager.ConfigPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.request.cloud.v2.CloudLibraryListLoadRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

/**
 * Created by suicheng on 2017/5/18.
 */

public class CloudLibraryListLoadAction extends BaseAction<LibraryDataHolder> {

    private String parentId;
    private List<Library> libraryList;

    public CloudLibraryListLoadAction() {
    }

    public CloudLibraryListLoadAction(String parentId) {
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        CloudManager cloudManager = dataHolder.getCloudManager();
        final CloudLibraryListLoadRequest loadRequest = new CloudLibraryListLoadRequest(parentId);
        cloudManager.submitRequest(dataHolder.getContext(), loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    libraryList = loadRequest.getLibraryList();
                } else {
                    ToastUtils.showToast(dataHolder.getContext().getApplicationContext(),
                            R.string.online_library_load_error);
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public List<Library> getLibraryList() {
        return libraryList;
    }
}
