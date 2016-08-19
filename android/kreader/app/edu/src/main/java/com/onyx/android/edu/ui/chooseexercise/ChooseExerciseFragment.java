package com.onyx.android.edu.ui.chooseexercise;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.ChooseMultiAdapter;
import com.onyx.android.edu.adapter.ExerciseAdapter;
import com.onyx.android.edu.adapter.SubjectAdapter;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.base.Config;
import com.onyx.android.edu.ui.exercisedetail.ExerciseDetailActivity;
import com.onyx.android.edu.ui.exercisepractise.ExercisePractiseActivity;
import com.onyx.android.edu.ui.findexercise.FindExerciseActivity;
import com.onyx.android.sdk.ui.view.DynamicMultiRadioGroupView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/6/28.
 */
public class ChooseExerciseFragment extends BaseFragment implements ChooseExerciseContract.ChooseExerciseView, View.OnClickListener {

    private static final String TAG = ChooseExerciseFragment.class.getSimpleName();

    @Bind(R.id.choose_type_layout)
    LinearLayout chooseTypeLayout;
    @Bind(R.id.subject_recyclerview)
    RecyclerView subjectRecyclerview;
    @Bind(R.id.textbook_view)
    DynamicMultiRadioGroupView textbookView;
    @Bind(R.id.question_type_view)
    DynamicMultiRadioGroupView questionTypeView;
    @Bind(R.id.difficulty_levels_view)
    DynamicMultiRadioGroupView difficultyLevelsView;
    @Bind(R.id.stage_title)
    TextView stageTitle;
    @Bind(R.id.select_learning_stage_text)
    TextView selectLearningStageText;
    @Bind(R.id.select_learning_stage_image)
    ImageButton selectLearningStageImage;
    @Bind(R.id.check_detail_text)
    TextView checkDetailText;
    @Bind(R.id.check_detail_image)
    ImageButton checkDetailImage;
    @Bind(R.id.exercise_test)
    ImageView exerciseTest;
    @Bind(R.id.find_more)
    TextView findMore;
    @Bind(R.id.exercise_arrow_go)
    ImageView exerciseArrowGo;
    @Bind(R.id.exercise_layout)
    RelativeLayout exerciseLayout;
    @Bind(R.id.exercise_list)
    RecyclerView exerciseList;
    @Bind(R.id.sync_practice)
    Button syncPractice;
    @Bind(R.id.general_practice)
    Button generalPractice;

    private ChooseExerciseContract.ChooseExercisePresenter mPresenter;

    public static ChooseExerciseFragment newInstance() {
        return new ChooseExerciseFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_choose_exercise;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    protected void initView() {
        mPresenter.subscribe();

        syncPractice.setOnClickListener(this);
        generalPractice.setOnClickListener(this);
        selectLearningStageText.setOnClickListener(this);
        selectLearningStageImage.setOnClickListener(this);
        findMore.setOnClickListener(this);
        exerciseArrowGo.setOnClickListener(this);
        checkDetailText.setOnClickListener(this);
        checkDetailImage.setOnClickListener(this);

        initSubjectView();
        initTextBookView();
        initQuestionTypeView();
        initDifficultyLevelsView();
        initExerciseView();
    }

    private void initSubjectView() {
        SubjectAdapter subjectAdapter = new SubjectAdapter(Config.subjectNames, Config.subjectResIds);
        subjectRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        subjectRecyclerview.setAdapter(subjectAdapter);
    }

    private void initTextBookView() {
        String[] list = {"公共版", "人教版", "鲁教版", "清华大学出", "清华大学出"};
        textbookView.setMultiAdapter(new ChooseMultiAdapter(list, 2, 4, R.drawable.rectangle_radio));
        textbookView.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {

                }
            }
        });
    }

    private void initQuestionTypeView() {
        String[] list = {"单选题", "多选题", "主观题"};
        questionTypeView.setMultiAdapter(new ChooseMultiAdapter(list, 1, 4, R.drawable.rectangle_radio));
        questionTypeView.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {

                }
            }
        });
    }

    private void initDifficultyLevelsView() {
        String[] list = {"基础", "提高", "拓展"};
        difficultyLevelsView.setMultiAdapter(new ChooseMultiAdapter(list, 1, 4, R.drawable.rectangle_radio));
        difficultyLevelsView.setOnCheckedChangeListener(new DynamicMultiRadioGroupView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                if (isChecked) {

                }
            }
        });
    }

    private void initExerciseView(){
        ExerciseAdapter exerciseAdapter = new ExerciseAdapter();
        exerciseList.setLayoutManager(new GridLayoutManager(getActivity(),4));
        exerciseList.setAdapter(exerciseAdapter);
        exerciseAdapter.setCallBack(new ExerciseAdapter.CallBack() {
            @Override
            public void OnClickItemListener() {
                startActivity(new Intent(getActivity(), ExerciseDetailActivity.class));
            }
        });
    }

    @Override
    public void setPresenter(ChooseExerciseContract.ChooseExercisePresenter presenter) {
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
        if (v.equals(syncPractice) || v.equals(generalPractice)){
            startActivity(new Intent(getActivity(), ExercisePractiseActivity.class));
        }else if (v.equals(selectLearningStageImage) || v.equals(selectLearningStageText)){
            startActivity(new Intent(getActivity(), ChooseStudyingStageActivity.class));
        }else if (v.equals(findMore) || v.equals(exerciseArrowGo)){
            startActivity(new Intent(getActivity(), FindExerciseActivity.class));
        }else if (v.equals(checkDetailImage) || v.equals(checkDetailText)){
            startActivity(new Intent(getActivity(), ExercisePractiseActivity.class));
        }
    }
}
