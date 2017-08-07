package com.onyx.android.dr.data;


import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.local.PencilSketchQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;

/**
 * Created by zhouzhiming on 2017/8/3.
 */
public class PencilSketchData {
    private ArrayList<String> htmlTitle;

    public void submitRequest(Context context, final BaseDataRequest req, final BaseCallback callBack) {
        DataManager dataManager = DRApplication.getDataManager();
        dataManager.submit(context, req, callBack);
    }

    public void getAllPencilSketch(Context context, PencilSketchQueryAll req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }
}
