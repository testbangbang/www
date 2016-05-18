package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;


/**
 * Created by Solskjaer49 on 2014/4/15.
 */
public class DialogSetValue extends DialogBaseOnyx {

    private enum ButtonDirection {ADD, MINUS}


    public static abstract class DialogCallback {

        public abstract void valueChange(int newValue);

        public abstract void done(boolean isValueChange,int newValue);
    }

    private static final String TAG = DialogSetValue.class.getSimpleName();
    private DialogCallback mCallback;
    private SeekBar mValueControlSeekBar;
    private EditText mValueControlEditText;
    private ImageView addButton;
    private ImageView minusButton;
    private TextView mTittleTextView;
    private TextView mDialogTittleTextView;
    private TextView mTotalPageTextView;
    private Button mCancelButton;
    private Button mConfirmButton;

    private int mSeekBarMinValue =0;
    private int mSeekBarMaxValue;
    private int mSeekBarRange=0;
    private int previousValue;
    private int stepSize = 1;
    private String outOfRangeErrorString;
    private String illegalErrorString;
    private InputMethodManager im;

    public DialogSetValue(Context context, int currentProgress, int minValue, int maxValue,
                          boolean isNeedInstantUpdate, boolean isCustomizedArea, int dialogTittleResID,
                          int valueTittleResID, final DialogCallback callback) {
        this(context, R.layout.dialog_set_value, currentProgress, minValue, maxValue, isNeedInstantUpdate, isCustomizedArea,
                context.getResources().getString(dialogTittleResID),
                context.getResources().getString(valueTittleResID), callback);
    }

    public DialogSetValue(Context context, int currentProgress, int minValue, int maxValue,
                          boolean isNeedInstantUpdate, boolean isCustomizedArea, String dialogTittle,
                          String valueTittle, final DialogCallback callback) {
        this(context, R.layout.dialog_set_value, currentProgress, minValue, maxValue, isNeedInstantUpdate, isCustomizedArea,
                dialogTittle,
                valueTittle, callback);
    }

    public DialogSetValue(Context context, int layoutID, int currentProgress, int minValue, int maxValue,
                          boolean isNeedInstantUpdate, boolean isCustomizedArea, int dialogTittleResID,
                          int valueTittleResID, final DialogCallback callback) {
        this(context, layoutID, currentProgress, minValue, maxValue, isNeedInstantUpdate, isCustomizedArea,
                context.getResources().getString(dialogTittleResID),
                context.getResources().getString(valueTittleResID), callback);
    }

    public DialogSetValue(Context context, int layoutID, int currentProgress, int minValue, int maxValue,
                          boolean isNeedInstantUpdate, boolean isCustomizedArea, String dialogTittle,
                          String valueTittle, final DialogCallback callback) {
        super(context);
        setCanceledOnTouchOutside(false);
        this.setContentView(layoutID);
        mCallback = callback;
        previousValue = currentProgress;
        mSeekBarMaxValue = maxValue;
        mSeekBarMinValue = minValue;
        mSeekBarRange = mSeekBarMaxValue - mSeekBarMinValue;
        outOfRangeErrorString = context.getString(R.string.outOfRangRerror);
        illegalErrorString = context.getString(R.string.illegalInput);
        im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        initViews(isCustomizedArea);
        initData(dialogTittle, valueTittle, currentProgress, maxValue);
        if (isNeedInstantUpdate) {
            updateStatus(currentProgress, maxValue);
        }
        bindListener();
    }

    private void initData(String dialogTittle, String valueTittle, int currentProgress, int size) {
        if (valueTittle != null && mTittleTextView != null) {
            mTittleTextView.setText(valueTittle);
        }
        if (dialogTittle != null && mDialogTittleTextView != null) {
            mDialogTittleTextView.setText(dialogTittle);
        }
        if (mTotalPageTextView != null) {
            mTotalPageTextView.setText(Integer.toString(size));
        }
        mValueControlSeekBar.setProgress(currentProgress - mSeekBarMinValue);
        mValueControlEditText.setText(Integer.toString(mValueControlSeekBar.getProgress() + mSeekBarMinValue));
    }


    private void initViews(boolean isCustomizedArea) {
        mValueControlSeekBar = (SeekBar) findViewById(R.id.seekBar_valueControl);
        mValueControlEditText = (EditText) findViewById(R.id.editText_ValueInput);
        mDialogTittleTextView = (TextView) findViewById(R.id.textView_dialog_Tittle);
        mTittleTextView = (TextView) findViewById(R.id.textView_tittle);
        mTotalPageTextView = (TextView) findViewById(R.id.textView_page_count);
        addButton = (ImageView) findViewById(R.id.imageView_AddButton);
        minusButton = (ImageView) findViewById(R.id.imageView_MinusButton);
        mCancelButton = (Button) findViewById(R.id.button_Cancel);
        mConfirmButton = (Button) findViewById(R.id.button_Confirm);
        if (isCustomizedArea) {
            LinearLayout mCustomizedLayout = (LinearLayout) findViewById(R.id.customize_Area);
            mCustomizedLayout.setVisibility(View.VISIBLE);
        }
        mValueControlSeekBar.setMax(mSeekBarRange);
    }

    private void bindListener() {
        mValueControlSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setEditTextValue(progress);
                mValueControlEditText.setError(null);
                mCallback.valueChange(progress + mSeekBarMinValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mValueControlEditText.setError(null);
                mValueControlEditText.clearFocus();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (im.isActive()) {
                    im.hideSoftInputFromWindow(mValueControlEditText.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        mValueControlEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        int newProgressValue = Integer.parseInt(v.getText().toString());
                        setSeekBarValue(newProgressValue);
                    } catch (Exception e) {
                        mValueControlEditText.setError(illegalErrorString);
                    }
                }
                return false;
            }
        });

        addButton.setOnClickListener(new ValueChangeButtonOnClickListener(ButtonDirection.ADD));
        minusButton.setOnClickListener(new ValueChangeButtonOnClickListener(ButtonDirection.MINUS));
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.done(false,0);
                dismiss();
            }
        });
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int editTextValue = Integer.parseInt(mValueControlEditText.getText().toString());
                    if (mValueControlSeekBar.getProgress() + mSeekBarMinValue ==
                            editTextValue) {
                        mCallback.done(true, mValueControlSeekBar.getProgress());
                        dismiss();
                    } else {
                        setSeekBarValue(editTextValue);
                    }
                } catch (Exception e) {
                    mValueControlEditText.setError(illegalErrorString);
                }
            }
        });
    }

    private boolean setSeekBarValue(int newValue) {
        boolean succeedFlag = false;
        if (newValue <= mSeekBarMaxValue && newValue >= mSeekBarMinValue) {
            mValueControlSeekBar.setProgress(newValue - mSeekBarMinValue);
            setEditTextValue(newValue - mSeekBarMinValue);
            succeedFlag = true;
        } else {
            mValueControlEditText.setError(outOfRangeErrorString);
        }
        return succeedFlag;
    }

    private boolean setEditTextValue(int graphicalValue) {
        boolean succeedFlag = false;
        if (graphicalValue <= mSeekBarRange && graphicalValue >= 0) {
            mValueControlEditText.setText(Integer.toString(graphicalValue + mSeekBarMinValue));
            succeedFlag = true;
        } else {
            mValueControlEditText.setError(outOfRangeErrorString);
        }
        return succeedFlag;
    }

    public void setStepSize(int newStepSize) {
        this.stepSize = newStepSize;
    }

    public int getCurrentStepSize() {
        return this.stepSize;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mCallback.done(false,0);
            dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void updateStatus(int curValue,int seekBarMaxValue) {
        this.mSeekBarMaxValue = seekBarMaxValue;
        if(mValueControlSeekBar!=null){
            mSeekBarRange=mSeekBarMaxValue-mSeekBarMinValue;
            mValueControlSeekBar.setMax(mSeekBarRange);
        }
        switch (curValue){
            case -1:
                setSeekBarValue(mSeekBarMinValue);
                previousValue = mSeekBarMinValue;
                break;
            default:
                if (setSeekBarValue(curValue)) {
                    previousValue = curValue;
                }
                break;
        }

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

    class ValueChangeButtonOnClickListener implements View.OnClickListener {

        ButtonDirection mDirection;

        ValueChangeButtonOnClickListener(ButtonDirection direction) {
            this.mDirection = direction;
        }

        @Override
        public void onClick(View v) {
            mValueControlEditText.clearFocus();
            if (im.isActive()) {
                im.hideSoftInputFromWindow(mValueControlEditText.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
            switch (mDirection) {
                case ADD:
                    setSeekBarValue(mValueControlSeekBar.getProgress() + stepSize+mSeekBarMinValue);
                    break;
                case MINUS:
                    setSeekBarValue(mValueControlSeekBar.getProgress() - stepSize+mSeekBarMinValue);
                    break;
                default:
                    break;
            }

        }
    }
}
