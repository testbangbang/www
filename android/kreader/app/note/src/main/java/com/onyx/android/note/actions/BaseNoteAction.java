package com.onyx.android.note.actions;

import android.app.Activity;
import com.onyx.android.note.activity.ManageActivity;
import com.onyx.android.sdk.common.request.BaseCallback;

/**
 * Created by zhuzeng on 6/26/16.
 */
public abstract class BaseNoteAction<T extends Activity> {

    public abstract void execute(final T activity,  final BaseCallback callback);

}
