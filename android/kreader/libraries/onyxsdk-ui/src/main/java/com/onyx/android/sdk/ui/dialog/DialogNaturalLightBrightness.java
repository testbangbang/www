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
    private String TAG_MIXING_LIGHT_RECOMMEND_PROGRESS = "mixing_light_recommend_progress";
    private String TAG_COLD_LIGHT_RECOMMEND_PROGRESS = "cold_light_recommend_progress";
    public static int BRIGHTNESS_ON = 31;
    public static float BRIGHTNESS_RATIO = 0.7f;
    public static int TYPE_WARM_LIGHT = 0;
    public static int TYPE_COLD_LIGHT = 1;
    private int defMixingLightProgress = 6;
    private int defColdLightProgress = 6;
    private boolean isLongClickOpenAndCloseLight = false;
    private boolean isMixingOpen = false;
    private int mTotalProgress;
    private int mMaxNumStars;
    private List<Integer> mLightSteps = new ArrayList<Integer>();

    private BroadcastReceiver mOpenAndCloseNaturalLightReceiver = null;
    private IntentFilter filter = null;
    private Context mContext = null;
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
            mMaxNumStars = mLightSteps.size() - 1;
            mRatingBarMixingLight.setNumStars(mMaxNumStars);
            mRatingBarColdLight.setNumStars(mMaxNumStars);
            mRatingBarMixingLight.setMax(mMaxNumStars);
            mRatingBarColdLight.setMax(mMaxNumStars);
        } else {
            mMaxNumStars = mRatingBarMixingLight.getNumStars();
            mLightSteps = initRangeArray(mMaxNumStars);
        }
        Collections.sort(mLightSteps);
        defMixingLightProgress = Settings.System.getInt(getContext().getContentResolver(),
                TAG_MIXING_LIGHT_RECOMMEND_PROGRESS, defMixingLightProgress) + mMaxNumStars;
        defColdLightProgress = Settings.System.getInt(getContext().getContentResolver(),
                TAG_COLD_LIGHT_RECOMMEND_PROGRESS, defColdLightProgress);
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

    private void setLightValue(int progress, int lightType) {
        if (isLongClickOpenAndCloseLight) {
            return;
        }
        if (mLightSteps.size() > 0) {
            int lightValue = mLightSteps.get(progress);
            String lightTag = lightType == TYPE_WARM_LIGHT ? WARM_LIGHT_VALUE : COLD_LIGHT_VALUE;
            Settings.System.putInt(getContext().getContentResolver(), NATURAL_LIGHT, lightType);
            Settings.System.putInt(this.getContext().getContentResolver(), lightTag, lightValue);
            FrontLightController.setNaturalBrightness(this.getContext(), lightValue);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cold_light) {
            mTotalProgress = defColdLightProgress;

        } else if (i == R.id.btn_warm_light) {
            mTotalProgress = defMixingLightProgress;

        } else if (i == R.id.imagebutton_light_switch) {
            if (mTotalProgress > 0) {
                mTotalProgress = 0;
                saveTotalProgress2Provider();
            } else {
                readTotalProgressFromProvider();
                if (mTotalProgress == 0) {
                    mTotalProgress = defMixingLightProgress;
                }
            }

        } else if (i == R.id.imagebutton_mixing_light_down) {
            mTotalProgress--;

        } else if (i == R.id.imagebutton_mixing_light_add) {
            mTotalProgress++;
        }

        updateRatingBarProgress();
    }

    private void readTotalProgressFromProvider() {
        int coldValue = getIndex(Settings.System.getInt(getContext().getContentResolver(), COLD_LIGHT_VALUE, 0));
        int warmValue = getIndex(Settings.System.getInt(getContext().getContentResolver(), WARM_LIGHT_VALUE, 0));
        calculateTotalProgress(coldValue, warmValue);
    }

    private void saveTotalProgress2Provider() {
        if (mTotalProgress > mMaxNumStars) {
            Settings.System.putInt(getContext().getContentResolver(), WARM_LIGHT_VALUE, mLightSteps.get(mTotalProgress - mMaxNumStars));
            Settings.System.putInt(getContext().getContentResolver(), COLD_LIGHT_VALUE, mLightSteps.get(mTotalProgress - mMaxNumStars));
        } else {
            Settings.System.putInt(getContext().getContentResolver(), WARM_LIGHT_VALUE, 0);
            Settings.System.putInt(getContext().getContentResolver(), COLD_LIGHT_VALUE, mLightSteps.get(mTotalProgress));
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        int i = ratingBar.getId();
        int progress = ratingBar.getProgress();
        if (i == R.id.ratingbar_cold_light_settings) {
            updateSwitchButtonStatus(progress);
            if (!isMixingOpen) {
                mTotalProgress = progress;
                setLightValue(0, TYPE_WARM_LIGHT);
                setLightValue(progress, TYPE_COLD_LIGHT);
            }

        } else if (i == R.id.ratingbar_mixing_light_settings) {
            if (progress > 0) {
                isMixingOpen = true;
            } else {
                isMixingOpen = false;
            }
            if (isMixingOpen) {
                mTotalProgress = progress + mMaxNumStars;
                setLightValue(progress, TYPE_COLD_LIGHT);
                setLightValue((int) (progress * BRIGHTNESS_RATIO), TYPE_WARM_LIGHT);
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
        readTotalProgressFromProvider();
        updateRatingBarProgress();
    }

    @Override
    public void cancel() {
        super.cancel();
        saveTotalProgress2Provider();
        if (mOpenAndCloseNaturalLightReceiver != null) {
            mContext.unregisterReceiver(mOpenAndCloseNaturalLightReceiver);
        }
    }

    private void updateRatingBarProgress() {
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
                    calculateTotalProgress(getIndex(cold_light), getIndex(warm_light));
                    updateRatingBarProgress();
                }
            }
        };
        filter = IntentFilterFactory.getOpenAndCloseFrontLightFilter();
        mContext.registerReceiver(mOpenAndCloseNaturalLightReceiver, filter);
    }

    private void calculateTotalProgress(int coldStep, int warmStep) {
        if (warmStep > 0) {
            mTotalProgress = warmStep + mMaxNumStars;
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

