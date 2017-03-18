package com.onyx.android.sdk.ui.dialog;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.onyx.android.sdk.api.device.FrontLightController;
import com.onyx.android.sdk.utils.IntentFilterFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DialogNaturalLightBrightness extends OnyxBaseDialog {

    static final String TAG = DialogNaturalLightBrightness.class.getSimpleName();

    private RatingBar mRatingBarWarmLightSettings = null;
    private RatingBar mRatingBarColdLightSettings = null;
    private String COLD_LIGHT_VALUE = "cold_light_value";
    private String WARM_LIGHT_VALUE = "warm_light_value";
    private String NATURAL_LIGHT = "natural_light";
    private String WARM_LIGHT = "warm_light";
    private String COLD_LIGHT = "cold_light";

    private BroadcastReceiver mOpenAndCloseNaturalLightReceiver = null;
    private IntentFilter filter = null;
    private Context mContext = null;

    /**
     * Brightness value for fully off
     */
    public static final int BRIGHTNESS_OFF = 0;


    /**
     * Brightness value for fully on
     */
    public static int BRIGHTNESS_ON = 31;

    final static int BIG_STEP_LIMIT = 31;

    private boolean isLongClickOpenAndCloseWarmLight = false;
    private boolean isLongClickOpenAndCloseColdLight = false;
    private boolean isWarmLightChange = false;

    private List<Integer> mLightSteps = new ArrayList<Integer>();

    private List<Integer> initRangeArray(int numStarts) {
        List<Integer> brightnessList = new ArrayList<Integer>(numStarts);
        for (int i = 0; i <= numStarts; i++) {
            brightnessList.add(BRIGHTNESS_ON * i / numStarts);
        }
        return brightnessList;
    }

    public DialogNaturalLightBrightness(Context context) {
        super(context, com.onyx.android.sdk.ui.R.style.CustomDialog);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLightSteps = FrontLightController.getNaturalLightValueList(getContext());
        int targetLayoutID = com.onyx.android.sdk.ui.R.layout.dialog_natural_brightness_step;
        setContentView(targetLayoutID);
        mRatingBarWarmLightSettings = (RatingBar) findViewById(com.onyx.android.sdk.ui.R.id.ratingbar_warm_light_settings);
        mRatingBarColdLightSettings = (RatingBar) findViewById(com.onyx.android.sdk.ui.R.id.ratingbar_cold_light_settings);
        mRatingBarWarmLightSettings.setFocusable(false);
        mRatingBarColdLightSettings.setFocusable(false);

        mOpenAndCloseNaturalLightReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && intent.getAction() != null
                        && (IntentFilterFactory.ACTION_OPEN_FRONT_LIGHT.equals(intent.getAction())
                        || IntentFilterFactory.ACTION_CLOSE_FRONT_LIGHT.equals(intent.getAction()))) {
                    isLongClickOpenAndCloseWarmLight = true;
                    isLongClickOpenAndCloseColdLight = true;
                    Bundle bundle = intent.getBundleExtra(IntentFilterFactory.INTENT_FRONT_LIGHT_VALUE);
                    int warm_light = bundle.getInt(WARM_LIGHT, 0);
                    int cold_light = bundle.getInt(COLD_LIGHT, 0);
                    mRatingBarWarmLightSettings.setProgress(getIndex(warm_light));
                    mRatingBarColdLightSettings.setProgress(getIndex(cold_light));
                }
            }
        };
        filter = IntentFilterFactory.getOpenAndCloseFrontLightFilter();
        mContext.registerReceiver(mOpenAndCloseNaturalLightReceiver, filter);

        if (mLightSteps != null) {
            mRatingBarWarmLightSettings.setNumStars(mLightSteps.size() - 1);
            mRatingBarWarmLightSettings.setMax(mLightSteps.size() - 1);
            mRatingBarColdLightSettings.setNumStars(mLightSteps.size() - 1);
            mRatingBarColdLightSettings.setMax(mLightSteps.size() - 1);
        } else {
            int numStarts = mRatingBarWarmLightSettings.getNumStars();
            mLightSteps = initRangeArray(numStarts);
        }
        Collections.sort(mLightSteps);

        mRatingBarWarmLightSettings.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                isWarmLightChange = true;
                Settings.System.putInt(getContext().getContentResolver(),NATURAL_LIGHT,0);
                changeLightState();
                isLongClickOpenAndCloseWarmLight = false;
            }
        });
        mRatingBarColdLightSettings.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                isWarmLightChange = false;
                Settings.System.putInt(getContext().getContentResolver(),NATURAL_LIGHT,1);
                changeLightState();
                isLongClickOpenAndCloseColdLight = false;
            }
        });

        setLightRatingBarDefaultProgress();

        // widgets for warm light
        ImageButton mWarmLightDown = (ImageButton) findViewById(com.onyx.android.sdk.ui.R.id.imagebutton_warm_light_down);
        mWarmLightDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRatingBarWarmLightSettings.setProgress(mRatingBarWarmLightSettings.getProgress() - 1);
            }
        });

        ImageButton mWarmLightAdd = (ImageButton) findViewById(com.onyx.android.sdk.ui.R.id.imagebutton_warm_light_add);
        mWarmLightAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mRatingBarWarmLightSettings.getProgress() == mRatingBarWarmLightSettings.getMax()) {
                    setFrontLightValue();
                } else {
                    mRatingBarWarmLightSettings.setProgress(mRatingBarWarmLightSettings.getProgress() + 1);
                }
            }
        });

        // widgets for cold light
        ImageButton mColdLightDown = (ImageButton) findViewById(com.onyx.android.sdk.ui.R.id.imagebutton_cold_light_down);
        mColdLightDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRatingBarColdLightSettings.setProgress(mRatingBarColdLightSettings.getProgress() - 1);
            }
        });

        ImageButton mColdLightAdd = (ImageButton) findViewById(com.onyx.android.sdk.ui.R.id.imagebutton_cold_light_add);
        mColdLightAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRatingBarColdLightSettings.getProgress() == mRatingBarColdLightSettings.getMax()) {
                    setFrontLightValue();
                } else {
                    mRatingBarColdLightSettings.setProgress(mRatingBarColdLightSettings.getProgress() + 1);
                }
            }
        });

        this.setCanceledOnTouchOutside(true);

    }

    private void changeLightState() {
        if (!isLongClickOpenAndCloseWarmLight && !isLongClickOpenAndCloseColdLight) {
            setFrontLightValue();
        }
    }

    private void setLightRatingBarDefaultProgress() {
        int coldValue = Settings.System.getInt(getContext().getContentResolver(), COLD_LIGHT_VALUE,0);
        mRatingBarColdLightSettings.setProgress(getIndex(coldValue));
        int warmValue = Settings.System.getInt(getContext().getContentResolver(),WARM_LIGHT_VALUE,0);
        mRatingBarWarmLightSettings.setProgress(getIndex(warmValue));
    }

    private void setFrontLightValue() {
        if (!(mLightSteps.size() > 0)) {
            return;
        }
        int value = 0;
        if (isWarmLightChange) {
            value = mLightSteps.get(mRatingBarWarmLightSettings.getProgress());
            Settings.System.putInt(this.getContext().getContentResolver(),WARM_LIGHT_VALUE,value);
        } else {
            value = mLightSteps.get(mRatingBarColdLightSettings.getProgress());
            Settings.System.putInt(this.getContext().getContentResolver(), COLD_LIGHT_VALUE,value);
        }
        FrontLightController.setNaturalBrightness(this.getContext(), value);
    }

    @Override
    public void cancel() {
        super.cancel();
        if (mOpenAndCloseNaturalLightReceiver != null) {
            mContext.unregisterReceiver(mOpenAndCloseNaturalLightReceiver);
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

