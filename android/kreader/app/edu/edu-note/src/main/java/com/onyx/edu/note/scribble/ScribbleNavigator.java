package com.onyx.edu.note.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;

/**
 * Created by solskjaer49 on 2017/6/22 12:01.
 */

public interface ScribbleNavigator {
    void renderCurrentPage();

    void renderCurrentPageWithCallback(BaseCallback callback);

    void goToSetting();

    void switchScribbleMode();
}
