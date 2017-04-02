package com.onyx.android.sdk.data.request.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 02/04/2017.
 */

public class FileSystemScanRequest extends BaseFSRequest {

    private List<String> root;
    private List<String> result;

    public FileSystemScanRequest(final List<String> r) {
        root = r;
    }

    public void execute(final DataManager dataManager) throws Exception {
        result = new ArrayList<>();
        for(String path : root) {
            FileUtils.collectFiles(path, null, true, result);
        }
    }

    public List<String> getResult() {
        return result;
    }

}
