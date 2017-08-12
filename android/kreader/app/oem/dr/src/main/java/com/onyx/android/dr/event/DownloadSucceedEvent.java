package com.onyx.android.dr.event;

import com.onyx.android.sdk.data.model.Metadata;

/**
 * Created by hehai on 17-8-4.
 */

public class DownloadSucceedEvent {
    private Metadata metadata;

    public DownloadSucceedEvent(Metadata metadata) {
        this.metadata = metadata;
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
