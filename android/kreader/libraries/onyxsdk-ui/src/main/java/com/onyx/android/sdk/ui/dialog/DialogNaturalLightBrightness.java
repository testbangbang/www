package com.onyx.android.sdk.ui.dialog;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.api.device.FrontLightController;
import com.onyx.android.sdk.utils.IntentFilterFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DialogNaturalLightBrightness extends Dialog implements View.OnClickListener, RatingBar.OnRatingBarChangeListener{

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
    private boolean isRevertColdLightProgress = false;
    private int mMaxNumStars;
    private int mLightClosedValue;
    private List<Integer> mLightSteps = new ArrayList<Integer>();

    private BroadcastReceiver mOpenAndCloseNaturalLightReceiver = null;
    private IntentFilter filter = null;
    private Context mContext = null;
    private RatingBar mRatingBarNeturalLight = null;
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
        mRatingBarNeturalLight = (RatingBar) findViewById(R.id.ratingbar_netural_light_settings);
        mMixingLightDown = (ImageButton) findViewById(R.id.imagebutton_mixing_light_down);
        mMixingLightAdd = (ImageButton) findViewById(R.id.imagebutton_mixing_light_add);
        mRatingBarNeturalLight.setFocusable(false);
    }

    private void initData() {
        mLightSteps = FrontLightController.getNaturalLightValueList(getContext());
        if (mLightSteps != null) {
            mMaxNumStars = mLightSteps.size() - 1;
            mRatingBarNeturalLight.setNumStars(mMaxNumStars * 2);
            mRatingBarNeturalLight.setMax(mMaxNumStars * 2);
        } else {
            mMaxNumStars = mRatingBarNeturalLight.getNumStars();
            mLightSteps = initRangeArray(mMaxNumStars);
        }
        Collections.sort(mLightSteps);
        defMixingLightProgress = Settings.System.getInt(getContext().getContentResolver(),
                TAG_MIXING_LIGHT_RECOMMEND_PROGRESS, defMixingLightProgress) + mMaxNumStars;
        defColdLightProgress = Settings.System.getInt(getContext().getContentResolver(),
                TAG_COLD_LIGHT_RECOMMEND_PROGRESS, defColdLightProgress);
        mLightClosedValue = isRevertColdLightProgress ? mMaxNumStars : 0;
        defColdLightProgress = isRevertColdLightProgress ? (mMaxNumStars - defColdLightProgress) : defColdLightProgress;
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
        mMixingLightDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRatingBarNeturalLight.setProgress(mRatingBarNeturalLight.getProgress() - 1);
            }
        });
        mMixingLightAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRatingBarNeturalLight.setProgress(mRatingBarNeturalLight.getProgress() + 1);
            }
        });
        mRatingBarNeturalLight.setOnRatingBarChangeListener(this);
    }

    private void setLightValue(int progress, int lightType) {
        if (isLongClickOpenAndCloseLight) {
            return;
        }
        if (mLightSteps.size() > 0) {
            saveTotalProgress2Provider(mRatingBarNeturalLight.getProgress());
            int lightValue = mLightSteps.get(progress);
            Settings.System.putInt(getContext().getContentResolver(), NATURAL_LIGHT, lightType);
            FrontLightController.setNaturalBrightness(this.getContext(), lightValue);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        int progress = 0;
        if (i == R.id.btn_cold_light) {
            progress = defColdLightProgress;
        } else if (i == R.id.btn_warm_light) {
            progress = defMixingLightProgress;
        } else if (i == R.id.imagebutton_light_switch) {
            if (mRatingBarNeturalLight.getProgress() == mLightClosedValue) {
                progress = readTotalProgressFromProvider();
                if (progress == mLightClosedValue) {
                    progress = defMixingLightProgress;
                }
            } else {
                saveTotalProgress2Provider(mRatingBarNeturalLight.getProgress());
                progress = mLightClosedValue;
            }
        }
        mRatingBarNeturalLight.setProgress(progress);
        isMixingOpen = progress > mMaxNumStars ? true : false;
    }

    private int readTotalProgressFromProvider() {
        int coldValue = getIndex(Settings.System.getInt(getContext().getContentResolver(), COLD_LIGHT_VALUE, 0));
        int warmValue = getIndex(Settings.System.getInt(getContext().getContentResolver(), WARM_LIGHT_VALUE, 0));
        return calculateTotalProgress(coldValue, warmValue);
    }

    private void saveTotalProgress2Provider(int progress) {
        if (progress > mMaxNumStars) {
            Settings.System.putInt(getContext().getContentResolver(), COLD_LIGHT_VALUE, mLightSteps.get(progress - mMaxNumStars));
            Settings.System.putInt(getContext().getContentResolver(), WARM_LIGHT_VALUE, mLightSteps.get((int)((progress - mMaxNumStars) * BRIGHTNESS_RATIO)));
        } else if (progress != mLightClosedValue) {
            Settings.System.putInt(getContext().getContentResolver(), WARM_LIGHT_VALUE, 0);
            Settings.System.putInt(getContext().getContentResolver(), COLD_LIGHT_VALUE, mLightSteps.get(progress));
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        int progress = ratingBar.getProgress();
        updateSwitchButtonStatus(progress);
        int lightValue = 0;
        if (progress > mMaxNumStars) {
            isMixingOpen = true;
            lightValue = progress - mMaxNumStars;
        } else {
            isMixingOpen = false;
            lightValue = isRevertColdLightProgress ? (mMaxNumStars - progress) : progress;
        }
        updateLightValue(lightValue);
    }

    private void updateLightValue(int lightValue) {
        if (isMixingOpen) {
            setLightValue(lightValue, TYPE_COLD_LIGHT);
            setLightValue((int) (lightValue * BRIGHTNESS_RATIO), TYPE_WARM_LIGHT);
        } else {
            setLightValue(0, TYPE_WARM_LIGHT);
            setLightValue(lightValue, TYPE_COLD_LIGHT);
        }
        isLongClickOpenAndCloseLight = false;
    }

    private void updateSwitchButtonStatus(int progress) {
        if (progress == mLightClosedValue) {
            mBtnLightSwitch.setImageResource(R.drawable.ic_light_off);
        } else {
            mBtnLightSwitch.setImageResource(R.drawable.ic_light);
        }
    }

    private void setRatingBarDefaultProgress() {
        mRatingBarNeturalLight.setProgress(readTotalProgressFromProvider());
    }

    @Override
    public void cancel() {
        super.cancel();
        saveTotalProgress2Provider(mRatingBarNeturalLight.getProgress());
        if (mOpenAndCloseNaturalLightReceiver != null) {
            mContext.unregisterReceiver(mOpenAndCloseNaturalLightReceiver);
        }
    }

    private void initBroadcast() {
        mOpenAndCloseNaturalLightReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String action = intent.getAction();
                    int progress = mLightClosedValue;
                    if (IntentFilterFactory.ACTION_OPEN_FRONT_LIGHT.equals(action)) {
                        progress = readTotalProgressFromProvider();
                        if (progress == mLightClosedValue) {
                            progress = defMixingLightProgress;
                        }
                    } else if (IntentFilterFactory.ACTION_CLOSE_FRONT_LIGHT.equals(action)) {
                        saveTotalProgress2Provider(mRatingBarNeturalLight.getProgress());
                    }
                    isLongClickOpenAndCloseLight = true;
                    mRatingBarNeturalLight.setProgress(progress);
                }
            }
        };
        filter = IntentFilterFactory.getOpenAndCloseFrontLightFilter();
        mContext.registerReceiver(mOpenAndCloseNaturalLightReceiver, filter);
    }

    private int calculateTotalProgress(int coldStep, int warmStep) {
        int progress = mLightClosedValue;
        if (warmStep > 0) {
            progress = coldStep + mMaxNumStars;
        } else {
            progress = isRevertColdLightProgress ? (mMaxNumStars - coldStep) : coldStep;
        }
        return progress;
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

