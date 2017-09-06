/**
 *
 */
package com.onyx.edu.reader.tts;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.onyx.android.sdk.common.request.WakeLockHolder;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author dxwts
 */
public class ReaderTtsService {

    private static final Class TAG = ReaderTtsService.class;

    public enum TtsState { Ready, SynthesizeTtsPrepare, SynthesizeTtsStart, SynthesizeTtsDone, MediaPlayStart, MediaPaused, MediaResume, MediaPlayDone, Stopped, Error}

    public static abstract class Callback {
        public abstract void onStart();
        public abstract void onPaused();
        public abstract void onDone();
        public abstract void onStopped();
        public abstract void onError();
    }

    private static final String UTTERANCE_ID = ReaderTtsService.class.getSimpleName();

    private volatile WakeLockHolder wakeLockHolder = new WakeLockHolder();

    private Context context = null;
    private Callback callback = null;

    private Locale currentLocale = null;
    private TextToSpeech ttsService = null;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private String text = null;
    private TtsState ttsState = TtsState.Ready;

    Handler handler = new Handler();

    public ReaderTtsService(final Context context, Callback callback, String engine) {
        this.context = context;
        this.callback = callback;

        final Runnable runnable = initializeRunnable(context.getApplicationContext(), engine);
        new Thread(runnable).start();
    }

    public ReaderTtsService(final Context context, Callback callback) {
        this(context, callback, null);
    }

    private  Runnable initializeRunnable(final Context context, final String engine) {
        return new Runnable() {
            @Override
            public void run() {
                ttsService = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

                    @Override
                    public void onInit(int status) {
                        Debug.d(getClass(), "onInit: " + status);
                        if (status == TextToSpeech.SUCCESS) {
                            initTtsService();
                        }
                    }
                }, engine);
            }
        };
    }


    private void initTtsService() {
        ttsService.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(final String utteranceId) {
                Debug.d(TAG, "UtteranceProgressListener: onStart");
            }

            @Override
            public void onDone(final String utteranceId) {
                Debug.d(TAG, "UtteranceProgressListener: onDone");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!UTTERANCE_ID.equals(utteranceId)) {
                            return;
                        }
                        handleState(TtsState.SynthesizeTtsDone);
                    }
                });
            }

            @Override
            public void onError(final String utteranceId) {
                Debug.d(TAG, "UtteranceProgressListener: onError");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleState(TtsState.Error);
                    }
                });
            }
        });
        initTtsLanguage();
    }

    private void initTtsLanguage() {
        Locale locale = null;
        final String languageCode = context.getResources().getConfiguration().locale.getISO3Language();
        Debug.d(TAG, "languageCode = " + languageCode);
        if (!languageCode.equals("other")) {
            try {
                locale = new Locale(languageCode);
            } catch (Exception e) {
                Debug.w(TAG, e);
            }
        }
        if (locale == null || ttsService.isLanguageAvailable(locale) < 0) {
            locale = Locale.getDefault();
            if (ttsService.isLanguageAvailable(locale) < 0) {
                locale = Locale.ENGLISH;
            }
        }
        currentLocale = locale;
        ttsService.setLanguage(locale);
    }

    public void setSpeechRate(float rate) {
        if (ttsService != null) {
            ttsService.setSpeechRate(rate);
        }
    }

    public boolean isSpeaking() {
        return ttsState == TtsState.SynthesizeTtsStart || ttsState == TtsState.MediaPlayStart;
    }

    public boolean isPaused() {
        return ttsState == TtsState.MediaPaused;
    }

    /**
     * stop or wait current playing finished to start a new play
     *
     * @param text
     */
    public void startTts(String text) {
        this.text = text;
        handleState(TtsState.SynthesizeTtsPrepare);
    }

    public void reset() {
        setTtsState(TtsState.Ready);
    }

    public void pause() {
        handleState(TtsState.MediaPaused);
    }

    public void resume() {
        handleState(TtsState.MediaResume);
    }

    public void stop() {
        if (ttsState == TtsState.Stopped) {
            return;
        }
        if (mediaPlayer != null) {
            closeMediaPlayer();
        }
        ttsService.stop();
        handleState(TtsState.Stopped);
    }

    public void shutdown() {
        stop();
        ttsService.shutdown();
        ttsService = null;
        context = null;
        callback = null;
        mediaPlayer = null;
    }

    private void handleState(TtsState state) {
        Debug.d("handleState: " + state + ", current state: " + ttsState);
        switch (state) {
            case SynthesizeTtsPrepare:
                if (ttsState == TtsState.Stopped ||
                        ttsState == TtsState.SynthesizeTtsPrepare || ttsState == TtsState.SynthesizeTtsStart ||
                        ttsState == TtsState.SynthesizeTtsDone || ttsState == TtsState.MediaPlayStart) {
                    return;
                }
                if (StringUtils.isNullOrEmpty(text)) {
                    handleState(TtsState.Error);
                    return;
                }
                if (synthesizeTts(text)) {
                    if (ttsState != TtsState.MediaPaused) {
                        setTtsState(TtsState.SynthesizeTtsStart);
                        onStart();
                    }
                } else {
                    handleState(TtsState.Error);
                }
                break;
            case SynthesizeTtsDone:
                if (ttsState != TtsState.Stopped) {
                    if (prepareMediaPlayer()) {
                        handleState(TtsState.MediaPlayStart);
                    } else {
                        handleState(TtsState.Error);
                    }
                }
                break;
            case MediaPlayStart:
                if (ttsState != TtsState.Stopped && ttsState != TtsState.MediaPaused) {
                    setTtsState(TtsState.MediaPlayStart);
                    startMediaPlayer();
                    onStart();
                }
                break;
            case MediaPaused:
                if (ttsState == TtsState.MediaPlayStart) {
                    pauseMediaPlayer();
                }
                setTtsState(TtsState.MediaPaused);
                onPaused();
                break;
            case MediaResume:
                if (ttsState == TtsState.MediaPaused) {
                    setTtsState(TtsState.MediaPlayStart);
                    handleState(TtsState.MediaPlayStart);
                }
                break;
            case MediaPlayDone:
                if (ttsState != TtsState.Stopped && ttsState != TtsState.MediaPaused) {
                    setTtsState(TtsState.Ready);
                    onDone();
                }
                break;
            case Stopped:
                setTtsState(TtsState.Stopped);
                onStopped();
                break;
            case Error:
                setTtsState(TtsState.Error);
                onError();
                break;
            default:
                assert(false);
        }
    }

    private boolean synthesizeTts(String text) {
        Benchmark benchmark = new Benchmark();
        HashMap<String, String> callbackMap = new HashMap<String, String>();
        callbackMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID);
        int res = ttsService.synthesizeToFile(text, callbackMap, getTempWaveFile().getAbsolutePath());
        benchmark.report("synthesizeToFile");

        if (res == TextToSpeech.ERROR) {
            Debug.w(TAG, "TTS synthesize failed");
            return false;
        } else {
            return true;
        }
    }

    private boolean prepareMediaPlayer() {
        try {
            if (!getTempWaveFile().exists()) {
                Debug.w(TAG, "tts wave file not exists: " + getTempWaveFile().getAbsolutePath());
                return false;
            }

            if (mediaPlayer != null) {
                closeMediaPlayer();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    Debug.d("media player onCompletion");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            handleState(TtsState.MediaPlayDone);
                        }
                    });
                }
            });

            Debug.d("play wave file: " + ttsState);
            mediaPlayer.setDataSource(getTempWaveFile().getAbsolutePath());
            mediaPlayer.prepare();
            if (mediaPlayer.getDuration() <= 64) {
                // even pico tts failed, it may still synthesize a blank wav file with 64ms duration
                Debug.e(getClass(), "tts speech duration too short: " + mediaPlayer.getDuration());
                return false;
            }
            return true;
        } catch (Exception e) {
            Debug.w(TAG, e);
            return false;
        }
    }

    private void startMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private void pauseMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void closeMediaPlayer() {
        try {
            if (mediaPlayer == null) {
                return;
            }
            mediaPlayer.setOnCompletionListener(null);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Throwable tr) {
            Debug.w(TAG, tr);
        }
    }

    private File getTempWaveFile() {
        File cache_folder = new File("/mnt/ramdisk");
        if (!cache_folder.exists()) {
            cache_folder = context.getExternalCacheDir();
        }

        return new File(cache_folder, "tts_temp.wav");
    }

    private void setTtsState(TtsState state) {
        Debug.d("setTtsState: " + state);
        ttsState = state;
        if (ttsState == TtsState.SynthesizeTtsStart || ttsState == TtsState.MediaPlayStart) {
            acquireWakeLock();
        } else {
            releaseWakeLock();
        }
    }

    private void acquireWakeLock() {
        wakeLockHolder.acquireWakeLock(context, WakeLockHolder.WAKEUP_FLAGS, getClass().getSimpleName());
    }

    private void releaseWakeLock() {
        wakeLockHolder.forceReleaseWakeLock();
    }

    private void onStart() {
        if (callback != null) {
            callback.onStart();
        }
    }

    private void onPaused() {
        if (callback != null) {
            callback.onPaused();
        }
    }

    private void onDone() {
        if (callback != null) {
            callback.onDone();
        }
    }

    private void onStopped() {
        if (callback != null) {
            callback.onStopped();
        }
    }

    private void onError() {
        if (callback != null) {
            callback.onError();
        }
    }
}
