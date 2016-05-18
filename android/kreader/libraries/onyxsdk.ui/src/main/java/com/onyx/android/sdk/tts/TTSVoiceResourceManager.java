/**
 *
 */
package com.onyx.android.sdk.tts;

import static android.provider.Settings.Secure.TTS_DEFAULT_LANG;
import static android.provider.Settings.Secure.TTS_DEFAULT_SYNTH;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.onyx.android.sdk.device.DeviceInfo;
import com.onyx.android.sdk.device.DeviceInfo.DeviceBrand;
import com.onyx.android.sdk.ui.dialog.DialogResourceNotFound;

/**
 * @author jim
 *
 */
public class TTSVoiceResourceManager {

    private static final String TAG = "TTSVoiceResourceManager";

    public static String getDefaultEngine(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), TTS_DEFAULT_SYNTH);
    }

    public static String getDefaultLanguage(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), TTS_DEFAULT_LANG);
    }

    private static boolean currentSettingsNeedExternalTTSVoices(Context context) {
        DeviceBrand brand = DeviceInfo.currentDevice.getDeviceBrand();
        if (brand == DeviceBrand.Artatech || brand == DeviceBrand.ArtatechPlay) {
            return true;
        }
        return false;
    }

    public static boolean checkTTSVoiceResource(Context context) {
        if (currentSettingsNeedExternalTTSVoices(context)) {
            TTSVoiceResourceIvona res = new TTSVoiceResourceIvona();
            if (!res.hasDefaultFiles()) {
                DialogResourceNotFound dialogForRes = new DialogResourceNotFound(context, res);
                dialogForRes.show();
                return false;
            }
            else {
                Log.i(TAG, "tts voice res exists");
            }
        }

        return true;
    }

}
