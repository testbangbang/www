package com.onyx.android.plato.presenter;

import com.onyx.android.plato.data.ParseAnswerData;
import com.onyx.android.plato.interfaces.ParseAnswerView;
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

    public void getExplanation() {
        parseAnswerView.setExplanation();
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
