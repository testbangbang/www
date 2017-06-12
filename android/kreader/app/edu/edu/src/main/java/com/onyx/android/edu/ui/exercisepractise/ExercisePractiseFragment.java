package com.onyx.android.edu.ui.exercisepractise;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.ExercisePractiseAdapter;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.ui.chapter.ChapterTypeActivity;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/8/18.
 */
public class ExercisePractiseFragment extends BaseFragment implements ExercisePractiseContract.ExercisePractiseView {

    @Bind(R.id.pre_button)
    ImageButton preButton;
    @Bind(R.id.page_size_indicator)
    TextView pageSizeIndicator;
    @Bind(R.id.next_button)
    ImageButton nextButton;
    @Bind(R.id.finished_exercise)
    Button finishedExercise;
    @Bind(R.id.random_exercise)
    Button randomExercise;
    @Bind(R.id.unfinished_exercise)
    Button unfinishedExercise;
    @Bind(R.id.exercise_list)
    PageRecyclerView exerciseList;

    private ExercisePractiseContract.ExercisePractisePresenter exercisePractisePresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_exercise_practise;
    }

    @Override
    public void setPresenter(ExercisePractiseContract.ExercisePractisePresenter presenter) {

    }

    public static ExercisePractiseFragment newInstance() {
        return new ExercisePractiseFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initView(){
        ExercisePractiseAdapter exercisePractiseAdapter = new ExercisePractiseAdapter();
        exerciseList.setAdapter(exercisePractiseAdapter);
        exercisePractiseAdapter.setCallBack(new ExercisePractiseAdapter.CallBack() {
            @Override
            public void onItemClick() {
                startActivity(new Intent(getActivity(), ChapterTypeActivity.class));
            }
        });
    }
}
