package com.onyx.edu.reader.ui.actions;

import android.util.Base64;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.api.ReaderRichMedia;
import com.onyx.android.sdk.reader.host.request.Base64ByteArrayRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.io.IOException;

/**
 * Created by lxm on 2018/1/10.
 */

public class PlayAudioAction extends BaseAction {

    public static final String AUDIO_BASE64_PRE = "data:audio/amr;base64,";

    private ReaderRichMedia richMedia;

    public PlayAudioAction(ReaderRichMedia richMedia) {
        this.richMedia = richMedia;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        if (readerDataHolder.getMediaManager().isPlaying()) {
            readerDataHolder.getMediaManager().stop();
            return;
        }

        final Base64ByteArrayRequest byteArrayRequest = new Base64ByteArrayRequest(richMedia.getData());
        readerDataHolder.submitNonRenderRequest(byteArrayRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                String url = AUDIO_BASE64_PRE + byteArrayRequest.getBase64();
                playAudio(readerDataHolder, url);
            }
        });
    }

    private void playAudio(final ReaderDataHolder readerDataHolder, String url) {
        try {
            readerDataHolder.getMediaManager().play(url);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
