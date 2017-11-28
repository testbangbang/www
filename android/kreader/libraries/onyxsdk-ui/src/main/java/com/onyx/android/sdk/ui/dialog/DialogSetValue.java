package com.onyx.android.sdk.ui.dialog;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.utils.InputMethodUtils;


/**
 * Created by Solskjaer49 on 2016/9/2.
 */
public class DialogSetValue extends OnyxAlertDialog {
    static final public String ARGS_DIALOG_TITLE = "args_dialog_title";
    static final public String ARGS_VALUE_TITLE = "args_value_title";
    static final public String ARGS_CHANGE_LAYOUT_IN_LOW_DPI = "args_change_layout_in_low_dpi";
    static final public String ARGS_MIN_VALUE = "args_min_value";
    static final public String ARGS_MAX_VALUE = "args_max_value";
    static final public String ARGS_CURRENT_VALUE = "args_current_value";
    static final public String ARGS_STEP_SIZE = "args_step_size";
    static final public String ARGS_INSTANT_UPDATE = "args_instant_update";

    private enum ButtonDirection {ADD, MINUS}

    public interface DialogCallback {
        void valueChange(int newValue);

        void done(boolean isValueChange, int newValue);
    }

    private static final String TAG = DialogSetValue.class.getSimpleName();
    private SeekBar mValueControlSeekBar;
    private EditText mValueControlEditText;
    private ImageView addButton;
    private ImageView minusButton;
    private TextView mTittleTextView;

    private int seekBarMinValue = 0;
    private int seekBarMaxValue;
    private int seekBarRange = 0;
    private int stepSize = 1;
    private int currentProgress;
    private String outOfRangeErrorString;
    private String illegalErrorString;

    public DialogSetValue setCallback(DialogCallback callback) {
        this.callback = callback;
        return this;
    }

    private DialogCallback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    callback.done(false,0);
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final String dialogTitle = getArguments().getString(ARGS_DIALOG_TITLE);
        final String valueTitle = getArguments().getString(ARGS_VALUE_TITLE);
//        final boolean changeLayoutInLowDPI = getArguments().getBoolean(ARGS_CHANGE_LAYOUT_IN_LOW_DPI);
        final boolean isNeedInstantUpdate = getArguments().getBoolean(ARGS_INSTANT_UPDATE, true);
        currentProgress = getArguments().getInt(ARGS_CURRENT_VALUE, 0);
        seekBarMinValue = getArguments().getInt(ARGS_MIN_VALUE, 0);
        seekBarMaxValue = getArguments().getInt(ARGS_MAX_VALUE, Integer.MAX_VALUE);
        stepSize = getArguments().getInt(ARGS_STEP_SIZE, 1);
        outOfRangeErrorString = getActivity().getString(R.string.outOfRangRerror);
        illegalErrorString = getActivity().getString(R.string.illegalInput);
        setParams(new Params().setTittleString(dialogTitle)
                .setCanceledOnTouchOutside(false)
                .setCustomContentLayoutResID(R.layout.alert_dialog_set_value)
                .setCustomViewAction(new CustomViewAction() {
                    @Override
                    public void onCreateCustomView(View customView, TextView pageIndicator) {
                        initViews(customView);
                        mTittleTextView.setText(valueTitle);
                        if (isNeedInstantUpdate) {
                            updateStatus(currentProgress, seekBarMaxValue);
                        }
                        bindListener();
                    }
                }).setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int editTextValue = Integer.parseInt(mValueControlEditText.getText().toString());
                            if (mValueControlSeekBar.getProgress() + seekBarMinValue == editTextValue) {
                                dismissWithCallback();
                            } else {
                                boolean success = setSeekBarValue(editTextValue);
                                if (success) {
                                    dismissWithCallback();
                                }
                            }
                        } catch (Exception e) {
                            mValueControlEditText.setError(illegalErrorString);
                        }
                    }
                }).setNegativeAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.done(false, 0);
                        dismiss();
                    }
                }));
        super.onCreate(savedInstanceState);
    }

    private void dismissWithCallback() {
        callback.done(true, mValueControlSeekBar.getProgress());
        dismiss();
    }

    private void initViews(View parentView) {
        mValueControlSeekBar = (SeekBar) parentView.findViewById(R.id.seekBar_valueControl);
        mValueControlEditText = (EditText) parentView.findViewById(R.id.editText_ValueInput);
        mTittleTextView = (TextView) parentView.findViewById(R.id.textView_tittle);
        addButton = (ImageView) parentView.findViewById(R.id.imageView_AddButton);
        minusButton = (ImageView) parentView.findViewById(R.id.imageView_MinusButton);
        mValueControlSeekBar.setMax(seekBarRange);
        mValueControlSeekBar.setProgress(currentProgress - seekBarMinValue);
        mValueControlEditText.setText(String.format(getActivity().getResources().getConfiguration().locale,
                "%d", mValueControlSeekBar.getProgress() + seekBarMinValue));
    }

    private void bindListener() {
        mValueControlSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setEditTextValue(progress);
                mValueControlEditText.setError(null);
                callback.valueChange(progress + seekBarMinValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mValueControlEditText.setError(null);
                mValueControlEditText.clearFocus();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                InputMethodUtils.hideInputKeyboard(getActivity());
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
    }

    private boolean setSeekBarValue(int newValue) {
        boolean succeedFlag = false;
        if (newValue <= seekBarMaxValue && newValue >= seekBarMinValue) {
            mValueControlSeekBar.setProgress(newValue - seekBarMinValue);
            setEditTextValue(newValue - seekBarMinValue);
            succeedFlag = true;
        } else {
            mValueControlEditText.setError(outOfRangeErrorString);
        }
        return succeedFlag;
    }

    private boolean setEditTextValue(int graphicalValue) {
        boolean succeedFlag = false;
        if (graphicalValue <= seekBarRange && graphicalValue >= 0) {
            mValueControlEditText.setText(String.format(getActivity().getResources().getConfiguration().locale,
                    "%d", graphicalValue + seekBarMinValue));
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

    public void updateStatus(int curValue, int seekBarMaxValue) {
        this.seekBarMaxValue = seekBarMaxValue;
        if (mValueControlSeekBar != null) {
            seekBarRange = this.seekBarMaxValue - seekBarMinValue;
            mValueControlSeekBar.setMax(seekBarRange);
        }
        switch (curValue) {
            case -1:
                setSeekBarValue(seekBarMinValue);
                break;
            default:
                setSeekBarValue(curValue);
                break;
        }

    }

    private class ValueChangeButtonOnClickListener implements View.OnClickListener {
        ButtonDirection mDirection;

        ValueChangeButtonOnClickListener(ButtonDirection direction) {
            this.mDirection = direction;
        }

        @Override
        public void onClick(View v) {
            mValueControlEditText.clearFocus();
            InputMethodUtils.hideInputKeyboard(getActivity());
            switch (mDirection) {
                case ADD:
                    setSeekBarValue(mValueControlSeekBar.getProgress() + stepSize + seekBarMinValue);
                    break;
                case MINUS:
                    setSeekBarValue(mValueControlSeekBar.getProgress() - stepSize + seekBarMinValue);
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
    }

    public void show(FragmentManager manager) {
        super.show(manager, TAG);
    }
}
