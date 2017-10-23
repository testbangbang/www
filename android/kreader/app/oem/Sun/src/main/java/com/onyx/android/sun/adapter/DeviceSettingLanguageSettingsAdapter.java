package com.onyx.android.sun.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.onyx.android.sun.R;
import com.onyx.android.sun.devicesetting.AppConfig;
import com.onyx.android.sun.devicesetting.SystemLanguage;
import com.onyx.android.sun.devicesetting.SystemLanguageInformation;
import com.onyx.android.sun.view.PageRecyclerView;

import java.util.Locale;

/**
 * Created by huxiaomao on 2016/12/13.
 */

public class DeviceSettingLanguageSettingsAdapter extends PageRecyclerView.PageAdapter {
    private static final String TAG = DeviceSettingLanguageSettingsAdapter.class.getSimpleName();
    private int row = AppConfig.DeviceSettingInfo.DEVICE_SETTING_LANGUAGE_SETTINGS_PAGE_VIEW_ROW;
    private int column = AppConfig.DeviceSettingInfo.DEVICE_SETTING_LANGUAGE_SETTINGS_PAGE_VIEW_COLUMN;
    private SystemLanguageInformation languages;
    private Locale currentSystemLanguage;

    public DeviceSettingLanguageSettingsAdapter(Context context) {
        row = AppConfig.sharedInstance(context).getDeviceSettingLanguageSettingsPageViewRow();
        column = AppConfig.sharedInstance(context).getDeviceSettingLanguageSettingsPageViewColumn();

        currentSystemLanguage = SystemLanguage.getCurrentLanguage(context);
    }

    public void setLanguages(SystemLanguageInformation languages) {
        this.languages = languages;
    }

    public Locale getLocale() {
        return currentSystemLanguage;
    }

    @Override
    public int getRowCount() {
        return row;
    }

    @Override
    public int getColumnCount() {
        return column;
    }

    @Override
    public int getDataCount() {
        if (languages == null) {
            return 0;
        } else {
            return languages.localeLanguageInfoList.size();
        }
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.language_settings_view_item, parent, false));
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        SystemLanguage.LocaleLanguageInfo localeLanguageInfo = languages.localeLanguageInfoList.get(position);
        Holder view = (Holder) holder;
        view.tvLanguage.setText(localeLanguageInfo.getLabel());
        if (currentSystemLanguage.getCountry().equals(localeLanguageInfo.getLocale().getCountry())) {
            view.ivCheck.setVisibility(View.VISIBLE);
        } else {
            view.ivCheck.setVisibility(View.GONE);
        }
        view.itemView.setTag(position);
    }

    @Override
    public void setOnItemClick(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        super.setOnItemClick(onRecyclerViewItemClickListener);
    }

    @Override
    public void onClick(View v) {
        Object object = v.getTag();
        if (object == null) {
            return;
        }
        int position = (Integer)object;
        SystemLanguage.LocaleLanguageInfo localeLanguageInfo = languages.localeLanguageInfoList.get(position);
        currentSystemLanguage = localeLanguageInfo.getLocale();
        languages.currentLanguage = localeLanguageInfo.getLabel();
        notifyDataSetChanged();
    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView tvLanguage;
        ImageView ivCheck;

        public Holder(View view) {
            super(view);
            tvLanguage = (TextView) view.findViewById(R.id.language_settings_language);
            ivCheck = (ImageView) view.findViewById(R.id.language_settings_check);
            view.setOnClickListener(DeviceSettingLanguageSettingsAdapter.this);
        }
    }
}
