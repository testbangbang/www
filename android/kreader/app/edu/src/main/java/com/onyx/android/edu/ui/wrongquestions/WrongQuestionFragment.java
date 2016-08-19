package com.onyx.android.edu.ui.wrongquestions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.WrongResultAdapter;
import com.onyx.android.edu.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/6/28.
 */
public class WrongQuestionFragment extends BaseFragment implements WrongQuestionContract.View {

    @Bind(R.id.wrong_list)
    RecyclerView wrongList;
    @Bind(R.id.wrong_info)
    TextView wrongInfo;
    private WrongQuestionContract.Presenter mPresenter;

    public static WrongQuestionFragment newInstance() {
        return new WrongQuestionFragment();
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
        return R.layout.fragment_wrong_question;
    }

    protected void initView() {
        wrongList.setLayoutManager(new GridLayoutManager(getActivity(),4));
        wrongList.setAdapter(new WrongResultAdapter());
    }

    @Override
    public void setPresenter(WrongQuestionContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unSubscribe();
        ButterKnife.unbind(this);
    }

}
