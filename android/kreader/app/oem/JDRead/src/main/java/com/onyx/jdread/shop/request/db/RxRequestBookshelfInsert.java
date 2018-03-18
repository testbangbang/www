package com.onyx.jdread.shop.request.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.exception.EmptyObjectException;

/**
 * Created by jackdeng on 2017/12/21.
 */

public class RxRequestBookshelfInsert extends RxBaseDBRequest {

    private Metadata metadata;

    public RxRequestBookshelfInsert(DataManager dm, Metadata metadata) {
        super(dm);
        this.metadata = metadata;
    }

    @Override
    public Object call() throws Exception {
        insert();
        return this;
    }

    public void insert() throws Exception{
        if (metadata != null) {
            Metadata findMeta = getDataProvider().findMetadataByIdString(getAppContext(), metadata.getIdString());
            if (findMeta != null && findMeta.hasValidId()) {
                getDataProvider().removeMetadata(getAppContext(), findMeta);
            }
            getDataProvider().saveMetadata(getAppContext(), metadata);
        } else {
            throw new EmptyObjectException(ResManager.getString(R.string.empty_object));
        }
    }
}
