package com.onyx.android.libsetting.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import android.widget.SeekBar;

import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.databinding.ActivitySoundSettingBinding;
import com.onyx.android.libsetting.manager.AudioAdmin;

public class SoundSettingActivity extends OnyxAppCompatActivity {
    static final String TAG = SoundSettingActivity.class.getSimpleName();
    AudioAdmin audioAdmin;
    SeekBar volumeControlSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioAdmin = new AudioAdmin(this, new AudioAdmin.Callback() {
            @Override
            public void onVolumeChanged(int newVolume) {
                volumeControlSeekBar.setProgress(newVolume);
            }
        });
        initView();
    }

    private void initView() {
        ActivitySoundSettingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sound_setting);
        initSupportActionBarWithCustomBackFunction();
        volumeControlSeekBar = binding.volumeSeekBar;
        volumeControlSeekBar.setMax(audioAdmin.getStreamMaxVolume());
        volumeControlSeekBar.setProgress(audioAdmin.getStreamVolume());
        volumeControlSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioAdmin.setStreamVolume(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.buttonMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioAdmin.setStreamVolume(0);
            }
        });
        binding.buttonMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioAdmin.setStreamVolume(volumeControlSeekBar.getMax());
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.sound_preference,
                new SoundSettingActivity.SoundSettingPreferenceFragment()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        audioAdmin.registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        audioAdmin.unregisterReceiver();
    }

    public static class SoundSettingPreferenceFragment extends PreferenceFragmentCompat {
        CheckBoxPreference touchSoundPreference;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.sound_setting);
            touchSoundPreference = (CheckBoxPreference) findPreference(getString(R.string.touch_sound_key));
            touchSoundPreference.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.SOUND_EFFECTS_ENABLED, 1) != 0);
            touchSoundPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    ((SoundSettingActivity) getActivity()).audioAdmin.setSoundEffectEnabled((Boolean) newValue);
                    return true;
                }
            });
        }
    }
}
