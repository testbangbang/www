package com.onyx.android.dr.reader.tts;

import com.onyx.android.dr.reader.common.ReadSettingTtsConfig;
import com.onyx.android.dr.reader.event.NotifyTtsStateChangedEvent;
import com.onyx.android.dr.reader.handler.TTSHandler;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by joy on 7/20/16.
 */
public class ReaderTtsManager {

    @SuppressWarnings("unused")
    private static final String TAG = ReaderTtsManager.class.getSimpleName();

    private ReaderPresenter readerPresenter;
    private ReaderTtsService ttsService;
    private TTSHandler ttsHandler;
    private String text;

    public ReaderTtsManager(final ReaderPresenter readerPresenter,TTSHandler ttsHandler) {
        this.readerPresenter = readerPresenter;
        this.ttsHandler = ttsHandler;
        ttsService = new ReaderTtsService(readerPresenter.getReaderView().getApplicationContext(), new ReaderTtsService.Callback() {
            @Override
            public void onStart() {
                EventBus.getDefault().post(new NotifyTtsStateChangedEvent().onStart());
            }

            @Override
            public void onPaused() {
                EventBus.getDefault().post(new NotifyTtsStateChangedEvent().onPaused());
            }

            @Override
            public void onDone() {
                EventBus.getDefault().post(new NotifyTtsStateChangedEvent().onDone());
            }

            @Override
            public void onStopped() {
                EventBus.getDefault().post(new NotifyTtsStateChangedEvent().onStopped());
            }

            @Override
            public void onError() {
                EventBus.getDefault().post(new NotifyTtsStateChangedEvent().onError());
            }
        });
    }

    public boolean isSpeaking() {
        return ttsService.isSpeaking();
    }

    public void setSpeechRate(float rate) {
        ttsService.setSpeechRate(rate);
    }

    public float getSpeechRate(){
        if(ttsService != null) {
            return ttsService.getSpeechRate();
        }

        return ReadSettingTtsConfig.NORMAL_SPEED;
    }

    public void supplyText(final String text) {
        this.text = text;
    }

    public void play() {
        if (ttsService.isPaused()) {
            ttsService.resume();
            return;
        }

        reset();
        if (StringUtils.isNotBlank(text)) {
            ttsService.startTts(text);
            text = null;
        } else {
            ttsHandler.requestSentenceForTts();
        }
    }

    public void pause() {
        ttsService.pause();
    }

    public void stop() {
        ttsService.stop();
    }

    public void shutdown() {
        ttsService.shutdown();
        ttsService = null;
    }

    private void reset() {
        ttsService.reset();
    }


}
