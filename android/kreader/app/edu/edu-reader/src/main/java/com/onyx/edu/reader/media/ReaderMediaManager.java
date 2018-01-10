package com.onyx.edu.reader.media;

import android.media.MediaPlayer;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by lxm on 2018/1/10.
 */

public class ReaderMediaManager {

    private MediaPlayer mediaPlayer;

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

    public void play(FileDescriptor fd) throws IOException {
        getMediaPlayer().reset();
        getMediaPlayer().setDataSource(fd);
        getMediaPlayer().prepare();
        getMediaPlayer().start();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}
