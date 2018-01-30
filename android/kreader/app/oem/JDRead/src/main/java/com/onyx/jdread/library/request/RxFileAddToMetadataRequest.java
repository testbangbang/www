package com.onyx.jdread.library.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;

import java.io.File;

/**
 * Created by hehai on 17-12-27.
 */

public class RxFileAddToMetadataRequest extends RxBaseDBRequest {
    private File srcFile;

    public RxFileAddToMetadataRequest(DataManager dm, File srcFile) {
        super(dm);
        this.srcFile = srcFile;
    }

    @Override
    public RxFileAddToMetadataRequest call() throws Exception {
        if (!srcFile.exists()) {
            return this;
        }
        Metadata metadata = Metadata.createFromFile(srcFile,false);
        metadata.setHashTag(metadata.getNativeAbsolutePath());
        getDataProvider().saveMetadata(getAppContext(), metadata);
        return this;
    }
}
