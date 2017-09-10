package com.onyx.android.sdk.data.request.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by suicheng on 2017/9/10.
 */
public class StorageFileListLoadRequest extends BaseDataRequest {

    private File targetDir;
    private List<String> filterFileList;
    private List<File> resultFileList = new ArrayList<>();

    public StorageFileListLoadRequest(File targetDir, List<String> filterFileList) {
        this.targetDir = targetDir;
        this.filterFileList = filterFileList;
    }

    public List<File> getResultFileList() {
        return resultFileList;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        resultFileList = loadStorageFileList(targetDir);
    }

    private List<File> loadStorageFileList(File targetDir) throws ContentException {
        List<File> fileList;
        if (!CollectionUtils.isNullOrEmpty(filterFileList)) {
            return new ArrayList<>(Arrays.asList(targetDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    for (String filterPath : filterFileList) {
                        boolean accept = filename.contains(FileUtils.getFileName(filterPath));
                        if (accept) {
                            return true;
                        }
                    }
                    return false;
                }
            })));
        }
        fileList = new ArrayList<>(Arrays.asList(targetDir.listFiles()));
        return fileList;
    }
}
