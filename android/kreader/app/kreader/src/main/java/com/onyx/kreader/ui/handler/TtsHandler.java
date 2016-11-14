package com.onyx.kreader.ui.handler;

import android.util.Log;
import android.view.KeyEvent;

import android.widget.Toast;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderSentence;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.host.request.GetSentenceRequest;
import com.onyx.kreader.host.request.RenderRequest;
import com.onyx.kreader.tts.ReaderTtsManager;
import com.onyx.kreader.ui.actions.GotoPageAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.events.ChangeOrientationEvent;
import com.onyx.kreader.ui.events.TtsErrorEvent;
import com.onyx.kreader.ui.events.TtsRequestSentenceEvent;
import com.onyx.kreader.utils.PagePositionUtils;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by joy on 7/29/16.
 */
public class TtsHandler extends BaseHandler {

    private static final String TAG = TtsHandler.class.getSimpleName();

    private ReaderDataHolder readerDataHolder;
    private ReaderSentence currentSentence;
    private boolean stopped;

    public TtsHandler(HandlerManager parent) {
        super(parent);

        readerDataHolder = getParent().getReaderDataHolder();
    }

    @Override
    public boolean onKeyUp(ReaderDataHolder readerDataHolder, int keyCode, KeyEvent event) {
        final int page = readerDataHolder.getCurrentPage();
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (page > 0) {
                    ttsStop();
                    gotoPage(readerDataHolder, page -1);
                }
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (page < readerDataHolder.getPageCount() - 1) {
                    ttsStop();
                    gotoPage(readerDataHolder, page + 1);
                }
                return true;
            default:
                break;
        }
        return false;
    }

    public void onError() {
        stopped = true;
        readerDataHolder.submitRenderRequest(new RenderRequest());
    }

    public void ttsPlay() {
        stopped = false;
        readerDataHolder.getTtsManager().play();
    }

    public void ttsPause() {
        readerDataHolder.getTtsManager().pause();
    }

    public void ttsStop() {
        currentSentence = null;
        stopped = true;
        readerDataHolder.getTtsManager().stop();
        readerDataHolder.submitRenderRequest(new RenderRequest());
    }

    public void setSpeechRate(float rate) {
        SingletonSharedPreference.setTtsSpeechRate(readerDataHolder.getContext(),rate);
        readerDataHolder.getTtsManager().stop();
        readerDataHolder.getTtsManager().setSpeechRate(rate);
        if (currentSentence != null) {
            readerDataHolder.getTtsManager().supplyText(currentSentence.getReaderSelection().getText());
            readerDataHolder.getTtsManager().play();
        }
    }

    public float getSpeechRate(){
        return SingletonSharedPreference.getTtsSpeechRate(readerDataHolder.getContext());
    }

    private void gotoPage(final ReaderDataHolder readerDataHolder, final int page) {
        new GotoPageAction(PagePositionUtils.fromPageNumber(page)).execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ttsPlay();
            }
        });
    }

    public boolean requestSentenceForTts() {
        if (currentSentence != null) {
            if (currentSentence.isEndOfDocument()) {
                Debug.d(TAG, "end of document");
                readerDataHolder.getTtsManager().stop();
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
                if (stopped) {
                    return;
                }
                currentSentence = sentenceRequest.getSentenceResult();
                if (currentSentence == null) {
                    Log.w(TAG, "getById sentence failed");
                    return;
                }
                dumpCurrentSentence();
                if (StringUtils.isNullOrEmpty(currentSentence.getReaderSelection().getText())) {
                    requestSentenceForTts();
                    return;
                }
                readerDataHolder.getTtsManager().supplyText(currentSentence.getReaderSelection().getText());
                readerDataHolder.getTtsManager().play();
            }
        });
        return true;
    }

    private void dumpCurrentSentence() {
        Debug.d(TAG, "current sentence: %s, [%s, %s], %b, %b",
                StringUtils.deleteNewlineSymbol(currentSentence.getReaderSelection().getText()),
                currentSentence.getReaderSelection().getStartPosition(),
                currentSentence.getReaderSelection().getEndPosition(),
                currentSentence.isEndOfScreen(),
                currentSentence.isEndOfDocument());
    }

}
