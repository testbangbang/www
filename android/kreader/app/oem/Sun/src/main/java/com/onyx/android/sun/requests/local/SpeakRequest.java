package com.onyx.android.sun.requests.local;

import com.onyx.android.sun.requests.requestTool.BaseLocalRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;
import com.onyx.android.sun.utils.MediaManager;

/**
 * Created by li on 2017/10/27.
 */

public class SpeakRequest extends BaseLocalRequest {
    private MediaManager mediaManager;
    private String fileName;

    public SpeakRequest(MediaManager mediaManager, String fileName) {
        this.mediaManager = mediaManager;
        this.fileName = fileName;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        mediaManager.speakRecord(fileName);
    }
}
