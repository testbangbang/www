package con.onyx.android.libsetting.util.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.provider.Settings;

import static con.onyx.android.libsetting.util.Constant.VOLUME_CHANGED_ACTION;

/**
 * Created by solskjaer49 on 2016/12/6 18:41.
 */

public class AudioAdmin {
    private Context context;
    private AudioManager audioManager;
    private IntentFilter filter = new IntentFilter(VOLUME_CHANGED_ACTION);

    public AudioAdmin setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    private Callback callback;

    public AudioAdmin(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public interface Callback {
        void onVolumeChanged(int newVolume);
    }

    public void setSoundEffectEnabled(boolean enabled) {
        if (enabled) {
            audioManager.loadSoundEffects();
        } else {
            audioManager.unloadSoundEffects();
        }
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SOUND_EFFECTS_ENABLED, enabled ? 1 : 0);
    }

    private BroadcastReceiver volumeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case VOLUME_CHANGED_ACTION:
                    if (callback != null) {
                        callback.onVolumeChanged(audioManager.getStreamVolume(getCurrentStreamType()));
                    }
                    break;
            }
        }
    };

    public boolean registerReceiver() {
        if (context == null) {
            return false;
        }
        context.registerReceiver(volumeChangedReceiver, filter);
        return true;
    }

    public boolean unregisterReceiver() {
        if (context == null) {
            return false;
        }
        context.registerReceiver(volumeChangedReceiver, filter);
        return true;
    }

    public void setStreamVolume(int value) {
        audioManager.setStreamVolume(getCurrentStreamType(), value, 0);
    }

    private int getCurrentStreamType() {
        if (audioManager.isMusicActive()) {
            return AudioManager.STREAM_MUSIC;
        }
        return AudioManager.STREAM_RING;
    }

    public int getStreamMaxVolume() {
        return audioManager.getStreamMaxVolume(getCurrentStreamType());
    }

    public int getStreamVolume() {
        return audioManager.getStreamVolume(getCurrentStreamType());
    }
}
