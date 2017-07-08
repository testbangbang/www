package com.onyx.android.dr.reader.handler;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.common.ReadSettingTtsConfig;
import com.onyx.android.dr.reader.common.ToastManage;
import com.onyx.android.dr.reader.event.ChangeScreenEvent;
import com.onyx.android.dr.reader.event.ChangeSpeechRateEvent;
import com.onyx.android.dr.reader.event.NotifyTtsStateChangedEvent;
import com.onyx.android.dr.reader.event.SentenceRequestResultEvent;
import com.onyx.android.dr.reader.event.StartTtsPlayEvent;
import com.onyx.android.dr.reader.event.TtsSpeakingStateEvent;
import com.onyx.android.dr.reader.event.TtsStopStateEvent;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.dr.reader.tts.ReaderTtsManager;
import com.onyx.android.sdk.data.ControlType;
import com.onyx.android.sdk.data.CustomBindKeyBean;
import com.onyx.android.sdk.reader.api.ReaderSentence;
import com.onyx.android.sdk.utils.ChineseTextUtils;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by huxiaomao on 17/5/8.
 */

public class TTSHandler extends BaseHandler {
    private static final String TAG = TTSHandler.class.getSimpleName();
    private String initialPosition;
    private ReaderTtsManager readerTtsManager;
    private ReaderSentence currentSentence;
    private boolean stopped = true;

    public TTSHandler(ReaderPresenter readerPresenter) {
        super(readerPresenter);
        readerTtsManager = new ReaderTtsManager(readerPresenter, this);

        EventBus.getDefault().register(this);
    }

    public ReaderTtsManager getReaderTtsManager() {
        return readerTtsManager;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        final String touchArea = getTouchAreaCode(getReaderPresenter(), event);
        CustomBindKeyBean object = getControlBinding(ControlType.TOUCH, touchArea);
        if (object == null) {
            return false;
        }
        String action = object.getAction();
        String args = object.getArgs();

        return processSingleTapUp(getReaderPresenter(), action, args);
    }

    @Subscribe
    public void onNotifyTtsStateChangedEvent(final NotifyTtsStateChangedEvent event) {
        event.onDispatchEvent(this);
    }

    public void onTtsStateChanged() {
        if (readerTtsManager.isSpeaking()) {
            EventBus.getDefault().post(new TtsSpeakingStateEvent());
        } else {
            EventBus.getDefault().post(new TtsStopStateEvent());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (getReaderPresenter().getReaderViewInfo().canPrevScreen) {
                    ttsStop();
                    getReaderPresenter().getBookOperate().prevScreen();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (getReaderPresenter().getReaderViewInfo().canNextScreen) {
                    ttsStop();
                    getReaderPresenter().getBookOperate().nextScreen();
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                ttsStop();
                break;
            default:
                break;
        }
        return false;
    }

    public void onError() {
        stopped = true;
        getReaderPresenter().getBookOperate().redrawPage();
    }

    public void ttsPlay() {
        stopped = false;
        getReaderTtsManager().play();
    }

    public void ttsPause() {
        getReaderTtsManager().pause();
    }

    @Override
    public void onStop() {
        ttsStop();
    }

    public void ttsStop() {
        currentSentence = null;
        stopped = true;
        getReaderTtsManager().stop();
        getReaderPresenter().getBookOperate().redrawPage();
    }

    public void setInitialPosition(String initialPosition) {
        this.initialPosition = initialPosition;
    }

    @Subscribe
    public void onChangeSpeechRateEvent(ChangeSpeechRateEvent event) {
        float rate = event.getRate();
        if (rate == getReaderTtsManager().getSpeechRate()) {
            return;
        }
        getReaderTtsManager().stop();
        getReaderTtsManager().setSpeechRate(rate);
        if (currentSentence != null) {
            getReaderTtsManager().supplyText(cleanUpText(currentSentence.getReaderSelection().getText()));
            getReaderTtsManager().play();
        }
    }

    public float getSpeechRate() {
        return ReadSettingTtsConfig.getSpeechRate(getReaderPresenter().getReaderView().getViewContext());
    }

    @Subscribe
    public void onStartTtsPlayEvent(StartTtsPlayEvent event) {
        ttsPlay();
    }

    @Subscribe
    public void onSentenceRequestResultEvent(SentenceRequestResultEvent event) {
        Throwable throwable = event.getThrowable();
        if (throwable != null) {
            ToastManage.showMessage(getReaderPresenter().getReaderView().getViewContext(),
                    R.string.get_page_text_failed);
            ttsStop();
            return;
        }
        if (stopped) {
            return;
        }
        currentSentence = event.getCurrentSentence();
        if (currentSentence == null) {
            ToastManage.showMessage(getReaderPresenter().getReaderView().getViewContext(),
                    R.string.get_page_text_failed);
            ttsStop();
            return;
        }
        if (!currentSentence.isNonBlank()) {
            requestSentenceForTts();
            return;
        }

        getReaderTtsManager().supplyText(cleanUpText(currentSentence.getReaderSelection().getText()));
        getReaderTtsManager().play();
    }

    @Subscribe
    public void onChangeScreenEvent(ChangeScreenEvent event) {
        requestSentenceForTts();
    }

    public boolean requestSentenceForTts() {
        if (currentSentence != null) {
            if (currentSentence.isEndOfDocument()) {
                getReaderTtsManager().stop();
                return false;
            }
            if (currentSentence.isEndOfScreen()) {
                currentSentence = null;
                getReaderPresenter().getBookOperate().ttsNextScreen();
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
        getReaderPresenter().getBookOperate().getSentenceRequest(startPosition);
        return true;
    }

    private String cleanUpText(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        return ChineseTextUtils.removeWhiteSpacesBetweenChineseText(text);
    }
}
