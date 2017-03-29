package com.onyx.android.libsetting.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.onyx.android.sdk.utils.ActivityUtil;

import java.util.ArrayList;
import java.util.List;

public class DeviceMainSettingActivity extends OnyxAppCompatActivity {
    SettingConfig config;
    ActivityDeviceMainSettingBinding binding;
    ModelInfo info;
    SettingFunctionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();

    }

    private void updateData() {
        info = new ModelInfo(Build.MODEL, Build.ID, Build.VERSION.RELEASE, R.drawable.device_logo);
        binding.setInfo(info);
        config = SettingConfig.sharedInstance(this);
        adapter.dataList.clear();
        adapter.dataList.addAll(config.getSettingItemList(this));
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_main_setting);
        RecyclerView recyclerView = binding.functionRecyclerView;
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 6);
        adapter = new SettingFunctionAdapter(this, new ArrayList<SettingItem>());
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
                Intent intent = null;
                switch (itemCategory) {
                    case SettingCategory.NETWORK:
                        intent = new Intent(DeviceMainSettingActivity.this, NetworkSettingActivity.class);
                        break;
                    case SettingCategory.SECURITY:
                        intent = new Intent(DeviceMainSettingActivity.this, SecuritySettingActivity.class);
                        break;
                    case SettingCategory.STORAGE:
                        intent = config.getStorageSettingIntent(DeviceMainSettingActivity.this);
                        break;
                    case SettingCategory.LANGUAGE_AND_INPUT:
                        intent = new Intent(DeviceMainSettingActivity.this, LanguageInputSettingActivity.class);
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
                    case SettingCategory.ERROR_REPORT:
                        if (!TextUtils.isEmpty(config.getErrorReportAction())) {
                            intent = new Intent(config.getErrorReportAction());
                        }
                        break;
                    default:
                        Toast.makeText(DeviceMainSettingActivity.this, "Under Construction", Toast.LENGTH_SHORT).show();
                        return;
                }
                if (intent != null) {
                    startActivity(intent);
                }
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

        binding.buttonOta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.startActivitySafely(DeviceMainSettingActivity.this,
                        new Intent(DeviceMainSettingActivity.this, FirmwareOTAActivity.class));
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

    static class SettingFunctionAdapter extends RecyclerView.Adapter<SettingFunctionAdapter.MainSettingItemViewHolder> {
        private List<SettingItem> dataList;
        private LayoutInflater layoutInflater;
        private Context context;
        private ViewGroup parent;
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
            if (getRowCount() > 0) {
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
            return dataList.size() < 6 ? 2 : 3;
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
