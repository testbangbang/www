package com.onyx.phone.reader.action;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.manager.AppUpdateManager;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.request.cloud.ApplicationUpdateRequest;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.PackageUtils;
import com.onyx.phone.reader.R;
import com.onyx.phone.reader.reader.data.ReaderDataHolder;
import com.onyx.phone.reader.ui.dialog.DialogHolder;
import com.onyx.phone.reader.ui.dialog.DialogLoading;
import com.onyx.phone.reader.ui.dialog.MaterialDialogProgress;
import com.onyx.phone.reader.utils.ToastUtils;

import java.io.File;
import java.util.List;

/**
 * Created by suicheng on 2017/8/17.
 */
public class AppUpdateCheckAction extends BaseAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final AppUpdateManager.AppUpdateConfig config = AppUpdateManager.AppUpdateConfig.create(R.mipmap.ic_launcher, false);
        AppUpdateManager.checkUpdate(readerDataHolder.getContext().getApplicationContext(),
                readerDataHolder.getCloudManager(), config, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e != null) {
                            BaseCallback.invoke(baseCallback, request, e);
                            return;
                        }
                        ApplicationUpdate updateInfo = ((ApplicationUpdateRequest) request).getApplicationUpdate();
                        if (updateInfo == null) {
                            return;
                        }
                        showUpdateInfoDialog(readerDataHolder.getContext(), config, updateInfo, baseCallback);
                    }
                });
    }

    private void showUpdateInfoDialog(final Context context, final AppUpdateManager.AppUpdateConfig config,
                                      final ApplicationUpdate updateInfo, final BaseCallback baseCallback) {
        List<String> changeLogList = updateInfo.getChangeLogList();
        if (CollectionUtils.isNullOrEmpty(changeLogList)) {
            changeLogList.add(context.getString(R.string.app_update_change_log_empty_content));
        }
        MaterialDialog.Builder builder = DialogHolder.getDialogBaseBuilder(context, context.getString(R.string.app_update_new_version_detect),
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startDownload(context, config, updateInfo, baseCallback);
                    }
                });
        builder.items(changeLogList)
                .positiveText(R.string.update)
                .show();
    }

    private void startDownload(final Context context, final AppUpdateManager.AppUpdateConfig config,
                               final ApplicationUpdate updateInfo, final BaseCallback baseCallback) {
        final DialogLoading dialog = new MaterialDialogProgress(context, R.string.downloading, false);
        dialog.show();
        boolean result = AppUpdateManager.checkUpdatedFileDownload(context.getApplicationContext(), config,
                updateInfo, new BaseCallback() {
                    @Override
                    public void progress(BaseRequest request, ProgressInfo info) {
                        setDialogProgress(dialog, info);
                    }

                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        dismissDialog(dialog);
                        if (e == null) {
                            startInstall(context, updateInfo);
                        } else {
                            e.printStackTrace();
                            ToastUtils.showShortToast(context.getApplicationContext(), R.string.download_failed);
                        }
                        BaseCallback.invoke(baseCallback, request, null);
                    }
                });
        if (!result) {
            dismissDialog(dialog);
        }
    }

    private void dismissDialog(final DialogLoading dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void setDialogProgress(final DialogLoading dialog, final BaseCallback.ProgressInfo info) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setProgress((int) info.progress);
        }
    }

    private void startInstall(Context context, ApplicationUpdate updateInfo) {
        boolean result = ActivityUtil.startActivitySafely(context, PackageUtils.getInstallIntent(
                new File(AppUpdateManager.getApkFilePath(context.getApplicationContext(), updateInfo))));
        if (!result) {
            ToastUtils.showShortToast(context.getApplicationContext(), R.string.install_failed);
        }
    }
}
