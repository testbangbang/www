package com.onyx.jdread.library.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;

/**
 * Created by hehai on 17-12-27.
 */

public class RxCopyBookToLibraryRequest extends RxBaseDBRequest {
    private File srcFile;
    private File targetFile;

    public RxCopyBookToLibraryRequest(DataManager dm, File srcFile, File targetFile) {
        super(dm);
        this.srcFile = srcFile;
        this.targetFile = targetFile;
    }

    @Override
    public RxCopyBookToLibraryRequest call() throws Exception {
        if (!srcFile.exists()) {
            return this;
        }
        FileUtils.copyFile(srcFile, targetFile);
        if (targetFile.exists()) {
            Metadata metadata = Metadata.createFromFile(targetFile);
            getDataProvider().saveMetadata(getAppContext(), metadata);
        }
        return this;
    }
}
