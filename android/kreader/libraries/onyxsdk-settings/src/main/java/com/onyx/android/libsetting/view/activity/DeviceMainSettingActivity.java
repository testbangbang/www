package com.onyx.android.libsetting.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.libsetting.data.SettingCategory;
import com.onyx.android.libsetting.databinding.ActivityDeviceMainSettingBinding;
import com.onyx.android.libsetting.databinding.DeviceMainSettingsItemBinding;
import com.onyx.android.libsetting.model.ModelInfo;
import com.onyx.android.libsetting.model.SettingItem;
import com.onyx.android.libsetting.util.CommonUtil;
import com.onyx.android.libsetting.view.BindingViewHolder;
import com.onyx.android.libsetting.view.DeviceMainSettingItemDecoration;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DeviceMainSettingActivity extends OnyxAppCompatActivity {
    SettingConfig config;
    ActivityDeviceMainSettingBinding binding;
    ModelInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        updateData();
    }

    private void updateData() {
        info = new ModelInfo(Build.MODEL, Build.ID, Build.VERSION.RELEASE, R.drawable.device_logo);
        binding.setInfo(info);
        config = SettingConfig.sharedInstance(this);
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_main_setting);
        RecyclerView recyclerView = binding.functionRecyclerView;
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 6);
        final SettingFunctionAdapter adapter = new SettingFunctionAdapter(this, buildSettingItem());
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return calculateSpanSizeBySettingItemSize(position, adapter.getItemCount());
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAnimation(null);
        adapter.setItemClickListener(new SettingFunctionAdapter.ItemClickListener() {
            @Override
            public void itemClick(@SettingCategory.SettingCategoryDef int itemCategory) {
                Intent intent;
                switch (itemCategory) {
                    case SettingCategory.NETWORK:
                        intent = new Intent(DeviceMainSettingActivity.this, NetworkSettingActivity.class);
                        break;
                    case SettingCategory.SECURITY:
                        intent = new Intent(DeviceMainSettingActivity.this, SecuritySettingActivity.class);
                        break;
                    case SettingCategory.STORAGE:
                        intent = new Intent(DeviceMainSettingActivity.this, StorageSettingActivity.class);
                        break;
                    case SettingCategory.LANGUAGE_AND_INPUT:
                        intent = new Intent(DeviceMainSettingActivity.this, LanguageInputDateSettingActivity.class);
                        break;
                    case SettingCategory.DATE_TIME_SETTING:
                        intent = new Intent(DeviceMainSettingActivity.this, DateTimeSettingActivity.class);
                        break;
                    case SettingCategory.POWER:
                        intent = new Intent(DeviceMainSettingActivity.this, PowerSettingActivity.class);
                        break;
                    case SettingCategory.APPLICATION_MANAGEMENT:
                        intent = new Intent(DeviceMainSettingActivity.this, ApplicationSettingActivity.class);
                        break;
                    case SettingCategory.USER_SETTING:
                        intent = new Intent(DeviceMainSettingActivity.this, UserSettingActivity.class);
                        break;
                    default:
                        Toast.makeText(DeviceMainSettingActivity.this, "Under Construction", Toast.LENGTH_SHORT).show();
                        return;
                }
                startActivity(intent);
            }
        });
        recyclerView.addItemDecoration(new DeviceMainSettingItemDecoration());
        recyclerView.setAdapter(adapter);

        binding.icon.setClickable(false);
        binding.icon.setFocusable(false);
        binding.infoArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(config.getDeviceInfoIntent());
            }
        });
    }

    // TODO: 2016/11/30 temp max 3 line layout
    private int calculateSpanSizeBySettingItemSize(int position, int settingItemSize) {
        switch (settingItemSize) {
            case 4:
            case 6:
                return 3;
            case 5:
            case 7:
                return position < 3 ? 2 : 3;
            case 8:
                return position < 6 ? 2 : 3;
            case 9:
                return 2;
            default:
                return 1;
        }
    }

    // TODO: 2016/11/30 sample item list,will load these from config.
    private List<SettingItem> buildSettingItem() {
        ArrayList<SettingItem> itemArrayList = new ArrayList<>();
        itemArrayList.add(new SettingItem(SettingCategory.NETWORK, R.drawable.ic_setting_wlan, getString(R.string.setting_network)));
        itemArrayList.add(new SettingItem(SettingCategory.USER_SETTING, R.drawable.ic_user_setting, getString(R.string.setting_user_setting)));
        itemArrayList.add(new SettingItem(SettingCategory.POWER, R.drawable.ic_setting_power, getString(R.string.setting_power), ""));
        itemArrayList.add(new SettingItem(SettingCategory.LANGUAGE_AND_INPUT, R.drawable.ic_setting_language, getString(R.string.setting_lang_input)));
        itemArrayList.add(new SettingItem(SettingCategory.DATE_TIME_SETTING, R.drawable.ic_setting_date, getString(R.string.setting_date_time)));
        itemArrayList.add(new SettingItem(SettingCategory.APPLICATION_MANAGEMENT, R.drawable.ic_setting_application, getString(R.string.setting_application)));
        itemArrayList.add(new SettingItem(SettingCategory.SECURITY, R.drawable.ic_security, getString(R.string.setting_security)));
        itemArrayList.add(new SettingItem(SettingCategory.ERROR_REPORT, R.drawable.ic_error_report, getString(R.string.setting_error_report)));
        return itemArrayList;
    }

    static class SettingFunctionAdapter extends RecyclerView.Adapter<SettingFunctionAdapter.MainSettingItemViewHolder> {
        private List<SettingItem> dataList;
        private LayoutInflater layoutInflater;
        private Context context;
        private ViewGroup parent;
        private int rowCount = -1;
        private double parentHeight = -1;
        private int itemHeight = -1;

        SettingFunctionAdapter setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
            return this;
        }

        public SettingFunctionAdapter setItemLongClickListener(ItemLongClickClickListener itemLongClickListener) {
            this.itemLongClickListener = itemLongClickListener;
            return this;
        }

        private ItemClickListener itemClickListener;
        private ItemLongClickClickListener itemLongClickListener;

        SettingFunctionAdapter(Context cxt, List<SettingItem> list) {
            super();
            context = cxt;
            layoutInflater = LayoutInflater.from(context);
            dataList = new ArrayList<>();
            dataList.addAll(list);
            rowCount = dataList.size() < 6 ? 2 : 3;
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        @Override
        public MainSettingItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            DeviceMainSettingsItemBinding binding = DeviceMainSettingsItemBinding
                    .inflate(layoutInflater, viewGroup, false);
            parent = viewGroup;
            return new MainSettingItemViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final MainSettingItemViewHolder viewHolder, int position) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.itemClick(dataList.get(viewHolder.getAdapterPosition()).getItemCategory());
                    }
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemLongClickListener != null) {
                        itemLongClickListener.itemLongClick(dataList.get(viewHolder.getAdapterPosition()).getItemCategory());
                        return true;
                    }
                    return false;
                }
            });
            if (rowCount > 0) {
                parentHeight = parentHeight == -1 ? calculateParentHeight() : parentHeight;
                itemHeight = itemHeight == -1 ? (int) Math.floor((parentHeight) / getRowCount()) : itemHeight;
                if (itemHeight > 0) {
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
                    params.height = itemHeight;
                    viewHolder.itemView.setLayoutParams(params);
                }
            }
            viewHolder.bindTo(dataList.get(position));
        }

        private int calculateParentHeight() {
            return (int) (CommonUtil.getWindowHeight(context) * ((PercentRelativeLayout.LayoutParams)
                    parent.getLayoutParams()).getPercentLayoutInfo().heightPercent);
        }

        public int getRowCount() {
            return rowCount;
        }

        public SettingFunctionAdapter setRowCount(int rowCount) {
            this.rowCount = rowCount;
            return this;
        }

        class MainSettingItemViewHolder extends BindingViewHolder<DeviceMainSettingsItemBinding, SettingItem> {

            MainSettingItemViewHolder(DeviceMainSettingsItemBinding binding) {
                super(binding);
            }

            public void bindTo(SettingItem item) {
                mBinding.setItem(item);
                mBinding.executePendingBindings();
            }

        }

        interface ItemClickListener {
            void itemClick(@SettingCategory.SettingCategoryDef int itemCategory);
        }

        interface ItemLongClickClickListener {
            boolean itemLongClick(@SettingCategory.SettingCategoryDef int itemCategory);
        }
    }


}
