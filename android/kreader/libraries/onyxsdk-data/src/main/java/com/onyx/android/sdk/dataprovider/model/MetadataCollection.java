package com.onyx.android.sdk.dataprovider.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by zhuzeng on 8/26/16.
 */
public class MetadataCollection extends BaseModel {

    @Column
    @Index
    String documentUniqueId = null;

    @Column
    @Index
    String libraryUniqueId = null;

    public String getDocumentUniqueId() {
        return documentUniqueId;
    }

    public void setDocumentUniqueId(String documentUniqueId) {
        this.documentUniqueId = documentUniqueId;
    }

    public String getLibraryUniqueId() {
        return libraryUniqueId;
    }

    public void setLibraryUniqueId(String libraryUniqueId) {
        this.libraryUniqueId = libraryUniqueId;
    }

}
