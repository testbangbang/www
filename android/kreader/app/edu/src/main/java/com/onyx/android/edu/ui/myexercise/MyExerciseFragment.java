package com.onyx.android.edu.ui.myexercise;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.TestAdapter;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.base.EntityConfig;
import com.onyx.android.edu.ui.exerciserespond.ExerciseRespondActivity;
import com.onyx.android.edu.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by ming on 16/6/28.
 */
public class MyExerciseFragment extends BaseFragment implements MyExerciseContract.View, View.OnClickListener, TestAdapter.OnItemClickListener {

    private static final String TAG = "MyTestFragment";

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.all)
    RadioButton mAll;
    @Bind(R.id.unfinished)
    RadioButton mUnfinished;
    @Bind(R.id.finished)
    RadioButton mFinished;
    @Bind(R.id.replied)
    RadioButton mReplied;
    @Bind(R.id.group_status)
    RadioGroup mGroupStatus;
    @Bind(R.id.test_recycler)
    PageRecyclerView mTestRecycler;

    private MyExerciseContract.Presenter mPresenter;
    private TestAdapter mTestAdapter;
    private int mLayoutType = EntityConfig.GRID_LAYOUT;

    public static MyExerciseFragment newInstance() {
        return new MyExerciseFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_exercise;
    }

    protected void initView(View root, Bundle savedInstanceState) {
        mPresenter.subscribe();
        mGroupStatus.check(R.id.all);
    }

    protected void initData() {
        int size = 100;
        List<String> paperList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            paperList.add(i + "");
        }
        int layoutId;
        if (mLayoutType == EntityConfig.GRID_LAYOUT){
            layoutId = R.layout.item_grid_test;
            int column = 4;
            int row = 3;
            mTestRecycler.setLayoutManager(new GridLayoutManager(getActivity(),column));
            mTestAdapter = new TestAdapter(paperList,layoutId,mLayoutType,row,column);
        }else {
            int row = 6;
            layoutId = R.layout.item_linear_test;
            mTestRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            mTestAdapter = new TestAdapter(paperList,layoutId,mLayoutType,row,1);
        }
        mTestAdapter.setOnItemClickListener(this);
        mTestRecycler.setAdapter(mTestAdapter);
    }

    @Override
    public void setPresenter(MyExerciseContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unSubscribe();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void OnClick() {
        Intent intent = new Intent(getActivity(), ExerciseRespondActivity.class);
        startActivity(intent);
    }
}
