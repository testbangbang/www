package com.onyx.android.plato.presenter;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.PracticeParseRequestBean;
import com.onyx.android.plato.cloud.bean.PracticeParseResultBean;
import com.onyx.android.plato.common.CommonNotices;
import com.onyx.android.plato.data.ParseAnswerData;
import com.onyx.android.plato.event.EmptyEvent;
import com.onyx.android.plato.interfaces.ParseAnswerView;
import com.onyx.android.plato.requests.cloud.GetPracticeParseRequest;
import com.onyx.android.plato.requests.local.GetRecordRequest;
import com.onyx.android.plato.requests.local.RecorderRequest;
import com.onyx.android.plato.requests.local.SaveRecordRequest;
import com.onyx.android.plato.requests.local.SpeakRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;
import com.onyx.android.plato.utils.MediaManager;
import com.onyx.android.plato.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

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
}
