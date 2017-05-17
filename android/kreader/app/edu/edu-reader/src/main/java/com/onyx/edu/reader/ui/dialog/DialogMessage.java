package com.onyx.edu.reader.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.onyx.edu.reader.R;

/**
 * Created by joy on 11/1/16.
 */
public class DialogMessage extends DialogBase {

    public DialogMessage(final Context context, final int stringResId) {
        super(context);
        setContentView(R.layout.dialog_message);
        ((TextView)findViewById(R.id.textview_title)).setText(stringResId);

        findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogMessage.this.dismiss();
            }
        });
    }

}
