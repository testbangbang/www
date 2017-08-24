package com.onyx.edu.manager.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.request.cloud.ApplicationUpdateRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.PackageUtils;
import com.onyx.edu.manager.AdminApplication;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.adapter.FuncSelectAdapter;
import com.onyx.edu.manager.adapter.ItemClickListener;
import com.onyx.android.sdk.data.manager.AppUpdateManager;
import com.onyx.android.sdk.data.manager.AppUpdateManager.AppUpdateConfig;
import com.onyx.edu.manager.manager.ContentManager;
import com.onyx.edu.manager.model.FuncItemEntity;
import com.onyx.edu.manager.view.activity.AccountInfoActivity;
import com.onyx.edu.manager.view.activity.FactoryActivity;
import com.onyx.edu.manager.view.activity.QrScannerActivity;
import com.onyx.edu.manager.view.activity.UserManagerActivity;
import com.onyx.edu.manager.view.dialog.DialogHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by suicheng on 2017/7/8.
 */
public class FuncSelectFragment extends Fragment {
    private static boolean hasAppUpdateChecked = false;

    @Bind(R.id.content_pageView)
    RecyclerView contentPageView;

    private FuncSelectAdapter selectAdapter;
    private List<FuncItemEntity> funcItemEntityList = new ArrayList<>();

    public static Fragment newInstance() {
        return new FuncSelectFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_func_select, container, false);
        ButterKnife.bind(this, view);
        initView((ViewGroup) view);
        initData();

        return view;
    }

    private void initView(ViewGroup parentView) {
        initToolbar(parentView);
        contentPageView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        contentPageView.setAdapter(selectAdapter = new FuncSelectAdapter(funcItemEntityList));
        selectAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(int position, View view) {
                ActivityUtil.startActivitySafely(view.getContext(), funcItemEntityList.get(position).intent);
            }

            @Override
            public void onLongClick(int position, View view) {
            }
        });
    }

    private void initToolbar(View parentView) {
        View view = parentView.findViewById(R.id.toolbar_header);
        view.findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        TextView titleView = (TextView) view.findViewById(R.id.toolbar_title);
        titleView.setText(R.string.main_item_func_select);
    }

    private void initData() {
        prepareData();
        if (!NetworkUtil.isWiFiConnected(getContext())) {
            ToastUtils.showToast(getContext().getApplicationContext(), R.string.network_is_not_connected);
        } else {
            checkAppUpdate();
        }
        funcItemEntityList.addAll(loadData());
        selectAdapter.notifyDataSetChanged();
    }

    private void prepareData() {
        if (hasAppUpdateCheck()) {
            return;
        }
        setAppUpdateCheck(!ContentManager.isAppUpdateCheckTimeExpires(getContext()));
    }

    private List<FuncItemEntity> loadData() {
        List<FuncItemEntity> itemEntityList = new ArrayList<>();
        itemEntityList.add(FuncItemEntity.create(R.mipmap.ic_code, getString(R.string.main_item_scanner),
                new Intent(getContext(), QrScannerActivity.class)));
        itemEntityList.add(FuncItemEntity.create(R.mipmap.ic_code_more, getString(R.string.main_item_scanner_batch),
                new Intent(getContext(), FactoryActivity.class)));
        itemEntityList.add(FuncItemEntity.create(R.mipmap.ic_manage, getString(R.string.main_item_user_manager),
                new Intent(getContext(), UserManagerActivity.class)));
        itemEntityList.add(FuncItemEntity.create(R.mipmap.ic_account, getString(R.string.main_item_account_info),
                new Intent(getContext(), AccountInfoActivity.class)));
        return itemEntityList;
    }

    private void checkAppUpdate() {
        if (hasAppUpdateCheck()) {
            return;
        }
        final AppUpdateConfig config = AppUpdateConfig.create(R.mipmap.ic_launcher, false);
        AppUpdateManager.checkUpdate(getContext().getApplicationContext(), AdminApplication.getUpdateCheckManager(),
                config, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e != null) {
                            return;
                        }
                        ApplicationUpdate updateInfo = ((ApplicationUpdateRequest) request).getApplicationUpdate();
                        if (updateInfo == null) {
                            return;
                        }
                        showUpdateInfoDialog(config, updateInfo);
                    }
                });
    }

    private void showUpdateInfoDialog(final AppUpdateConfig config, final ApplicationUpdate updateInfo) {
        setAppUpdateCheck(true);
        List<String> changeLogList = updateInfo.getChangeLogList();
        if (CollectionUtils.isNullOrEmpty(changeLogList)) {
            changeLogList.add(getString(R.string.app_update_change_log_empty_content));
        }
        MaterialDialog.Builder builder = DialogHolder.getDialogBaseBuilder(getContext(), getString(R.string.app_update_new_version_detect),
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startDownload(config, updateInfo);
                    }
                });
        builder.items(changeLogList)
                .positiveText(R.string.update)
                .show();
    }

    private boolean hasAppUpdateCheck() {
        return hasAppUpdateChecked;
    }

    private void setAppUpdateCheck(boolean checked) {
        hasAppUpdateChecked = checked;
        ContentManager.setAppUpdateCheckTime(getContext(), new Date());
    }

    private void startDownload(final AppUpdateConfig config, final ApplicationUpdate updateInfo) {
        final MaterialDialog dialog = DialogHolder.showDownloadingDialog(getContext(), getString(R.string.downloading));
        boolean result = AppUpdateManager.checkUpdatedFileDownload(getContext().getApplicationContext(), config,
                updateInfo, new BaseCallback() {
                    @Override
                    public void progress(BaseRequest request, ProgressInfo info) {
                        setDialogProgress(dialog, info);
                    }

                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        dismissDialog(dialog);
                        if (e != null) {
                            ToastUtils.showToast(getContext().getApplicationContext(), R.string.download_failed);
                            return;
                        }
                        startInstall(updateInfo);
                    }
                });
        if (!result) {
            dismissDialog(dialog);
        }
    }

    private void dismissDialog(final MaterialDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void setDialogProgress(final MaterialDialog dialog, final BaseCallback.ProgressInfo info) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setProgress((int) info.progress);
        }
    }

    private void startInstall(ApplicationUpdate updateInfo) {
        boolean result = ActivityUtil.startActivitySafely(getContext(), PackageUtils.getInstallIntent(
                new File(AppUpdateManager.getApkFilePath(getContext().getApplicationContext(), updateInfo))));
        if (!result) {
            ToastUtils.showToast(getContext().getApplicationContext(), R.string.install_failed);
        }
    }
}
