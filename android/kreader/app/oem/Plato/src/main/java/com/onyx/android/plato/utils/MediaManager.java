package com.onyx.android.plato.utils;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by li on 2017/10/24.
 */

public class MediaManager {
    private MediaRecorder mr;
    private static MediaManager mediaManager;
    private MediaPlayer mp;
    private static final int RECORDING_BITRATE = 12200;
    private MediaMetadataRetriever mmr;

    public static MediaManager newInstance() {
        if (mediaManager == null) {
            mediaManager = new MediaManager();
        }
        return mediaManager;
    }

    public void startRecord(String recordName) {
        if (mr == null) {
            mr = new MediaRecorder();
            mr.setAudioSource(MediaRecorder.AudioSource.MIC);
            mr.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mr.setAudioEncodingBitRate(RECORDING_BITRATE);
            mr.setOnErrorListener(new MediaRecorderErrorListener());
        }

        mr.setOutputFile(recordName);
        try {
            mr.prepare();
            mr.start();
        } catch (IOException e) {
            e.printStackTrace();
            mr.release();
            mr = null;
        }
    }

    public void stopRecord() {
        if (mr != null) {
            mr.stop();
            mr.release();
            mr = null;
        }
    }

    public void speakRecord(String name) {
        if (mp == null) {
            mp = new MediaPlayer();
            mp.setOnErrorListener(new MediaPlayErrorListener());
        } else {
            mp.stop();
            mp.reset();
        }

        try {
            mp.setDataSource(name);
            mp.prepare();
            mp.seekTo(0);
            int duration = mp.getDuration();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
            mp.release();
            mp = null;
        }
    }

    public int getDurationInSeccend(String path) {
        if (mmr == null) {
            mmr = new MediaMetadataRetriever();
        }
        mmr.setDataSource(path);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Integer.parseInt(duration) / 1000;
    }

    public void stopSpeak() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public class MediaPlayErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.i("MediaPlayErrorListener", "onError: " + what);
            return false;
        }
    }

    public class MediaRecorderErrorListener implements MediaRecorder.OnErrorListener {

        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.i("MediaRecorderError", "onError: " + what);
        }
    }
}
