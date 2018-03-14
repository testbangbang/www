package com.onyx.edu.homework.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.homework.HomeworkSubmitAnswer;
import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.action.CheckAnswerAction;
import com.onyx.edu.homework.action.CheckWifiAction;
import com.onyx.edu.homework.action.HomeworkSubmitAction;
import com.onyx.edu.homework.action.note.MakeHomeworkPagesAnswerActionChain;
import com.onyx.edu.homework.databinding.DialogSubmitBinding;
import com.onyx.edu.homework.event.ReloadQuestionViewEvent;
import com.onyx.edu.homework.event.ShowRecordFragmentEvent;
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
        setCanceledOnTouchOutside(false);
        this.questions = questions;
        initView();
    }

    private void initView() {
        binding.message.setText(R.string.submit_tips);
        binding.action0.setText(R.string.continue_answer);
        binding.action0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBundle.getInstance().post(new ReloadQuestionViewEvent());
                dismiss();
            }
        });
        binding.action1.setText(R.string.submit);
        binding.action1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareSubmit();
            }
        });
        binding.tvAction2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DataBundle.getInstance().post(new ShowRecordFragmentEvent());
                dismiss();
            }
        });
        checkQuestionAnswer();
    }

    private void checkQuestionAnswer() {
        new CheckAnswerAction(questions).execute(getContext(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                initQuestionInfo();
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
        binding.hasAnswer.setText(getContext().getString(R.string.has_answer, hasAnswerCount, notAnswerCount));
    }

    private void prepareSubmit() {
        if (NetworkUtil.isWiFiConnected(getContext())) {
            submit();
            return;
        }
        onWifiConnect();
        final CheckWifiAction checkWifiAction = new CheckWifiAction();
        checkWifiAction.execute(getContext(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (checkWifiAction.isConnected()) {
                    submit();
                }
            }
        });
    }

    private boolean submitChoiceQuestionDraft() {
        return getDataBundle().afterReview();
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
        int width = (int) getContext().getResources().getDimension(R.dimen.scribble_view_width);
        int height = (int) getContext().getResources().getDimension(R.dimen.scribble_view_height);
        Rect size =  new Rect(0, 0, width, height);
        new MakeHomeworkPagesAnswerActionChain(submitChoiceQuestionDraft() ? totalAnswers : fillAnswers, size).execute(DataBundle.getInstance().getNoteViewHelper(), new BaseCallback() {
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
        new HomeworkSubmitAction(getDataBundle().getChildId(),
                getDataBundle().getPersonalHomeworkId(),
                totalAnswers).execute(getContext(), new BaseCallback() {
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

    private DataBundle getDataBundle() {
        return DataBundle.getInstance();
    }

    private void onWifiConnect() {
        binding.message.setText(R.string.opening_wifi);
        binding.action1.setVisibility(View.INVISIBLE);
        binding.action0.setVisibility(View.INVISIBLE);
        binding.tvAction2.setVisibility(View.INVISIBLE);
    }

    private void onStartSubmit() {
        binding.message.setText(R.string.submitting);
        binding.action1.setVisibility(View.INVISIBLE);
        binding.action0.setVisibility(View.INVISIBLE);
        binding.tvAction2.setVisibility(View.INVISIBLE);
    }

    private void onFailSubmit(Throwable e) {
        binding.message.setText(R.string.submit_fail);
        binding.action1.setVisibility(View.VISIBLE);
        binding.action0.setVisibility(View.VISIBLE);
        binding.tvAction2.setVisibility(View.VISIBLE);
    }

    private void onSuccessSubmit() {
        binding.message.setText(R.string.submit_success);
        binding.action1.setVisibility(View.GONE);
        binding.action0.setText(R.string.close);
        binding.action0.setVisibility(View.VISIBLE);
        binding.tvAction2.setVisibility(View.VISIBLE);
        DataBundle.getInstance().post(new SubmitEvent());
    }
}
