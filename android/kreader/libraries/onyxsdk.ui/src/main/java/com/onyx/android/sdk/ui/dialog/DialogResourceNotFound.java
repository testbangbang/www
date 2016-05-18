/**
 *
 */
package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.res.IResource;

/**
 * @author jim
 *
 */
public class DialogResourceNotFound extends DialogBaseOnyx {

    private static final String TAG = "DialogDictionaryNotFound";

    private Context context;
    private IResource resource;
    private TextView message;
    private Button ok;
    private Button cancel;

    public DialogResourceNotFound(Context context, IResource res) {
        super(context);
        this.context = context;
        this.resource = res;
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_resource_not_found);

        message = (TextView) findViewById(R.id.text_view_res_not_found);
        message.setText(context.getResources().getString(resource.getNotFoundMessageID()));

        ok = (Button) findViewById(R.id.res_not_found_ok_button);
        cancel = (Button) findViewById(R.id.res_not_found_cancel_button);

        ok.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(TAG, "OK clicked");
                dismiss();
            }
        });
        cancel.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(TAG, "Cancel clicked");
                dismiss();
            }
        });
    }

}
