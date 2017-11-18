package com.onyx.edu.student.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudLibraryContentListRequest;
import com.onyx.android.sdk.data.request.cloud.v2.PushNotificationLoadRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.student.holder.LibraryDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/8/20.
 */
public class CloudContentLoadAction extends BaseAction<LibraryDataHolder> {

    private QueryArgs args;
    private LibraryDataModel libraryDataModel;
    private boolean loadPushNotification;

    public CloudContentLoadAction(QueryArgs queryArgs, boolean loadNotification) {
        this.args = queryArgs;
        this.loadPushNotification = loadNotification;
    }

    public LibraryDataModel getDataModel() {
        return libraryDataModel;
    }

    @Override
    public void execute(final Context context, final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        final CloudLibraryContentListRequest listRequest = new CloudLibraryContentListRequest(args);
        dataHolder.getCloudManager().submitRequestToSingle(context, listRequest, new BaseCallback() {
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
                if (libraryDataModel == null || CollectionUtils.isNullOrEmpty(libraryDataModel.visibleBookList)) {
                    BaseCallback.invoke(baseCallback, request, null);
                    return;
                }
                if (loadPushNotification) {
                    loadPushNotification(context, dataHolder, baseCallback);
                }
            }
        });
    }

    private void loadPushNotification(Context context, LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        List<String> idList = new ArrayList<>();
        for (Metadata metadata : libraryDataModel.visibleBookList) {
            idList.add(metadata.getCloudId());
        }
        final PushNotificationLoadRequest loadRequest = new PushNotificationLoadRequest(idList, true);
        dataHolder.getCloudManager().submitRequest(context, loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    libraryDataModel.notificationMap = loadRequest.getNotificationMap();
                }
                BaseCallback.invoke(baseCallback, request, null);
            }
        });
    }
}
