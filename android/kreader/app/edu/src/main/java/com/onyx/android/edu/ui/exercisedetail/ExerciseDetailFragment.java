package com.onyx.android.edu.ui.exercisedetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseFragment;

/**
 * Created by ming on 16/8/18.
 */
public class ExerciseDetailFragment extends BaseFragment implements ExerciseDetailContract.ExerciseDetailView{

    ExerciseDetailContract.ExerciseDetailPresenter exerciseDetailPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_exercise_detail;
    }

    public static ExerciseDetailFragment newInstance() {
        return new ExerciseDetailFragment();
    }

    @Override
    public void setPresenter(ExerciseDetailContract.ExerciseDetailPresenter presenter) {
        this.exerciseDetailPresenter = presenter;
    }
}
