package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.db.ContentDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by zhuzeng on 8/26/16.
 * Mapping from metadata id to library id.
 */
@Table(database = ContentDatabase.class)
public class MetadataCollection extends BaseData {

    @Column
    @Index
    private String documentUniqueId = null;

    @Column
    @Index
    private String libraryUniqueId = null;

    public MetadataCollection() {
    }

    public static MetadataCollection create(final String docId, final String libId) {
        MetadataCollection metadataCollection = new MetadataCollection();
        metadataCollection.documentUniqueId = docId;
        metadataCollection.libraryUniqueId = libId;
        return metadataCollection;
    }

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
