package com.onyx.edu.manager.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.v2.AccountInfoFetchRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.edu.manager.AdminApplication;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.manager.ContentManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2017/7/10.
 */
public class AccountInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    private void initView() {
        initToolbar();
    }

    private void initToolbar() {
        View view = findViewById(R.id.toolbar_header);
        view.findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleView = (TextView) view.findViewById(R.id.toolbar_title);
        titleView.setText(R.string.main_item_account_info);
    }

    private void initData() {
        NeoAccountBase neoAccount = ContentManager.getAccount(this);
        if (!checkAccountValid(neoAccount)) {
            return;
        }
        updateAccountInfo(neoAccount);
        fetchAccountFromCloud();
    }

    private void fetchAccountFromCloud() {
        final AccountInfoFetchRequest infoFetchRequest = new AccountInfoFetchRequest();
        AdminApplication.getCloudManager().submitRequest(this, infoFetchRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                if (infoFetchRequest.isTokenInvalid()) {
                    checkAccountValid(null);
                    return;
                }
                NeoAccountBase cloudAccount = infoFetchRequest.getResultAccount();
                if (cloudAccount == null) {
                    return;
                }
                updateAccountInfo(cloudAccount);
                saveToLocalAccount(cloudAccount);
            }
        });
    }

    private void saveToLocalAccount(NeoAccountBase cloudAccount) {
        NeoAccountBase accountBase = ContentManager.getAccount(this);
        if (accountBase == null) {
            ContentManager.saveAccount(this, cloudAccount);
            return;
        }
        cloudAccount.token = accountBase.token;
        cloudAccount.tokenExpiresIn = accountBase.tokenExpiresIn;
        ContentManager.saveAccount(this, cloudAccount);
    }

    private void updateAccountInfo(NeoAccountBase account) {
        intAccountInfoItem(R.id.layout_username, getString(R.string.account_info_user_name_id), account.name);
        intAccountInfoItem(R.id.layout_organization, getString(R.string.account_info_organization), account.orgName);
        intAccountInfoItem(R.id.layout_phone, getString(R.string.account_info_phone), account.phone);
        intAccountInfoItem(R.id.layout_permission, getString(R.string.account_info_permission), "");
    }

    private void intAccountInfoItem(int parentId, String leftTextLabel, String rightTextContent) {
        View parentView = findViewById(parentId);
        TextView infoLabel = (TextView) parentView.findViewById(R.id.info_label);
        infoLabel.setText(leftTextLabel);
        TextView contentLabel = (TextView) parentView.findViewById(R.id.info_content);
        contentLabel.setText(rightTextContent);
    }

    private boolean checkAccountValid(NeoAccountBase neoAccount) {
        if (!NeoAccountBase.isValid(neoAccount) || neoAccount.isTokenTimeExpired()) {
            ToastUtils.showToast(getApplicationContext(), R.string.account_is_invalid);
            resetAccountAndFinish();
            return false;
        }
        return true;
    }

    @OnClick(R.id.btn_sign_out)
    public void onSignOutClick() {
        ToastUtils.showToast(getApplicationContext(), R.string.logout_success);
        resetAccountAndFinish();
    }

    private void resetAccountAndFinish() {
        ContentManager.saveAccount(this, null);
        startActivityWithClearTask(this, MainActivity.class);
        finish();
    }

    public static void startActivityWithClearTask(Context context, Class<? extends Activity> cls) {
        Intent intent = new Intent(context, cls)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
