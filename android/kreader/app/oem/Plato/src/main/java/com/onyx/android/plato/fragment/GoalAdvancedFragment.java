package com.onyx.android.plato.fragment;


import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.adapter.CourseAdapter;
import com.onyx.android.plato.adapter.KnowledgeProgressAdapter;
import com.onyx.android.plato.cloud.bean.GetSubjectAbilityResultBean;
import com.onyx.android.plato.cloud.bean.KnowledgeProgressResult;
import com.onyx.android.plato.cloud.bean.SubjectBean;
import com.onyx.android.plato.databinding.FragmentGoalAdvancedBinding;
import com.onyx.android.plato.interfaces.GoalAdvancedView;
import com.onyx.android.plato.presenter.GoalAdvancedPresenter;
import com.onyx.android.plato.view.DisableScrollGridManager;
import com.onyx.android.plato.view.DividerItemDecoration;
import com.onyx.android.plato.view.RosePieChart;
import com.onyx.android.plato.view.RosePieChart.PieEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/11/1.
 */

public class GoalAdvancedFragment extends BaseFragment implements GoalAdvancedView {

    private FragmentGoalAdvancedBinding goalAdvancedBinding;
    private DividerItemDecoration dividerItemDecoration;
    private String[] course = SunApplication.getInstance().getResources().getStringArray(R.array.goal_advanced_course);
    private String[] term = SunApplication.getInstance().getResources().getStringArray(R.array.goal_advanced_term);
    private int termColumnCount = SunApplication.getInstance().getResources().getInteger(R.integer.term_adapter_col);
    private int termRowCount = SunApplication.getInstance().getResources().getInteger(R.integer.term_adapter_row);
    private int itemDividerSpace = SunApplication.getInstance().getResources().getInteger(R.integer.goal_advanced_recycler_view_item_divider_space);
    private KnowledgeProgressAdapter knowledgeProgressAdapter;
    private GoalAdvancedPresenter goalAdvancedPresenter;

    @Override
    protected void loadData() {
        goalAdvancedPresenter = new GoalAdvancedPresenter(this);
        goalAdvancedPresenter.getSubjectAbility(null,null,null);
    }

    @NonNull
    private List<PieEntry> convertAbilityBeanToPieEntry(GetSubjectAbilityResultBean.AbilityBean abilityBean) {
        List<PieEntry> pieData = new ArrayList<>();
        for (int i = 0; i < abilityBean.modules.size(); i++) {
            GetSubjectAbilityResultBean.AbilityBean.ModulesBean modulesBean = abilityBean.modules.get(i);
            PieEntry pieEntry = new PieEntry();
            pieEntry.setName(modulesBean.name);
            pieEntry.setValue(modulesBean.score);
            pieData.add(pieEntry);
        }
        return pieData;
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        initCommonData(binding);
        initCourseRecyclerView();
        initTermRecyclerView();
        initAbilityData();
    }

    private void initAbilityData() {
        goalAdvancedBinding.goalAdvancedKnProgressRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        goalAdvancedBinding.goalAdvancedKnProgressRecyclerView.addItemDecoration(dividerItemDecoration);
        knowledgeProgressAdapter = new KnowledgeProgressAdapter();
        goalAdvancedBinding.goalAdvancedKnProgressRecyclerView.setAdapter(knowledgeProgressAdapter);
        goalAdvancedBinding.goalAdvancedPieChart.setOnItemClickListener(new RosePieChart.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
    }

    private void initTermRecyclerView() {
        goalAdvancedBinding.goalAdvancedTermRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        goalAdvancedBinding.goalAdvancedTermRecyclerView.addItemDecoration(dividerItemDecoration);
        CourseAdapter termAdapter = new CourseAdapter(termColumnCount, termRowCount);
        termAdapter.setData(coverToSubjectBean(term));
        goalAdvancedBinding.goalAdvancedTermRecyclerView.setAdapter(termAdapter);
    }

    private List<SubjectBean> coverToSubjectBean(String[] source) {
        List<SubjectBean> list = new ArrayList();
        for (int i = 0; i < source.length; i++) {
            SubjectBean bean = new SubjectBean();
            bean.name = source[i];
            list.add(bean);
        }
        return list;
    }

    private void initCourseRecyclerView() {
        goalAdvancedBinding.goalAdvancedCourseRecyclerView.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        goalAdvancedBinding.goalAdvancedCourseRecyclerView.addItemDecoration(dividerItemDecoration);
        CourseAdapter courseAdapter = new CourseAdapter();
        courseAdapter.setData(coverToSubjectBean(course));
        goalAdvancedBinding.goalAdvancedCourseRecyclerView.setAdapter(courseAdapter);
    }

    private void initCommonData(ViewDataBinding binding) {
        goalAdvancedBinding = (FragmentGoalAdvancedBinding) binding;
        dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setSpace(itemDividerSpace);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_goal_advanced;
    }

    @Override
    public boolean onKeyBack() {
        return false;
    }

    @Override
    public void setSubjectAbilityData(GetSubjectAbilityResultBean.AbilityBean abilityBean) {
        if (abilityBean != null) {
            List<PieEntry> pieData = convertAbilityBeanToPieEntry(abilityBean);
            goalAdvancedBinding.goalAdvancedPieChart.setPieEntries(pieData);
            goalAdvancedBinding.setAbilityBean(abilityBean);
        }
    }

    @Override
    public void setKnowledgeProgressData(KnowledgeProgressResult resultBean) {
        if (resultBean != null && resultBean.data != null) {
            knowledgeProgressAdapter.setData(resultBean.data);
        }
    }
}
