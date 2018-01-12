package com.onyx.android.plato.presenter;

import android.support.annotation.NonNull;

import com.onyx.android.plato.cloud.bean.GetSubjectAbilityRequestBean;
import com.onyx.android.plato.cloud.bean.GetSubjectAbilityResultBean;
import com.onyx.android.plato.cloud.bean.SubjectBean;
import com.onyx.android.plato.data.GoalAdvancedFragmentData;
import com.onyx.android.plato.event.EmptyEvent;
import com.onyx.android.plato.interfaces.GoalAdvancedView;
import com.onyx.android.plato.requests.cloud.GetSubjectAbilityRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;
import com.onyx.android.plato.view.RosePieChart;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

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
                if (resultBean == null) {
                    EventBus.getDefault().post(new EmptyEvent());
                    return;
                }
                GetSubjectAbilityResultBean.AbilityBean data = resultBean.data;
                if (data != null) {
                    goalAdvancedView.setSubjectAbilityData(data);
                }
            }
        });
    }

    @NonNull
    public List<RosePieChart.PieEntry> convertAbilityBeanToPieEntry(GetSubjectAbilityResultBean.AbilityBean abilityBean) {
        List<RosePieChart.PieEntry> pieData = new ArrayList<>();
        for (int i = 0; i < abilityBean.modules.size(); i++) {
            GetSubjectAbilityResultBean.AbilityBean.ModulesBean modulesBean = abilityBean.modules.get(i);
            RosePieChart.PieEntry pieEntry = new RosePieChart.PieEntry();
            pieEntry.setName(modulesBean.name);
            pieEntry.setValue(modulesBean.score);
            pieData.add(pieEntry);
        }
        return pieData;
    }

    public List<SubjectBean> coverToSubjectBean(String[] source) {
        List<SubjectBean> list = new ArrayList();
        for (int i = 0; i < source.length; i++) {
            SubjectBean bean = new SubjectBean();
            bean.name = source[i];
            list.add(bean);
        }
        return list;
    }
}
