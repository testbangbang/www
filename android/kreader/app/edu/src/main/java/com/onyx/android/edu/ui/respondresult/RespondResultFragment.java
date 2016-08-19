package com.onyx.android.edu.ui.respondresult;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.ResultMultiAdapter;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.bean.PaperResult;
import com.onyx.android.edu.ui.exerciserespond.ExerciseRespondActivity;
import com.onyx.android.sdk.ui.view.DynamicMultiRadioGroupView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/6/28.
 */
public class RespondResultFragment extends BaseFragment implements RespondResultContract.View, View.OnClickListener {

    @Bind(R.id.analysis_wrong_button)
    Button analysisWrongButton;
    @Bind(R.id.result_radio_group)
    DynamicMultiRadioGroupView resultRadioGroup;
    @Bind(R.id.all_analysis_button)
    Button allAnalysisButton;

    private RespondResultContract.Presenter mPresenter;

    public static RespondResultFragment newInstance() {
        return new RespondResultFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_respond_result;
    }

    protected void initView() {
        mPresenter.subscribe();
        analysisWrongButton.setOnClickListener(this);
        allAnalysisButton.setOnClickListener(this);

        initResultView();
    }

    private void initResultView() {
        int count = 4;
        String[] texts = new String[count];
        for (int i = 0; i < count; i++) {
            texts[i] = (i + 1) + "";
        }
        List<Integer> rights = new ArrayList<>();
        rights.add(1);
        rights.add(2);
        ResultMultiAdapter resultMultiAdapter = new ResultMultiAdapter(texts, rights, 1, 8, R.drawable.wrong_oval);
        resultRadioGroup.setMultiAdapter(resultMultiAdapter);
    }

    @Override
    public void showResult(PaperResult paperResult) {
    }

    @Override
    public void setPresenter(RespondResultContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unSubscribe();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(analysisWrongButton) || v.equals(allAnalysisButton)) {
            Intent intent = new Intent(getActivity(), ExerciseRespondActivity.class);
            intent.putExtra(ExerciseRespondActivity.SHOW_ANSWER, true);
            startActivity(intent);
        }
    }
}
