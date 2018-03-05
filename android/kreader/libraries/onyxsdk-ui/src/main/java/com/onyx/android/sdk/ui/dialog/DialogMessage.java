package com.onyx.android.sdk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;

/**
 * Created by joy on 11/1/16.
 */
public class DialogMessage extends Dialog {

    public DialogMessage(final Context context, final String message) {
        super(context, R.style.dialog_no_title);
        setContentView(R.layout.dialog_message);
        ((TextView)findViewById(R.id.textview_title)).setText(message);

        findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogMessage.this.dismiss();
            }
        });
    }


}
