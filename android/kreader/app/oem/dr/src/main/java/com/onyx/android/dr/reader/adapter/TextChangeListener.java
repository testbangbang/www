package com.onyx.android.dr.reader.adapter;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by hehai on 17-8-24.
 */

public abstract class TextChangeListener implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onEditTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    abstract void onEditTextChanged(CharSequence s, int start, int before, int count);
}
