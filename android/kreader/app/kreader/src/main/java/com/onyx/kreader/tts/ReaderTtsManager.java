package com.onyx.kreader.tts;

import android.app.Activity;
import android.content.Context;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.host.request.RenderRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by joy on 7/20/16.
 */
public class ReaderTtsManager {

    private static final String TAG = ReaderTtsManager.class.getSimpleName();

    public static abstract class Callback {
        public abstract void requestSentence();
        public abstract void onStateChanged();
        public abstract void onError();
    }

    private Callback callback;

    private ReaderTtsService ttsService;
    private String text;

    public ReaderTtsManager(final Context context) {
        ttsService = new ReaderTtsService((Activity)context, new ReaderTtsService.Callback() {
            @Override
            public void onStart() {
                callback.onStateChanged();
            }

            @Override
            public void onPaused() {
                callback.onStateChanged();
            }

            @Override
            public void onDone() {
                if (callback != null) {
                    callback.requestSentence();
                }
            }

            @Override
            public void onStopped() {
                callback.onStateChanged();
            }

            @Override
            public void onError() {
                if (callback != null) {
                    callback.onError();
                }
            }
        });
    }

    public void registerCallback(Callback callback) {
        this.callback = callback;
    }

    public boolean isSpeaking() {
        return ttsService.isSpeaking();
    }

    public void setSpeechRate(float rate) {
        ttsService.setSpeechRate(rate);
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
            if (callback != null) {
                callback.requestSentence();
            }
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
        callback = null;
    }

    private void reset() {
        ttsService.reset();
    }


}
