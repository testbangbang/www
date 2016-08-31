package com.onyx.android.sdk.data.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;

/**
 * Created by zhuzeng on 8/26/16.
 */
public class MetadataCollection extends BaseData {

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
