package com.onyx.android.plato.presenter;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.GetAnalysisBean;
import com.onyx.android.plato.cloud.bean.InsertParseBean;
import com.onyx.android.plato.cloud.bean.InsertParseRequestBean;
import com.onyx.android.plato.cloud.bean.PracticeParseRequestBean;
import com.onyx.android.plato.cloud.bean.PracticeParseResultBean;
import com.onyx.android.plato.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.plato.cloud.bean.UploadBean;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.common.CommonNotices;
import com.onyx.android.plato.common.Constants;
import com.onyx.android.plato.data.ParseAnswerData;
import com.onyx.android.plato.event.EmptyEvent;
import com.onyx.android.plato.interfaces.ParseAnswerView;
import com.onyx.android.plato.requests.cloud.GetAnalysisRequest;
import com.onyx.android.plato.requests.cloud.GetPracticeParseRequest;
import com.onyx.android.plato.requests.cloud.InsertAnalysisRequest;
import com.onyx.android.plato.requests.cloud.RequestUploadFile;
import com.onyx.android.plato.requests.local.GetRecordRequest;
import com.onyx.android.plato.requests.local.RecorderRequest;
import com.onyx.android.plato.requests.local.SaveRecordRequest;
import com.onyx.android.plato.requests.local.SpeakRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;
import com.onyx.android.plato.utils.MediaManager;
import com.onyx.android.plato.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

/**
 * Created by li on 2017/10/26.
 */

public class ParseAnswerPresenter {
    private ParseAnswerView parseAnswerView;
    private final ParseAnswerData parseAnswerData;

    public ParseAnswerPresenter(ParseAnswerView parseAnswerView) {
        this.parseAnswerView = parseAnswerView;
        parseAnswerData = new ParseAnswerData();
    }

    public void getExplanation(int taskId, int questionId, int studentId) {
        PracticeParseRequestBean requestBean = new PracticeParseRequestBean();
        requestBean.id = questionId;
        requestBean.pid = taskId;
        requestBean.studentId = studentId;
        final GetPracticeParseRequest rq = new GetPracticeParseRequest(requestBean);
        parseAnswerData.getParse(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                PracticeParseResultBean resultBean = rq.getResultBean();
                if (resultBean == null) {
                    EventBus.getDefault().post(new EmptyEvent());
                    return;
                }

                parseAnswerView.setExplanation(resultBean.data);
            }
        });
    }

    public void startRecord(MediaManager mediaManager, String fileName) {
        RecorderRequest rq = new RecorderRequest(mediaManager, fileName);
        parseAnswerData.startRecord(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }

    public void speakRecord(MediaManager mediaManager, String fileName) {
        SpeakRequest rq = new SpeakRequest(mediaManager, fileName);
        parseAnswerData.speakRecord(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }

    public void saveRecord(int taskId, int questionId, String recordPath, long duration) {
        final SaveRecordRequest rq = new SaveRecordRequest(taskId, questionId, recordPath, duration);
        parseAnswerData.saveRecord(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                String result = rq.getResult();
                if (!StringUtil.isNullOrEmpty(result)) {
                    CommonNotices.show(result);
                }
            }
        });
    }

    public void getRecord(int taskId, int questionId) {
        final GetRecordRequest rq = new GetRecordRequest(taskId + "", questionId + "");
        parseAnswerData.getRecord(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                long recordDuration = rq.getRecordDuration();
                if (recordDuration == -1) {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.lose_data));
                    return;
                }

                parseAnswerView.setRecordDuration(recordDuration);
            }
        });
    }

    public void getAnalysis(int taskId, int questionId, int studentId) {
        PracticeParseRequestBean requestBean = new PracticeParseRequestBean();
        requestBean.id = questionId;
        requestBean.pid = taskId;
        requestBean.studentId = studentId;

        final GetAnalysisRequest rq = new GetAnalysisRequest(requestBean);
        parseAnswerData.getAnalysis(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetAnalysisBean resultBean = rq.getResultBean();
                if (resultBean == null) {
                    EventBus.getDefault().post(new EmptyEvent());
                    return;
                }
                parseAnswerView.setAnalysis(resultBean.data);
            }
        });
    }

    public void getUploadVoiceKey(File file) {
        final RequestUploadFile rq = new RequestUploadFile(file);
        parseAnswerData.getUploadVoiceKey(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UploadBean bean = rq.getBean();
                if (bean == null || bean.data == null) {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.upload_failed));
                    return;
                }

                UploadBean.UploadResult data = bean.data;
                String voiceUrl = CloudApiContext.APPEND_URL + data.key;
                parseAnswerView.setVoiceUrl(voiceUrl);
            }
        });
    }

    public void insertAnalysis(int taskId, int questionId, int studentId, List<Integer> ids, String radio, List<InsertParseBean> errors) {
        InsertParseRequestBean requestBean = new InsertParseRequestBean();
        requestBean.ids = ids;
        requestBean.radio = radio;
        requestBean.error = errors;

        final InsertAnalysisRequest rq = new InsertAnalysisRequest(taskId, questionId, studentId, requestBean);
        parseAnswerData.insertAnalysis(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                SubmitPracticeResultBean resultBean = rq.getResultBean();
                if (resultBean == null || !Constants.OK.equals(resultBean.msg)) {
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.submit_failed));
                } else {
                    parseAnswerView.insertAnalysis();
                }
            }
        });
    }
}
