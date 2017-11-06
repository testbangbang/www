package com.onyx.android.sun.fragment;

import android.databinding.ViewDataBinding;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.cloud.bean.Question;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.databinding.ParseAnswerBinding;
import com.onyx.android.sun.event.TimerEvent;
import com.onyx.android.sun.event.ToCorrectEvent;
import com.onyx.android.sun.interfaces.ParseAnswerView;
import com.onyx.android.sun.presenter.ParseAnswerPresenter;
import com.onyx.android.sun.utils.MediaManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

/**
 * Created by li on 2017/10/26.
 */

public class ParseAnswerFragment extends BaseFragment implements View.OnClickListener, ParseAnswerView, View.OnTouchListener {
    private ParseAnswerBinding parseAnswerBinding;
    private Question questionData;
    private String title;
    private ParseAnswerPresenter parseAnswerPresenter;
    private MediaManager mediaManager;
    private long startTime;
    private long endTime;
    private long duration;
    private TimerEvent timerEvent;

    @Override
    protected void loadData() {
        parseAnswerPresenter = new ParseAnswerPresenter(this);
        parseAnswerPresenter.getExplanation();
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        parseAnswerBinding = (ParseAnswerBinding) binding;
        //TODO:parseAnswerBinding.parseQuestionView.setQuestionData(questionData, title);
        parseAnswerBinding.parseQuestionView.setFinished(true);
        parseAnswerBinding.parseTitleBar.setTitle(SunApplication.getInstance().getString(R.string.parse_of_answer));
        parseAnswerBinding.parseTitleBar.titleBarRecord.setVisibility(View.GONE);
        parseAnswerBinding.parseTitleBar.titleBarSubmit.setVisibility(View.GONE);
        mediaManager = MediaManager.newInstance();
        timerEvent = new TimerEvent();
        parseAnswerBinding.parseDeleteSound.setEnabled(false);
        parseAnswerBinding.parseMistakeDelete.setEnabled(false);
        setVisible(R.id.parse_mistake_layout);
    }

    @Override
    protected void initListener() {
        parseAnswerBinding.parseTitleBar.setListener(this);
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

        File file = new File(dir, "haha.amr");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    @Override
    protected int getRootView() {
        return R.layout.parse_answer_layout;
    }

    @Override
    public boolean onKeyBack() {
        return false;
    }

    public void setQuestionData(Question questionData, String title) {
        this.questionData = questionData;
        this.title = title;
        if (parseAnswerBinding != null) {
            //TODO:parseAnswerBinding.parseQuestionView.setQuestionData(questionData, title);
            parseAnswerBinding.parseQuestionView.setFinished(true);
        }

        if (parseAnswerPresenter != null) {
            parseAnswerPresenter.getExplanation();
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
            CommonNotices.show("请录入语音");
            return false;
        }
        setVisible(id);
        parseAnswerBinding.setRecorderTime(duration + "s");
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
    public void setExplanation() {
        if (parseAnswerBinding == null) {
            return;
        }
        String userAnswer = String.format(SunApplication.getInstance().getResources().getString(R.string.user_answer), "a");
        String correctAnswer = String.format(SunApplication.getInstance().getResources().getString(R.string.correct_answer), "a");
        parseAnswerBinding.setUserAnswer(userAnswer + "(" + ")");
        parseAnswerBinding.setCorrectAnswer(correctAnswer + "(" + ")");
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
                    CommonNotices.show("按住时间太短");
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
