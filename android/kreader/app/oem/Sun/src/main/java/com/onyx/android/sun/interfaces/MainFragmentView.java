package com.onyx.android.sun.interfaces;

import com.onyx.android.sun.cloud.bean.PersonalAbilityResultBean;

import com.onyx.android.sun.cloud.bean.ContentBean;

import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-10-10.
 */

public interface MainFragmentView {
    void setPractices(List<ContentBean> list);

    void setSubjectScore(PersonalAbilityResultBean.DataBean data, Map<String, Float> subjectScoreMap);
}
