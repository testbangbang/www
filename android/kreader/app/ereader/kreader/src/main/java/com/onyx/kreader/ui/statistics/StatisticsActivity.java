package com.onyx.kreader.ui.statistics;

import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.common.receiver.NetworkConnectChangedReceiver;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.StatisticsCloudManager;
import com.onyx.android.sdk.data.model.StatisticsResult;
import com.onyx.android.sdk.data.request.cloud.GetStatisticsRequest;
import com.onyx.android.sdk.data.request.cloud.PushStatisticsRequest;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.ui.view.OnyxCustomViewPager;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.kreader.R;
import com.onyx.kreader.device.DeviceConfig;
import com.onyx.kreader.ui.dialog.DialogLoading;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 2017/2/9.
 */

public class StatisticsActivity extends ActionBarActivity {

    private static final String TAG = "StatisticsActivity";

    @Bind(R.id.back_icon)
    ImageView backIcon;
    @Bind(R.id.pager)
    OnyxCustomViewPager pager;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.page)
    TextView page;

    private CloudStore cloudStore;
    private StatisticsCloudManager cloudManager;
    private DataStatisticsFragment dataStatisticsFragment;
    private ReadRecordFragment readRecordFragment;
    private DialogLoading dialogLoading;
    private NetworkConnectChangedReceiver networkConnectChangedReceiver;
    private int[] pageTitles = {R.string.data_analysis, R.string.reading_record};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ButterKnife.bind(this);
        initView();
        initData();
        registerReceiver();
        getStatistics();
        checkWifi();
    }

    private void initView() {
        pager.setPagingEnabled(false);
        pager.setUseGesturesPage(true);
        pager.setUseKeyPage(true);
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updatePageTitle();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void checkWifi() {
        if (Device.currentDevice().hasWifi(this) && !NetworkUtil.isWiFiConnected(this)) {
            OnyxCustomDialog.getConfirmDialog(this, getString(R.string.wifi_dialog_content), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NetworkUtil.enableWiFi(StatisticsActivity.this, true);
                }
            }, null).show();
        }
    }

    private void updatePageTitle() {
        int index = pager.getCurrentItem();
        int count = pager.getChildCount();
        String pagePosition = String.format("%d/%d", index + 1, count);
        page.setText(pagePosition);
        String pageTitle = getString(pageTitles[index]);
        String network = String.format("(%s)", NetworkUtil.isWiFiConnected(this) ? getString(R.string.network_data) : getString(R.string.local_data));
        String text = pageTitle + network;
        title.setText(text);
    }

    private void registerReceiver() {
        networkConnectChangedReceiver = new NetworkConnectChangedReceiver(new NetworkConnectChangedReceiver.NetworkChangedListener() {
            @Override
            public void onNetworkChanged(boolean connected, int networkType) {
                updatePageTitle();
                if (connected) {
                    pushStatistics();
                    getStatistics();
                }
            }

            @Override
            public void onNoNetwork() {
                getStatistics();
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkConnectChangedReceiver, filter);
    }

    private void pushStatistics() {
        final PushStatisticsRequest statisticsRequest = new PushStatisticsRequest(this, null, DeviceConfig.sharedInstance(this).getStatisticsUrl());
        getCloudManager().submitRequest(this, statisticsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }

    private void getStatistics() {
        dialogLoading = getDialogLoading();
        dialogLoading.show();
        final GetStatisticsRequest statisticsRequest = new GetStatisticsRequest(this, DeviceConfig.sharedInstance(this).getStatisticsUrl());
        getCloudStore().submitRequest(this, statisticsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (StatisticsActivity.this.isFinishing()) {
                    return;
                }
                dismissDialog();
                if (e != null) {
                    Toast.makeText(StatisticsActivity.this, R.string.load_statistical_data_failed, Toast.LENGTH_SHORT).show();
                    return;
                }
                StatisticsResult statisticsResult = statisticsRequest.getStatisticsResult();
                dataStatisticsFragment.setStatisticsResult(statisticsResult);
                readRecordFragment.setStatisticsResult(statisticsResult);
            }
        });
    }

    private DialogLoading getDialogLoading() {
        if (dialogLoading == null) {
            dialogLoading = new DialogLoading(this,
                    getString(R.string.loading), false, null);
        }
        return dialogLoading;
    }

    private CloudStore getCloudStore() {
        if (cloudStore == null) {
            cloudStore = new CloudStore();
        }
        return cloudStore;
    }

    private StatisticsCloudManager getCloudManager() {
        if (cloudManager == null) {
            cloudManager = new StatisticsCloudManager();
        }
        return cloudManager;
    }

    private void initData() {
        dataStatisticsFragment = DataStatisticsFragment.newInstance();
        readRecordFragment = ReadRecordFragment.newInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        dismissDialog();
        super.onDestroy();
        if (networkConnectChangedReceiver != null) {
            unregisterReceiver(networkConnectChangedReceiver);
        }
        ButterKnife.unbind(this);
    }

    private void dismissDialog() {
        if (dialogLoading != null && dialogLoading.isShowing()) {
            dialogLoading.dismiss();
            dialogLoading = null;
        }
    }

    private class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = null;
            switch (position) {
                case 0:
                    f = dataStatisticsFragment;
                    break;
                case 1:
                    f = readRecordFragment;
            }
            updatePageTitle();
            return f;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
