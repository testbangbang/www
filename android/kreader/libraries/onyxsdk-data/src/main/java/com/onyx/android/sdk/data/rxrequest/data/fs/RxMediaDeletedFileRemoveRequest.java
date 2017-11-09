package com.onyx.android.sdk.data.rxrequest.data.fs;

import android.content.ContentResolver;
import android.provider.MediaStore;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.MimeTypeUtils;

import java.util.List;

/**
 * Created by suicheng on 2017/3/28.
 */
public class RxMediaDeletedFileRemoveRequest extends RxBaseFSRequest {

    private List<String> targetPathList;

    public RxMediaDeletedFileRemoveRequest(DataManager dataManager,List<String> targetPathList) {
        super(dataManager);
        this.targetPathList = targetPathList;
    }

    @Override
    public RxMediaDeletedFileRemoveRequest call() throws Exception {
        proceedDeletedFileMediaInformation();
        return this;
    }

    private void proceedDeletedFileMediaInformation() {
        if (CollectionUtils.isNullOrEmpty(targetPathList)) {
            return;
        }
        ContentResolver resolver = getAppContext().getContentResolver();
        for (String deletedFilePath : targetPathList) {
            try {
                if (MimeTypeUtils.isAudioFile(FileUtils.getFileExtension(deletedFilePath))) {
                    resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.Audio.Media.DATA + "=" + "'" + deletedFilePath + "'", null);
                } else {
                    resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.Images.Media.DATA + "=" + "'" + deletedFilePath + "'", null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        targetPathList.clear();
    }
}
