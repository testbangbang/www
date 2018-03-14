package com.onyx.jdread.personal.request.local;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;

/**
 * Created by li on 2018/3/14.
 */

public class RxDeleteFileRequest extends RxBaseDBRequest {
    private File file;

    public RxDeleteFileRequest(DataManager dm, File file) {
        super(dm);
        this.file = file;
    }

    @Override
    public Object call() throws Exception {
        FileUtils.deleteFile(file, false);
        return this;
    }
}
