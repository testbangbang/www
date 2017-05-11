/**
 *
 */
package com.onyx.edu.reader.ui.dialog;

import android.content.Context;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import com.onyx.edu.reader.R;

/**
 * @author joy
 */
public class DialogPassword extends DialogBase {
    public interface OnPasswordEnteredListener {
        void onPasswordEntered(boolean success, String password);
    }

    private OnPasswordEnteredListener mOnPasswordEnteredListener = null;

    public void setOnPasswordEnteredListener(OnPasswordEnteredListener l) {
        mOnPasswordEnteredListener = l;
    }

    private void notifyPasswordEntered(boolean succ, String password) {
        if (mOnPasswordEnteredListener != null) {
            mOnPasswordEnteredListener.onPasswordEntered(succ, password);
        }
    }

    public DialogPassword(Context context) {
        super(context);
        setContentView(R.layout.dialog_password);

        setCanceledOnTouchOutside(false);
        final EditText txt_pwd = (EditText) this.findViewById(R.id.edittext_password);

        Button btn_set = (Button) this.findViewById(R.id.button_set);
        btn_set.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                notifyPasswordEntered(true, txt_pwd.getText().toString());
                DialogPassword.this.dismiss();
            }
        });

        Button btn_cancel = (Button) this.findViewById(R.id.button_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                notifyPasswordEntered(false, null);
                DialogPassword.this.cancel();
            }
        });

        CheckBox showPassword = (CheckBox) this.findViewById(R.id.show_password);
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                txt_pwd.setInputType(isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                CharSequence charSequence = txt_pwd.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            notifyPasswordEntered(false, null);
            // must cancel() explicitly, or else will causing strange problem in password listener,
            // don't know why 
            this.cancel();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
