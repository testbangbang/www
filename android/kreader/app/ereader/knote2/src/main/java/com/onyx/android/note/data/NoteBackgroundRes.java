package com.onyx.android.note.data;

import com.onyx.android.note.R;
import com.onyx.android.sdk.scribble.data.BackgroundRes;

/**
 * Created by lxm on 2018/3/5.
 */

public class NoteBackgroundRes implements BackgroundRes {

    @Override
    public int getLineResId() {
        return 0;
    }

    @Override
    public int getGridResId() {
        return R.drawable.scribble_back_ground_grid;
    }

    @Override
    public int getMusicResId() {
        return 0;
    }
}
