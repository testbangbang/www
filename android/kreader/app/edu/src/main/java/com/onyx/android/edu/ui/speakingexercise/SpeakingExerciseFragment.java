package com.onyx.android.edu.ui.speakingexercise;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.SpeakingTestAdapter;
import com.onyx.android.edu.adapter.TestAdapter;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.db.model.AtomicQuiz;
import com.onyx.android.edu.ui.exerciserespond.ExerciseRespondActivity;
import com.onyx.android.edu.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by ming on 16/6/28.
 */
public class SpeakingExerciseFragment extends BaseFragment implements SpeakingExerciseContract.View, View.OnClickListener, TestAdapter.OnItemClickListener {

    private static final String TAG = SpeakingExerciseFragment.class.getSimpleName();
    
    @Bind(R.id.shutdown)
    RadioButton mShutdown;
    @Bind(R.id.open)
    RadioButton mOpen;
    @Bind(R.id.open_translate_group)
    RadioGroup mOpenTranslateGroup;
    @Bind(R.id.speaking_recycler)
    PageRecyclerView mSpeakingRecycler;

    private SpeakingExerciseContract.Presenter mPresenter;

    public static SpeakingExerciseFragment newInstance() {
        return new SpeakingExerciseFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_speaking_test;
    }

    protected void initView(View root, Bundle savedInstanceState) {
        mPresenter.subscribe();
        mOpenTranslateGroup.check(R.id.shutdown);
    }

    protected void initData() {
        List<AtomicQuiz> atomicQuizList = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            AtomicQuiz quiz = new AtomicQuiz();
            atomicQuizList.add(quiz);
        }
        mSpeakingRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        SpeakingTestAdapter speakingTestAdapter = new SpeakingTestAdapter(atomicQuizList);
        mSpeakingRecycler.setAdapter(speakingTestAdapter);
    }

    @Override
    public void setPresenter(SpeakingExerciseContract.Presenter presenter) {
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
