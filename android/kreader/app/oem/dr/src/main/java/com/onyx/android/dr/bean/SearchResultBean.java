package com.onyx.android.dr.bean;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.ProductCart;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;

/**
 * Created by hehai on 17-9-12.
 */

public class SearchResultBean {
    private boolean isFirst;
    private boolean isChecked;
    private Metadata metadata;

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public SearchResultBean(Metadata metadata) {
        this.metadata = metadata;
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
