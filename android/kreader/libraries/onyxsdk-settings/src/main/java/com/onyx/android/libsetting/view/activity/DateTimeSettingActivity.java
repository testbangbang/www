package com.onyx.android.libsetting.view.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.libsetting.databinding.ActivityDateTimeSettingBinding;
import com.onyx.android.libsetting.util.DateTimeSettingUtil;
import com.onyx.android.libsetting.util.DeviceFeatureUtil;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.utils.ActivityUtil;

import java.util.Calendar;

import static android.content.Intent.ACTION_TIMEZONE_CHANGED;
import static android.content.Intent.ACTION_TIME_CHANGED;
import static android.content.Intent.ACTION_TIME_TICK;

public class DateTimeSettingActivity extends OnyxAppCompatActivity {
    ActivityDateTimeSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_date_time_setting);
        initSupportActionBarWithCustomBackFunction();
        getSupportFragmentManager().beginTransaction().replace(R.id.date_preference,
                new DateTimeSettingPreferenceFragment()).commit();
    }

    public static class DateTimeSettingPreferenceFragment extends PreferenceFragmentCompat implements
            TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
        private Calendar dummyDate;
        CheckBoxPreference autoDateTimePreference;
        CheckBoxPreference is24HourFormatPreference;
        Preference dateConfig, timeConfig, timeZoneConfig;
        IntentFilter filter;
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case ACTION_TIME_TICK:
                    case ACTION_TIME_CHANGED:
                    case ACTION_TIMEZONE_CHANGED:
                        updateTimeAndDateDisplay();
                        break;
                }
            }
        };

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.date_time_setting);
            initView();
            hidePreferenceByDeviceFeature();
        }

        private void initView() {
            autoDateTimePreference = (CheckBoxPreference) findPreference(getString(R.string.auto_time_setting_key));
            is24HourFormatPreference = (CheckBoxPreference) findPreference(getString(R.string.set_time_twenty_four_key));
            timeConfig = findPreference(getString(R.string.time_config_key));
            dateConfig = findPreference(getString(R.string.date_config_key));
            timeZoneConfig = findPreference(getString(R.string.set_time_zone_key));
            autoDateTimePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    DateTimeSettingUtil.setAutoTimeEnabled(getContext(), (Boolean) newValue);
                    return true;
                }
            });
            is24HourFormatPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    DateTimeSettingUtil.set24HourEnabled(getContext(), (Boolean) newValue);
                    return true;
                }
            });
            timeZoneConfig.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ActivityUtil.startActivitySafely(getActivity(),
                            SettingConfig.sharedInstance(getContext()).getTimeZoneSettingIntent());
                    return true;
                }
            });
            dateConfig.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Calendar calendar = Calendar.getInstance();
                    Dialog d = new DatePickerDialog(getContext(), DateTimeSettingPreferenceFragment.this,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                    d.show();
                    return true;
                }
            });
            timeConfig.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Calendar calendar = Calendar.getInstance();
                    Dialog d = new TimePickerDialog(getContext(), DateTimeSettingPreferenceFragment.this,
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            DateFormat.is24HourFormat(getContext()));
                    d.show();
                    return true;
                }
            });
        }

        private void updateData() {
            dummyDate = Calendar.getInstance();
            autoDateTimePreference.setChecked(DateTimeSettingUtil.isAutoTimeEnabled(getContext()));
            is24HourFormatPreference.setChecked(DateTimeSettingUtil.is24HourEnabled(getContext()));
        }

        @Override
        public void onResume() {
            super.onResume();
            onPostResume();
        }

        private void onPostResume() {
            filter = new IntentFilter(ACTION_TIME_CHANGED);
            filter.addAction(ACTION_TIME_TICK);
            filter.addAction(ACTION_TIMEZONE_CHANGED);
            updateData();
            getContext().registerReceiver(receiver, filter);
            updateTimeAndDateDisplay();
        }

        @Override
        public void onPause() {
            super.onPause();
            getContext().unregisterReceiver(receiver);
        }

        private void hidePreferenceByDeviceFeature() {
            if (!DeviceFeatureUtil.hasWifi(getContext())) {
                autoDateTimePreference.setChecked(false);
                autoDateTimePreference.setVisible(false);
            }
        }

        public void updateTimeAndDateDisplay() {
            java.text.DateFormat shortDateFormat = DateFormat.getDateFormat(getContext());
            final Calendar now = Calendar.getInstance();
            dummyDate.setTimeZone(now.getTimeZone());
            dummyDate.set(now.get(Calendar.YEAR), 11, 31, 13, 0, 0);
            String nowTime = DateFormat.getTimeFormat(getContext()).format(now.getTime());
            timeConfig.setSummary(nowTime);
            timeZoneConfig.setSummary(DateTimeSettingUtil.getTimeZoneText(now.getTimeZone()));
            String nowDate = shortDateFormat.format(now.getTime());
            dateConfig.setSummary(nowDate);
            ((DateTimeSettingActivity) getActivity()).binding.currentTime.setText(nowDate + " " + nowTime);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            long when = c.getTimeInMillis();
            if (when / 1000 < Integer.MAX_VALUE) {
                DateTimeSettingUtil.changeSystemTime(getContext(), when);
            }
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            long when = c.getTimeInMillis();
            if (when / 1000 < Integer.MAX_VALUE) {
                DateTimeSettingUtil.changeSystemTime(getContext(), when);
            }
        }
    }


}
