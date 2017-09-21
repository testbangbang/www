package com.onyx.kreader.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.onyx.kreader.R;

/**
 * Created by joy on 11/1/16.
 */
public class DialogMessage extends DialogBase {
    private TextView title;

    public DialogMessage(final Context context) {
        super(context);
        setContentView(R.layout.dialog_message);
        title = (TextView) findViewById(R.id.textview_title);

        findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogMessage.this.dismiss();
            }
        });
    }

    public DialogMessage(final Context context, final int stringResId) {
        this(context);
        title.setText(stringResId);
    }

    public DialogMessage(final Context context, final String message) {
        this(context);
        title.setText(message);
    }
}
