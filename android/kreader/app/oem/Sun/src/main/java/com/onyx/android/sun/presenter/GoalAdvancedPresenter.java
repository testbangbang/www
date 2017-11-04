package com.onyx.android.sun.presenter;


import com.onyx.android.sun.cloud.bean.GetSubjectAbilityRequestBean;
import com.onyx.android.sun.cloud.bean.GetSubjectAbilityResultBean;
import com.onyx.android.sun.data.GoalAdvancedFragmentData;
import com.onyx.android.sun.interfaces.GoalAdvancedView;
import com.onyx.android.sun.requests.cloud.GetSubjectAbilityRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;

/**
 * Created by jackdeng on 2017/11/2.
 */

public class GoalAdvancedPresenter {
    private GoalAdvancedView goalAdvancedView;
    private GoalAdvancedFragmentData goalAdvancedFragmentData;

    public GoalAdvancedPresenter(GoalAdvancedView goalAdvancedView) {
        this.goalAdvancedView = goalAdvancedView;
        goalAdvancedFragmentData = new GoalAdvancedFragmentData();
    }

    public void getSubjectAbility(String id, String course, String term) {
        GetSubjectAbilityRequestBean requestBean = new GetSubjectAbilityRequestBean();
        requestBean.id = id;
        requestBean.course = course;
        requestBean.term = term;
        final GetSubjectAbilityRequest rq = new GetSubjectAbilityRequest(requestBean);
        goalAdvancedFragmentData.getSubjectAbility(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetSubjectAbilityResultBean resultBean = rq.getResultBean();
                if (resultBean == null || resultBean.data == null) {
                    return;
                }
                GetSubjectAbilityResultBean.AbilityBean data = resultBean.data;
                if (data != null) {
                    goalAdvancedView.setSubjectAbilityData(data);
                }
            }
        });
    }
}
