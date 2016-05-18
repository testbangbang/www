/**
 * 
 */
package com.onyx.android.sdk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;

import com.onyx.android.sdk.ui.R;

/**
 * @author qingyue
 *
 */
public class DialogProgressBarRotundity extends Dialog
{
    public DialogProgressBarRotundity(Context context)
    {
        super(context, R.style.dialog_progress_style);

        setContentView(R.layout.dialog_progressbar_rotundity);
    }

    @Override
        public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.cancel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
