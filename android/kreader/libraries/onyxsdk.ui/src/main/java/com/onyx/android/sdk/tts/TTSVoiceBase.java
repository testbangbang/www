/**
 *
 */
package com.onyx.android.sdk.tts;

import java.io.File;

/**
 * @author jim
 *
 */
public class TTSVoiceBase {

    public static final String TTS_ENGINE_NAME_IVONA = "TTSIvona";

    public static boolean hasVoiceFiles(String basePath) {
        File dir = new File(basePath);
        if (dir.exists()) {
            File[] fileList = dir.listFiles();
            if (fileList.length > 0) {
                return true;
            }
        }
        return false;
    }

}
