package com.onyx.android.sdk.ui.data;

import com.onyx.android.sdk.data.ReaderMenuState;

/**
 * Created by joy on 6/28/16.
 */
public class ReaderLayerMenuState extends ReaderMenuState {
    public final String TTS_STATE_TAG = "tts_state";

    public enum TtsState { Stopped, Speaking, Paused }

    public ReaderLayerMenuState() {
        setValue(TTS_STATE_TAG, TtsState.Stopped);
    }

    public TtsState getTtsState() {
        return (TtsState)getValue(TTS_STATE_TAG);
    }

    public void setTtsState(TtsState state) {
        setValue(TTS_STATE_TAG, state);
    }
}

