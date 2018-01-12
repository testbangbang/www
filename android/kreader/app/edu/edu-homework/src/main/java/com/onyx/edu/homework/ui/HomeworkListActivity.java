package com.onyx.edu.homework.ui;

import android.content.DialogInterface;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.receiver.NetworkConnectChangedReceiver;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Subject;
import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.android.sdk.data.model.homework.QuestionReview;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.action.CheckAnswerAction;
import com.onyx.edu.homework.action.GetHomeworkReviewsAction;
import com.onyx.edu.homework.action.HomeworkListActionChain;
import com.onyx.edu.homework.action.ShowAnalysisAction;
import com.onyx.edu.homework.action.note.ShowExitDialogAction;
import com.onyx.edu.homework.base.BaseActivity;
import com.onyx.edu.homework.data.Config;
import com.onyx.edu.homework.data.HomeworkIntent;
import com.onyx.edu.homework.data.SaveDocumentOption;
import com.onyx.edu.homework.databinding.ActivityHomeworkListBinding;
import com.onyx.edu.homework.event.CloseSubMenuEvent;
import com.onyx.edu.homework.event.DoneAnswerEvent;
import com.onyx.edu.homework.event.GotoQuestionPageEvent;
import com.onyx.edu.homework.event.ResumeNoteEvent;
import com.onyx.edu.homework.event.StopNoteEvent;
import com.onyx.edu.homework.event.SubmitEvent;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by lxm on 2017/12/5.
 */

public class HomeworkListActivity extends BaseActivity {

    private ActivityHomeworkListBinding binding;
    private NetworkConnectChangedReceiver networkConnectChangedReceiver;
    private List<Question> questions;
    private HomeworkIntent homeworkIntent;
    private RecordFragment recordFragment;
    private QuestionFragment questionFragment;
    private int currentPage = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_homework_list);
        DataBundle.getInstance().register(this);
        initView();
        handleIntent();
        homeworkRequest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
        DataBundle.getInstance().unregister(this);
        DataBundle.getInstance().quit();
    }

    private void initView() {
        binding.prevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevPage(v);
            }
        });
        binding.nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextPage(v);
            }
        });
        binding.total.setText(getString(R.string.total, 0));
        binding.hasAnswer.setText(getString(R.string.has_answer, 0));
        binding.notAnswer.setText(getString(R.string.not_answer, 0));
        binding.submit.setText(R.string.submit);
        binding.answerRecord.setText(R.string.answer_record);
        binding.getResult.setText(R.string.get_result);
        binding.answerRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecordFragment();
            }
        });
        binding.getResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHomeworkReview();
            }
        });
        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubmitDialog();
            }
        });
        binding.toolbar.backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();
            }
        });
        binding.analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnalysisDialog();
            }
        });
        hideMessage();
    }

    private void showAnalysisDialog() {
        if (CollectionUtils.isNullOrEmpty(questions)) {
            return;
        }
        if (currentPage >= questions.size()) {
            return;
        }
        if (getQuestionFragment() == null) {
            return;
        }
        getQuestionFragment().saveQuestion(SaveDocumentOption.onStopSaveOption(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                new ShowAnalysisAction(questions.get(currentPage)).execute(HomeworkListActivity.this, null);
            }
        });
    }

    private void nextPage(final View view) {
        if (CollectionUtils.isNullOrEmpty(questions)) {
            return;
        }
        final int next = currentPage + 1;
        if (next >= questions.size() || getQuestionFragment() == null) {
            return;
        }
        getQuestionFragment().saveQuestion(SaveDocumentOption.onPageSaveOption(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                setCurrentPage(next);
                reloadQuestionFragment(currentPage);
            }
        });
    }

    private void prevPage(final View view) {
        if (CollectionUtils.isNullOrEmpty(questions)) {
            return;
        }
        final int prev = currentPage - 1;
        if (prev < 0 || getQuestionFragment() == null) {
            return;
        }
        getQuestionFragment().saveQuestion(SaveDocumentOption.onPageSaveOption(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                setCurrentPage(prev);
                reloadQuestionFragment(currentPage);
            }
        });
    }

    private void showSubmitDialog() {
        if (getQuestionFragment() == null) {
            return;
        }
        getQuestionFragment().saveQuestion(SaveDocumentOption.onStopSaveOption(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                SubmitDialog dialog = new SubmitDialog(HomeworkListActivity.this, questions);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        reloadQuestionFragment(currentPage);
                    }
                });
                dialog.show();
            }
        });
    }

    private void getHomeworkReview() {
        getDataBundle().post(new StopNoteEvent(false));
        GetHomeworkReviewsAction reviewsAction = new GetHomeworkReviewsAction(getDataBundle().getHomeworkId(), questions, true, true);
        reviewsAction.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getDataBundle().post(new ResumeNoteEvent());
                if (!getDataBundle().isReview()) {
                    Toast.makeText(HomeworkListActivity.this, R.string.not_review, Toast.LENGTH_SHORT).show();
                    return;
                }
                hideRecordFragment(false);
                reloadQuestionFragment(currentPage);
                updateViewState();
                showTotalScore();
            }
        });
    }

    private void handleIntent() {
        String extraData = getIntent().getStringExtra(MetadataUtils.INTENT_EXTRA_DATA_METADATA);
        if (StringUtils.isNullOrEmpty(extraData)) {
            return;
        }
        homeworkIntent = JSON.parseObject(extraData, HomeworkIntent.class);
    }

    private void homeworkRequest() {
        if (homeworkIntent == null || homeworkIntent.child == null) {
            showMessage(R.string.no_find_homework);
            return;
        }
        String libraryId = homeworkIntent.child._id;
        if (StringUtils.isNullOrEmpty(libraryId)) {
            showMessage(R.string.no_find_homework);
            return;
        }
        showMessage(R.string.loading_questions);
        final HomeworkListActionChain actionChain = new HomeworkListActionChain(libraryId);
        actionChain.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                questions = actionChain.getQuestions();
                //check can get homework, if not open wifi
                if (CollectionUtils.isNullOrEmpty(questions)) {
                    checkWifi(true);
                    return;
                }
                initToolbarTitle();
                hideMessage();
                updateViewState();
                showTotalScore();
                initQuestions(questions);
            }

        });
    }

    private void initToolbarTitle() {
        String title = homeworkIntent.child.title;
        Subject subject = getDataBundle().getHomework().subject;
        Date beginTime = getDataBundle().getHomework().beginTime;
        if (subject != null && !StringUtils.isNullOrEmpty(subject.name)) {
            title += "  " + getString(R.string.subject, subject.name);
        }
        if (beginTime != null) {
            String time = DateTimeUtil.formatDate(beginTime, DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM);
            title += "  " + getString(R.string.publish_time, time);
        }
        binding.toolbar.title.setText(title);
    }

    private boolean checkWifi(boolean showMessage) {
        if (Device.currentDevice().hasWifi(this) && !NetworkUtil.isWiFiConnected(this)) {
            if (showMessage) {
                showMessage(R.string.opening_wifi);
            }
            registerReceiver();
            NetworkUtil.enableWiFi(this, true);
            return false;
        }
        return true;
    }

    private void registerReceiver() {
        if (networkConnectChangedReceiver != null) {
            return;
        }
        networkConnectChangedReceiver = new NetworkConnectChangedReceiver(new NetworkConnectChangedReceiver.NetworkChangedListener() {
            @Override
            public void onNetworkChanged(boolean connected, int networkType) {
                hideMessage();
                if (connected && CollectionUtils.isNullOrEmpty(questions)) {
                    homeworkRequest();
                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectChangedReceiver, filter);
    }

    private void unregisterReceiver() {
        if (networkConnectChangedReceiver != null) {
            unregisterReceiver(networkConnectChangedReceiver);
        }
    }

    private void showMessage(@StringRes int messageId) {
        binding.message.setVisibility(View.VISIBLE);
        binding.message.setText(messageId);
    }

    private void hideMessage() {
        binding.message.setVisibility(View.GONE);
    }

    private void showTotalScore() {
        if (!Config.getInstance().isShowScore()) {
            return;
        }
        if (!getDataBundle().isReview() || questions == null) {
            return;
        }
        float score = 0f;
        for (Question question : questions) {
            QuestionReview review = question.review;
            if (review == null) {
                continue;
            }
            score += review.score;
        }
        binding.result.setText(getString(R.string.score, score));
    }

    private void initQuestions(final List<Question> questions) {
        if (CollectionUtils.isNullOrEmpty(questions)) {
            return;
        }
        questionFragment = QuestionFragment.newInstance(questions.get(0));
        getSupportFragmentManager().beginTransaction().replace(R.id.question_layout, questionFragment).commit();
        updateShowInfo();
        checkAnswer();
        updateOnPageChanged(0);
    }

    private void updateShowInfo() {
        binding.total.setText(getString(R.string.total, questions.size()));
    }

    @Subscribe
    public void onDoneAnswerEvent(DoneAnswerEvent event) {
        checkAnswer();
    }

    private void checkAnswer() {
        new CheckAnswerAction(questions).execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                updateAnswerInfo();
            }
        });
    }

    private void updateAnswerInfo() {
        if (questions == null) {
            return;
        }
        int hasAnswerCount = 0;
        for (Question question : questions) {
            if (question.doneAnswer) {
                hasAnswerCount++;
            }
        }
        int notAnswerCount = questions.size() - hasAnswerCount;
        binding.hasAnswer.setText(getString(R.string.has_answer, hasAnswerCount));
        binding.notAnswer.setText(getString(R.string.not_answer, notAnswerCount));
    }

    @Nullable
    private QuestionFragment getQuestionFragment() {
        return questionFragment;
    }

    private void updateOnPageChanged(int position) {
        int current = position + 1;
        int total = questions.size();
        if (position >= total) {
            return;
        }
        binding.page.setText(getString(R.string.question_page, current + File.separator + total));

        if (getDataBundle().isReview()) {
            Question question = questions.get(position);
            if (question.review != null) {
                binding.answerIcon.setImageResource(question.review.isRightAnswer() ? R.drawable.ic_right : R.drawable.ic_wrong);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EpdController.invalidate(getWindow().getDecorView(), UpdateMode.GC);
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Subscribe
    public void onSubmitEvent(SubmitEvent event) {
        updateViewState();
    }

    @Subscribe
    public void onGotoQuestionPageEvent(GotoQuestionPageEvent event) {
        if (event.hideRecord) {
            hideRecordFragment(true);
        }
        setCurrentPage(event.page);
        reloadQuestionFragment(currentPage);
    }

    public void setCurrentPage(int page) {
        page = Math.max(0, page);
        page = Math.min(page, questions == null ? 0 : questions.size() - 1);
        this.currentPage = page;
    }

    public void reloadQuestionFragment(int position) {
        if (CollectionUtils.isNullOrEmpty(questions)) {
            return;
        }
        questionFragment.reloadQuestion(questions.get(position));
        updateOnPageChanged(position);
    }

    public DataBundle getDataBundle() {
        return DataBundle.getInstance();
    }

    private void updateViewState() {
        binding.analysis.setVisibility(getDataBundle().isReview() ? View.VISIBLE : View.GONE);
        binding.answerIcon.setVisibility(getDataBundle().isReview() ? View.VISIBLE : View.GONE);
        binding.answerRecord.setVisibility(getDataBundle().isDoing() ? View.VISIBLE : View.GONE);
        binding.submit.setVisibility(getDataBundle().isReview() ? View.GONE : View.VISIBLE);
        binding.result.setVisibility((getDataBundle().isReview() && Config.getInstance().isShowScore()) ? View.VISIBLE : View.GONE);
        binding.getResult.setVisibility(getDataBundle().isSubmitted() ? View.VISIBLE : View.GONE);
        binding.submit.setText(getDataBundle().isDoing() ? R.string.submit : R.string.submited);
        binding.submit.setEnabled(getDataBundle().isDoing());
    }

    @Override
    public void onBackPressed() {
        getDataBundle().post(new CloseSubMenuEvent());
        showExitDialog();
    }

    private void showExitDialog() {
        if (getQuestionFragment() == null) {
            new ShowExitDialogAction().execute(HomeworkListActivity.this, null);
            return;
        }
        getQuestionFragment().saveQuestion(SaveDocumentOption.onStopSaveOption(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                new ShowExitDialogAction().execute(HomeworkListActivity.this, null);
            }
        });
    }

    private void toggleRecordFragment() {
        if (getQuestionFragment() == null) {
            return;
        }
        if (recordFragment == null) {
            getQuestionFragment().saveQuestion(SaveDocumentOption.onPageSaveOption(), new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    showRecordFragment();
                }
            });
        }else {
            hideRecordFragment(true);
        }
    }

    private void showRecordFragment() {
        recordFragment = RecordFragment.newInstance(questions);
        getSupportFragmentManager().beginTransaction().replace(R.id.record_layout, recordFragment).commit();
        binding.answerRecord.setText(R.string.return_answer);
    }

    private void hideRecordFragment(boolean reload) {
        if (recordFragment == null) {
            return;
        }
        binding.answerRecord.setText(R.string.answer_record);
        getSupportFragmentManager().beginTransaction().remove(recordFragment).commit();
        recordFragment = null;
        if (reload) {
            reloadQuestionFragment(currentPage);
        }
    }

}