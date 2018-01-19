package com.onyx.edu.reader.ui.actions;

import android.util.Base64;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.api.ReaderRichMedia;
import com.onyx.android.sdk.reader.host.request.Base64ByteArrayRequest;
import com.onyx.android.sdk.reader.host.request.PlayAudioRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.MediaPlayStartEvent;

import java.io.IOException;

/**
 * Created by lxm on 2018/1/10.
 */

public class PlayAudioAction extends BaseAction {

    private ReaderRichMedia richMedia;

    public PlayAudioAction(ReaderRichMedia richMedia) {
        this.richMedia = richMedia;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        if (richMedia == null) {
            return;
        }

        final PlayAudioRequest playAudioRequest = new PlayAudioRequest(readerDataHolder.getMediaManager().getMediaPlayer(),
                richMedia.getData());
        readerDataHolder.submitNonRenderRequest(playAudioRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    readerDataHolder.getMediaManager().onStart();
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
