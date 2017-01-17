package com.onyx.android.libsetting.view;

import android.content.Context;
import android.content.Intent;
import android.support.v14.preference.R.attr;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;

import com.onyx.android.libsetting.R;
import com.onyx.android.sdk.ui.view.CustomIconView;
import com.onyx.android.sdk.utils.ActivityUtil;

/**
 * Created by solskjaer49 on 2016/12/13 17:27.
 */

public class OnyxCustomIMEPreference extends Preference {
    public OnyxCustomIMEPreference setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    private Callback callback;
    private AppCompatCheckBox checkBox;

    public InputMethodInfo getImeInfo() {
        return imeInfo;
    }

    private InputMethodInfo imeInfo;
    private CustomIconView settingIcon;

    public interface Callback {
        void onCheckBoxReady();
    }

    public OnyxCustomIMEPreference(Context context) {
        this(context, null);
    }

    public OnyxCustomIMEPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OnyxCustomIMEPreference(Context context, AttributeSet attrs) {
        this(context, attrs, attr.preferenceStyle);
    }

    public OnyxCustomIMEPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.onyx_ime_preference_material);
        setWidgetLayoutResource(R.layout.onyx_custom_preference_setting_widget);
    }

    @Override
    public void onBindViewHolder(final PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        settingIcon = (CustomIconView) holder.findViewById(R.id.onyx_custom_setting_icon);
        checkBox = (AppCompatCheckBox) holder.findViewById(R.id.ime_enable_cb);
        holder.itemView.setClickable(false);
        holder.findViewById(R.id.title_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isEnabled()) {
                    checkBox.toggle();
                    holder.itemView.performClick();
                }
            }
        });
        settingIcon.setEnabled(!TextUtils.isEmpty(imeInfo.getSettingsActivity()));
        settingIcon.setClickable(!TextUtils.isEmpty(imeInfo.getSettingsActivity()));
        settingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                final String settingsActivity = imeInfo.getSettingsActivity();
                if (!TextUtils.isEmpty(settingsActivity)) {
                    intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName(imeInfo.getPackageName(), settingsActivity);
                }
                ActivityUtil.startActivitySafely(getContext(), intent);
            }
        });
        if (callback != null) {
            callback.onCheckBoxReady();
        }
    }

    public boolean isIMEChecked() {
        return checkBox != null && checkBox.isChecked();
    }

    public void setIMEChecked(boolean isChecked) {
        if (checkBox != null) {
            checkBox.setChecked(isChecked);
        }
    }

    public void setIMECheckedEnabled(boolean isEnabled) {
        checkBox.setEnabled(isEnabled);
    }

    public boolean isIMECheckedEnbaled() {
        return checkBox != null && checkBox.isEnabled();
    }

    public OnyxCustomIMEPreference setImeInfo(InputMethodInfo imeInfo) {
        this.imeInfo = imeInfo;
        return this;
    }
}
