package com.onyx.kcb.event;

import com.onyx.android.sdk.data.model.Metadata;

/**
 * Created by hehai on 17-11-15.
 */

public class MetadataItemClickEvent {
    private Metadata metadata;

    public MetadataItemClickEvent(Metadata metadata) {
        this.metadata = metadata;
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
