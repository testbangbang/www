package com.onyx.kreader.ui.settings;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.dialog.DialogBase;

/**
 * Created by solskjaer49 on 15/2/11 15:54.
 */
public class DialogBindHotKey extends DialogBase {
    static String TAG = DialogBindHotKey.class.getSimpleName();
    private int keyUpCount = 0;
    private String mFunctionToBindKey;
    TextView functionToBindTextView, currentBindKeyTextView;

    public interface OnUserSetKeyListener {
        public void onSet(String keyCodeString, String function, String args);
    }

    private OnUserSetKeyListener mOnSetListener;

    public void setOnFilterListener(OnUserSetKeyListener l) {
        mOnSetListener = l;
    }

    public DialogBindHotKey(Context context, String functionToBindTittle, String functionToBindKey, int currentBindKeyCode) {
        super(context);
        this.setContentView(R.layout.dialog_bind_hot_key);
        mFunctionToBindKey = functionToBindKey;
        functionToBindTextView = (TextView) findViewById(R.id.textView_to_bind_function);
        currentBindKeyTextView = (TextView) findViewById(R.id.textView_current_bind_key);
        functionToBindTextView.setText(functionToBindTittle);
        currentBindKeyTextView.setText(getNameByKeyCode(currentBindKeyCode));
        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private String getNameByKeyCode(int currentBindKeyCode) {
        switch (currentBindKeyCode) {
            case KeyEvent.KEYCODE_UNKNOWN:
                return getContext().getResources().getString(R.string.dialog_bind_hot_key_none);
            case KeyEvent.KEYCODE_PAGE_DOWN:
                return getContext().getResources().getString(R.string.dialog_bind_hot_key_next_page_button);
            case KeyEvent.KEYCODE_PAGE_UP:
                return getContext().getResources().getString(R.string.dialog_bind_hot_key_prev_page_button);
        }
        return KeyEvent.keyCodeToString(currentBindKeyCode);
    }

    @Override
    public void show() {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        lp.width = (dm.widthPixels * 2) / 3;
        lp.height = (dm.heightPixels * 2) / 5;
        this.getWindow().setAttributes(lp);
        super.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() != KeyEvent.KEYCODE_BACK && event.getKeyCode() != KeyEvent.KEYCODE_MENU) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                if (keyUpCount < 1) {
                    mOnSetListener.onSet(KeyEvent.keyCodeToString(event.getKeyCode()), mFunctionToBindKey, "");
                    keyUpCount++;
                    dismiss();
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
