package com.onyx.edu.reader.ui.handler;

import com.onyx.android.sdk.reader.api.ReaderRichMedia;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.reader.media.ReaderMediaManager;
import com.onyx.edu.reader.ui.actions.PlayAudioAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.dialog.DialogMediaPlay;
import com.onyx.edu.reader.ui.events.CloseMediaPlayDialogEvent;

import java.util.List;

/**
 * Created by lxm on 2018/1/12.
 */

public class MediaPlayHandler extends BaseHandler {

    private ReaderDataHolder readerDataHolder;

    public MediaPlayHandler(HandlerManager parent) {
        super(parent);
        readerDataHolder = parent.getReaderDataHolder();
    }

    @Override
    public void onActivate(ReaderDataHolder readerDataHolder, HandlerInitialState initialState) {
        super.onActivate(readerDataHolder, initialState);
        new DialogMediaPlay(readerDataHolder).show();
    }

    public void start() {
        List<ReaderRichMedia> richMedias = readerDataHolder.getReaderUserDataInfo().getRichMedias(readerDataHolder.getFirstPageInfo());
        if (CollectionUtils.isNullOrEmpty(richMedias)) {
            readerDataHolder.getEventBus().post(new CloseMediaPlayDialogEvent());
            return;
        }
        new PlayAudioAction(richMedias.get(0)).execute(readerDataHolder, null);
    }

    public void resume() {
        getMediaManager().resume();
    }

    public void stop() {
        getMediaManager().stop();
    }

    public void quit() {
        getMediaManager().quit();
    }

    public void pause() {
        getMediaManager().pause();
    }

    public ReaderMediaManager getMediaManager() {
        return readerDataHolder.getMediaManager();
    }
}
