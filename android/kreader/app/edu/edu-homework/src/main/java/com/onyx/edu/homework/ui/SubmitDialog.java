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
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.action.HomeworkSubmitAction;
import com.onyx.edu.homework.action.note.MakeHomeworkPagesAnswerActionChain;
import com.onyx.edu.homework.data.HomeworkState;
import com.onyx.edu.homework.databinding.DialogSubmitBinding;
import com.onyx.edu.homework.event.ResumeNoteEvent;
import com.onyx.edu.homework.event.SubmitEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/12/8.
 */

public class SubmitDialog extends OnyxBaseDialog {

    private DialogSubmitBinding binding;
    private List<Question> questions;
    private NoteViewHelper noteViewHelper;

    public SubmitDialog(@NonNull Context context, List<Question> questions) {
        super(context, R.style.NoTitleDialog);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_submit, null, false);
        setContentView(binding.getRoot());
        setCanceledOnTouchOutside(false);
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
        if (CollectionUtils.isNullOrEmpty(questions)) {
            return;
        }
        final List<HomeworkSubmitAnswer> totalAnswers = new ArrayList<>();
        final List<HomeworkSubmitAnswer> fillAnswers = new ArrayList<>();
        for (Question question : questions) {
            HomeworkSubmitAnswer answer = question.createAnswer();
            totalAnswers.add(answer);
            if (!question.isChoiceQuestion()) {
                fillAnswers.add(answer);
            }
        }
        onStartSubmit();
        new MakeHomeworkPagesAnswerActionChain(fillAnswers, questions).execute(getNoteViewHelper(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    submitImpl(totalAnswers);
                }else {
                    onFailSubmit(e);
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
                    onFailSubmit(e);
                }
            }
        });
    }

    private void onStartSubmit() {
        binding.message.setText(R.string.submitting);
        binding.action1.setVisibility(View.INVISIBLE);
        binding.action0.setVisibility(View.INVISIBLE);
    }

    private void onFailSubmit(Throwable e) {
        binding.message.setText(R.string.submit_fail);
        binding.action1.setVisibility(View.VISIBLE);
        binding.action0.setVisibility(View.VISIBLE);
    }

    private void onSuccessSubmit() {
        binding.message.setText(R.string.submit_success);
        binding.action1.setVisibility(View.GONE);
        binding.action0.setText(R.string.close);
        binding.action0.setVisibility(View.VISIBLE);
        DataBundle.getInstance().post(new SubmitEvent());
    }

    private NoteViewHelper getNoteViewHelper() {
        if (noteViewHelper == null) {
            noteViewHelper = new NoteViewHelper();
        }
        return noteViewHelper;
    }
}
