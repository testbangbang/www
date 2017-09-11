package com.onyx.android.dr.interfaces;

import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.PayBean;

/**
 * Created by hehai on 17-9-6.
 */

public interface BookDetailView {

    void setBookDetail(CloudMetadata metadata);

    void setOrderId(String id);
}
