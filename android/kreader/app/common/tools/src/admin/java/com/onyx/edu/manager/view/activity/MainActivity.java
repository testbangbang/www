package com.onyx.edu.manager.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.afollestad.materialdialogs.MaterialDialog;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.v2.AdministratorIndexServiceRequest;
import com.onyx.edu.manager.AppApplication;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.event.LoginSuccessEvent;
import com.onyx.edu.manager.manager.ContentManager;
import com.onyx.edu.manager.utils.DoubleClickExitHelper;
import com.onyx.edu.manager.view.dialog.DialogHolder;
import com.onyx.edu.manager.view.fragment.ApplyFragment;
import com.onyx.edu.manager.view.fragment.FuncSelectFragment;
import com.onyx.edu.manager.view.fragment.LoginFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 * Created by suicheng on 2017/6/17.
 */
public class MainActivity extends AppCompatActivity {

    private DoubleClickExitHelper clickExitHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initConfig();
        initData();
    }

    private void initConfig() {
        clickExitHelper = new DoubleClickExitHelper(this);
    }

    private void initData() {
        loadIndexService();
    }

    private void loadIndexService() {
        final MaterialDialog dialog = DialogHolder.showProgressDialog(this, "准备中...");
        final AdministratorIndexServiceRequest indexServiceRequest = new AdministratorIndexServiceRequest(
                Constant.CLOUD_MAIN_INDEX_SERVER_API,
                IndexService.createIndexService(this));
        AppApplication.getCloudManager().submitRequest(this, indexServiceRequest,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        dialog.dismiss();
                        IndexService cloudIndexService = indexServiceRequest.getResultIndexService();
                        if (e != null || !IndexService.hasValidServer(cloudIndexService)) {
                            processIndexException();
                            return;
                        }
                        if (indexServiceRequest.isIndexServiceHasChanged()) {
                            clearAccountInfo();
                        }
                        postLoadIndexService();
                    }
                });
    }

    private void processIndexException() {
        showFragment(ApplyFragment.newInstance());
    }

    private void postLoadIndexService() {
        if (hasAccount()) {
            showFragment(FuncSelectFragment.newInstance());
        } else {
            showFragment(LoginFragment.newInstance());
        }
    }

    private void clearAccountInfo() {
        ContentManager.saveAccount(this, null);
    }

    private boolean hasAccount() {
        NeoAccountBase neoAccount = ContentManager.getAccount(this);
        if (NeoAccountBase.isValid(neoAccount) && !neoAccount.isTokenTimeExpired()) {
            AppApplication.updateCloudManagerToken(neoAccount.token);
            return true;
        } else {
            return false;
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.id_content, fragment);
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return clickExitHelper.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
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
    public void onLoginSuccessEvent(LoginSuccessEvent event) {
        showFragment(FuncSelectFragment.newInstance());
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
