package com.onyx.android.libsetting.request;

import com.onyx.android.libsetting.SettingManager;
import com.onyx.android.libsetting.util.OTAUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 2017/2/11 18:35.
 */

public class CheckLocalFirmwareLegalityRequest extends BaseSettingRequest {

    public CheckLocalFirmwareLegalityRequest(List<String> pathList) {
        this.pathList = pathList;
    }

    private List<String> pathList = new ArrayList<>();
    public String targetPath = null;
    public boolean isLegal;

    @Override
    public void execute(SettingManager settingManager) throws Exception {
        for (String path : pathList) {
            if (OTAUtil.checkLocalUpdateZipLegality(path)) {
                targetPath = path;
                isLegal = true;
                break;
            }
        }
    }
}
