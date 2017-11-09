package com.onyx.android.sdk.data.rxrequest.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;

/**
 * Created by ming on 2017/2/8.
 */

public class RxGetFileMd5Request extends RxBaseFSRequest {

    private String filePath;
    private String md5;

    public RxGetFileMd5Request(DataManager dataManager,String filePath) {
        super(dataManager);
        this.filePath = filePath;
    }

    @Override
    public RxGetFileMd5Request call() throws Exception {
        File file = new File(filePath);
        md5 = FileUtils.computeFullMD5Checksum(file);
        return this;
    }

    public String getMd5() {
        return md5;
    }
}
