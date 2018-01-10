package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.api.ReaderRichMedia;
import com.onyx.android.sdk.reader.host.request.SaveAudioDataToFileRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

import java.io.FileDescriptor;
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
    public void execute(final ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        if (readerDataHolder.getMediaManager().isPlaying()) {
            readerDataHolder.getMediaManager().stop();
            return;
        }
        final SaveAudioDataToFileRequest fileRequest = new SaveAudioDataToFileRequest(richMedia.getData());
        readerDataHolder.submitNonRenderRequest(fileRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                playAudio(readerDataHolder, fileRequest.getFileDescriptor());
            }
        });
    }

    private void playAudio(final ReaderDataHolder readerDataHolder, FileDescriptor fileDescriptor) {
        try {
            readerDataHolder.getMediaManager().play(fileDescriptor);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
