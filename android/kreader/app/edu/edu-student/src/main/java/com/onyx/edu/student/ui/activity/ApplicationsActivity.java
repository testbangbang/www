package com.onyx.edu.student.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.AppDataInfo;
import com.onyx.android.sdk.data.request.data.fs.ApplicationListLoadRequest;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.ApplicationUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.student.R;
import com.onyx.edu.student.StudentApp;
import com.onyx.edu.student.databinding.ActivityApplicationsBinding;
import com.onyx.edu.student.databinding.ApplicationsItemBinding;
import com.onyx.edu.student.device.DeviceConfig;
import com.onyx.edu.student.manager.ConfigPreferenceManager;
import com.onyx.edu.student.receiver.AppStatusReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/2/16.
 */
public class ApplicationsActivity extends OnyxAppCompatActivity {

    private ActivityApplicationsBinding binding;
    PageRecyclerView contentPageView;

    private AppStatusReceiver appStatusReceiver;

    private List<AppDataInfo> appDataInfoList = new ArrayList<>();
    private boolean showCleanTestAppMenu = false;

    private int pageRowCount = 5;
    private int pageColCount = 4;

    private Integer getLayoutId() {
        return R.layout.activity_applications;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutId());

        initConfig();
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullUpdateView();
    }

    private void fullUpdateView() {
        if (DeviceConfig.sharedInstance(getApplicationContext()).enableFullRefresh()) {
            EpdController.invalidate(getWindow().getDecorView().getRootView(), UpdateMode.GC);
        }
    }


    private void initConfig() {
        initAppStatusReceiver();
    }

    private void initView() {
        initSupportActionBarWithCustomBackFunction();
        initContentPageView();
    }

    private void initContentPageView() {
        contentPageView = binding.contentPageView;
        contentPageView.setLayoutManager(new DisableScrollGridManager(this));
        contentPageView.setAdapter(new PageRecyclerView.PageAdapter<AppItemViewHolder>() {
            @Override
            public int getRowCount() {
                return pageRowCount;
            }

            @Override
            public int getColumnCount() {
                return pageColCount;
            }

            @Override
            public int getDataCount() {
                return CollectionUtils.getSize(appDataInfoList);
            }

            @Override
            public AppItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                ApplicationsItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.applications_item, parent, false);
                return new AppItemViewHolder(binding);
            }

            @Override
            public void onPageBindViewHolder(AppItemViewHolder holder, int position) {
                holder.itemView.setTag(position);
                holder.bind(appDataInfoList.get(position));
            }
        });
    }

    private void initData() {
        loadData();
    }

    private void loadData() {
        DeviceConfig config = DeviceConfig.sharedInstance(getApplicationContext());
        final ApplicationListLoadRequest listRequest = new ApplicationListLoadRequest(
                config.getAppFilters(), config.getTestApps(), getCustomizedIconApps());
        StudentApp.getLibraryDataHolder().getDataManager().submit(getApplicationContext(), listRequest,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        dismissProgressDialog(request);
                        if (e != null) {
                            e.printStackTrace();
                            return;
                        }
                        showCleanTestAppMenu = listRequest.isTestAppExist();
                        appDataInfoList = listRequest.getAppInfoList();
                        appDataInfoList.addAll(getExtraItemInfo());
                        notifyDataChanged();
                    }
                });
        showProgressDialog(listRequest, null);
    }

    private Map<String, String> getCustomizedIconApps() {
        Map<String, String> iconMaps = DeviceConfig.sharedInstance(this).getCustomizedIconApps();
        iconMaps.put("com.youngy.ui", "app_youngy");
        iconMaps.put("com.onyx.edu.reader", "app_statistics");
        iconMaps.put("com.onyx.unitconversion", "app_unit_conversion");
        return iconMaps;
    }

    private List<AppDataInfo> getExtraItemInfo() {
        List<AppDataInfo> list = new ArrayList<>();
        list.add(getSettingItemInfo());
        return list;
    }

    private AppDataInfo getSettingItemInfo() {
        AppDataInfo appDataInfo = new AppDataInfo();
        appDataInfo.intent = ConfigPreferenceManager.getSettingsIntent();
        appDataInfo.labelName = getString(R.string.settings);
        appDataInfo.iconDrawable = getResources().getDrawable(R.drawable.app_setting);
        return appDataInfo;
    }

    private void notifyDataChanged() {
        contentPageView.notifyDataSetChanged();
    }

    private void initAppStatusReceiver() {
        appStatusReceiver = new AppStatusReceiver();
        appStatusReceiver.setAppStateListener(new AppStatusReceiver.AppStateListener() {
            @Override
            public void onAppStateChanged(Intent intent) {
                loadData();
            }
        });
        appStatusReceiver.initReceiver(this, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.application_option_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            switch (menu.getItem(i).getItemId()) {
                case R.id.menu_clear_test_apps:
                    if (showCleanTestAppMenu) {
                        menu.getItem(i).setEnabled(true);
                    } else {
                        menu.getItem(i).setEnabled(false);
                    }
                    break;
                default:
                    menu.getItem(i).setEnabled(true);
                    break;
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear_test_apps:
                clearAllTestResource();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearAllTestResource() {
        ApplicationUtil.setSystemVerifyFlagDone(this, DeviceConfig.VERIFY_DICTIONARY_TAG);
        ApplicationUtil.setSystemVerifyFlagDone(this, DeviceConfig.VERIFY_BOOKS_TAG);
        clearAllTestApps();
        loadData();
    }

    private boolean clearAllTestApps() {
        return ApplicationUtil.clearAllTestApps(this, getDeviceConfig().getTestApps());
    }

    private DeviceConfig getDeviceConfig() {
        return DeviceConfig.sharedInstance(this);
    }

    private void processContentItemClick(int position) {
        ActivityUtil.startActivitySafely(this, appDataInfoList.get(position).intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearReceiverRegistered();
    }

    private void clearReceiverRegistered() {
        try {
            appStatusReceiver.disable(this);
        } catch (Exception e) {
        }
    }

    class AppItemViewHolder extends RecyclerView.ViewHolder {
        private final ApplicationsItemBinding binding;

        private AppItemViewHolder(ApplicationsItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processContentItemClick((Integer) itemView.getTag());
                }
            });
        }

        public void bind(AppDataInfo appInfo) {
            binding.imageViewIcon.setImageDrawable(appInfo.iconDrawable);
            binding.textViewTitle.setText(appInfo.labelName);
        }
    }
}
