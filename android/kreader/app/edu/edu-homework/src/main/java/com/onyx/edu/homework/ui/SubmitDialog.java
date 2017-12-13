package com.onyx.edu.homework.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.HomeworkSubmitAnswer;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.action.HomeworkSubmitAction;
import com.onyx.edu.homework.action.note.HomeworkPagesAnswerBase64ActionChain;
import com.onyx.edu.homework.base.BaseDialog;
import com.onyx.edu.homework.data.HomeworkState;
import com.onyx.edu.homework.databinding.DialogSubmitBinding;
import com.onyx.edu.homework.event.SubmitEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/12/8.
 */

public class SubmitDialog extends OnyxBaseDialog {

    private DialogSubmitBinding binding;
    private List<Question> questions;

    public SubmitDialog(@NonNull Context context, List<Question> questions) {
        super(context, R.style.NoTitleDialog);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_submit, null, false);
        setContentView(binding.getRoot());
        this.questions = questions;
        initView();
    }

    private void initView() {
        initQuestionInfo();
        binding.message.setText(R.string.submit_tips);
        binding.action0.setText(R.string.continue_answer);
        binding.action0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        binding.action1.setText(R.string.submit);
        binding.action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void initQuestionInfo() {
        if (questions == null) {
            return;
        }
        binding.total.setText(getContext().getString(R.string.total, questions.size()));
        int hasAnswerCount = 0;
        for (Question question : questions) {
            if (question.doneAnswer) {
                hasAnswerCount++;
            }
        }
        int notAnswerCount = questions.size() - hasAnswerCount;
        binding.hasAnswer.setText(getContext().getString(R.string.has_answer, hasAnswerCount));
        binding.notAnswer.setText(getContext().getString(R.string.not_answer, notAnswerCount));
    }

    private void submit() {
        if (questions == null) {
            return;
        }
        final List<HomeworkSubmitAnswer> totalAnswers = new ArrayList<>();
        final List<HomeworkSubmitAnswer> fillAnswers = new ArrayList<>();
        for (Question question : questions) {
            List<HomeworkSubmitAnswer> answers = question.createAnswer();
            totalAnswers.addAll(answers);
            if (!question.isChoiceQuestion()) {
                fillAnswers.addAll(answers);
            }
        }
        int width = (int) getContext().getResources().getDimension(R.dimen.scribble_view_width);
        int height = (int) getContext().getResources().getDimension(R.dimen.scribble_view_height);
        Rect size = new Rect(0, 0, width, height);
        onStartSubmit();
        new HomeworkPagesAnswerBase64ActionChain(fillAnswers, size).execute(DataBundle.getInstance().getNoteViewHelper(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    submitImpl(totalAnswers);
                }else {
                    onFailSubmit();
                }

            }
        });
    }

    private void submitImpl(List<HomeworkSubmitAnswer> totalAnswers) {
        new HomeworkSubmitAction(DataBundle.getInstance().getHomeworkId(), totalAnswers).execute(getContext(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    onSuccessSubmit();
                }else {
                    onFailSubmit();
                }
            }
        });
    }

    private void onStartSubmit() {
        binding.message.setText(R.string.submitting);
        binding.action1.setVisibility(View.INVISIBLE);
        binding.action0.setVisibility(View.INVISIBLE);
    }

    private void onFailSubmit() {
        binding.message.setText(R.string.submit_fail);
        binding.action1.setVisibility(View.VISIBLE);
        binding.action0.setVisibility(View.VISIBLE);
    }

    private void onSuccessSubmit() {
        binding.message.setText(R.string.submit_success);
        binding.action1.setVisibility(View.GONE);
        binding.action0.setText(R.string.exit);
        binding.action0.setVisibility(View.VISIBLE);
        DataBundle.getInstance().setState(HomeworkState.DONE);
        DataBundle.getInstance().post(new SubmitEvent());
    }
}
