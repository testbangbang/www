package com.onyx.android.plato.presenter;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.cloud.bean.PracticeParseRequestBean;
import com.onyx.android.plato.cloud.bean.PracticeParseResultBean;
import com.onyx.android.plato.common.CommonNotices;
import com.onyx.android.plato.data.ParseAnswerData;
import com.onyx.android.plato.interfaces.ParseAnswerView;
import com.onyx.android.plato.requests.cloud.GetPracticeParseRequest;
import com.onyx.android.plato.requests.local.RecorderRequest;
import com.onyx.android.plato.requests.local.SpeakRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;
import com.onyx.android.plato.utils.MediaManager;

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
                    CommonNotices.show(SunApplication.getInstance().getResources().getString(R.string.login_activity_request_failed));
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
}
