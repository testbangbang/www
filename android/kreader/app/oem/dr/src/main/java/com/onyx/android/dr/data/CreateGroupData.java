package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.request.cloud.CreateGroupRequest;
import com.onyx.android.sdk.common.request.BaseCallback;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhouzhiming on 2017/8/29.
 */
public class CreateGroupData {
    private List<String> gradeList = new ArrayList<>();
    String[] gradeArray = DRApplication.getInstance().getResources().getStringArray(R.array.GradeList);

    public List<String> loadGradeData(Context context) {
        for (int i = 0; i < gradeArray.length; i++) {
            gradeList.add(gradeArray[i]);
        }
        return gradeList;
    }

    public void createGroup(CreateGroupRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }
}
