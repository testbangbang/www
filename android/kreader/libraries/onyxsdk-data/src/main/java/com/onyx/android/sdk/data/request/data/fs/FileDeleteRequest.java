package com.onyx.android.sdk.data.request.data.fs;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.FileErrorPolicy;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by suicheng on 2017/9/20.
 */
public class FileDeleteRequest extends BaseFSRequest {

    private List<File> sourceFiles;
    private List<File> sourceFlattenedFileList;
    private List<File> skipSourceFileList = new ArrayList<>();

    public FileErrorPolicy errorPolicy = FileErrorPolicy.Retry;

    private AtomicBoolean abortHolder = new AtomicBoolean();

    public FileDeleteRequest(final List<File> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    public FileDeleteRequest(final List<File> sourceFiles, FileErrorPolicy errorPolicy) {
        this.sourceFiles = sourceFiles;
        this.errorPolicy = errorPolicy;
    }

    public void setSkipSourceFileList(List<File> skipSourceFileList) {
        this.skipSourceFileList = skipSourceFileList;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        processFileRemove();
    }

    private void processFileRemove() throws ContentException {
        if (sourceFlattenedFileList == null) {
            sourceFlattenedFileList = getFlattenedFileList(sourceFiles);
        }
        while (true) {
            if (isAbort() || CollectionUtils.isNullOrEmpty(sourceFlattenedFileList)) {
                return;
            }
            File file = getFlattenedListFirstItem();
            if (isFileSkipped(file)) {
                removeFlattenedListFirstItem();
                continue;
            }

            if (!file.delete()) {
                if (isFileErrorSkippedByUser()) {
                    removeFlattenedListFirstItem();
                    skipSourceFileList.add(file);
                    if (errorPolicy == FileErrorPolicy.Skip) {
                        errorPolicy = FileErrorPolicy.Retry;
                    }
                    continue;
                } else {
                    throw new ContentException.FileDeleteException(file);
                }
            }
            removeFlattenedListFirstItem();
        }
    }

    private ArrayList<File> getFlattenedFileList(List<File> pathFileList) {
        ArrayList<File> flattenedFiles = new ArrayList<>();
        if (CollectionUtils.isNullOrEmpty(pathFileList)) {
            return flattenedFiles;
        }
        for (File file : pathFileList) {
            FileUtils.collectFileTree(file, flattenedFiles, abortHolder);
        }
        Collections.reverse(flattenedFiles);
        return flattenedFiles;
    }

    private void removeFlattenedListFirstItem() {
        sourceFlattenedFileList.remove(0);
    }

    private File getFlattenedListFirstItem() {
        return sourceFlattenedFileList.get(0);
    }

    private boolean isFileSkipped(File file) {
        for (File skipFile : skipSourceFileList) {
            if (skipFile.getAbsolutePath().startsWith(file.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    private boolean isFileErrorSkippedByUser() {
        return errorPolicy.isSkipPolicy();
    }

    @Override
    public void setAbort() {
        super.setAbort();
        abortHolder.set(isAbort());
    }
}
