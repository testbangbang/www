package com.onyx.android.dr.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.HideLoadingProgressEvent;
import com.onyx.android.dr.event.StartDownloadingEvent;
import com.onyx.android.dr.fragment.BaseFragment;
import com.onyx.android.dr.fragment.BookshelfFragment;
import com.onyx.android.dr.fragment.DeviceSettingFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by zhouzhiming on 2017/8/16.
 */
public class SettingActivity extends BaseActivity {
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    private DeviceSettingFragment deviceSettingFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int tag = -1;
    private Map<Integer, BaseFragment> childViewList = new HashMap<Integer, BaseFragment>();
    private int currentPageID = -1;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        getIntentData();
        initTitleData();
    }

    private void getIntentData() {
        tag = Constants.DEVICE_SETTING_FRAGMENT;
        startFragment(tag);
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.ic_settings);
        title.setText(getString(R.string.menu_settings));
    }

    private void startFragment(int tag) {
        if (tag == Constants.DEVICE_SETTING_FRAGMENT) {
            if (deviceSettingFragment == null) {
                deviceSettingFragment = new DeviceSettingFragment();
                fragmentTransaction.add(R.id.fragment_container, deviceSettingFragment);
                childViewList.put(tag, deviceSettingFragment);
            } else {
                fragmentTransaction.show(deviceSettingFragment);
            }
        }
        currentPageID = tag;
        fragmentTransaction.commit();
    }

    @OnClick({R.id.image_view_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                onBackClick();
                break;
        }
    }

    private void onBackClick() {
        if (deviceSettingFragment != null) {
            boolean tag = deviceSettingFragment.onKeyBack();
            if (!tag) {
                finish();
            }
        }
    }

    private BaseFragment getPageView(int pageID) {
        BaseFragment baseFragment = childViewList.get(pageID);
        if (baseFragment == null) {
            switch (pageID) {
                case Constants.DEVICE_SETTING_FRAGMENT:
                    baseFragment = new BookshelfFragment();
                    break;
            }
        } else {
            baseFragment.isStored = true;
        }
        return baseFragment;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        BaseFragment currentFragment = getPageView(currentPageID);
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (currentFragment != null && currentFragment.onKeyBack()) {
                return true;
            }
        }
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_PAGE_UP) {
            if (currentFragment != null && currentFragment.onKeyPageUp()) {
                return true;
            }
        }
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_PAGE_DOWN) {
            if (currentFragment != null && currentFragment.onKeyPageDown()) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartDownloadingEvent(StartDownloadingEvent event) {
        showProgressDialog(null, R.string.downloading, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideLoadingProgressEvent(HideLoadingProgressEvent event) {
        dismissAllProgressDialog();
    }
}
