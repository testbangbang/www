/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onyx.android.dr.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class Recorder implements OnCompletionListener, OnErrorListener {
    private static final String SAMPLE_PREFIX = "recording";
    private static final String SAMPLE_PATH_KEY = "sample_path";
    private static final String SAMPLE_LENGTH_KEY = "sample_length";
    private static final String FOLDER_NAME = "/soundrecorder";
    private static final String FILE_PATH = "/sdcard/soundrecorder";
    public static final int IDLE_STATE = 0;
    public static final int RECORDING_STATE = 1;
    public static final int PLAYING_STATE = 2;
    public static final int MINUTE_VALUE = 540;
    public static final int SIXTY = 60;
    public static final int PROGRESS_VALUE = 100;
    private int state = IDLE_STATE;
    public static final int NO_ERROR = 0;
    public static final int SDCARD_ACCESS_ERROR = 1;
    public static final int INTERNAL_ERROR = 2;
    public static final int IN_CALL_RECORD_ERROR = 3;
    public static final int CARDINAL_NUMBER = 1000;
    private OnStateChangedListener mOnStateChangedListener = null;
    private long sampleStart = 0;
    private int sampleLength = 0;
    private File sampleFile = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    public interface OnStateChangedListener {
        void onStateChanged(int state);
        void onError(int error);
    }

    public Recorder() {
    }
    
    public void saveState(Bundle recorderState) {
        recorderState.putString(SAMPLE_PATH_KEY, sampleFile.getAbsolutePath());
        recorderState.putInt(SAMPLE_LENGTH_KEY, sampleLength);
    }
    
    public int getMaxAmplitude() {
        if (state != RECORDING_STATE)
            return 0;
        return recorder.getMaxAmplitude();
    }
    
    public void restoreState(Bundle recorderState) {
        String samplePath = recorderState.getString(SAMPLE_PATH_KEY);
        if (samplePath == null)
            return;
        int sampleLength = recorderState.getInt(SAMPLE_LENGTH_KEY, -1);
        if (sampleLength == -1)
            return;
        File file = new File(samplePath);
        if (!file.exists())
            return;
        if (sampleFile != null
                && sampleFile.getAbsolutePath().compareTo(file.getAbsolutePath()) == 0)
            return;
        delete();
        sampleFile = file;
        this.sampleLength = sampleLength;
        signalStateChanged(IDLE_STATE);
    }
    
    public void setOnStateChangedListener(OnStateChangedListener listener) {
        mOnStateChangedListener = listener;
    }
    
    public int state() {
        return state;
    }
    
    public int progress() {
        if (state == RECORDING_STATE || state == PLAYING_STATE)
            return (int) ((System.currentTimeMillis() - sampleStart)/1000);
        return 0;
    }
    
    public int sampleLength() {
        return sampleLength;
    }

    public File sampleFile() {
        return sampleFile;
    }
    
    /**
     * Resets the recorder state. If a sample was recorded, the file is deleted.
     */
    public void delete() {
        stop();
        if (sampleFile != null)
            sampleFile.delete();
        sampleFile = null;
        sampleLength = 0;
        signalStateChanged(IDLE_STATE);
    }
    
    /**
     * Resets the recorder state. If a sample was recorded, the file is left on disk and will 
     * be reused for a new recording.
     */
    public void clear() {
        stop();
        sampleLength = 0;
        signalStateChanged(IDLE_STATE);
    }
    
    public void startRecording(int outputfileformat, String extension, Context context) {
        stop();
        if (sampleFile == null) {
            File sampleDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + FOLDER_NAME);
            if (!sampleDir.exists()) {
                try{
                    sampleDir.mkdir();
                } 
                catch(SecurityException se){
                }
            }
            // Workaround for broken sdcard support on the device.
            if (!sampleDir.canWrite())
                sampleDir = new File(FILE_PATH);
            try {
                sampleFile = File.createTempFile(SAMPLE_PREFIX, extension, sampleDir);
            } catch (IOException e) {
                setError(SDCARD_ACCESS_ERROR);
                return;
            }
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(outputfileformat);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(sampleFile.getAbsolutePath());
        // Handle IOException
        try {
            recorder.prepare();
        } catch(IOException exception) {
            setError(INTERNAL_ERROR);
            recorder.reset();
            recorder.release();
            recorder = null;
            return;
        }
        try {
            recorder.start();
        } catch (RuntimeException exception) {
            AudioManager audioManger = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            boolean isInCall = ((audioManger.getMode() == AudioManager.MODE_IN_CALL) ||
                    (audioManger.getMode() == AudioManager.MODE_IN_COMMUNICATION));
            if (isInCall) {
                setError(IN_CALL_RECORD_ERROR);
            } else {
                setError(INTERNAL_ERROR);
            }
            recorder.reset();
            recorder.release();
            recorder = null;
            return;
        }
        sampleStart = System.currentTimeMillis();
        setState(RECORDING_STATE);
    }
    
    public void stopRecording() {
        if (recorder == null)
            return;
        recorder.stop();
        recorder.release();
        recorder = null;
        sampleLength = (int)( (System.currentTimeMillis() - sampleStart)/CARDINAL_NUMBER );
        setState(IDLE_STATE);
    }
    
    public void startPlayback() {
        stop();
        player = new MediaPlayer();
        try {
            player.setDataSource(sampleFile.getAbsolutePath());
            player.setOnCompletionListener(this);
            player.setOnErrorListener(this);
            player.prepare();
            player.start();
        } catch (IllegalArgumentException e) {
            setError(INTERNAL_ERROR);
            player = null;
            return;
        } catch (IOException e) {
            setError(SDCARD_ACCESS_ERROR);
            player = null;
            return;
        }
        sampleStart = System.currentTimeMillis();
        setState(PLAYING_STATE);
    }
    
    public void stopPlayback() {
        // we were not in playback
        if (player == null)
            return;
        player.stop();
        player.release();
        player = null;
        setState(IDLE_STATE);
    }
    
    public void stop() {
        stopRecording();
        stopPlayback();
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        stop();
        setError(SDCARD_ACCESS_ERROR);
        return true;
    }

    public void onCompletion(MediaPlayer mp) {
        stop();
    }
    
    private void setState(int stateValue) {
        if (stateValue == state)
            return;
        state = stateValue;
        signalStateChanged(state);
    }
    
    private void signalStateChanged(int state) {
        if (mOnStateChangedListener != null)
            mOnStateChangedListener.onStateChanged(state);
    }
    
    private void setError(int error) {
        if (mOnStateChangedListener != null)
            mOnStateChangedListener.onError(error);
    }
}
