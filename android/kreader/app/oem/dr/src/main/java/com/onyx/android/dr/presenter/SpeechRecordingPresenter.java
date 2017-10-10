package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.data.InformalEssayData;
import com.onyx.android.dr.interfaces.SpeechRecordingView;
import com.onyx.android.dr.request.local.InformalEssayQueryAll;
import com.onyx.android.dr.request.local.InformalEssayQueryByTitle;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class SpeechRecordingPresenter {
    private final SpeechRecordingView speechRecordingView;
    private InformalEssayData infromalEssayData;
    private Context context;
    private String tag = "";

    public SpeechRecordingPresenter(Context context, SpeechRecordingView speechRecordingView) {
        this.speechRecordingView = speechRecordingView;
        this.context = context;
        infromalEssayData = new InformalEssayData();
    }

    public void getAllInformalEssayData() {
        final InformalEssayQueryAll req = new InformalEssayQueryAll();
        infromalEssayData.getAllInformalEssay(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void getInformalEssayQueryByTitle(String keyword) {
        final InformalEssayQueryByTitle req = new InformalEssayQueryByTitle(keyword);
        infromalEssayData.getInformalEssayQueryByTitle(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                speechRecordingView.setInformalEssayByTitle(req.getData());
            }
        });
    }
}
