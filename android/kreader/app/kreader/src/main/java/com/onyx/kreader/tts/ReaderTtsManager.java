package com.onyx.kreader.tts;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderSentence;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.host.request.GetSentenceRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.actions.GotoPageAction;
import com.onyx.kreader.utils.PagePositionUtils;

/**
 * Created by joy on 7/20/16.
 */
public class ReaderTtsManager {
    public static abstract class Callback {
        public abstract void onStateChanged();
    }

    private ReaderActivity readerActivity;
    private ReaderTtsService ttsService;
    private ReaderSentence currentSentence;

    public ReaderTtsManager(final ReaderActivity readerActivity, final Callback callback) {
        this.readerActivity = readerActivity;

        ttsService = new ReaderTtsService(readerActivity, new ReaderTtsService.Callback() {
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
//                callback.onStateChanged();
                requestSentenceForTts();
            }

            @Override
            public void onStopped() {
                callback.onStateChanged();
            }

            @Override
            public void onError() {
                callback.onStateChanged();
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

    private void requestSentenceForTts() {
        String startPosition = currentSentence == null ? "" : currentSentence.getNextPosition();
        final GetSentenceRequest sentenceRequest = new GetSentenceRequest(readerActivity.getCurrentPage(), startPosition);
        readerActivity.submitRequest(sentenceRequest, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e != null) {
                            return;
                        }
                        currentSentence = sentenceRequest.getSentenceResult();
                        if (currentSentence == null || currentSentence.isEndOfDocument()) {
                            if (currentSentence == null) {
                                Debug.d("getSentenceResult failed");
                            }
                            if (currentSentence != null && currentSentence.isEndOfDocument()) {
                                Debug.d("sentence at end of document");
                            }
                            return;
                        }
                        Debug.d("current sentence: " + currentSentence.getReaderSelection().getText() +
                                ", " + currentSentence.isEndOfScreen() +
                                ", " + currentSentence.isEndOfDocument());
                        if (currentSentence.isEndOfScreen()) {
                            currentSentence = null;
                            String next = PagePositionUtils.fromPageNumber(readerActivity.getCurrentPage() + 1);
                            new GotoPageAction(next).execute(readerActivity, new BaseCallback() {
                                @Override
                                public void done(BaseRequest request, Throwable e) {
                                    requestSentenceForTts();
                                }
                            });
                            return;
                        }
                        if (StringUtils.isNullOrEmpty(currentSentence.getReaderSelection().getText())) {
                            requestSentenceForTts();
                            return;
                        }
                        ttsService.startTts(currentSentence.getReaderSelection().getText());
                    }
                });
    }
}
