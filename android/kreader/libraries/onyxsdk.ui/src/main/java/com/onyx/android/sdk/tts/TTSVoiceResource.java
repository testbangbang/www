/**
 *
 */
package com.onyx.android.sdk.tts;

import com.onyx.android.sdk.res.IResource;
import com.onyx.android.sdk.res.ResourceType;
import com.onyx.android.sdk.ui.R;

/**
 * @author jim
 *
 */
public class TTSVoiceResource implements IResource {

    private boolean hasDefaultFiles = false;

    public TTSVoiceResource() {
        this.hasDefaultFiles = TTSVoiceBase.hasVoiceFiles(getBasePath());
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ResourceType getType() {
        return ResourceType.TTS_VOICE;
    }

    @Override
    public String getBasePath() {
        return null;
    }

    @Override
    public boolean hasDefaultFiles() {
        return hasDefaultFiles;
    }

    @Override
    public String getDefaultFilesDownloadLink() {
        return null;
    }

    @Override
    public int getNotFoundMessageID() {
        return R.string.resource_not_found_tts_voice;
    }

}
