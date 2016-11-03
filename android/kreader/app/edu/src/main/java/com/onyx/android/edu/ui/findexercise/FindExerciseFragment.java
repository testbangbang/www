package com.onyx.android.edu.ui.findexercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.ChooseMultiAdapter;
import com.onyx.android.edu.adapter.ExercisePageAdapter;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.base.Config;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.DynamicMultiRadioGroupView;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/8/18.
 */
public class FindExerciseFragment extends BaseFragment implements FindExerciseContract.FindExerciseView {

    @Bind(R.id.subject_view)
    DynamicMultiRadioGroupView subjectView;
    @Bind(R.id.pre_button)
    ImageButton preButton;
    @Bind(R.id.page_size_indicator)
    TextView pageSizeIndicator;
    @Bind(R.id.next_button)
    ImageButton nextButton;
    @Bind(R.id.exercise_grid)
    PageRecyclerView exerciseGrid;

    FindExerciseContract.FindExercisePresenter findExercisePresenter;

    public static FindExerciseFragment newInstance() {
        return new FindExerciseFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_find_exercise;
    }

    @Override
    public void setPresenter(FindExerciseContract.FindExercisePresenter presenter) {
        this.findExercisePresenter = presenter;
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

    private void initView() {
        initSubjectView();
        initExerciseView();
    }

    private void initSubjectView(){
//        subjectView.setMultiAdapter(new ChooseMultiAdapter(Config.subjectNames, 2, 6, R.drawable.rectangle_radio));
//        subjectView.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
//                if (isChecked) {
//
//                }
//            }
//        });
    }

    private void initExerciseView(){
        exerciseGrid.setLayoutManager(new DisableScrollGridManager(getActivity()));
        exerciseGrid.setAdapter(new ExercisePageAdapter());
    }
}
