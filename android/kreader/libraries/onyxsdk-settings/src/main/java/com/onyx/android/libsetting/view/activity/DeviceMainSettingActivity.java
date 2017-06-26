package com.onyx.android.libsetting.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.libsetting.data.SettingCategory;
import com.onyx.android.libsetting.databinding.ActivityDeviceMainSettingBinding;
import com.onyx.android.libsetting.databinding.DeviceMainSettingsItemBinding;
import com.onyx.android.libsetting.model.ModelInfo;
import com.onyx.android.libsetting.model.SettingItem;
import com.onyx.android.libsetting.util.CommonUtil;
import com.onyx.android.libsetting.util.QRCodeUtil;
import com.onyx.android.libsetting.view.BindingViewHolder;
import com.onyx.android.libsetting.view.DeviceMainSettingItemDecoration;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.ApplicationUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.onyx.android.libsetting.util.Constant.EDU_DEVICE_INFO_TRIGGER_VALUE;

public class DeviceMainSettingActivity extends OnyxAppCompatActivity {
    SettingConfig config;
    ActivityDeviceMainSettingBinding binding;
    ModelInfo info;
    SettingFunctionAdapter adapter;
    GridLayoutManager layoutManager;
    int eduDeviceInfoCount = 0;

    private static final float miniPercent = 0.20f;

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

        updateView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_main_setting);
        RecyclerView recyclerView = binding.functionRecyclerView;
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 12);
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
                Intent intent = SettingCategory.getConfigIntentByCategory(DeviceMainSettingActivity.this, itemCategory);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
        recyclerView.addItemDecoration(AppCompatUtils.isColorDevice(this) ?
                new DeviceMainSettingItemDecoration(this.getResources().getColor(android.R.color.black), 3) :
                new DeviceMainSettingItemDecoration());
        recyclerView.setAdapter(adapter);

        binding.icon.setClickable(false);
        binding.icon.setFocusable(false);
        binding.infoArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (config.isEnableSystemSettings()) {
                    startActivity(config.getDeviceInfoIntent());
                }
            }
        });

        binding.buttonOta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.startActivitySafely(DeviceMainSettingActivity.this,
                        new Intent(DeviceMainSettingActivity.this, FirmwareOTAActivity.class));
            }
        });

        binding.buttonCleanTestApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllTestApps();
                updateView();
            }
        });

        //TODO:custom view for normal/colorDevice;
        if (AppCompatUtils.isColorDevice(this)) {
            binding.infoArea.setBackground(getResources().getDrawable(R.drawable.main_setting_bg));
            try {
                binding.macQrCodeImageView.setImageBitmap(QRCodeUtil.getQRCodeCFABitmap(this));
                binding.deviceInfoEnterImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eduDeviceInfoCount++;
                        if (eduDeviceInfoCount > (EDU_DEVICE_INFO_TRIGGER_VALUE / 2)) {
                            if (eduDeviceInfoCount >= EDU_DEVICE_INFO_TRIGGER_VALUE) {
                                eduDeviceInfoCount = 0;
                                Intent intent = SettingCategory.getConfigIntentByCategory(DeviceMainSettingActivity.this, SettingCategory.DEVICE_INFO);
                                if (intent != null) {
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(DeviceMainSettingActivity.this, getString(R.string.still_need_times_to_activate,
                                        EDU_DEVICE_INFO_TRIGGER_VALUE - eduDeviceInfoCount), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            } catch (WriterException e) {
                e.printStackTrace();
            }
            binding.deviceDetailArea.setVisibility(View.GONE);
        } else {
            binding.infoArea.setBackground(getResources().getDrawable(R.drawable.image_button_bg));
            binding.macQrCodeLayout.setVisibility(View.GONE);
        }
    }

    private void updateView() {
        for (Iterator<SettingItem> iterator = adapter.dataList.iterator(); iterator.hasNext(); ) {
            switch (iterator.next().getItemCategory()) {
                case SettingCategory.PRODUCTION_TEST:
                    if (!verifyTestAppsRecord()) {
                        binding.buttonCleanTestApps.setVisibility(View.VISIBLE);
                    } else {
                        binding.buttonCleanTestApps.setVisibility(View.GONE);
                        iterator.remove();
                    }
                    break;
                case SettingCategory.FIRMWARE_UPDATE:
                    binding.buttonOta.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
        if (config.isForceUseSingleRow()) {
            layoutManager.setSpanCount(adapter.dataList.size());
        }
        adapter.notifyDataSetChanged();
        PercentRelativeLayout.LayoutParams params = (PercentRelativeLayout.LayoutParams) binding.infoArea.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo info = params.getPercentLayoutInfo();
        info.heightPercent = 1 - (miniPercent * adapter.getRowCount());
        binding.infoArea.requestLayout();
    }

    // TODO: 2016/11/30 temp max 3 line layout
    private int calculateSpanSizeBySettingItemSize(int position, int settingItemSize) {
        if (config.isForceUseSingleRow()){
            return 1;
        }
        switch (settingItemSize) {
            case 2:
            case 6:
                return 6;
            case 4:
                return 3;
            case 5:
            case 7:
                return position < 3 ? 4 : 6;
            case 8:
                return position < 6 ? 4 : 6;
            case 3:
            case 9:
                return 4;
            default:
                return 2;
        }
    }

    private boolean clearAllTestApps() {
        return ApplicationUtil.clearAllTestApps(this, config.getTestApps());
    }

    private boolean verifyTestAppsRecord() {
        List<String> testApps = config.getTestApps();
        for (String testApp : testApps) {
            if (!ApplicationUtil.testAppRecordExist(this, testApp)) {
                return false;
            }
        }
        return true;
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

            PercentRelativeLayout.LayoutParams params = (PercentRelativeLayout.LayoutParams) parent.getLayoutParams();
            PercentLayoutHelper.PercentLayoutInfo info = params.getPercentLayoutInfo();
            info.heightPercent = miniPercent * getRowCount();
            parent.requestLayout();
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
                parentHeight = parentHeight == -1 ? calculateParentHeight(getRowCount()) : parentHeight;
                itemHeight = itemHeight == -1 ? (int) Math.floor((parentHeight) / getRowCount()) : itemHeight;
                if (itemHeight > 0) {
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
                    params.height = itemHeight;
                    viewHolder.itemView.setLayoutParams(params);
                }
            }
            viewHolder.bindTo(dataList.get(position));
        }

        private int calculateParentHeight(int row) {
            return (int) (CommonUtil.getWindowHeight(context) * miniPercent * row);
        }

        public int getRowCount() {
            if (SettingConfig.sharedInstance(context).isForceUseSingleRow()) {
                return 1;
            }

            if (dataList.size() >= 6) {
                return 3;
            } else if (dataList.size() > 4) {
                return 2;
            } else {
                return 1;
            }
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
