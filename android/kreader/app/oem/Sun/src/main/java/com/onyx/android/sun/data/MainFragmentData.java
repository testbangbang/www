package com.onyx.android.sun.data;

import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.PersonalAbilityResultBean;
import com.onyx.android.sun.requests.cloud.HomeworkUnfinishedRequest;
import com.onyx.android.sun.requests.cloud.SubjectAbilityRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hehai on 17-10-10.
 */

public class MainFragmentData {
    private Map<String, Float> scores = new HashMap<>();

    public Map<String, Float> getSubjectScoreMap() {
        return scores;
    }

    public void getSubjectScore(final SubjectAbilityRequest req, final BaseCallback baseCallback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                scores.clear();
                PersonalAbilityResultBean resultBean = req.getResultBean();
                if (resultBean != null && resultBean.data != null &&
                        !CollectionUtils.isNullOrEmpty(resultBean.data.subjectAbilityDtoList)) {
                    for (PersonalAbilityResultBean.DataBean.SubjectAbilityDtoListBean bean :
                            resultBean.data.subjectAbilityDtoList) {
                        scores.put(bean.course, bean.value);
                    }
                }
                invoke(baseCallback, request, e);
            }
        });
    }

    public void getPractices(HomeworkUnfinishedRequest req, BaseCallback baseCallback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), req, baseCallback);
    }
}
