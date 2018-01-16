package com.onyx.android.eschool.action;

import com.onyx.android.eschool.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.request.cloud.v2.CloudLibraryContentListRequest;


/**
 * Created by suicheng on 2017/8/20.
 */
public class CloudContentLoadAction extends BaseAction<LibraryDataHolder> {

    private QueryArgs args;
    private LibraryDataModel libraryDataModel;

    public CloudContentLoadAction(QueryArgs queryArgs) {
        this.args = queryArgs;
    }

    public LibraryDataModel getDataModel() {
        return libraryDataModel;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        final CloudLibraryContentListRequest listRequest = new CloudLibraryContentListRequest(args);
        dataHolder.getCloudManager().submitRequestToSingle(dataHolder.getContext(), listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    BaseCallback.invoke(baseCallback, request, e);
                    return;
                }
                dataHolder.getCloudViewInfo().getCurrentQueryArgs().useMemCloudDbPolicy();
                libraryDataModel = LibraryDataModel.create(listRequest.getMetadataQueryResult(),
                        listRequest.getLibraryQueryResult(),
                        listRequest.getThumbnailMap());
                BaseCallback.invoke(baseCallback, request, null);
            }
        });
    }
}
