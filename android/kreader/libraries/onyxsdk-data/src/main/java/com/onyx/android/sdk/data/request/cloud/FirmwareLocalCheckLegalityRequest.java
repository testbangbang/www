package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.utils.OTAUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/3/11.
 */
public class FirmwareLocalCheckLegalityRequest extends BaseCloudRequest {

    private List<String> pathList = new ArrayList<>();
    public String targetPath = null;

    public FirmwareLocalCheckLegalityRequest(List<String> pathList) {
        this.pathList = pathList;
    }

    public String getLegalityTargetPath() {
        return targetPath;
    }

    @Override
    public void execute(CloudManager dataManager) throws Exception {
        for (String path : pathList) {
            if (OTAUtil.checkLocalUpdateZipLegality(path)) {
                targetPath = path;
                break;
            }
        }
    }
}
