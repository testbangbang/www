package com.onyx.android.sdk.ui.dialog;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;

import com.onyx.android.sdk.api.device.FrontLightController;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.utils.IntentFilterFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DialogNaturalLightBrightness extends OnyxBaseDialog implements View.OnClickListener, RatingBar.OnRatingBarChangeListener, View.OnTouchListener {

    static final String TAG = DialogNaturalLightBrightness.class.getSimpleName();

    private String COLD_LIGHT_VALUE = "cold_light_value";
    private String WARM_LIGHT_VALUE = "warm_light_value";
    private String NATURAL_LIGHT = "natural_light";
    private String WARM_LIGHT = "warm_light";
    private String COLD_LIGHT = "cold_light";
    public static int BRIGHTNESS_ON = 31;
    private boolean isLongClickOpenAndCloseLight = false;
    private boolean isMixingOpen = false;
    private int mTotalProgress = 0;

    private BroadcastReceiver mOpenAndCloseNaturalLightReceiver = null;
    private IntentFilter filter = null;
    private Context mContext = null;

    private List<Integer> mLightSteps = new ArrayList<Integer>();
    private RatingBar mRatingBarMixingLight = null;
    private RatingBar mRatingBarColdLight = null;
    private ImageButton mBtnLightSwitch;
    private Button mBtnWarmLight;
    private Button mBtnColdLight;
    private ImageButton mMixingLightDown;
    private ImageButton mMixingLightAdd;

    public DialogNaturalLightBrightness(Context context) {
        super(context, com.onyx.android.sdk.ui.R.style.CustomDialog);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int targetLayoutID = com.onyx.android.sdk.ui.R.layout.dialog_natural_brightness_step;
        setContentView(targetLayoutID);

        initView();
        initData();
        initEvent();
        initBroadcast();
        setRatingBarDefaultProgress();
        this.setCanceledOnTouchOutside(true);
    }

    private void initView() {
        mBtnColdLight = (Button) findViewById(R.id.btn_cold_light);
        mBtnWarmLight = (Button) findViewById(R.id.btn_warm_light);
        mBtnLightSwitch = (ImageButton) findViewById(R.id.imagebutton_light_switch);
        mRatingBarMixingLight = (RatingBar) findViewById(R.id.ratingbar_mixing_light_settings);
        mRatingBarColdLight = (RatingBar) findViewById(R.id.ratingbar_cold_light_settings);
        mMixingLightDown = (ImageButton) findViewById(R.id.imagebutton_mixing_light_down);
        mMixingLightAdd = (ImageButton) findViewById(R.id.imagebutton_mixing_light_add);
        mRatingBarMixingLight.setFocusable(false);
        mRatingBarColdLight.setFocusable(false);
        mRatingBarMixingLight.setFocusable(false);
    }

    private void initData() {
        mLightSteps = FrontLightController.getNaturalLightValueList(getContext());
        if (mLightSteps != null) {
            mRatingBarMixingLight.setNumStars(mLightSteps.size() - 1);
            mRatingBarColdLight.setNumStars(mLightSteps.size() - 1);
            mRatingBarMixingLight.setMax(mLightSteps.size() - 1);
            mRatingBarColdLight.setMax(mLightSteps.size() - 1);
        } else {
            int numStarts = mRatingBarMixingLight.getNumStars();
            mLightSteps = initRangeArray(numStarts);
        }
        Collections.sort(mLightSteps);
    }

    private List<Integer> initRangeArray(int numStarts) {
        List<Integer> brightnessList = new ArrayList<Integer>(numStarts);
        for (int i = 0; i <= numStarts; i++) {
            brightnessList.add(BRIGHTNESS_ON * i / numStarts);
        }
        return brightnessList;
    }

    private void initEvent() {
        mBtnLightSwitch.setOnClickListener(this);
        mBtnColdLight.setOnClickListener(this);
        mBtnWarmLight.setOnClickListener(this);
        mMixingLightDown.setOnClickListener(this);
        mMixingLightAdd.setOnClickListener(this);
        mRatingBarColdLight.setOnRatingBarChangeListener(this);
        mRatingBarMixingLight.setOnRatingBarChangeListener(this);
        mRatingBarColdLight.setOnTouchListener(this);
        mRatingBarMixingLight.setOnTouchListener(this);
    }

    private void setLightValue(int step, int lightType) {
        if (isLongClickOpenAndCloseLight) {
            return;
        }
        if (mLightSteps.size() > 0) {
            int lightValue = mLightSteps.get(step);
            Settings.System.putInt(getContext().getContentResolver(), NATURAL_LIGHT, lightType);
            String lightTag = lightType == 0 ? WARM_LIGHT_VALUE : COLD_LIGHT_VALUE;
            Settings.System.putInt(this.getContext().getContentResolver(), lightTag, lightValue);
            FrontLightController.setNaturalBrightness(this.getContext(), lightValue);
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cold_light) {
            mTotalProgress = 6;

        } else if (i == R.id.btn_warm_light) {
            mTotalProgress = 22;

        } else if (i == R.id.imagebutton_light_switch) {
            if (mTotalProgress > 0) {
                saveLightValueConfig();
                mTotalProgress = 0;
            } else {
                getLightValueConfig();
                if(mTotalProgress == 0){
                    mTotalProgress = 22;
                }
            }

        } else if (i == R.id.imagebutton_mixing_light_down) {
            mTotalProgress--;

        } else if (i == R.id.imagebutton_mixing_light_add) {
            mTotalProgress++;

        }
        updateProgress();
    }

    private void getLightValueConfig() {
        int coldValue = getIndex(Settings.System.getInt(getContext().getContentResolver(), COLD_LIGHT_VALUE, 0));
        int warmValue = getIndex(Settings.System.getInt(getContext().getContentResolver(), WARM_LIGHT_VALUE, 0));
        getTotalProgress(coldValue, warmValue);
    }

    private void saveLightValueConfig() {
        if (mTotalProgress > 16) {
            Settings.System.putInt(getContext().getContentResolver(), WARM_LIGHT_VALUE, mLightSteps.get(mTotalProgress - 16));
            Settings.System.putInt(getContext().getContentResolver(), COLD_LIGHT_VALUE, mLightSteps.get(mTotalProgress - 16));
        } else {
            Settings.System.putInt(getContext().getContentResolver(), WARM_LIGHT_VALUE, 0);
            Settings.System.putInt(getContext().getContentResolver(), COLD_LIGHT_VALUE, mLightSteps.get(mTotalProgress));
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        int i = ratingBar.getId();
        if (i == R.id.ratingbar_cold_light_settings) {
            updateSwitchButtonStatus(rating);
            if (!isMixingOpen) {
                mTotalProgress = (int) rating;
                setLightValue(0, 0);
                setLightValue(mRatingBarColdLight.getProgress(), 1);
            }

        } else if (i == R.id.ratingbar_mixing_light_settings) {
            if (rating > 0) {
                isMixingOpen = true;
            } else {
                isMixingOpen = false;
            }
            if (isMixingOpen) {
                mTotalProgress = (int) rating + mRatingBarColdLight.getMax();
                setLightValue(mRatingBarMixingLight.getProgress(), 1);
                setLightValue((int) (mRatingBarMixingLight.getProgress() * 0.7), 0);
            }

        }
        isLongClickOpenAndCloseLight = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int i = v.getId();
        if (i == R.id.ratingbar_cold_light_settings) {
            isMixingOpen = false;
            mRatingBarMixingLight.setProgress(0);

        } else if (i == R.id.ratingbar_mixing_light_settings) {
            isMixingOpen = true;
            mRatingBarColdLight.setProgress(mRatingBarColdLight.getMax());

        }
        return false;
    }

    private void updateSwitchButtonStatus(float rating) {
        if (rating > 0) {
            mBtnLightSwitch.setImageResource(com.onyx.android.sdk.ui.R.drawable.ic_light);
        } else {
            mBtnLightSwitch.setImageResource(com.onyx.android.sdk.ui.R.drawable.ic_light_off);
        }
    }

    private void setRatingBarDefaultProgress() {
        getLightValueConfig();
        updateProgress();
    }

    @Override
    public void cancel() {
        super.cancel();
        saveLightValueConfig();
        if (mOpenAndCloseNaturalLightReceiver != null) {
            mContext.unregisterReceiver(mOpenAndCloseNaturalLightReceiver);
        }
    }

    private void updateProgress() {
        if (mTotalProgress < mRatingBarColdLight.getMax() + 1) {
            isMixingOpen = false;
            mRatingBarMixingLight.setProgress(0);
            mRatingBarColdLight.setProgress(mTotalProgress);
        } else {
            isMixingOpen = true;
            mRatingBarColdLight.setProgress(mRatingBarColdLight.getMax());
            mRatingBarMixingLight.setProgress(mTotalProgress - mRatingBarColdLight.getMax());
        }
    }

    private void initBroadcast() {
        mOpenAndCloseNaturalLightReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && intent.getAction() != null
                        && (IntentFilterFactory.ACTION_OPEN_FRONT_LIGHT.equals(intent.getAction())
                        || IntentFilterFactory.ACTION_CLOSE_FRONT_LIGHT.equals(intent.getAction()))) {
                    isLongClickOpenAndCloseLight = true;
                    Bundle bundle = intent.getBundleExtra(IntentFilterFactory.INTENT_FRONT_LIGHT_VALUE);
                    int warm_light = bundle.getInt(WARM_LIGHT, 0);
                    int cold_light = bundle.getInt(COLD_LIGHT, 0);
                    getTotalProgress(getIndex(cold_light), getIndex(warm_light));
                    updateProgress();
                }
            }
        };
        filter = IntentFilterFactory.getOpenAndCloseFrontLightFilter();
        mContext.registerReceiver(mOpenAndCloseNaturalLightReceiver, filter);
    }

    private void getTotalProgress(int coldStep, int warmStep) {
        if (warmStep > 0) {
            mTotalProgress = warmStep + 16;
        } else {
            mTotalProgress = coldStep;
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

