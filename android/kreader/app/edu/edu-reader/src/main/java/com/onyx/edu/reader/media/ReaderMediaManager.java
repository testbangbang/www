package com.onyx.edu.reader.media;

import android.media.MediaPlayer;

import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.MediaPlayCompleteEvent;
import com.onyx.edu.reader.ui.events.MediaPlayStartEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by lxm on 2018/1/10.
 */

public class ReaderMediaManager {

    private MediaPlayer mediaPlayer;
    private EventBus eventBus;

    public ReaderMediaManager(EventBus bus) {
        this.eventBus = bus;
    }

    public MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        return mediaPlayer;
    }

    public void start() {
        getMediaPlayer().reset();
        getMediaPlayer().start();
    }

    public void reset() {
        getMediaPlayer().reset();
    }

    public void play(String url) throws IOException {
        getMediaPlayer().reset();
        getMediaPlayer().setDataSource(url);
        getMediaPlayer().prepare();
        getMediaPlayer().start();
        onStart();
    }

    public void onStart() {
        getEventBus().post(new MediaPlayStartEvent());
        getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                getEventBus().post(new MediaPlayCompleteEvent());
            }
        });
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void resume() {
        getMediaPlayer().seekTo(getMediaPlayer().getCurrentPosition());
        getMediaPlayer().start();
    }

    public void seekTo(int msec) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(msec);
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void quit() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}
