package com.onyx.android.sdk.data;

import java.util.HashSet;

/**
 * Created by Joy on 2014/4/9.
 */
public final class SelectionOption {
    private boolean mMustSelectAtLeastOne;
    private boolean mCanSelectMultiple;
    private HashSet<?> mSelections = new HashSet<Object>();

    public SelectionOption(boolean mustSelectAtLeastOne, boolean canSelectMultiple) {
        mMustSelectAtLeastOne = mustSelectAtLeastOne;
        mCanSelectMultiple = canSelectMultiple;
    }

    public boolean mustSelectAtLeastOne() {
        return mMustSelectAtLeastOne;
    }

    public boolean canSelectMultiple() {
        return mCanSelectMultiple;
    }

    public HashSet<?> getSelections() {
        return mSelections;
    }
}
