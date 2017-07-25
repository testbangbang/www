package com.onyx.einfo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onyx.einfo.InfoApp;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentImportFromJsonRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/4/18.
 */

public class ContentBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "ContentReceiver";

    public static final String CONTENT_EXTRA_NAME = "data";
    public static final String ACTION_LIBRARY_BUILD = "com.onyx.content.database.ACTION_BUILD_LIBRARY";
    public static final String ACTION_ADD_FILE_TO_LIBRARY = "com.onyx.content.database.ACTION_ADD_TO_LIBRARY";
    public static final String ACTION_ADD_JSON_CLOUD_METADATA = "com.onyx.content.database.ACTION_ADD_JSON_CLOUD_METADATA";

    @Override
    public void onReceive(Context context, Intent intent) {
        String data = intent.getStringExtra(CONTENT_EXTRA_NAME);
        if (ACTION_LIBRARY_BUILD.equals(intent.getAction())) {
        } else if (ACTION_ADD_FILE_TO_LIBRARY.equals(intent.getAction())) {
        } else if (ACTION_ADD_JSON_CLOUD_METADATA.equals(intent.getAction())) {
            cloudContentImportFromJson(context, data);
        }
    }

    public static void cloudContentImportFromJson(final Context context, String jsonFilePath) {
        List<String> filePathList = new ArrayList<>();
        filePathList.add(jsonFilePath);
        CloudContentImportFromJsonRequest listImportRequest = new CloudContentImportFromJsonRequest(filePathList);
        InfoApp.getCloudStore().getCloudManager().submitRequest(context, listImportRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                Log.i(TAG, "cloudContentImportFromJson " + (e == null ? "success" : "fail"));
            }
        });
    }
}
