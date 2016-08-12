package com.onyx.android.edu.result;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.bean.PaperResult;
import com.onyx.android.edu.testpaper.TestPaperActivity;

import java.util.List;

import butterknife.Bind;

/**
 * Created by ming on 16/6/28.
 */
public class ResultFragment extends BaseFragment implements ResultContract.View, View.OnClickListener {

    @Bind(R.id.question_result_layout)
    LinearLayout mQuestionResultLayout;
    @Bind(R.id.analysis_wrong_button)
    Button mAnalysisWrongButton;

    private ResultContract.Presenter mPresenter;

    public static ResultFragment newInstance() {
        return new ResultFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_result;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {
        mPresenter.subscribe();
        mAnalysisWrongButton.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void showResult(PaperResult paperResult) {
        addQuestionResult(paperResult.getResult());
    }

    @Override
    public void setPresenter(ResultContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unSubscribe();
    }

    private void addQuestionResult(List<Boolean> resultList) {
        int index = 1;
        for (Boolean result : resultList) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_question_result_item, null);
            TextView itemName = (TextView) view.findViewById(R.id.item_name);
            LinearLayout bgView = (LinearLayout) view.findViewById(R.id.question_result_view);
            bgView.setBackgroundResource(result ? R.drawable.right_oval : R.drawable.wrong_oval);
            itemName.setText(index + "");
            index++;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,20,20,20);
            bgView.setLayoutParams(layoutParams);
            mQuestionResultLayout.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.analysis_wrong_button:
                Intent intent = new Intent(getActivity(), TestPaperActivity.class);
                intent.putExtra(TestPaperActivity.SHOW_ANSWER,true);
                startActivity(intent);
                break;
        }
    }
}
