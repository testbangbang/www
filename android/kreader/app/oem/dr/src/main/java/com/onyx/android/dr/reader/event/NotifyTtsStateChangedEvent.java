package com.onyx.android.dr.reader.event;


import com.onyx.android.dr.reader.handler.TTSHandler;

/**
 * Created by huxiaomao on 17/5/15.
 */

public class NotifyTtsStateChangedEvent {
    public static final int TTS_STATE_START = 0;
    public static final int TTS_STATE_PAUSED = 1;
    public static final int TTS_STATE_DONE = 2;
    public static final int TTS_STATE_STOPPED = 3;
    public static final int TTS_STATE_ERROR = 4;
    public static final int TTS_QUIT_READING = 5;

    public int state;

    public NotifyTtsStateChangedEvent onStart() {
        state = TTS_STATE_START;
        return this;
    }

    public NotifyTtsStateChangedEvent onPaused() {
        state = TTS_STATE_PAUSED;
        return this;
    }

    public NotifyTtsStateChangedEvent onDone() {
        state = TTS_STATE_DONE;
        return this;
    }

    public NotifyTtsStateChangedEvent onStopped() {
        state = TTS_STATE_STOPPED;
        return this;
    }

    public NotifyTtsStateChangedEvent onError() {
        state = TTS_STATE_ERROR;
        return this;
    }

    public NotifyTtsStateChangedEvent onQuitReading() {
        state = TTS_QUIT_READING;
        return this;
    }

    public int getState() {
        return state;
    }

    public void onDispatchEvent(TTSHandler ttsHandler){
        switch (state){
            case TTS_STATE_START:
                break;
            case TTS_STATE_PAUSED:
                ttsHandler.ttsPause();
                break;
            case TTS_STATE_DONE:
                ttsHandler.requestSentenceForTts();
                break;
            case TTS_STATE_STOPPED:

                break;
            case TTS_STATE_ERROR:
                ttsHandler.onError();
                break;
            case TTS_QUIT_READING:
                ttsHandler.ttsStop();
                break;
        }
        ttsHandler.onTtsStateChanged();
    }
}
