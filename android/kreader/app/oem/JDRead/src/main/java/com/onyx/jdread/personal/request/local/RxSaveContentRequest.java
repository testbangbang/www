package com.onyx.jdread.personal.request.local;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;

/**
 * Created by li on 2018/3/14.
 */

public class RxSaveContentRequest extends RxBaseDBRequest {
    private String content;
    private File file;
    private boolean result;

    public RxSaveContentRequest(DataManager dm, File file, String content) {
        super(dm);
        this.file = file;
        this.content = content;
    }

    @Override
    public Object call() throws Exception {
        if (file.exists()) {
            file.delete();
        }
        result = FileUtils.saveContentToFile(content, file);
        return this;
    }

    public boolean getResult() {
        return result;
    }
}
