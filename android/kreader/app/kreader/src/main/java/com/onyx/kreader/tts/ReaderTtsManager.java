package com.onyx.kreader.tts;

import android.app.Activity;

import android.util.Log;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderSentence;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.host.request.GetSentenceRequest;
import com.onyx.kreader.host.request.RenderRequest;
import com.onyx.kreader.ui.actions.GotoPageAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.utils.PagePositionUtils;

/**
 * Created by joy on 7/20/16.
 */
public class ReaderTtsManager {

    private static final String TAG = ReaderTtsManager.class.getSimpleName();

    public static abstract class Callback {
        public abstract void onStateChanged();
    }

    private ReaderDataHolder readerDataHolder;
    private ReaderTtsService ttsService;
    private ReaderSentence currentSentence;

    public ReaderTtsManager(final ReaderDataHolder readerDataHolder, final Callback callback) {
        this.readerDataHolder = readerDataHolder;

        ttsService = new ReaderTtsService((Activity) readerDataHolder.getContext(), new ReaderTtsService.Callback() {
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
                requestSentenceForTts();
            }

            @Override
            public void onStopped() {
                callback.onStateChanged();
                readerDataHolder.submitRenderRequest(new RenderRequest());
            }

            @Override
            public void onError() {
                callback.onStateChanged();
                readerDataHolder.submitRenderRequest(new RenderRequest());
            }
        });
    }

    public boolean isSpeaking() {
        return ttsService.isSpeaking();
    }

    public boolean isPaused() {
        return ttsService.isPaused();
    }

    public void play() {
        if (ttsService.isPaused()) {
            ttsService.resume();
            return;
        }

        reset();
        requestSentenceForTts();
    }

    public void pause() {
        ttsService.pause();
    }

    public void stop() {
        ttsService.stop();
    }

    public void shutdown() {
        ttsService.shutdown();
    }

    private void reset() {
        currentSentence = null;
        ttsService.reset();
    }

    private boolean requestSentenceForTts() {
        if (currentSentence != null) {
            if (currentSentence.isEndOfDocument()) {
                Debug.d(TAG, "end of document");
                stop();
                return false;
            }
            if (currentSentence.isEndOfScreen()) {
                Debug.d(TAG, "end of page");
                currentSentence = null;
                String next = PagePositionUtils.fromPageNumber(readerDataHolder.getCurrentPage() + 1);
                new GotoPageAction(next).execute(readerDataHolder, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e != null) {
                            Log.w(TAG, e);
                            return;
                        }
                        requestSentenceForTts();
                    }
                });
                return true;
            }
        }

        String startPosition = currentSentence == null ? "" : currentSentence.getNextPosition();
        final GetSentenceRequest sentenceRequest = new GetSentenceRequest(readerDataHolder.getCurrentPage(), startPosition);
        readerDataHolder.submitRenderRequest(sentenceRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    Log.w(TAG, e);
                    return;
                }
                currentSentence = sentenceRequest.getSentenceResult();
                if (currentSentence == null) {
                    Log.w(TAG, "get sentence failed");
                    return;
                }
                dumpCurrentSentence();
                if (StringUtils.isNullOrEmpty(currentSentence.getReaderSelection().getText())) {
                    requestSentenceForTts();
                    return;
                }
                ttsService.startTts(currentSentence.getReaderSelection().getText());
            }
        });
        return true;
    }

    private void dumpCurrentSentence() {
        Debug.d("current sentence: " + currentSentence.getReaderSelection().getText() +
                ", " + currentSentence.isEndOfScreen() +
                ", " + currentSentence.isEndOfDocument());
    }
}
