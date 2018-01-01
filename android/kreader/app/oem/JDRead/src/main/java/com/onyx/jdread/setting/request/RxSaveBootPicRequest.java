package com.onyx.jdread.setting.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxBaseFSRequest;
import com.onyx.jdread.setting.utils.ScreenSaversUtil;

/**
 * Created by hehai on 17-3-28.
 */

public class RxSaveBootPicRequest extends RxBaseFSRequest {
    private String sourcePath;
    private String destPath;

    public RxSaveBootPicRequest(DataManager dataManager, String sourcePath, String destPath) {
        super(dataManager);
        this.sourcePath = sourcePath;
        this.destPath = destPath;
    }

    @Override
    public RxSaveBootPicRequest call() throws Exception {
        ScreenSaversUtil.saveScreen(sourcePath, destPath);
        return this;
    }
}
