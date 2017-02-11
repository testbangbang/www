package com.onyx.android.libsetting.action;

import android.content.Context;

import com.onyx.android.libsetting.SettingManager;
import com.onyx.android.libsetting.manager.OTAAdmin;
import com.onyx.android.libsetting.request.CheckLocalFirmwareLegalityRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 2017/2/11 19:02.
 */

public class CheckLocalFirmwareLegalityAction extends BaseSettingAction {
    public CheckLocalFirmwareLegalityAction(OTAAdmin.FirmwareCheckCallback callback, List<String> pathList) {
        this.callback = callback;
        this.pathList = pathList;
    }

    private List<String> pathList = new ArrayList<>();
    private OTAAdmin.FirmwareCheckCallback callback;

    @Override
    public void execute(Context context, SettingManager manager, final BaseCallback callback) {
        CheckLocalFirmwareLegalityRequest request = new CheckLocalFirmwareLegalityRequest(pathList);
        manager.submitRequest(context, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                CheckLocalFirmwareLegalityRequest req = (CheckLocalFirmwareLegalityRequest) request;
                CheckLocalFirmwareLegalityAction.this.callback.onPostCheck(
                        req.targetPath, req.isLegal);
            }
        });
    }

}
