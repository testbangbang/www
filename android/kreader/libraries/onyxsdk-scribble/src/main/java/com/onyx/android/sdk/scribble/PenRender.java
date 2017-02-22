package com.onyx.android.sdk.scribble;

import com.onyx.android.sdk.scribble.api.RawInputCallback;
import com.onyx.android.sdk.scribble.api.Render;
import com.onyx.android.sdk.scribble.touch.RawInputProcessor;

/**
 * Created by ming on 2017/2/22.
 */

public class PenRender implements Render {

    private RawInputProcessor rawInputProcessor = new RawInputProcessor();

    @Override
    public void start() {
        rawInputProcessor.start();
    }

    @Override
    public void resume() {
        rawInputProcessor.resume();
    }

    @Override
    public void pause() {
        rawInputProcessor.pause();
    }

    @Override
    public void stop() {
        rawInputProcessor.quit();
    }

    public void setRawInputCallback(RawInputCallback rawInputCallback) {
        rawInputProcessor.setRawInputCallback(rawInputCallback);
    }
}
