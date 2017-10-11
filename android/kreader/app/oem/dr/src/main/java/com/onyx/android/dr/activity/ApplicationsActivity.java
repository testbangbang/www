package com.onyx.android.dr.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.CustomAppInfo;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.AppDataInfo;
import com.onyx.android.sdk.data.request.data.fs.ApplicationListLoadRequest;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.ApplicationUtil;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2017/2/16.
 */
public class ApplicationsActivity extends BaseActivity {

    @Bind(R.id.page_view)
    PageRecyclerView contentPageView;
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.title_bar_right_menu)
    TextView titleBarRightMenu;

    private BroadcastReceiver appStatusReceiver;
    private IntentFilter appStatusFilter;

    private List<AppDataInfo> appDataInfoList = new ArrayList<>();
    private boolean showCleanTestAppMenu = false;
    private List<CustomAppInfo> customAppInfoList = new ArrayList<CustomAppInfo>() {
        {
            add(new CustomAppInfo(GroupHomePageActivity.class, R.string.group, R.drawable.ic_reader_group));
            add(new CustomAppInfo(SettingActivity.class, R.string.menu_settings, R.drawable.ic_settings));
            add(new CustomAppInfo(APPMarketActivity.class, R.string.app_market, R.drawable.market_logo));
            add(new CustomAppInfo(EBookStoreActivity.class, R.string.ebook_store, R.drawable.ic_reader_top_bookstore));
        }
    };

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_applications;
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullUpdateView();
    }

    private void fullUpdateView() {
        EpdController.postInvalidate(getWindow().getDecorView().getRootView(), UpdateMode.GC);
    }

    @Override
    protected void initConfig() {
        initAppStatusReceiver();
    }

    @Override
    protected void initView() {
        initSupportActionBarWithCustomBackFunction();
        initContentPageView();
        image.setImageResource(R.drawable.ic_application);
        titleBarTitle.setText(getString(R.string.application_list));
    }

    private void initContentPageView() {
        contentPageView.setLayoutManager(new DisableScrollGridManager(this));
        contentPageView.setAdapter(new PageRecyclerView.PageAdapter<AppItemViewHolder>() {
            @Override
            public int getRowCount() {
                return getResources().getInteger(R.integer.applications_activity_row);
            }

            @Override
            public int getColumnCount() {
                return getResources().getInteger(R.integer.applications_activity_col);
            }

            @Override
            public int getDataCount() {
                return CollectionUtils.getSize(appDataInfoList);
            }

            @Override
            public AppItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new AppItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.applications_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(AppItemViewHolder holder, int position) {
                holder.itemView.setTag(position);

                AppDataInfo appInfo = appDataInfoList.get(position);
                holder.iconImageView.setImageDrawable(appInfo.iconDrawable);
                holder.titleView.setText(appInfo.labelName);
            }
        });
    }

    @Override
    protected void initData() {
        loadData();
    }

    private void loadData() {
        final ApplicationListLoadRequest listRequest = new ApplicationListLoadRequest(DeviceConfig.sharedInstance(this).getAppFilters(),
                DeviceConfig.sharedInstance(this).getTestApps(),
                getCustomizedIconApps());
        DRApplication.getDataManager().submit(this, listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dismissProgressDialog(request);
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                showCleanTestAppMenu = listRequest.isTestAppExist();
                appDataInfoList = listRequest.getAppInfoList();
                addCustomIcon(appDataInfoList, customAppInfoList);
                notifyDataChanged();
            }
        });
        showProgressDialog(listRequest, null);
    }

    private void addCustomIcon(List<AppDataInfo> appDataInfoList, List<CustomAppInfo> customAppInfoList) {
        for (CustomAppInfo customAppInfo : customAppInfoList) {
            AppDataInfo appDataInfo = new AppDataInfo();
            appDataInfo.activityClassName = customAppInfo.getActivityClass().getName();
            appDataInfo.iconDrawable = getResources().getDrawable(customAppInfo.getResource());
            appDataInfo.intent = new Intent(this, customAppInfo.getActivityClass());
            appDataInfo.labelName = getString(customAppInfo.getLabelName());
            appDataInfo.isSystemApp = false;
            appDataInfoList.add(appDataInfo);
        }
    }

    private Map<String, String> getCustomizedIconApps() {
        Map<String, String> iconMaps = DeviceConfig.sharedInstance(this).getCustomizedIconApps();
        iconMaps.put("com.youngy.ui", "app_youngy");
        return iconMaps;
    }

    private void notifyDataChanged() {
        contentPageView.notifyDataSetChanged();
    }

    private void initAppStatusReceiver() {
        appStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_REPLACED) ||
                        action.equals(Intent.ACTION_PACKAGE_REMOVED) || action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)) {
                    loadData();
                }
            }
        };
        appStatusFilter = new IntentFilter();
        appStatusFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        appStatusFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        appStatusFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        appStatusFilter.addDataScheme("package");
        if (Build.VERSION.SDK_INT >= 14) {
            appStatusFilter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        }
        this.registerReceiver(appStatusReceiver, appStatusFilter);
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
            if (appStatusReceiver != null) {
                unregisterReceiver(appStatusReceiver);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.menu_back)
    public void onClick() {
        finish();
    }

    class AppItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imageView_icon)
        ImageView iconImageView;
        @Bind(R.id.textView_title)
        TextView titleView;

        public AppItemViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processContentItemClick((Integer) itemView.getTag());
                }
            });
            ButterKnife.bind(this, itemView);
        }
    }
}
