package com.onyx.android.sdk.scribble.view;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.widget.TextView;

/**
 * Created by ming on 2016/12/16.
 */

public class SpanInputConnection extends BaseInputConnection {
    private static final String TAG = "SpanInputConnection";

    public interface Callback {
        void commitText(CharSequence text, int newCursorPosition);
    }

    private SpannableStringBuilder editable;
    private TextView textView;
    private Callback callback;

    public SpanInputConnection(View targetView, boolean fullEditor, final Callback callback) {
        super(targetView, fullEditor);
        textView = (TextView) targetView;
        this.callback = callback;
    }

    public Editable getEditable() {
        if (editable == null) {
            editable = (SpannableStringBuilder) Editable.Factory.getInstance()
                    .newEditable("Placeholder");
        }
        return editable;
    }

    public boolean commitText(CharSequence text, int newCursorPosition) {
        Log.d(TAG, "commitText: " + text + "--newCursorPosition:" + newCursorPosition);
        if (callback != null) {
            callback.commitText(text, newCursorPosition);
        }
        return true;
    }
}
