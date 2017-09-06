package com.onyx.android.edu.ui.respondresult;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by li on 2017/9/5.
 */

public class CommitResultFragment extends BaseFragment {
    @Bind(R.id.commit_paper)
    Button commitPaper;
    @Bind(R.id.back_paper)
    Button backPaper;

    public static CommitResultFragment newInstance() {
        return new CommitResultFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.commit_result_layout;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.back_paper, R.id.commit_paper})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_paper:
                getActivity().finish();
                break;
            case R.id.commit_paper:
                ((RespondResultActivity) getActivity()).switchFragment();
                break;
        }
    }
}
