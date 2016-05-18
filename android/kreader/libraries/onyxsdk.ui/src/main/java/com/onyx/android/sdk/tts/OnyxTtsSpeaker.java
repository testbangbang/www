/**
 *
 */
package com.onyx.android.sdk.tts;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.onyx.android.sdk.data.util.ProfileUtil;
import com.onyx.android.sdk.device.DeviceInfo;
import com.onyx.android.sdk.ui.R;

/**
 * @author dxwts
 * 
 */
public class OnyxTtsSpeaker implements TextToSpeech.OnUtteranceCompletedListener
{

    private static final String TAG = "OnyxTtsSpeaker";
    private static final boolean VERBOSE_PROFILE = false;
    
    public static interface OnSpeakerCompletionListener
    {
        void onSpeakerCompletion();
    }
    private OnSpeakerCompletionListener mOnSpeakerCompletionListener = null;
    public void setOnSpeakerCompletionListener(OnSpeakerCompletionListener l)
    {
        mOnSpeakerCompletionListener = l;
    }
    private void notifyOnSpeakerCompletionListener()
    {
        if (mOnSpeakerCompletionListener != null) {
            mOnSpeakerCompletionListener.onSpeakerCompletion();
        }
    }

    private static final String UTTERANCE_ID = TAG;

    private Context mContext = null;
    private TextToSpeech mTtsService = null;
    private MediaPlayer mPlayer = new MediaPlayer();

    private Object mTtsLocker = new Object();
    private volatile PowerManager.WakeLock mWakeLock;
    private boolean mIsActive = false;
    private boolean mTtsSpeaking = false;
    private boolean mTtsPaused = false;
    private Locale mCurentLocale = null;

    private static final int ERROR_NOT_FOUND_VOICE_DATA = 1;
    private int mErrorCode = 0;

    public OnyxTtsSpeaker(Context context)
    {
        mContext = context;
        mTtsService = new TextToSpeech(mContext, new TextToSpeech.OnInitListener()
        {
            
            @Override
            public void onInit(int status)
            {
                onInitializationCompleted();
            }
        });
    }

    private void onInitializationCompleted()
    {
        Log.d(TAG, "onInitializationCompleted");
        
        mTtsService.setOnUtteranceCompletedListener(this);
        
        Locale locale = null;
        final String languageCode = mContext.getResources().getConfiguration().locale.getISO3Language();
        Log.v(TAG,"languageCode = " + languageCode);
        if ("other".equals(languageCode)) {
            locale = Locale.getDefault();
            if (mTtsService.isLanguageAvailable(locale) < 0) {
                locale = Locale.ENGLISH;
            }
        }
        else {
            try {
                locale = new Locale(languageCode);
            }
            catch (Exception e) {
            }
            if (locale == null || mTtsService.isLanguageAvailable(locale) < 0) {
                locale = Locale.getDefault();
                if (mTtsService.isLanguageAvailable(locale) < 0) {
                    locale = Locale.ENGLISH;
                }
            }
        }
        mCurentLocale = locale;
        mTtsService.setLanguage(locale);
    }

    private void onPlayerCompletion()
    {
        synchronized (mTtsLocker) {
            mTtsSpeaking = false;
            OnyxTtsSpeaker.this.notifyOnSpeakerCompletionListener();
        }
    }
    
    @Override
    public void onUtteranceCompleted(String uttId)
    {
        Log.d(TAG, "onUtteranceCompleted");

        synchronized (mTtsLocker) {
            if (mIsActive && UTTERANCE_ID.equals(uttId)) {
                try {
                    String wave_file = this.getTempWaveFile().getAbsolutePath();
                    
                    if(mPlayer != null) {
                        mPlayer.release();
                    }

                    mPlayer = new MediaPlayer();
                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                    {
                        
                        @Override
                        public void onCompletion(MediaPlayer mp)
                        {
                            onPlayerCompletion();
                        }
                    });
                    
                    if(new File(wave_file).exists()) {
                        try {
                            mPlayer.setDataSource(wave_file);
                            mPlayer.prepare();
                        }
                        catch (IOException e) {
                            Log.e(TAG, "exception", e);
                            onPlayerCompletion();
                        }

                        if (!mTtsPaused) {
                            mPlayer.start();
                        }
                    } else {
                        onPlayerCompletion();
                    }
                }
                catch (IllegalArgumentException e) {
                    Log.e(TAG, "exception", e);
                }
                catch (IllegalStateException e) {
                    Log.e(TAG, "exception", e);
                }
            }
        }
    }
    
    public boolean isOpened()
    {
        return this.isActive() || this.isPaused();
    }

    /**
     * both stopped and paused will cause active to be false
     * 
     * @return
     */
    public boolean isActive()
    {
        synchronized (mTtsLocker) {
            return mIsActive;
        }
    }
    
    @SuppressLint("Wakelock")
    private void setActive(boolean active)
    {
        Log.d(TAG, "setActive: " + active);
        synchronized (mTtsLocker) {
            mIsActive = active;

            if (active) {
                if (mWakeLock == null) {
                    mWakeLock = DeviceInfo.currentDevice.newWakeLock(mContext, TAG);
                    mWakeLock.acquire();
                }
            }
            else {
                if (mWakeLock != null) {
                    mWakeLock.release();
                    mWakeLock = null;
                }
            }
        }
    }
    
    public boolean isPaused()
    {
        synchronized (mTtsLocker) {
            return mTtsPaused;
        }
    }

    /**
     * stop or wait current playing finished to start a new play
     * 
     * @param text
     */
    public void startTts(String text)
    {
        synchronized (mTtsLocker) {
            if (mTtsSpeaking) {
                return;
            }
            
            if (text == null || text.trim().length() == 0) {
                return;
            }

            setActive(true);

            HashMap<String, String> callbackMap = new HashMap<String, String>();
            callbackMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID);

            String wave_file = this.getTempWaveFile().getAbsolutePath();

            if (VERBOSE_PROFILE) ProfileUtil.start(TAG, "synthesizeToFile");
            int res = mTtsService.synthesizeToFile(text, callbackMap, wave_file);
            if (VERBOSE_PROFILE) ProfileUtil.end(TAG, "synthesizeToFile");

            if (res == TextToSpeech.ERROR) {
                Log.w(TAG, "TTS synthesize failed");
                this.setActive(false);
            }
            else {
                mTtsSpeaking = true;
            }
            mTtsPaused = false;
        }
    }

    public void pause()
    {
        synchronized (mTtsLocker) {
            setActive(false);
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.pause();
            }
            mTtsPaused = true;
        }
    }
    
    public void resume()
    {
        synchronized (mTtsLocker) {
            setActive(true);
            if (mPlayer != null && mTtsPaused) {
                mPlayer.start();
                mTtsPaused = false;
            }
        }
    }

    public void stop()
    {
        synchronized (mTtsLocker) {
            setActive(false);
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
                mPlayer.setOnCompletionListener(null);
                mPlayer = null;
                
                mTtsPaused = false;
            }
            mTtsService.stop();
            mTtsSpeaking = false;
            mTtsPaused = false;
        }
    }
    
    public void shutdown()
    {
        assert(mTtsService != null);
        this.stop();
        mTtsService.shutdown();
    }

    private File getTempWaveFile()
    {
        File cache_folder = new File("/mnt/ramdisk");
        if (!cache_folder.exists()) {
            cache_folder = mContext.getExternalCacheDir();
        }

        return new File(cache_folder, "tts_temp.wav");
    }

    public boolean isLanguageAvailable() {
        mErrorCode = ERROR_NOT_FOUND_VOICE_DATA;
        return !(mTtsService.isLanguageAvailable(mCurentLocale) < 0);
    }

    public void showErrorInfo() {
        switch(mErrorCode) {
        case ERROR_NOT_FOUND_VOICE_DATA:
            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.dialog_tts_error);

            Button dialogButton = (Button) dialog.findViewById(R.id.button_ok);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            break;
        default:
            break;
        }
    }
}
