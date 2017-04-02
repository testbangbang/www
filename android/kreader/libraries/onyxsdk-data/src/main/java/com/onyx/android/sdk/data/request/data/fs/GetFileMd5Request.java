package com.onyx.android.sdk.data.request.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;

/**
 * Created by ming on 2017/2/8.
 */

public class GetFileMd5Request extends BaseDataRequest {

    private String filePath;
    private String md5;

    public GetFileMd5Request(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        File file = new File(filePath);
        md5 = FileUtils.computeFullMD5Checksum(file);
    }

    public String getMd5() {
        return md5;
    }
}
