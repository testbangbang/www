package com.onyx.edu.reader.ui.handler;

import android.app.Dialog;
import android.view.KeyEvent;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
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
        showMediaPlayDialog(readerDataHolder);
    }

    private void showMediaPlayDialog(final ReaderDataHolder readerDataHolder) {
        new DialogMediaPlay(readerDataHolder.getContext(),
                readerDataHolder.getEventBus(),
                readerDataHolder.getMediaManager().getMediaPlayer(),
                new DialogMediaPlay.MediaPlayListener() {
                    @Override
                    public void startMedia() {
                        start();
                    }

                    @Override
                    public void resumeMedia() {
                        resume();
                    }

                    @Override
                    public void stopMedia() {
                        stop();
                    }

                    @Override
                    public void pauseMedia() {
                        pause();
                    }

                    @Override
                    public void quitMedia() {
                        quit();
                    }

                    @Override
                    public void closeDialog(Dialog dialog) {
                        onCloseDialog(dialog);
                    }

                    @Override
                    public boolean keyUp(int keyCode, KeyEvent event) {
                        return onKeyUp(readerDataHolder, keyCode, event);
                    }

                    @Override
                    public void seekTo(int msec) {
                        readerDataHolder.getMediaManager().seekTo(msec);
                    }

                    @Override
                    public void nextPage() {
                        nextScreen(readerDataHolder, new BaseCallback() {
                            @Override
                            public void done(BaseRequest request, Throwable e) {
                                startMedia();
                            }
                        });
                    }
                }).show();
    }

    private void onCloseDialog(Dialog dialog) {
        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.READING_PROVIDER);
        readerDataHolder.removeActiveDialog(dialog);
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
