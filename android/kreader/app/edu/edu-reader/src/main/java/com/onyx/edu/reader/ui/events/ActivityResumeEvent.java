package com.onyx.edu.reader.ui.events;

import android.content.Context;

/**
 * Created by ming on 2017/3/21.
 */

public class ActivityResumeEvent {

    private Context context;

    public ActivityResumeEvent(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
