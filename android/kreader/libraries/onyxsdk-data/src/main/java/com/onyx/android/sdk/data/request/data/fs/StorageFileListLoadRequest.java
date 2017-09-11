package com.onyx.android.sdk.data.request.data.fs;

import android.util.Log;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.ComparatorUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by suicheng on 2017/9/10.
 */
public class StorageFileListLoadRequest extends BaseDataRequest {
    private static final String TAG = "StorageFileSystemLoad";

    private File targetDir;
    private List<String> filterFileList;
    private List<File> resultFileList = new ArrayList<>();
    private SortBy sortBy;
    private SortOrder sortOrder;

    public StorageFileListLoadRequest(File targetDir, List<String> filterFileList) {
        this.targetDir = targetDir;
        this.filterFileList = filterFileList;
    }

    public void setSort(SortBy sortBy, SortOrder sortOrder) {
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public List<File> getResultFileList() {
        return resultFileList;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        resultFileList = loadStorageFileList(targetDir);
        if (checkSortNeed()) {
            fileListSort(resultFileList, sortBy, sortOrder);
        }
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

    private void fileListSort(final List<File> fileList, SortBy sortBy, final SortOrder sortOrder) {
        if (CollectionUtils.isNullOrEmpty(fileList)) {
            return;
        }
        Benchmark benchmark = new Benchmark();
        switch (sortBy) {
            case Name:
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        int i = ComparatorUtils.booleanComparator(lhs.isDirectory(), rhs.isDirectory(), SortOrder.Desc);
                        if (i == 0) {
                            return ComparatorUtils.stringComparator(lhs.getName(), rhs.getName(), sortOrder);
                        }
                        return i;
                    }
                });
                break;
            case CreationTime:
                //Todo:Java 6 and belows seems could only get file's last modified time,could not get creation time.
                //reference site:http://stackoverflow.com/questions/6885269/getting-date-time-of-creation-of-a-file
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        int i = ComparatorUtils.booleanComparator(
                                lhs.isDirectory(), rhs.isDirectory(), SortOrder.Desc);
                        if (i == 0) {
                            return ComparatorUtils.longComparator(lhs.lastModified(), rhs.lastModified(), sortOrder);
                        }
                        return i;
                    }
                });
                break;
            case FileType:
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        int i = ComparatorUtils.booleanComparator(lhs.isDirectory(), rhs.isDirectory(), SortOrder.Desc);
                        if (i == 0) {
                            return ComparatorUtils.stringComparator(FileUtils.getFileExtension(lhs),
                                    FileUtils.getFileExtension(rhs), sortOrder);
                        }
                        return i;
                    }
                });
                break;
            case Size:
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        int i = ComparatorUtils.booleanComparator(lhs.isDirectory(), rhs.isDirectory(), SortOrder.Desc);
                        if (i == 0) {
                            return ComparatorUtils.longComparator(lhs.length(),
                                    rhs.length(), sortOrder);
                        }
                        return i;
                    }
                });
                break;
        }
        Log.w(TAG, "Sort duration:" + benchmark.duration() + "ms");
    }

    private boolean checkSortNeed() {
        return sortBy != null && sortOrder != null;
    }
}
