/**
 *
 */
package com.onyx.android.sdk.tts;

import com.onyx.android.sdk.device.EnvironmentUtil;

/**
 * @author jim
 *
 */
public class TTSVoiceResourceIvona extends TTSVoiceResource {

    public TTSVoiceResourceIvona() {
        super();
    }

    @Override
    public String getName() {
        return TTSVoiceBase.TTS_ENGINE_NAME_IVONA;
    }

    @Override
    public String getBasePath() {
        return EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath() + "/ivona";
    }

    @Override
    public String getDefaultFilesDownloadLink() {
        return "";
    }

}
