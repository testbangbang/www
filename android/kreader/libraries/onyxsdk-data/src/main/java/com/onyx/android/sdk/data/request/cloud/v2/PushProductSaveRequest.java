package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.v2.PushProductEvent;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;

/**
 * Created by suicheng on 2017/8/2.
 */

public class PushProductSaveRequest extends BaseCloudRequest {

    private PushProductEvent pushProduct;

    public PushProductSaveRequest(PushProductEvent cloudPushFile) {
        this.pushProduct = cloudPushFile;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Library library = pushProduct.library;
        Metadata metadata = pushProduct.metadata;
        if (library != null) {
            parent.getCloudDataProvider().addLibrary(library);
        }
        if (metadata != null) {
            parent.getCloudDataProvider().saveMetadata(getContext(), metadata);
            DataManagerHelper.saveCloudCollection(getContext(), parent.getCloudDataProvider(), getLibraryId(library),
                    metadata.getAssociationId());
        }
    }

    private String getLibraryId(Library library) {
        return library == null ? null : library.getIdString();
    }
}
