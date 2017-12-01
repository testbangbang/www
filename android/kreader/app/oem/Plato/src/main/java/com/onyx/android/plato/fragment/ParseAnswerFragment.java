package com.onyx.android.plato.fragment;

import android.content.DialogInterface;
import android.databinding.ViewDataBinding;
import android.os.Environment;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.adapter.MistakeAdapter;
import com.onyx.android.plato.cloud.bean.AnalysisBean;
import com.onyx.android.plato.cloud.bean.AnswerBean;
import com.onyx.android.plato.cloud.bean.InsertParseBean;
import com.onyx.android.plato.cloud.bean.KnowledgeBean;
import com.onyx.android.plato.cloud.bean.ParseBean;
import com.onyx.android.plato.cloud.bean.PracticeParseBean;
import com.onyx.android.plato.cloud.bean.QuestionViewBean;
import com.onyx.android.plato.cloud.bean.SubjectBean;
import com.onyx.android.plato.common.CommonNotices;
import com.onyx.android.plato.common.Constants;
import com.onyx.android.plato.databinding.ParseAnswerBinding;
import com.onyx.android.plato.event.TimerEvent;
import com.onyx.android.plato.event.ToCorrectEvent;
import com.onyx.android.plato.interfaces.ParseAnswerView;
import com.onyx.android.plato.presenter.ParseAnswerPresenter;
import com.onyx.android.plato.utils.MediaManager;
import com.onyx.android.plato.utils.StringUtil;
import com.onyx.android.plato.utils.Utils;
import com.onyx.android.plato.view.CustomDialog;
import com.onyx.android.plato.view.DisableScrollGridManager;
import com.onyx.android.plato.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private MistakeAdapter mistakeAdapter;
    private MistakeAdapter mistakeCustomAdapter;
    private String voiceUrl;
    private AnswerBean answerBean;
    private String radio = getOutputFile().getAbsolutePath();

    @Override
    protected void loadData() {
        parseAnswerPresenter = new ParseAnswerPresenter(this);
        parseAnswerPresenter.getExplanation(questionData.getTaskId(), questionData.getId());
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        parseAnswerBinding = (ParseAnswerBinding) binding;
        parseAnswerBinding.parseQuestionView.setQuestionData(questionData, title);
        parseAnswerBinding.parseQuestionView.setFinished(true);
        parseAnswerBinding.parseQuestionView.setParse(true);
        parseAnswerBinding.parseTitleBar.setTitle(SunApplication.getInstance().getString(R.string.parse_of_answer));
        parseAnswerBinding.parseTitleBar.titleBarRecord.setVisibility(View.GONE);
        parseAnswerBinding.parseTitleBar.titleBarSubmit.setVisibility(View.GONE);
        mediaManager = MediaManager.newInstance();
        timerEvent = new TimerEvent();
        parseAnswerBinding.parseAddSoundLayout.parseDeleteSound.setEnabled(false);
        parseAnswerBinding.parseMistakeLayout.parseMistakeDelete.setEnabled(false);

        parseAnswerBinding.parseMistakeLayout.mistakeRecycler.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        parseAnswerBinding.parseMistakeLayout.mistakeRecycler.addItemDecoration(dividerItemDecoration);
        mistakeAdapter = new MistakeAdapter();
        parseAnswerBinding.parseMistakeLayout.mistakeRecycler.setAdapter(mistakeAdapter);

        parseAnswerBinding.parseMistakeLayout.mistakeCustomRecycler.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        parseAnswerBinding.parseMistakeLayout.mistakeCustomRecycler.addItemDecoration(dividerItemDecoration);
        mistakeCustomAdapter = new MistakeAdapter();
        parseAnswerBinding.parseMistakeLayout.mistakeCustomRecycler.setAdapter(mistakeCustomAdapter);
    }

    @Override
    protected void initListener() {
        parseAnswerBinding.parseTitleBar.titleBarTitle.setOnClickListener(this);
        parseAnswerBinding.parseAddSoundLayout.parseAddSound.setOnClickListener(this);
        parseAnswerBinding.parseAddSoundLayout.parseDeleteSound.setOnClickListener(this);
        parseAnswerBinding.parseAddSoundLayout.parseSoundRead.setOnClickListener(this);
        parseAnswerBinding.parseModifySoundLayout.parseModify.setOnClickListener(this);
        parseAnswerBinding.parseAddSoundLayout.parseReadRecord.setOnClickListener(this);
        parseAnswerBinding.parseModifySoundLayout.parseReadRecordModify.setOnClickListener(this);
        parseAnswerBinding.parseMistakeLayout.parseMistakeAdd.setOnClickListener(this);
        parseAnswerBinding.parseMistakeLayout.parseMistakeInputImage.setOnTouchListener(this);
        parseAnswerBinding.parseAddSoundLayout.parseSoundInput.setOnTouchListener(this);
        parseAnswerBinding.parseAddSoundLayout.parseAddImage.setOnTouchListener(this);
        parseAnswerBinding.parseMistakeLayout.parseMistakeVoice.setOnClickListener(this);
        parseAnswerBinding.parseMistakeLayout.parseMistakeModify.setOnClickListener(this);
        parseAnswerBinding.parseMistakeLayout.mistakeCustom.setOnClickListener(this);
        parseAnswerBinding.parseMistakeLayout.parseDeleteVoice.setOnClickListener(this);
    }

    private File getOutputFile() {
        File dir = new File(Environment.getExternalStorageDirectory(), Constants.SOUND_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, Constants.SOUND_DIR + Constants.VOICE_FORMAT);
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
        EventBus.getDefault().post(new ToCorrectEvent(null));
        return true;
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
            parseAnswerBinding.parseQuestionView.setParse(true);
        }

        if (parseAnswerPresenter != null) {
            parseAnswerPresenter.getExplanation(questionData.getTaskId(), questionData.getId());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_title:
                EventBus.getDefault().post(new ToCorrectEvent(null));
                break;
            case R.id.parse_add_sound:
                parseAnswerPresenter.insertAnalysis(questionData.getTaskId(), questionData.getId(), null, voiceUrl, null);
                break;
            case R.id.parse_delete_sound:
                break;
            case R.id.parse_modify:
                modify();
                break;
            case R.id.parse_mistake_voice:
            case R.id.parse_read_record_modify:
            case R.id.parse_sound_read:
            case R.id.parse_read_record:
                readRecord();
                break;
            case R.id.parse_mistake_add:
                insertMistakes();
                break;
            case R.id.parse_mistake_modify:
                parseAnswerBinding.parseMistakeLayout.parseMistakeWhole.setBackgroundResource(R.drawable.rectangle_gray_stroke);
                setItemVisible(R.id.parse_delete_voice);
                mistakeCustomAdapter.clear();
                parseAnswerBinding.parseMistakeLayout.mistakeCustom.setVisibility(View.VISIBLE);
                break;
            case R.id.mistake_custom:
                showCustomDialog();
                break;
            case R.id.parse_delete_voice:
                setItemVisible(R.id.parse_mistake_input_text);
                break;
        }
    }

    private void modify() {
        if (answerBean.score == answerBean.value) {
            parseAnswerBinding.parseAddSoundLayout.parseReadRecord.setVisibility(View.VISIBLE);
            setVisible(R.id.parse_add_sound_layout);
        }
    }

    private void insertMistakes() {
        List<SubjectBean> systemError = mistakeAdapter.getData();
        List<Integer> ids = new ArrayList<>();
        for (SubjectBean bean : systemError) {
            if (bean.selected) {
                ids.add(bean.id);
            }
        }

        List<SubjectBean> customError = mistakeCustomAdapter.getData();
        List<InsertParseBean> errors = new ArrayList<>();
        for (SubjectBean subjectBean : customError) {
            InsertParseBean bean = new InsertParseBean();
            bean.name = subjectBean.name;
            errors.add(bean);
        }

        parseAnswerPresenter.insertAnalysis(questionData.getTaskId(), questionData.getId(), ids, voiceUrl, errors);
    }

    private void showCustomDialog() {
        View view = View.inflate(SunApplication.getInstance(), R.layout.custom_mistake_layout, null);
        final EditText editMistake = (EditText) view.findViewById(R.id.edit_mistake);
        CustomDialog customDialog = new CustomDialog.Builder(getActivity()).setContentView(view)
                .setTitle(SunApplication.getInstance().getResources().getString(R.string.error_analysis))
                .setNegativeButton(SunApplication.getInstance().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(SunApplication.getInstance().getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mistake = editMistake.getText().toString();
                        List<SubjectBean> data = mistakeCustomAdapter.getData();
                        SubjectBean subjectBean = new SubjectBean();
                        subjectBean.selected = true;
                        subjectBean.name = mistake;
                        data.add(subjectBean);
                        mistakeCustomAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }).create();

        customDialog.show();
    }

    private void readRecord() {
        parseAnswerPresenter.speakRecord(mediaManager, radio);
        timerEvent.timeCountDown(duration);
    }

    private void uploadVoice() {
        parseAnswerPresenter.getUploadVoiceKey(getOutputFile());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnTimerEvent(TimerEvent event) {
        parseAnswerBinding.setRecorderTime(event.getResult() + "s");
        if (event.getResult() == 0) {
            parseAnswerBinding.setRecorderTime(duration + "s");
        }
    }

    private void setVisible(int id) {
        parseAnswerBinding.setAddVisible(id == R.id.parse_add_sound_layout);
        parseAnswerBinding.setModifyVisible(id == R.id.parse_modify_sound_layout);
        parseAnswerBinding.setMistakeVisible(id == R.id.parse_mistake_layout);
    }

    @Override
    public void setExplanation(PracticeParseBean data) {
        this.data = data;
        List<AnswerBean> myAnswer = data.myAnswer;
        ParseBean parseBean = data.exerciseDto;
        if (myAnswer == null && myAnswer.size() == 0) {
            return;
        }
        answerBean = myAnswer.get(0);
        parseAnswerPresenter.getAnalysis(questionData.getTaskId(), questionData.getId());
        String correct = insertPMark(parseBean.answer, "(" + answerBean.accuracy + "%" + ")");
        String userAnswer = String.format(SunApplication.getInstance().getResources().getString(R.string.user_answer), answerBean.answer);
        String correctAnswer = String.format(SunApplication.getInstance().getResources().getString(R.string.correct_answer), Html.fromHtml(correct));
        parseAnswerBinding.setCorrectAnswer(correctAnswer);

        if (questionData.getExerciseSelections() != null && questionData.getExerciseSelections().size() > 0) {
            parseAnswerBinding.setUserAnswer(userAnswer + "(" + (answerBean.isCorrect ?
                    SunApplication.getInstance().getResources().getString(R.string.correct) :
                    SunApplication.getInstance().getResources().getString(R.string.mistake)) + ")");
            parseAnswerBinding.parseAnswerImage.setVisibility(View.GONE);
        } else {
            parseAnswerBinding.parseAnswerImage.setVisibility(View.VISIBLE);
            String scoreDetail = String.format(SunApplication.getInstance().getResources().getString(R.string.score_detail), answerBean.value, answerBean.score);
            String subjectAnswer = String.format(SunApplication.getInstance().getResources().getString(R.string.user_answer), scoreDetail);
            parseAnswerBinding.setUserAnswer(subjectAnswer);
            Utils.loadImageUrl(answerBean.answer, parseAnswerBinding.parseAnswerImage, R.drawable.ic_answer_area);
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
        setVisible(answerBean.value == answerBean.score ? R.id.parse_add_sound_layout : R.id.parse_mistake_layout);
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

    @Override
    public void setAnalysis(AnalysisBean analysisBean) {
        List<SubjectBean> systemErrors = analysisBean.systemErrors;
        if (systemErrors != null && systemErrors.size() > 0) {
            mistakeAdapter.setData(systemErrors);
        }

        List<SubjectBean> errors = analysisBean.errors;
        if (errors != null && errors.size() > 0) {
            mistakeCustomAdapter.setData(errors);
            parseAnswerBinding.parseMistakeLayout.mistakeCustom.setVisibility(View.GONE);
        }

        radio = analysisBean.radio;
        if (!StringUtil.isNullOrEmpty(radio)) {
            if (answerBean.value == answerBean.score) {
                setVisible(R.id.parse_modify_sound_layout);
            } else {
                setItemVisible(R.id.parse_mistake_sound_layout);
            }
            duration = mediaManager.getDurationInSecond(radio);
            parseAnswerBinding.setRecorderTime(duration + "s");
        }
    }

    @Override
    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
        parseAnswerBinding.setRecorderTime(duration + "s");
        setItemVisible(answerBean.value == answerBean.score ? R.id.parse_sound_read : R.id.parse_mistake_voice);
    }

    @Override
    public void insertAnalysis() {
        if (answerBean.value == answerBean.score) {
            setVisible(R.id.parse_modify_sound_layout);
        } else {
            setItemVisible(R.id.parse_mistake_sound_layout);
            parseAnswerBinding.parseMistakeLayout.parseMistakeWhole.setBackgroundColor(SunApplication.getInstance().getResources().getColor(R.color.white));
            parseAnswerBinding.parseMistakeLayout.mistakeCustom.setVisibility(View.GONE);
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
                uploadVoice();
                break;
        }
        return true;
    }

    public void setItemVisible(int itemId) {
        parseAnswerBinding.parseMistakeLayout.parseMistakeSoundLayout.setVisibility(itemId == R.id.parse_mistake_sound_layout ? View.VISIBLE : View.GONE);
        parseAnswerBinding.parseMistakeLayout.parseHandleLayout.setVisibility(itemId == R.id.parse_mistake_sound_layout ? View.GONE : View.VISIBLE);
        parseAnswerBinding.parseMistakeLayout.parseMistakeInputText.setVisibility(itemId == R.id.parse_mistake_input_text ? View.VISIBLE : View.GONE);
        parseAnswerBinding.parseMistakeLayout.parseMistakeVoice.setVisibility(itemId == R.id.parse_mistake_input_text ? View.GONE : View.VISIBLE);
        parseAnswerBinding.parseAddSoundLayout.parseSoundInput.setVisibility(itemId == R.id.parse_sound_input ? View.VISIBLE : View.GONE);
        parseAnswerBinding.parseAddSoundLayout.parseSoundRead.setVisibility(itemId == R.id.parse_sound_read ? View.VISIBLE : View.GONE);
        parseAnswerBinding.parseMistakeLayout.parseDeleteVoice.setVisibility(itemId == R.id.parse_delete_voice ? View.VISIBLE : View.GONE);
    }
}
