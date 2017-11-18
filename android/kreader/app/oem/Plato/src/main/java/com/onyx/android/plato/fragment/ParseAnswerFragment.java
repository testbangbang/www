package com.onyx.android.plato.fragment;

import android.databinding.ViewDataBinding;
import android.os.Environment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.AnswerBean;
import com.onyx.android.plato.cloud.bean.KnowledgeBean;
import com.onyx.android.plato.cloud.bean.ParseBean;
import com.onyx.android.plato.cloud.bean.PracticeParseBean;
import com.onyx.android.plato.cloud.bean.QuestionViewBean;
import com.onyx.android.plato.common.CommonNotices;
import com.onyx.android.plato.common.Constants;
import com.onyx.android.plato.databinding.ParseAnswerBinding;
import com.onyx.android.plato.event.TimerEvent;
import com.onyx.android.plato.event.ToCorrectEvent;
import com.onyx.android.plato.interfaces.ParseAnswerView;
import com.onyx.android.plato.presenter.ParseAnswerPresenter;
import com.onyx.android.plato.utils.MediaManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by li on 2017/10/26.
 */

public class ParseAnswerFragment extends BaseFragment implements View.OnClickListener, ParseAnswerView, View.OnTouchListener {
    private ParseAnswerBinding parseAnswerBinding;
    private QuestionViewBean questionData;
    private String title;
    private ParseAnswerPresenter parseAnswerPresenter;
    private MediaManager mediaManager;
    private long startTime;
    private long endTime;
    private long duration;
    private TimerEvent timerEvent;
    private PracticeParseBean data;

    @Override
    protected void loadData() {
        parseAnswerPresenter = new ParseAnswerPresenter(this);
        //TODO:fake id
        parseAnswerPresenter.getExplanation(1, 1523, 105);
        parseAnswerPresenter.getRecord(questionData.getTaskId(), questionData.getId());
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        parseAnswerBinding = (ParseAnswerBinding) binding;
        parseAnswerBinding.parseQuestionView.setQuestionData(questionData, title);
        parseAnswerBinding.parseQuestionView.setFinished(true);
        parseAnswerBinding.parseTitleBar.setTitle(SunApplication.getInstance().getString(R.string.parse_of_answer));
        parseAnswerBinding.parseTitleBar.titleBarRecord.setVisibility(View.GONE);
        parseAnswerBinding.parseTitleBar.titleBarSubmit.setVisibility(View.GONE);
        mediaManager = MediaManager.newInstance();
        timerEvent = new TimerEvent();
        parseAnswerBinding.parseDeleteSound.setEnabled(false);
        parseAnswerBinding.parseMistakeDelete.setEnabled(false);
        setVisible(questionData.isCorrect() ? R.id.parse_add_sound_layout : R.id.parse_mistake_layout);
    }

    @Override
    protected void initListener() {
        parseAnswerBinding.parseTitleBar.titleBarTitle.setOnClickListener(this);
        parseAnswerBinding.parseAddSound.setOnClickListener(this);
        parseAnswerBinding.parseDeleteSound.setOnClickListener(this);
        parseAnswerBinding.parseModify.setOnClickListener(this);
        parseAnswerBinding.parseReadRecord.setOnClickListener(this);
        parseAnswerBinding.parseReadRecordModify.setOnClickListener(this);
        parseAnswerBinding.parseMistakeAdd.setOnClickListener(this);
        parseAnswerBinding.parseMistakeInputImage.setOnTouchListener(this);
        parseAnswerBinding.parseSoundInput.setOnTouchListener(this);
        parseAnswerBinding.parseMistakeVoice.setOnClickListener(this);
        parseAnswerBinding.parseMistakeModify.setOnClickListener(this);
    }

    private File getOutputFile() {
        File dir = new File(Environment.getExternalStorageDirectory(), Constants.SOUND_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, questionData.getTaskId() + questionData.getId() + Constants.VOICE_FORMAT);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    private boolean isOutFileExist() {
        File dir = new File(Environment.getExternalStorageDirectory(), Constants.SOUND_DIR);
        if (!dir.exists()) {
            return false;
        }

        File file = new File(dir, questionData.getTaskId() + questionData.getId() + Constants.VOICE_FORMAT);
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    @Override
    protected int getRootView() {
        return R.layout.parse_answer_layout;
    }

    @Override
    public boolean onKeyBack() {
        return false;
    }

    public void setQuestionData(QuestionViewBean questionData, String title) {
        this.questionData = questionData;
        questionData.setShow(false);
        questionData.setScene(Constants.APK_NAME);
        questionData.setShowReaderComprehension(false);
        this.title = title;
        if (parseAnswerBinding != null) {
            parseAnswerBinding.parseQuestionView.setQuestionData(questionData, title);
            parseAnswerBinding.parseQuestionView.setFinished(true);
        }

        if (parseAnswerPresenter != null) {
            //TODO:fake id
            setVisible(questionData.isCorrect() ? R.id.parse_add_sound_layout : R.id.parse_mistake_layout);
            parseAnswerPresenter.getExplanation(1, 1523, 105);
            parseAnswerPresenter.getRecord(questionData.getTaskId(), questionData.getId());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_title:
                EventBus.getDefault().post(new ToCorrectEvent(null));
                break;
            case R.id.parse_add_sound:
                setRecordTime(R.id.parse_modify_sound_layout);
                break;
            case R.id.parse_delete_sound:
                break;
            case R.id.parse_modify:
                parseAnswerBinding.parseReadRecord.setVisibility(View.VISIBLE);
                setVisible(R.id.parse_add_sound_layout);
                break;
            case R.id.parse_read_record_modify:
                readRecord();
                break;
            case R.id.parse_read_record:
                readRecord();
                break;
            case R.id.parse_mistake_add:
                if (setRecordTime(R.id.parse_mistake_layout)) {
                    setItemVisible(R.id.parse_mistake_sound_layout);
                    parseAnswerBinding.parseMistakeLayout.setBackgroundColor(SunApplication.getInstance().getResources().getColor(R.color.white));
                }
                break;
            case R.id.parse_mistake_voice:
                readRecord();
                break;
            case R.id.parse_mistake_modify:
                parseAnswerBinding.parseMistakeLayout.setBackgroundResource(R.drawable.rectangle_gray_stroke);
                break;
        }
    }

    private void readRecord() {
        parseAnswerPresenter.speakRecord(mediaManager, getOutputFile().getAbsolutePath());
        timerEvent.timeCountDown(duration);
    }

    private boolean setRecordTime(int id) {
        if (duration <= 1) {
            CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.please_input_voice));
            return false;
        }
        setVisible(id);
        parseAnswerBinding.setRecorderTime(duration + "s");
        parseAnswerPresenter.saveRecord(questionData.getTaskId(), questionData.getId(), getOutputFile().getAbsolutePath(), duration);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnTimerEvent(TimerEvent event) {
        parseAnswerBinding.setRecorderTime(event.getResult() + "s");
        if (event.getResult() == 0) {
            parseAnswerBinding.setRecorderTime(duration + "s");
        }
    }

    private void setVisible(int id) {
        parseAnswerBinding.parseAddSoundLayout.setVisibility(id == R.id.parse_add_sound_layout ? View.VISIBLE : View.GONE);
        parseAnswerBinding.parseModifySoundLayout.setVisibility(id == R.id.parse_modify_sound_layout ? View.VISIBLE : View.GONE);
        parseAnswerBinding.parseMistakeLayout.setVisibility(id == R.id.parse_mistake_layout ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setExplanation(PracticeParseBean data) {
        this.data = data;
        List<AnswerBean> myAnswer = data.myAnswer;
        ParseBean parseBean = data.exerciseDto;
        if (myAnswer == null && myAnswer.size() == 0) {
            return;
        }
        AnswerBean answerBean = myAnswer.get(0);
        String correct = insertPMark(parseBean.answer, "(" + answerBean.accuracy + "%" + ")");
        String userAnswer = String.format(SunApplication.getInstance().getResources().getString(R.string.user_answer), answerBean.answer);
        String correctAnswer = String.format(SunApplication.getInstance().getResources().getString(R.string.correct_answer), Html.fromHtml(correct));
        parseAnswerBinding.setCorrectAnswer(correctAnswer);

        if (questionData.getExerciseSelections() != null && questionData.getExerciseSelections().size() > 0) {
            parseAnswerBinding.setUserAnswer(userAnswer + "(" + (answerBean.isCorrect ?
                    SunApplication.getInstance().getResources().getString(R.string.correct) :
                    SunApplication.getInstance().getResources().getString(R.string.mistake)) + ")");
            parseAnswerBinding.parseAnswerImage.setVisibility(View.GONE);
        }else {
            parseAnswerBinding.parseAnswerImage.setVisibility(View.VISIBLE);
            String scoreDetail = String.format(SunApplication.getInstance().getResources().getString(R.string.score_detail), answerBean.score, answerBean.value);
            String subjectAnswer = String.format(SunApplication.getInstance().getResources().getString(R.string.user_answer), scoreDetail);
            parseAnswerBinding.setUserAnswer(subjectAnswer);
            //TODO:load image answer
        }

        if (parseAnswerBinding.parseKnowledge.getChildCount() > 0) {
            parseAnswerBinding.parseKnowledge.removeAllViews();
        }
        List<KnowledgeBean> knowledgeDtoList = answerBean.knowledgeDtoList;
        if (knowledgeDtoList != null && knowledgeDtoList.size() > 0) {
            for (int i = 0; i < knowledgeDtoList.size(); i++) {
                KnowledgeBean knowledgeBean = knowledgeDtoList.get(i);
                TextView knowledge = createKnowledge(i + 1, knowledgeBean.name);
                parseAnswerBinding.parseKnowledge.addView(knowledge);
            }
        }

        parseAnswerBinding.setAnswerParse(Html.fromHtml(parseBean.analysis).toString());
    }

    @Override
    public void setRecordDuration(long recordDuration) {
        if (recordDuration == 0) {
            return;
        }

        duration = recordDuration;
        if (questionData.isCorrect()) {
            setVisible(R.id.parse_modify_sound_layout);
            parseAnswerBinding.setRecorderTime(recordDuration + "s");
        }
    }

    private String insertPMark(String htmlText, String insertText) {
        if (htmlText.contains(Constants.P_MARK)) {
            int index = htmlText.lastIndexOf(Constants.P_MARK);
            String result = htmlText.substring(0, index) + insertText + Constants.P_MARK;
            return result;
        }

        return htmlText + insertText;
    }

    private TextView createKnowledge(int i, String name) {
        TextView textView = new TextView(getActivity());
        textView.setText(String.format(SunApplication.getInstance().getResources().getString(R.string.create_knowledge), i, name));
        textView.setTextSize(SunApplication.getInstance().getResources().getDimension(R.dimen.level_three_heading_font));
        return textView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                parseAnswerPresenter.startRecord(mediaManager, getOutputFile().getAbsolutePath());
                startTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                endTime = System.currentTimeMillis();
                duration = ((endTime - startTime) / 1000);
                if (duration <= 1) {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.press_short));
                    File outputFile = getOutputFile();
                    outputFile.delete();
                    break;
                }
                mediaManager.stopRecord();
                break;
        }
        return true;
    }

    public void setItemVisible(int itemId) {
        parseAnswerBinding.parseMistakeSoundLayout.setVisibility(itemId == R.id.parse_mistake_sound_layout ? View.VISIBLE : View.GONE);
        parseAnswerBinding.parseMistakeInputText.setVisibility(itemId == R.id.parse_mistake_input_text ? View.VISIBLE : View.GONE);
    }
}
