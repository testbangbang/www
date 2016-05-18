package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;

/**
 * Created by solskjaer49 on 14-7-8 11:48.
 */
@Deprecated
public class DialogDeleteConfirm extends DialogBaseOnyx {

    private Button mPostiveButton;
    private Button mNegativeButton;
    private TextView mMessage;
    private TextView mTittle;

    public DialogDeleteConfirm(Context context) {
        super(context);
        this.setContentView(R.layout.dialog_delete_confirm);
        mPostiveButton = (Button) findViewById(R.id.button_ok);
        mNegativeButton = (Button) findViewById(R.id.button_cancel);
        mMessage = (TextView) findViewById(R.id.textView_message);
        mTittle = (TextView) findViewById(R.id.textView_tittle);
    }

    public DialogDeleteConfirm setButtonOnClickListener(View.OnClickListener positiveButtonListener,
                                                        View.OnClickListener negativeButtonListener) {
        mPostiveButton.setOnClickListener(positiveButtonListener);
        mNegativeButton.setOnClickListener(negativeButtonListener);
        return this;
    }

    public DialogDeleteConfirm setTittle(int resID){
        mTittle.setText(resID);
        return this;
    }

    public DialogDeleteConfirm setTittle(String resString){
        mTittle.setText(resString);
        return this;
    }
    public DialogDeleteConfirm setMessage(int resID){
        mMessage.setText(resID);
        return this;
    }
    public DialogDeleteConfirm setMessage(String resString){
        mMessage.setText(resString);
        return this;
    }
}
