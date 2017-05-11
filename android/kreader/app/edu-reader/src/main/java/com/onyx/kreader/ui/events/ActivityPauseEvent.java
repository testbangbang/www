package com.onyx.kreader.ui.events;

import android.content.Context;

/**
 * Created by ming on 2017/3/21.
 */

public class ActivityPauseEvent {

    private Context context;

    public ActivityPauseEvent(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
