package com.onyx.kreader.tts;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by joy on 7/20/16.
 */
public class ReaderTtsManager {

    @SuppressWarnings("unused")
    private static final String TAG = ReaderTtsManager.class.getSimpleName();

    private ReaderDataHolder readerDataHolder;
    private ReaderTtsService ttsService;
    private String text;

    public ReaderTtsManager(final ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
        ttsService = new ReaderTtsService(readerDataHolder.getContext(), new ReaderTtsService.Callback() {
            @Override
            public void onStart() {
                readerDataHolder.notifyTtsStateChanged();
            }

            @Override
            public void onPaused() {
                readerDataHolder.notifyTtsStateChanged();
            }

            @Override
            public void onDone() {
                readerDataHolder.notifyTtsRequestSentence();
            }

            @Override
            public void onStopped() {
                readerDataHolder.notifyTtsStateChanged();
            }

            @Override
            public void onError() {
                readerDataHolder.notifyTtsError();
            }
        });
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
            readerDataHolder.notifyTtsRequestSentence();
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
