package com.onyx.kreader.ui.handler;

import android.view.KeyEvent;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.api.ReaderSentence;
import com.onyx.android.sdk.reader.host.request.GetSentenceRequest;
import com.onyx.android.sdk.reader.host.request.RenderRequest;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.utils.ChineseTextUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.actions.GotoPositionAction;
import com.onyx.kreader.ui.actions.NextScreenAction;
import com.onyx.kreader.ui.actions.PreviousScreenAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.dialog.DialogTts;

/**
 * Created by joy on 7/29/16.
 */
public class TtsHandler extends BaseHandler {

    private static final Class TAG = TtsHandler.class;

    private ReaderDataHolder readerDataHolder;
    private String initialPosition;
    private String audioPath;
    private ReaderSentence currentSentence;
    private boolean stopped;

    private DialogTts dialogTts;

    public static HandlerInitialState createInitialState(String initialPosition,String audioPath) {
        HandlerInitialState state = new HandlerInitialState();
        state.ttsInitialPosition = initialPosition;
        state.audioPath = audioPath;
        return state;
    }

    public TtsHandler(HandlerManager parent) {
        super(parent);
        readerDataHolder = getParent().getReaderDataHolder();
    }

    @Override
    public void onActivate(ReaderDataHolder readerDataHolder, final HandlerInitialState initialState) {
        if (initialState != null) {
            initialPosition = initialState.ttsInitialPosition;
            audioPath = initialState.audioPath;
        }
        getDialogTts().show();
    }

    @Override
    public void onDeactivate(ReaderDataHolder readerDataHolder) {
        if (dialogTts == null) {
            return;
        }
        if (dialogTts.isShowing()) {
            dialogTts.dismiss();
        }
        dialogTts = null;
    }

    @Override
    public boolean onKeyUp(ReaderDataHolder readerDataHolder, int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (readerDataHolder.getReaderViewInfo().canPrevScreen) {
                    ttsStop();
                    prevScreen(readerDataHolder);
                }
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (readerDataHolder.getReaderViewInfo().canNextScreen) {
                    ttsStop();
                    nextScreen(readerDataHolder);
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
        readerDataHolder.getTtsManager().play(audioPath);
    }

    public String getAudioPath(){
        return audioPath;
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

    public void setInitialPosition(String initialPosition) {
        this.initialPosition = initialPosition;
    }

    public void setSpeechRate(float rate) {
        SingletonSharedPreference.setTtsSpeechRate(readerDataHolder.getContext(),rate);
        readerDataHolder.getTtsManager().stop();
        readerDataHolder.getTtsManager().setSpeechRate(rate);
        if (currentSentence != null) {
            readerDataHolder.getTtsManager().supplyText(cleanUpText(currentSentence.getReaderSelection().getText()));
            readerDataHolder.getTtsManager().play(null);
        }
    }

    public float getSpeechRate(){
        return SingletonSharedPreference.getTtsSpeechRate(readerDataHolder.getContext());
    }

    public void prevScreen(final ReaderDataHolder readerDataHolder) {
        new PreviousScreenAction().execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ttsPlay();
            }
        });
    }

    @Override
    public void nextScreen(ReaderDataHolder readerDataHolder) {
        new NextScreenAction().execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ttsPlay();
            }
        });
    }

    private DialogTts getDialogTts() {
        if (dialogTts == null) {
            dialogTts = new DialogTts(readerDataHolder);
        }
        return dialogTts;
    }

    private void gotoPage(final ReaderDataHolder readerDataHolder, final int page) {
        new GotoPositionAction(PagePositionUtils.fromPageNumber(page)).execute(readerDataHolder, new BaseCallback() {
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
                nextScreen(readerDataHolder);
                return true;
            }
        }

        String startPosition = null;
        if (StringUtils.isNotBlank(initialPosition)) {
            startPosition = initialPosition;
            initialPosition = null;
        }
        if (startPosition == null) {
            startPosition = currentSentence == null ? "" : currentSentence.getNextPosition();
        }
        final GetSentenceRequest sentenceRequest = new GetSentenceRequest(readerDataHolder.getCurrentPagePosition(), startPosition);
        readerDataHolder.submitRenderRequest(sentenceRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    Toast.makeText(readerDataHolder.getContext(), R.string.get_page_text_failed, Toast.LENGTH_LONG).show();
                    ttsStop();
                    Debug.w(TAG, e);
                    return;
                }
                if (stopped) {
                    return;
                }
                currentSentence = sentenceRequest.getSentenceResult();
                if (currentSentence == null) {
                    Toast.makeText(readerDataHolder.getContext(), R.string.get_page_text_failed, Toast.LENGTH_LONG).show();
                    ttsStop();
                    Debug.w(TAG, "get sentence failed");
                    return;
                }
                if (!currentSentence.isNonBlank()) {
                    requestSentenceForTts();
                    return;
                }
                dumpCurrentSentence();
                readerDataHolder.getTtsManager().supplyText(cleanUpText(currentSentence.getReaderSelection().getText()));
                readerDataHolder.getTtsManager().play(null);
            }
        });
        return true;
    }

    private String cleanUpText(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        return ChineseTextUtils.removeWhiteSpacesBetweenChineseText(text);
    }

    private void dumpCurrentSentence() {
        Debug.e(TAG, "current sentence: %s, [%s, %s], %b, %b",
                StringUtils.deleteNewlineSymbol(cleanUpText(currentSentence.getReaderSelection().getText())),
                currentSentence.getReaderSelection().getStartPosition(),
                currentSentence.getReaderSelection().getEndPosition(),
                currentSentence.isEndOfScreen(),
                currentSentence.isEndOfDocument());
    }

}
