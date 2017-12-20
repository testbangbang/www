package com.onyx.edu.homework.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.data.model.QuestionOption;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.utils.Base64ImageParser;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.action.DoAnswerAction;
import com.onyx.edu.homework.base.BaseFragment;
import com.onyx.edu.homework.data.Config;
import com.onyx.edu.homework.data.Constant;
import com.onyx.edu.homework.databinding.FragmentQuestionBinding;
import com.onyx.edu.homework.event.DoneAnswerEvent;
import com.onyx.edu.homework.utils.TextUtils;

import java.util.List;

/**
 * Created by lxm on 2017/12/9.
 */

public class QuestionFragment extends BaseFragment {

    private FragmentQuestionBinding binding;
    private Question question;

    private ScribbleFragment scribbleFragment;
    private NoteToolFragment toolFragment;
    private ReviewFragment reviewFragment;

    public static QuestionFragment newInstance(Question question) {
        QuestionFragment fragment = new QuestionFragment();
        fragment.setQuestion(question);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(question);
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    private void init(Question question) {
        initView(question);
        initFragment();
        initReviewInfo();
        updateViewState();
    }

    private void initView(final Question question) {
        initViewVisibility();
        int questionIndex = Math.max(question.QuesType - 1, 0);
        String questionType = getResources().getStringArray(R.array.question_type_list)[questionIndex];
        binding.questionType.setText(getString(R.string.question_type_str, questionType));
        binding.content.setText(TextUtils.fromHtml(question.content, new Base64ImageParser(getActivity()), null));
        bindQuestionOption(binding.option, question);
        binding.analysis.setText(R.string.analysis);
        binding.analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnalysisDialog(question);
            }
        });

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) binding.scribble.getLayoutParams();
        int heightResId = question.isFillQuestion() ? R.dimen.fill_question_scribble_view_height : R.dimen.scribble_view_height;
        lp.height = (int) getActivity().getResources().getDimension(heightResId);
        binding.scribble.setLayoutParams(lp);
    }

    private void initViewVisibility() {
        binding.reviewLayout.setVisibility(getDataBundle().isReview() && !question.isFillQuestion() ? View.VISIBLE : View.GONE);
        binding.score.setVisibility((getDataBundle().isReview() && Config.getInstance().isShowScore()) ? View.VISIBLE : View.GONE);

        binding.content.setVisibility(question.isFillQuestion() ? View.GONE : View.VISIBLE);
        binding.questionType.setVisibility(question.isFillQuestion() ? View.GONE : View.VISIBLE);
        boolean showScribble = !question.isChoiceQuestion();
        binding.scribble.setVisibility(showScribble ? View.VISIBLE : View.GONE);
        binding.scribbleLine.setVisibility(showScribble ? View.VISIBLE : View.GONE);
        binding.toolLayout.setVisibility(showScribble ? View.VISIBLE : View.GONE);
        binding.toolLayoutLine.setVisibility(showScribble && !question.isFillQuestion() ? View.VISIBLE : View.GONE);
    }

    private void showAnalysisDialog(Question question) {
        if (question == null || StringUtils.isNullOrEmpty(question.analysis)) {
            return;
        }
        Spanned analysis = TextUtils.fromHtml(question.analysis, new Base64ImageParser(getActivity()), null);
        OnyxCustomDialog.getMessageDialog(getActivity(), analysis).show();
    }

    public NoteViewHelper getNoteViewHelper() {
        return getDataBundle().getNoteViewHelper();
    }

    private void bindQuestionOption(RadioGroup group, Question question) {
        group.removeAllViews();
        if (!question.isChoiceQuestion()) {
            group.setVisibility(View.GONE);
            return;
        }
        group.setVisibility(View.VISIBLE);
        List<QuestionOption> options = question.options;
        for (QuestionOption option : options) {
            CompoundButton button = createCompoundButton(question, option);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margin = (int) getResources().getDimension(R.dimen.question_option_margin);
            lp.setMargins(0, margin, 0, margin);
            group.addView(button, lp);
            if (option.checked) {
                group.check(button.getId());
            }
        }
    }

    private CompoundButton createCompoundButton(final Question question, final QuestionOption option) {
        final boolean single = question.isSingleChoiceQuestion();
        final boolean enable = getDataBundle().isDoing();
        final CompoundButton button = single ? new RadioButton(getActivity()) : new CheckBox(getActivity());
        button.setText(Html.fromHtml(StringUtils.filterHtmlWrapChar(option.value), new Base64ImageParser(getActivity()), null));
        button.setTextSize(getResources().getDimension(R.dimen.question_option_text_size));
        button.setChecked(option.checked);
        button.setEnabled(enable);
        button.setGravity(Gravity.TOP);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (single && isChecked) {
                    unCheckOption(question);
                }
                option.setChecked(isChecked);
                if (buttonView.isPressed()) {
                    new DoAnswerAction(question, DataBundle.getInstance().getHomeworkId()).execute(getActivity(), new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable e) {
                            DataBundle.getInstance().post(new DoneAnswerEvent(question));
                        }
                    });
                }
            }
        });
        return button;
    }

    private void unCheckOption(Question question) {
        List<QuestionOption> options = question.options;
        if (options == null) {
            return;
        }
        for (QuestionOption option : options) {
            option.setChecked(false);
        }
    }

    private void gotoAnswerActivity(final Question question) {
        Intent intent = new Intent(getActivity(), AnswerActivity.class);
        intent.putExtra(Constant.TAG_QUESTION, question);
        startActivity(intent);
    }

    public void initFragment() {
        if (question.isChoiceQuestion()) {
            return;
        }
        getDataBundle().resetNoteViewHelper();
        getDataBundle().getNoteViewHelper().setDrawText(question.isFillQuestion() ? question.content : null);
        int initPageCount = 1;
        if (DataBundle.getInstance().isReview()) {
            if (question.review != null && !CollectionUtils.isNullOrEmpty(question.review.attachmentUrl)) {
                initPageCount = question.review.attachmentUrl.size();
            }
            reviewFragment = ReviewFragment.newInstance(question);
            getChildFragmentManager().beginTransaction().replace(R.id.scribble_layout, reviewFragment).commit();
        }else {
            scribbleFragment = ScribbleFragment.newInstance(question);
            getChildFragmentManager().beginTransaction().replace(R.id.scribble_layout, scribbleFragment).commit();
        }
        toolFragment = NoteToolFragment.newInstance(binding.subMenuLayout, initPageCount);
        getChildFragmentManager().beginTransaction().replace(R.id.tool_layout, toolFragment).commit();
    }

    private void removeFragment() {
        if (reviewFragment != null) {
            getChildFragmentManager().beginTransaction().remove(reviewFragment).commit();
            reviewFragment = null;
        }
        if (scribbleFragment != null) {
            getChildFragmentManager().beginTransaction().remove(scribbleFragment).commit();
            scribbleFragment = null;
        }
        if (toolFragment != null) {
            getChildFragmentManager().beginTransaction().remove(toolFragment).commit();
            toolFragment = null;
        }
    }

    public void reloadQuestion(Question question) {
        this.question = question;
        removeFragment();
        init(question);
    }

    public DataBundle getDataBundle() {
        return DataBundle.getInstance();
    }

    public void updateViewState() {
        boolean enable = getDataBundle().isDoing();
        int size = binding.option.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = binding.option.getChildAt(i);
            view.setEnabled(enable);
        }
        binding.option.setClickable(enable);
    }

    private void initReviewInfo() {
        if (!getDataBundle().isReview()) {
            return;
        }
        if (question.answers != null) {
            Spanned answers = TextUtils.fromHtml(StringUtils.filterHtmlWrapChar(question.answers), new Base64ImageParser(getActivity()), null);
            binding.rightAnswer.setText(getString(R.string.right_answer, answers));
        }
        if (question.review != null) {
            binding.score.setText(getString(R.string.score, question.review.score));
            binding.rightWrongIcon.setImageResource(question.review.isRightAnswer() ? R.drawable.ic_right : R.drawable.ic_wrong);
        }
        binding.analysis.setVisibility(StringUtils.isNullOrEmpty(question.analysis) ? View.GONE : View.VISIBLE);
    }

    public void saveQuestion(BaseCallback callback) {
        if (question.isChoiceQuestion() || !getDataBundle().isDoing() || scribbleFragment == null) {
            BaseCallback.invoke(callback, null, null);
            return;
        }
        scribbleFragment.saveDocument(callback);
    }
}
