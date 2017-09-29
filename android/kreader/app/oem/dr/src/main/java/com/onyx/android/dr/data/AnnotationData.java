package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.local.RequestGetAnnotationList;
import com.onyx.android.dr.request.local.RequestRemoveAnnotationList;
import com.onyx.android.sdk.common.request.BaseCallback;

/**
 * Created by hehai on 17-9-27.
 */

public class AnnotationData {
    public void getAnnotationList(RequestGetAnnotationList req, BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, baseCallback);
    }

    public void removeAnnotation(RequestRemoveAnnotationList req, BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, baseCallback);
    }
}
