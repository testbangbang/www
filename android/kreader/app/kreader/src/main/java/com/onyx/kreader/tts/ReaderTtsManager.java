package com.onyx.kreader.tts;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.host.request.GetPageTextRequest;
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
    private Callback callback;
    private ReaderTtsService ttsService;

    public ReaderTtsManager(final ReaderActivity readerActivity, final Callback callback) {
        this.readerActivity = readerActivity;
        this.callback = callback;

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
                callback.onStateChanged();
                if (readerActivity.getCurrentPage() < readerActivity.getPageCount() - 1) {
                    String next = PagePositionUtils.fromPageNumber(readerActivity.getCurrentPage() + 1);
                    new GotoPageAction(next).execute(readerActivity, new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable e) {
                            requestPageTextForTts();
                        }
                    });
                }
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

        requestPageTextForTts();
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

    private void requestPageTextForTts() {
        final GetPageTextRequest textRequest = new GetPageTextRequest(readerActivity.getCurrentPage());
        readerActivity.getReader().submitRequest(readerActivity,
                textRequest, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e != null) {
                            return;
                        }
                        ttsService.startTts(textRequest.getText());
                    }
                });
    }
}
