package com.onyx.android.sdk.data.rxrequest.data.fs;

import android.graphics.Bitmap;
import android.util.Log;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/9/10.
 */
public class RxStorageFileListLoadRequest extends RxBaseFSRequest {
    private static final String TAG = "StorageFileSystemLoad";

    private File targetDir;
    private List<String> filterFileList;
    private List<File> resultFileList = new ArrayList<>();
    private SortBy sortBy;
    private SortOrder sortOrder;
    private static Map<String, CloseableReference<Bitmap>> thumbnailMapCache = new HashMap<>();

    public RxStorageFileListLoadRequest(DataManager dataManager, File targetDir, List<String> filterFileList) {
        super(dataManager);
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
    public RxStorageFileListLoadRequest call() throws Exception {
        resultFileList = loadStorageFileList(targetDir);
        if (checkSortNeed()) {
            fileListSort(resultFileList, sortBy, sortOrder);
        }
        loadThumbnai();
        return this;
    }

    private void loadThumbnai() {
        if (resultFileList != null && !resultFileList.isEmpty()) {
            for (int i = 0; i < resultFileList.size(); i++) {
                File file = resultFileList.get(i);
                if (file.exists() && file.isFile()) {
                    String absolutePath = file.getAbsolutePath();
                    if (!thumbnailMapCache.containsKey(absolutePath)){
                        CloseableReference<Bitmap> bitmapCloseableReference = loadThumbnailSource(absolutePath);
                        if (bitmapCloseableReference != null && bitmapCloseableReference.isValid()) {
                            thumbnailMapCache.put(absolutePath, bitmapCloseableReference);
                        }
                    }
                }
                if (i >= Constant.PRE_LOAD_THUMBNAI_COUNT) {
                    return;
                }
            }
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
                FileUtils.sortListByName(fileList, sortOrder);
                break;
            case CreationTime:
                FileUtils.sortListByCreationTime(fileList, sortOrder);
                break;
            case FileType:
                FileUtils.sortListByFileType(fileList, sortOrder);
                break;
            case Size:
                FileUtils.sortListBySize(fileList, sortOrder);
                break;
        }
        Log.w(TAG, "Sort duration:" + benchmark.duration() + "ms");
    }

    private boolean checkSortNeed() {
        return sortBy != null && sortOrder != null;
    }

    private CloseableReference<Bitmap> loadThumbnailSource(String originContentPath) {
        return getDataManager().getCacheManager().getBitmapRefCache(originContentPath);
    }

    public Map<String, CloseableReference<Bitmap>> getThumbnailSourceCache() {
        return thumbnailMapCache;
    }
}
