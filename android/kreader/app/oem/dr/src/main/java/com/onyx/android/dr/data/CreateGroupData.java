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
    private List<String> annualList = new ArrayList<>();
    private List<String> classList = new ArrayList<>();
    String[] gradeArray = DRApplication.getInstance().getResources().getStringArray(R.array.GradeList);
    String[] annualArray = DRApplication.getInstance().getResources().getStringArray(R.array.AnnualDataList);
    String[] classArray = DRApplication.getInstance().getResources().getStringArray(R.array.ClassDataList);

    public List<String> loadGradeData(Context context) {
        for (int i = 0; i < gradeArray.length; i++) {
            gradeList.add(gradeArray[i]);
        }
        return gradeList;
    }

    public List<String> loadAnnualData(Context context) {
        for (int i = 0; i < annualArray.length; i++) {
            annualList.add(annualArray[i]);
        }
        return annualList;
    }

    public List<String> loadClassData(Context context) {
        for (int i = 0; i < classArray.length; i++) {
            classList.add(classArray[i]);
        }
        return classList;
    }

    public void createGroup(CreateGroupRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }
}
