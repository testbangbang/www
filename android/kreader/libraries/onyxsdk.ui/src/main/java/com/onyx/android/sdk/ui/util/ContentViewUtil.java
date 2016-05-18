package com.onyx.android.sdk.ui.util;

import android.view.KeyEvent;

/**
 * Created by solskjaer49 on 14/12/30 11:30.
 */
public class ContentViewUtil {
    public static final int FLAG_TAINTED = 0x80000008;
    public enum SelectionMode {NormalMode, PasteMode, MultiSelectMode}

    static public boolean isActionTainted(KeyEvent event) {
        return (event.getFlags() == FLAG_TAINTED);
    }
}
