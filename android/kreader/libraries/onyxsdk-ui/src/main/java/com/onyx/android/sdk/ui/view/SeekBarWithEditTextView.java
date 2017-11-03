package com.onyx.android.sdk.ui.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;


/**
 * Created by joy on 11/28/16.
 */

public class SeekBarWithEditTextView extends LinearLayout {

    public static abstract class Callback {
        public abstract void valueChange(int newValue);
    }

    private enum ButtonDirection {ADD, MINUS}

    class ValueChangeButtonOnClickListener implements View.OnClickListener {

        ButtonDirection mDirection;

        ValueChangeButtonOnClickListener(ButtonDirection direction) {
            this.mDirection = direction;
        }

        @Override
        public void onClick(View v) {
            editText.clearFocus();
            if (im.isActive()) {
                im.hideSoftInputFromWindow(editText.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
            switch (mDirection) {
                case ADD:
                    setSeekBarProgress(Math.min(seekBar.getProgress() + stepSize, seekBar.getMax()));
                    break;
                case MINUS:
                    setSeekBarProgress(Math.max(seekBar.getProgress() - stepSize, 0));
                    break;
                default:
                    break;
            }

        }
    }

    public class TextValidator implements TextWatcher {
        private final TextView textView;

        public TextValidator(TextView textView) {
            this.textView = textView;
        }

        public void validate(TextView textView, String text) {
            try {
                int value = Integer.parseInt(textView.getText().toString());
                if (value < minValue || value > maxValue) {
                    editText.setError(outOfRangeErrorString);
                }
            } catch (Exception e) {
                editText.setError(illegalErrorString);
            }
        }

        @Override
        final public void afterTextChanged(Editable s) {
            String text = textView.getText().toString();
            validate(textView, text);
        }

        @Override
        final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

        @Override
        final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }
    }

    private Callback callback;

    private int stepSize = 1;
    private int initialValue;
    private int minValue;
    private int maxValue;

    private String outOfRangeErrorString;
    private String illegalErrorString;

    private TextView titleTextView;
    private EditText editText;
    private ImageView addButton;
    private ImageView minusButton;
    private SeekBar seekBar;

    private InputMethodManager im;

    private boolean ignoreSeekBarTrackingValue = false;

    public SeekBarWithEditTextView(Context context) {
        super(context);
        initView();
    }

    public SeekBarWithEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SeekBarWithEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setIgnoreSeekBarTrackingValue(boolean ignoreSeekBarTrackingValue) {
        this.ignoreSeekBarTrackingValue = ignoreSeekBarTrackingValue;
    }

    public boolean isCurrentValueValid() {
        return editText.getError() == null;
    }

    public int getCurrentValue() {
        return Integer.parseInt(editText.getText().toString());
    }

    public int getInitialValue() {
        return initialValue;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void updateValue(String title, int initialValue, int minValue, int maxValue) {
        titleTextView.setText(title);
        updateValue(initialValue, minValue, maxValue);
    }

    public void updateValue(int stringResId, int initialValue, int minValue, int maxValue) {
        titleTextView.setText(stringResId);
        updateValue(initialValue, minValue, maxValue);
    }

    private void updateValue(int initialValue, int minValue, int maxValue) {
        this.initialValue = initialValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        seekBar.setMax(maxValue - minValue);
        setSeekBarProgress(valueToProgress(initialValue));
    }

    private void initView() {
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seekbar_with_edittext_view, this, true);

        titleTextView = (TextView) findViewById(R.id.textView_tittle);
        editText = (EditText) findViewById(R.id.editText_ValueInput);
        addButton = (ImageView) findViewById(R.id.imageView_AddButton);
        minusButton = (ImageView) findViewById(R.id.imageView_MinusButton);
        seekBar = (SeekBar) findViewById(R.id.seekBar_valueControl);

        outOfRangeErrorString = getContext().getString(R.string.outOfRangRerror);
        illegalErrorString = getContext().getString(R.string.illegalInput);

        im = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        bindListener();
    }

    private void bindListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (setEditTextValue(progressToValue(progress))) {
                    if (ignoreSeekBarTrackingValue && fromUser) {
                        return;
                    }
                    if (callback != null) {
                        callback.valueChange(progressToValue(progress));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                editText.setError(null);
                editText.clearFocus();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (im.isActive()) {
                    im.hideSoftInputFromWindow(editText.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                if (ignoreSeekBarTrackingValue && callback != null) {
                    callback.valueChange(progressToValue(seekBar.getProgress()));
                }
            }
        });
        editText.addTextChangedListener(new TextValidator(editText));
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        int value = Integer.parseInt(v.getText().toString());
                        setSeekBarProgress(valueToProgress(value));
                    } catch (Exception e) {
                        editText.setError(illegalErrorString);
                    }
                }
                return false;
            }
        });

        addButton.setOnClickListener(new ValueChangeButtonOnClickListener(ButtonDirection.ADD));
        minusButton.setOnClickListener(new ValueChangeButtonOnClickListener(ButtonDirection.MINUS));
    }

    private boolean setSeekBarProgress(int progress) {
        boolean succeedFlag = false;
        if (progress <= seekBar.getMax()) {
            seekBar.setProgress(progress);
            setEditTextValue(progressToValue(progress));
            succeedFlag = true;
        } else {
            editText.setError(outOfRangeErrorString);
        }
        return succeedFlag;
    }

    private boolean setEditTextValue(int value) {
        boolean succeedFlag = false;
        if (value <= maxValue && value >= minValue) {
            editText.setText(String.valueOf(value));
            editText.setError(null);
            succeedFlag = true;
        } else {
            editText.setError(outOfRangeErrorString);
        }
        return succeedFlag;
    }

    private int progressToValue(int progress) {
        return progress + minValue;
    }

    private int valueToProgress(int value) {
        return value - minValue;
    }
}
