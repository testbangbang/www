package com.onyx.android.dr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.SystemLanguageInformation;
import com.onyx.android.dr.util.AppConfig;
import com.onyx.android.dr.util.SystemLanguage;
import com.onyx.android.dr.view.PageRecyclerView;

import java.util.List;
import java.util.Locale;


/**
 * Created by yangzhongping on 17-5-5.
 */
public class DeviceSettingLanguageSettingsAdapter extends PageRecyclerView.PageAdapter {
    private static final String TAG = DeviceSettingLanguageSettingsAdapter.class.getSimpleName();
    private int row = AppConfig.DeviceSettingInfo.DEVICE_SETTING_LANGUAGE_SETTINGS_PAGE_VIEW_ROW;
    private int column = AppConfig.DeviceSettingInfo.DEVICE_SETTING_LANGUAGE_SETTINGS_PAGE_VIEW_COLUMN;
    private SystemLanguageInformation languages;
    private Locale currentSystemLanguage;
    String[] data = DRApplication.getInstance().getResources().getStringArray(R.array.device_setting_language);
    String[] dataLocale = DRApplication.getInstance().getResources().getStringArray(R.array.device_setting_locale);
    private List<SystemLanguage.LocaleLanguageInfo> localeLanguageInfoList;

    public DeviceSettingLanguageSettingsAdapter(Context context) {
        row = AppConfig.sharedInstance(context).getDeviceSettingLanguageSettingsPageViewRow();
        column = AppConfig.sharedInstance(context).getDeviceSettingLanguageSettingsPageViewColumn();

        currentSystemLanguage = SystemLanguage.getCurrentLanguage(context);
    }

    public void setLanguages(SystemLanguageInformation languages) {
        this.languages = languages;
        localeLanguageInfoList = languages.localeLanguageInfoList;
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
        return data == null ? Constants.VALUE_ZERO : data.length;
    }

    @Override
    public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.language_settings_view_item, parent, false));
    }

    @Override
    public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        String bean = data[position];
        Holder view = (Holder) holder;
        view.tvLanguage.setText(bean);

        if (currentSystemLanguage.getCountry().equals(dataLocale[position])) {
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

        int position = (Integer) object;
        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemClick(v, position);
        }
    }

    public void setConfirm(boolean confirm, int position) {
        if (confirm) {
            String bean = data[position];
            for (SystemLanguage.LocaleLanguageInfo localeLanguageInfo : localeLanguageInfoList) {
                if (bean.equals(localeLanguageInfo.getLabel())) {
                    currentSystemLanguage = localeLanguageInfo.getLocale();
                    languages.currentLanguage = localeLanguageInfo.getLabel();
                }
            }
            notifyDataSetChanged();
        }
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
