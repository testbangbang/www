package com.onyx.android.sdk.ui.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.*;
import android.widget.RatingBar.OnRatingBarChangeListener;
import com.onyx.android.sdk.api.device.FrontLightController;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.utils.IntentFilterFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DialogBrightness extends OnyxBaseDialog {

    static final String TAG = DialogBrightness.class.getSimpleName();

    private RatingBar mRatingBarLightSettings = null;

    /**
     * Brightness value for fully off
     */
    public static final int BRIGHTNESS_OFF = 0;


    /**
     * Brightness value for fully on
     */
    public static int BRIGHTNESS_ON = 255;

    final static int BIG_STEP_LIMIT = 25;

    private ToggleButton mLightSwitch;

    private boolean isLongClickOpenAndCloseFrontLight = false;

    private BroadcastReceiver mOpenAndCloseFrontLightReceiver = null;
    private IntentFilter filter = null;
    private Context mContext = null;

    private List<Integer> mLightSteps = new ArrayList<Integer>();

    private List<Integer> initRangeArray(int numStarts) {
        List<Integer> brightnessList = new ArrayList<Integer>(numStarts);
        for (int i = 0; i <= numStarts; i++) {
            brightnessList.add(BRIGHTNESS_ON * i / numStarts);
        }
        return brightnessList;
    }

    public DialogBrightness(Context context) {
        super(context, R.style.CustomDialog);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLightSteps = FrontLightController.getFrontLightValueList(getContext());
        int targetLayoutID = R.layout.dialog_brightness_normal_step;
        if (mLightSteps != null && mLightSteps.size() < BIG_STEP_LIMIT) {
            targetLayoutID = R.layout.dialog_brightness_big_step;
        }
        setContentView(targetLayoutID);
        RelativeLayout dialogTittleBar = (RelativeLayout) findViewById(R.id.dialog_tittleBar);
        TextView dialogTittle = (TextView) dialogTittleBar.findViewById(R.id.textView_title);
        dialogTittle.setText(R.string.brightness);
        dialogTittleBar.findViewById(R.id.page_size_indicator).setVisibility(View.GONE);
        mRatingBarLightSettings = (RatingBar) findViewById(R.id.ratingbar_light_settings);
        mRatingBarLightSettings.setFocusable(false);

        mOpenAndCloseFrontLightReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && intent.getAction() != null
                        && (IntentFilterFactory.ACTION_OPEN_FRONT_LIGHT.equals(intent.getAction())
                        || IntentFilterFactory.ACTION_CLOSE_FRONT_LIGHT.equals(intent.getAction()))) {
                    isLongClickOpenAndCloseFrontLight = true;
                    int front_light_value = intent.getIntExtra(IntentFilterFactory.INTENT_FRONT_LIGHT_VALUE, 0);
                    mRatingBarLightSettings.setProgress(getIndex(front_light_value));
                }
            }
        };
        filter = IntentFilterFactory.getOpenAndCloseFrontLightFilter();
        mContext.registerReceiver(mOpenAndCloseFrontLightReceiver, filter);


        if (mLightSteps != null) {
            mRatingBarLightSettings.setNumStars(mLightSteps.size() - 1);
            mRatingBarLightSettings.setMax(mLightSteps.size() - 1);
        } else {
            int numStarts = mRatingBarLightSettings.getNumStars();
            mLightSteps = initRangeArray(numStarts);
        }
        Collections.sort(mLightSteps);

        mRatingBarLightSettings.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                changeLightState();
                isLongClickOpenAndCloseFrontLight = false;
            }
        });

        setLightRatingBarDefaultProgress();

        ImageButton mLightDown = (ImageButton) findViewById(R.id.imagebutton_light_down);
        mLightDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRatingBarLightSettings.setProgress(mRatingBarLightSettings.getProgress() - 1);
            }
        });

        ImageButton mLightAdd = (ImageButton) findViewById(R.id.imagebutton_light_add);
        mLightAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mRatingBarLightSettings.getProgress() == mRatingBarLightSettings.getMax()) {
                    setFrontLightValue();
                } else {
                    mRatingBarLightSettings.setProgress(mRatingBarLightSettings.getProgress() + 1);
                }
            }
        });

        mLightSwitch = (ToggleButton) findViewById(R.id.togglebutton_light_switch);
        mLightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mLightSwitch.isChecked()) {
                    FrontLightController.turnOn(DialogBrightness.this.getContext());
                } else {
                    FrontLightController.turnOff(DialogBrightness.this.getContext());
                }
            }
        });

        if (FrontLightController.isLightOn(this.getContext())) {
            mLightSwitch.setChecked(true);
        } else {
            mLightSwitch.setChecked(false);
        }

        this.setCanceledOnTouchOutside(true);

    }

    private void changeLightState() {
        if (!isLongClickOpenAndCloseFrontLight) {
            setFrontLightValue();
        }
        updateLightSwitch();
    }

    private void updateLightSwitch() {
        if (mLightSwitch != null) {
            if (mRatingBarLightSettings.getProgress() != 0 && !mLightSwitch.isChecked()) {
                mLightSwitch.setChecked(true);
            } else if (mRatingBarLightSettings.getProgress() == 0 && mLightSwitch.isChecked()) {
                mLightSwitch.setChecked(false);
            }
        }
    }

    private void setLightRatingBarDefaultProgress() {
        int value = FrontLightController.getBrightness(this.getContext());
        mRatingBarLightSettings.setProgress(getIndex(value));
    }

    private void setFrontLightValue() {
        if (!(mLightSteps.size() > 0)) {
            return;
        }
        int value = mLightSteps.get(mRatingBarLightSettings.getProgress());
        FrontLightController.setBrightness(this.getContext(), value);
        FrontLightController.setBrightnessConfigValue(this.getContext(), value);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mRatingBarLightSettings.setProgress(mRatingBarLightSettings.getProgress() - 1);
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mRatingBarLightSettings.getProgress() == mRatingBarLightSettings.getMax()) {
                    setFrontLightValue();
                } else {
                    mRatingBarLightSettings.setProgress(mRatingBarLightSettings.getProgress() + 1);
                }
                return true;
            default:
                break;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void cancel() {
        super.cancel();
        if (mOpenAndCloseFrontLightReceiver != null) {
            mContext.unregisterReceiver(mOpenAndCloseFrontLightReceiver);
        }
    }

    private int getIndex(int val) {
        int index = Collections.binarySearch(mLightSteps, val);
        if (index == -1) {
            index = 0;
        } else if (index < 0) {
            if (Math.abs(index) <= mLightSteps.size()) {
                index = Math.abs(index) - 2;
            } else {
                index = mLightSteps.size() - 1;
            }
        }
        return index;
    }

}

