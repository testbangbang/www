package com.onyx.edu.student.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.edu.student.R;
import com.onyx.edu.student.databinding.ActivityHomeBinding;
import com.onyx.edu.student.events.DataRefreshEvent;
import com.onyx.edu.student.model.MainNormalItem;
import com.onyx.edu.student.model.MainZoneViewModel;
import com.onyx.edu.student.utils.QRCodeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/10/20.
 */
public class HomeActivity extends OnyxAppCompatActivity {
    private static final String TAG = "HomeActivity";

    private ActivityHomeBinding binding;
    private EventBus eventBus = new EventBus();

    public final MainZoneViewModel zoneViewModel = new MainZoneViewModel(eventBus);
    public final List<MainNormalItem> normalItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void preBinding() {
        normalItemList.add(getMainNormalItem(R.string.home_item_material, R.drawable.home_teaching_material,
                new Intent(this, MaterialSeriesActivity.class)));
        normalItemList.add(getMainNormalItem(R.string.home_item_self_exam, R.drawable.home_self_exam, null));
        normalItemList.add(getMainNormalItem(R.string.home_item_homework, R.drawable.home_homework, null));
        normalItemList.add(getMainNormalItem(R.string.home_item_message, R.drawable.home_message, null));
        normalItemList.add(getMainNormalItem(R.string.home_item_record, R.drawable.home_class_record, null));
        normalItemList.add(getMainNormalItem(R.string.home_item_application, R.drawable.home_application,
                new Intent(this, ApplicationsActivity.class)));
    }

    private MainNormalItem getMainNormalItem(String title, int resId, Intent intent) {
        return new MainNormalItem(eventBus).setTitle(title).setResId(resId).setIntent(intent);
    }

    private MainNormalItem getMainNormalItem(int titleResId, int resId, Intent intent) {
        return getMainNormalItem(getString(titleResId), resId, intent);
    }

    private void initView() {
        preBinding();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.setViewModel(this);
        eventBus.register(this);
    }

    private void initData() {
        loadAccount();
    }

    private void loadAccount() {
        NeoAccountBase account = new NeoAccountBase();
        account.name = getString(R.string.test_user_name);
        account.orgName = getString(R.string.test_user_group);
        zoneViewModel.setAccount(account);
        zoneViewModel.updateAvatar(BitmapFactory.decodeResource(getResources(), R.drawable.user_girl_glass));
        zoneViewModel.updateQrCodeImage(loadQrImage());
    }

    private Bitmap loadQrImage() {
        try {
            File cacheFile = new File(QRCodeUtil.CFA_QR_CODE_FILE_PATH);
            if (FileUtils.fileExist(cacheFile.getAbsolutePath())) {
                return BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainItemEvent(MainNormalItem item) {
        if (item == null) {
            return;
        }
        boolean success = ActivityUtil.startActivitySafely(this, item.getIntent());
        if (!success) {
            ToastUtils.showToast(getApplicationContext(), R.string.no_support_yet);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataRefreshEvent(DataRefreshEvent event) {
    }
}
