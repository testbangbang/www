package com.onyx.android.sun.interfaces;

import com.onyx.android.sun.cloud.bean.GetSubjectAbilityResultBean;
import com.onyx.android.sun.cloud.bean.KnowledgeProgressResult;

/**
 * Created by jackdeng on 2017/11/2.
 */

public interface GoalAdvancedView {
    void setSubjectAbilityData(GetSubjectAbilityResultBean.AbilityBean abilityBean);
    void setKnowledgeProgressData(KnowledgeProgressResult resultBean);
}
