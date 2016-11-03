package com.onyx.android.edu.ui.exerciserespond;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.adapter.QuestionsPagerAdapter;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.base.BaseQuestionView;
import com.onyx.android.edu.bean.PaperResult;
import com.onyx.android.edu.db.model.Chapter;
import com.onyx.android.edu.ui.respondresult.RespondResultActivity;
import com.onyx.android.edu.utils.JsonUtils;
import com.onyx.android.edu.view.ChoiceQuestionView;
import com.onyx.android.edu.view.CustomViewPager;
import com.onyx.android.edu.view.SubjectiveQuestionView;
import com.onyx.libedu.model.ChooseQuestionVariable;
import com.onyx.libedu.model.Question;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 16/6/24.
 */
public class ExerciseRespondFragment extends BaseFragment implements View.OnClickListener,ExerciseRespondContract.ExerciseRespondView {

    @Bind(R.id.paper_pager)
    CustomViewPager mPaperPager;
    @Bind(R.id.left_arrow)
    ImageView mLeftArrow;
    @Bind(R.id.paper_index)
    TextView mPaperIndex;
    @Bind(R.id.right_arrow)
    ImageView mRightArrow;

    private QuestionsPagerAdapter mQuestionsPagerAdapter;

    private ExerciseRespondContract.ExerciseRespondPresenter mExerciseRespondPresenter;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_exercise_respond;
    }

    protected void initView() {
        mLeftArrow.setOnClickListener(this);
        mRightArrow.setOnClickListener(this);
    }

    protected void initData() {
        mExerciseRespondPresenter.subscribe();
    }

    public static ExerciseRespondFragment newInstance() {
        return new ExerciseRespondFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mExerciseRespondPresenter.unSubscribe();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.left_arrow:{
                closeSoftInput();
                int index = mPaperPager.getCurrentItem();
                int last = index - 1;
                if (index > 0){
                    mPaperPager.setCurrentItem(last,false);
                    updatePaperIndex();
                }
            }
            break;
            case R.id.right_arrow:{
                closeSoftInput();
                int index = mPaperPager.getCurrentItem();
                int count = mQuestionsPagerAdapter.getCount();
                int next = index + 1;
                if (next < count){
                    BaseQuestionView selectView = mQuestionsPagerAdapter.getViewList().get(index);
                    if (selectView.hasAnswers() || selectView.isShowAnswer()){
                        mPaperPager.setCurrentItem(next,false);
                        updatePaperIndex();
                    }else {
                        showToast(getString(R.string.ask_select_answer));
                    }
                }else {
                    enterResultActivity();
                }
            }
            break;
        }
    }

    private void closeSoftInput() {
    }

    private void updatePaperIndex(){
        int index = mPaperPager.getCurrentItem() + 1;
        int count = mQuestionsPagerAdapter.getCount();
        String str = String.format("%d/%d",index,count);
        mPaperIndex.setText(str);
    }

    @Override
    public void setPresenter(ExerciseRespondContract.ExerciseRespondPresenter exerciseRespondPresenter) {
        mExerciseRespondPresenter = exerciseRespondPresenter;
    }

    private void enterResultActivity(){
        PaperResult paperResult = mExerciseRespondPresenter.getPaperResult(mQuestionsPagerAdapter.getViewList());
        getActivity().finish();
        Intent intent = new Intent(getActivity(), RespondResultActivity.class);
        intent.putExtra(RespondResultActivity.RESULT, JsonUtils.toJson(paperResult));
        startActivity(intent);
    }

    @Override
    public void showQuestions(List<Question> questions, ChooseQuestionVariable variable, boolean showAnswer) {
        List<BaseQuestionView> selectViews = new ArrayList<>();
        for (Question question : questions) {
            if (question.getQuestionOptions() != null && question.getQuestionOptions().size() > 0) {
                selectViews.add(generateChoiceQuestion(question, showAnswer));
            }else {
                selectViews.add(generateSubjectiveQuestion(question, showAnswer));
            }
        }
        mQuestionsPagerAdapter = new QuestionsPagerAdapter(selectViews);
        mPaperPager.setAdapter(mQuestionsPagerAdapter);
        mPaperPager.setPagingEnabled(false);

        updatePaperIndex();
    }

    private ChoiceQuestionView generateChoiceQuestion(Question question, boolean showAnswer){
        ChoiceQuestionView choiceQuestionView = new ChoiceQuestionView(getActivity(),
                showAnswer,
                question.getQuestionOptions(),
                question.getQuestionAnalytical().getAnswer(),
                question.getStem(),
                question.getQuestionAnalytical().getQuestionAnalyze());
        return choiceQuestionView;
    }

    private SubjectiveQuestionView generateSubjectiveQuestion(Question question, boolean showAnswer){
        SubjectiveQuestionView subjectiveQuestionView = new SubjectiveQuestionView(getActivity(),
                showAnswer,
                question.getQuestionAnalytical().getAnswer(),
                question.getStem(),
                question.getQuestionAnalytical().getQuestionAnalyze());
        return subjectiveQuestionView;
    }
}
