package com.onyx.kreader.ui.dialog;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.kreader.R;
import com.onyx.android.sdk.ui.view.SeekBarWithEditTextView;


/**
 * Created by Solskjaer49 on 2014/4/15.
 */
public class DialogSetValue extends DialogBase {

    public static abstract class DialogCallback {
        public abstract void valueChange(int newValue);
        public abstract void done(boolean isValueChange, int oldValue, int newValue);
    }

    private DialogCallback mCallback;
    private TextView mDialogTittleTextView;
    private SeekBarWithEditTextView seekBarWithEditTextView;
    private Button mCancelButton;
    private Button mConfirmButton;

    public DialogSetValue(Context context, int initialValue, int minValue, int maxValue,
                          boolean isNeedInstantUpdate, boolean isCustomizedArea, int dialogTittleResID,
                          int valueTittleResID, final DialogCallback callback) {
        this(context, R.layout.dialog_set_value, initialValue, minValue, maxValue, isNeedInstantUpdate, isCustomizedArea,
                context.getResources().getString(dialogTittleResID),
                context.getResources().getString(valueTittleResID), callback);
    }

    public DialogSetValue(Context context, int initialValue, int minValue, int maxValue,
                          boolean isNeedInstantUpdate, boolean isCustomizedArea, String dialogTittle,
                          String valueTittle, final DialogCallback callback) {
        this(context, R.layout.dialog_set_value, initialValue, minValue, maxValue, isNeedInstantUpdate, isCustomizedArea,
                dialogTittle,
                valueTittle, callback);
    }

    public DialogSetValue(Context context, int layoutID, int initialValue, int minValue, int maxValue,
                          boolean isNeedInstantUpdate, boolean isCustomizedArea, int dialogTittleResID,
                          int valueTittleResID, final DialogCallback callback) {
        this(context, layoutID, initialValue, minValue, maxValue, isNeedInstantUpdate, isCustomizedArea,
                context.getResources().getString(dialogTittleResID),
                context.getResources().getString(valueTittleResID), callback);
    }

    public DialogSetValue(Context context, int layoutID, int initialValue, int minValue, int maxValue,
                          boolean isNeedInstantUpdate, boolean isCustomizedArea, String dialogTittle,
                          String valueTittle, final DialogCallback callback) {
        super(context);
        setCanceledOnTouchOutside(false);
        this.setContentView(layoutID);
        mCallback = callback;
        initView(isCustomizedArea);
        initData(dialogTittle, valueTittle, initialValue, minValue, maxValue);
        bindListener();
    }

    private void initData(String dialogTittle, String valueTittle, int initialValue, int minValue, int maxValue) {
        if (dialogTittle != null && mDialogTittleTextView != null) {
            mDialogTittleTextView.setText(dialogTittle);
        }
        seekBarWithEditTextView.updateValue(valueTittle, initialValue, minValue, maxValue);
        seekBarWithEditTextView.setCallback(new SeekBarWithEditTextView.Callback() {
            @Override
            public void valueChange(int newValue) {
                if (mCallback != null) {
                    mCallback.valueChange(newValue);
                }
            }
        });
    }

    private void initView(boolean isCustomizedArea) {
        mDialogTittleTextView = (TextView) findViewById(R.id.textView_dialog_Tittle);
        seekBarWithEditTextView = (SeekBarWithEditTextView) findViewById(R.id.seekbar_view);
        mCancelButton = (Button) findViewById(R.id.button_Cancel);
        mConfirmButton = (Button) findViewById(R.id.button_Confirm);
        if (isCustomizedArea) {
            LinearLayout mCustomizedLayout = (LinearLayout) findViewById(R.id.customize_Area);
            mCustomizedLayout.setVisibility(View.VISIBLE);
        }
    }

    private void bindListener() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.done(false, seekBarWithEditTextView.getInitialValue(), 0);
                dismiss();
            }
        });
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seekBarWithEditTextView.isCurrentValueValid()) {
                    mCallback.done(true, seekBarWithEditTextView.getInitialValue(),
                            seekBarWithEditTextView.getCurrentValue());
                    dismiss();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mCallback.done(false, seekBarWithEditTextView.getInitialValue(), 0);
            dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void show(int windowsHeight, int windowWidth, int positionX, int positionY) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        if (positionX != -1) {
            lp.x = positionX;
        }
        if (positionY != -1) {
            lp.y = positionY;
        }
        if (windowWidth >= 0) {
            lp.width = windowWidth;
        }
        if (windowsHeight >= 0) {
            lp.height = windowsHeight;
        }
        this.getWindow().setAttributes(lp);
        super.show();
    }

}
