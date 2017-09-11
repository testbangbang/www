package com.onyx.einfo.action;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.data.fs.FileCollectionRequest;
import com.onyx.android.sdk.data.request.data.fs.MediaDeletedFileRemoveRequest;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.MimeTypeUtils;
import com.onyx.einfo.holder.LibraryDataHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by suicheng on 2017/9/7.
 */

public class MediaScanAction extends BaseAction<LibraryDataHolder> {
    private static final String TAG = "MediaScanAction";

    private List<String> mediaDir = new ArrayList<>();
    private Set<String> mediaFilesSet = new LinkedHashSet<>();
    private boolean forceFullUpdate;

    public MediaScanAction(List<String> mediaDir, HashSet<String> mediaFilesCollection, boolean forceFullUpdate) {
        this.mediaDir = mediaDir;
        this.mediaFilesSet = mediaFilesCollection;
        this.forceFullUpdate = forceFullUpdate;
    }

    @Override
    public void execute(LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        scanMedia(dataHolder, mediaDir, forceFullUpdate);
    }

    public void enableMediaScanner(final Context context, boolean enable) {
        Intent intent = new Intent();
        intent.setAction("onyx.media.scanner.control");
        intent.putExtra("enable", enable);
        context.sendBroadcast(intent);
    }

    private List<String> generatePath(final List<String> fileName) {
        List<String> list = new ArrayList<>();
        for (String s : fileName) {
            File path = new File(EnvironmentUtil.getExternalStorageDirectory(), s);
            if (path.exists()) {
                list.add(path.getAbsolutePath());
            }
            path = new File(EnvironmentUtil.getRemovableSDCardDirectory(), s);
            if (path.exists()) {
                list.add(path.getAbsolutePath());
            }
        }
        return list;
    }

    private Set<String> mediaContentTypes() {
        Set<String> set = new HashSet<>();
        set.addAll(MimeTypeUtils.getImageExtension());
        set.addAll(MimeTypeUtils.getAudioExtension());
        return set;
    }

    private void scanMedia(LibraryDataHolder dataHolder, List<String> mediaDir, boolean forceFullUpdate) {
        List<String> list = new ArrayList<>();
        if (!CollectionUtils.isNullOrEmpty(mediaDir)) {
            list.addAll(mediaDir);
        }
        scanMediaFolders(dataHolder, list, forceFullUpdate);
    }

    private void scanMediaFolders(final LibraryDataHolder dataHolder, final List<String> dir,
                                  final boolean forceFullUpdate) {
        Log.i(TAG, "collect media files: " + dir.toString());
        final FileCollectionRequest collectionRequest = new FileCollectionRequest(generatePath(dir), mediaContentTypes()) {
            @Override
            public String getIdentifier() {
                return TAG;
            }
        };
        dataHolder.getDataManager().submitToMulti(dataHolder.getContext().getApplicationContext(), collectionRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                enableMediaScanner(dataHolder.getContext().getApplicationContext(), true);
                if (CollectionUtils.isNullOrEmpty(collectionRequest.getResultFileList())) {
                    return;
                }
                buildScanListAndSendScanAction(dataHolder, collectionRequest.getResultFileList(), forceFullUpdate);
            }
        });
    }

    private void buildScanListAndSendScanAction(final LibraryDataHolder dataHolder, final List<String> fileList,
                                                boolean forceFullUpdate) {
        List<String> removeList = new ArrayList<>();
        Iterator<String> iterator = mediaFilesSet.iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            if (!FileUtils.fileExist(path)) {
                removeList.add(path);
                iterator.remove();
                forceFullUpdate = true;
            }
        }

        for (String path : fileList) {
            if (mediaFilesSet.add(path)) {
                forceFullUpdate = true;
            }
        }

        if (removeList.size() > 0) {
            cleanNonExistentMediaFiles(dataHolder, removeList);
        }

        if (forceFullUpdate) {
            scanFileByMediaScanner(dataHolder);
        }
    }

    private void cleanNonExistentMediaFiles(final LibraryDataHolder dataHolder, final List<String> list) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        MediaDeletedFileRemoveRequest deletedFileRemoveRequest = new MediaDeletedFileRemoveRequest(list){
            @Override
            public String getIdentifier() {
                return TAG;
            }
        };
        dataHolder.getDataManager().submitToMulti(dataHolder.getContext().getApplicationContext(), deletedFileRemoveRequest,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e == null) {
                            list.clear();
                        }
                    }
                });
    }

    private void scanFileByMediaScanner(LibraryDataHolder dataHolder) {
        final String[] fileArray = mediaFilesSet.toArray(new String[mediaFilesSet.size()]);
        MediaScannerConnection.scanFile(dataHolder.getContext().getApplicationContext(), fileArray, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i(TAG, "Scan finished: " + path);
                    }
                });
    }
}
