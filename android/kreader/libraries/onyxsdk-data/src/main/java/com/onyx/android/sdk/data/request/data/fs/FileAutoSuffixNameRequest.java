package com.onyx.android.sdk.data.request.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.List;

/**
 * Created by ming on 2017/7/25.
 */

public class FileAutoSuffixNameRequest extends BaseDataRequest {

    private List<String> fileNames;

    private String fileName;
    private String fileNamePrefix;

    public FileAutoSuffixNameRequest(List<String> fileNames, String fileNamePrefix) {
        this.fileNames = fileNames;
        this.fileNamePrefix = fileNamePrefix;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        fileName = generateFileAutoSuffixName(fileNames);
    }

    public String getFileName() {
        return fileName;
    }

    private String generateFileAutoSuffixName(List<String> fileNames) {
        int size = fileNames.size();
        size++;
        String fileName = fileNamePrefix;
        for (int i = 0; i < size; i++) {
            String name = fileNamePrefix;
            if (i > 0) {
                name += i;
            }
            if (!matchFileName(name, fileNames)) {
                fileName = name;
                break;
            }
        }
        return fileName;
    }

    private boolean matchFileName(final String matchName, List<String> fileNames) {
        for (String file : fileNames) {
            if (file.equals(matchName)) {
                return true;
            }
        }
        return false;
    }
}
