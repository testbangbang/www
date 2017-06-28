package com.onyx.android.libsetting.view;

import android.content.Context;
import android.support.v14.preference.R.attr;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Switch;

import com.onyx.android.libsetting.R;

/**
 * Created by solskjaer49 on 2016/12/13 17:27.
 */

public class OnyxCustomSwitchPreference extends Preference {
    public OnyxCustomSwitchPreference setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    private Callback callback;
    private Switch switchWidget;

    public interface Callback {
        void onSwitchClicked();

        void onSwitchReady();
    }

    public OnyxCustomSwitchPreference(Context context) {
        this(context, null);
    }

    public OnyxCustomSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OnyxCustomSwitchPreference(Context context, AttributeSet attrs) {
        this(context, attrs, attr.preferenceStyle);
    }

    public OnyxCustomSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.onyx_custom_preference_material);
        setWidgetLayoutResource(R.layout.onyx_custom_preference_switch_widget);
    }

    @Override
    public void onBindViewHolder(final PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        switchWidget = (Switch) holder.findViewById(R.id.onyx_custom_switch);
        holder.itemView.setClickable(false);
        holder.findViewById(R.id.title_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.performClick();
            }
        });
        switchWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onSwitchClicked();
                }
            }
        });
        if (callback!=null) {
            callback.onSwitchReady();
        }
    }

    public void setSwitchEnabled(boolean isEnabled) {
        if (switchWidget != null) {
            switchWidget.setEnabled(isEnabled);
        }
    }

    public void setSwitchChecked(boolean isChecked) {
        if (switchWidget != null) {
            switchWidget.setChecked(isChecked);
        }
    }
}
