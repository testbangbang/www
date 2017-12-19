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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

import com.onyx.android.sdk.api.device.FrontLightController;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.utils.IntentFilterFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DialogNaturalLightBrightness extends Dialog implements View.OnLongClickListener, View.OnClickListener {

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
    private String  OPTION_1_WARM_LIGHT = "option_1_warm_light";
    private String  OPTION_1_COLD_LIGHT = "option_1_cold_light";
    private String  OPTION_2_WARM_LIGHT = "option_2_warm_light";
    private String  OPTION_2_COLD_LIGHT = "option_2_cold_light";
    private String  OPTION_3_WARM_LIGHT = "option_3_warm_light";
    private String  OPTION_3_COLD_LIGHT = "option_3_cold_light";

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
        super(context,R.style.CustomDialog);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLightSteps = FrontLightController.getNaturalLightValueList(getContext());
        int targetLayoutID = R.layout.dialog_natural_brightness_step;
        setContentView(targetLayoutID);
        mRatingBarWarmLightSettings = (RatingBar) findViewById(R.id.ratingbar_warm_light_settings);
        mRatingBarColdLightSettings = (RatingBar) findViewById(R.id.ratingbar_cold_light_settings);
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
        ImageButton mWarmLightDown = (ImageButton) findViewById(R.id.imagebutton_warm_light_down);
        mWarmLightDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRatingBarWarmLightSettings.setProgress(mRatingBarWarmLightSettings.getProgress() - 1);
            }
        });

        ImageButton mWarmLightAdd = (ImageButton) findViewById(R.id.imagebutton_warm_light_add);
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
        ImageButton mColdLightDown = (ImageButton) findViewById(R.id.imagebutton_cold_light_down);
        mColdLightDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRatingBarColdLightSettings.setProgress(mRatingBarColdLightSettings.getProgress() - 1);
            }
        });

        ImageButton mColdLightAdd = (ImageButton) findViewById(R.id.imagebutton_cold_light_add);
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

        Button mOption1 = (Button) findViewById(R.id.button_option1);
        mOption1.setOnLongClickListener(this);
        mOption1.setOnClickListener(this);

        Button mOption2 = (Button) findViewById(R.id.button_option2);
        mOption2.setOnLongClickListener(this);
        mOption2.setOnClickListener(this);

        Button mOption3 = (Button) findViewById(R.id.button_option3);
        mOption3.setOnLongClickListener(this);
        mOption3.setOnClickListener(this);
    }

    private void saveNaturalLightProgress(Context context, String optionWarm, String optionCold) {
        int optionWarmLight = mRatingBarWarmLightSettings.getProgress();
        int optionColdLight = mRatingBarColdLightSettings.getProgress();
        putSettingsSystemInt(context, optionWarm, optionWarmLight);
        putSettingsSystemInt(context, optionCold, optionColdLight);
        Toast.makeText(context, R.string.save_success, Toast.LENGTH_LONG).show();
    }

    private void changeNaturalLightProgress(Context context, String optionWarm, String optionCold) {
        if (OPTION_1_WARM_LIGHT.equals(optionWarm) && OPTION_1_COLD_LIGHT.equals(optionCold)) {
            setNaturalLightProgress(context, optionWarm, optionCold, 3, 0);
        }
        if (OPTION_2_WARM_LIGHT.equals(optionWarm) && OPTION_2_COLD_LIGHT.equals(optionCold)) {
            setNaturalLightProgress(context, optionWarm, optionCold, 6, 12);
        }
        if (OPTION_3_WARM_LIGHT.equals(optionWarm) && OPTION_3_COLD_LIGHT.equals(optionCold)) {
            setNaturalLightProgress(context, optionWarm, optionCold, 15, 17);
        }
    }

    private void setNaturalLightProgress(Context context, String optionWarm, String optionCold,
                                         int warmLightDefaultValue, int coldLightDefaultValue) {
        mRatingBarWarmLightSettings.setProgress(getSettingsSystemInt(context, optionWarm, warmLightDefaultValue));
        mRatingBarColdLightSettings.setProgress(getSettingsSystemInt(context, optionCold, coldLightDefaultValue));
    }

    private void changeLightState() {
        if (!isLongClickOpenAndCloseWarmLight && !isLongClickOpenAndCloseColdLight) {
            setFrontLightValue();
        }
    }

    private void setLightRatingBarDefaultProgress() {
        int coldValue = FrontLightController.getColdLightDeviceValue(getContext());
        mRatingBarColdLightSettings.setProgress(getIndex(coldValue));
        int warmValue = FrontLightController.getWarmLightDeviceValue(getContext());
        mRatingBarWarmLightSettings.setProgress(getIndex(warmValue));
    }

    private void setFrontLightValue() {
        if (!(mLightSteps.size() > 0)) {
            return;
        }
        int value = 0;
        if (isWarmLightChange) {
            value = mLightSteps.get(mRatingBarWarmLightSettings.getProgress());
            putSettingsSystemInt(getContext(), WARM_LIGHT_VALUE, value);
        } else {
            value = mLightSteps.get(mRatingBarColdLightSettings.getProgress());
            putSettingsSystemInt(getContext(), COLD_LIGHT_VALUE, value);
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

    public int getSettingsSystemInt(Context context, String lightKey, int defaultValue) {
        return Settings.System.getInt(context.getContentResolver(), lightKey, defaultValue);
    }

    public void putSettingsSystemInt(Context context, String lightKey, int lightValue) {
        Settings.System.putInt(context.getContentResolver(), lightKey, lightValue);
    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();
        if(id == R.id.button_option1){
            saveNaturalLightProgress(mContext, OPTION_1_WARM_LIGHT, OPTION_1_COLD_LIGHT);
        }else if(id == R.id.button_option2){
            saveNaturalLightProgress(mContext, OPTION_2_WARM_LIGHT, OPTION_2_COLD_LIGHT);
        }else if (id == R.id.button_option3){
            saveNaturalLightProgress(mContext, OPTION_3_WARM_LIGHT, OPTION_3_COLD_LIGHT);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.button_option1){
            changeNaturalLightProgress(mContext, OPTION_1_WARM_LIGHT, OPTION_1_COLD_LIGHT);
        }else if(id == R.id.button_option2){
            changeNaturalLightProgress(mContext, OPTION_2_WARM_LIGHT, OPTION_2_COLD_LIGHT);
        }else if (id == R.id.button_option3){
            changeNaturalLightProgress(mContext, OPTION_3_WARM_LIGHT, OPTION_3_COLD_LIGHT);
        }
    }
}

