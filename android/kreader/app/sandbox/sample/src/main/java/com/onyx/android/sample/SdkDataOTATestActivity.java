package com.onyx.android.sample;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.request.cloud.FirmwareLocalCheckLegalityRequest;
import com.onyx.android.sdk.data.request.cloud.FirmwareUpdateRequest;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2017/3/23.
 */
public class SdkDataOTATestActivity extends AppCompatActivity {
    private static final String TAG = SdkDataOTATestActivity.class.getSimpleName();

    @Bind(R.id.button_test_firmware)
    Button buttonTestFirmware;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_data_ota_test);
        ButterKnife.bind(this);
    }

    private File getUpdateZipFile() {
        return new File(OTAManager.LOCAL_PATH_SDCARD);
    }

    @OnClick(R.id.button_test_download)
    void testDownloadClick() {
        String url = "change to your own public link here";
        File file = getUpdateZipFile();
        downloadFile(url, file.getAbsolutePath());
    }

    private void downloadFile(String url, final String filePath) {
        BaseDownloadTask task = OnyxDownloadManager.getInstance().download(url, filePath, url, new BaseCallback() {
            @Override
            public void start(BaseRequest request) {
            }

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {
                String progressInfo = String.format("Download-Progress:%.1f", (float) info.progress);
                showToast(progressInfo);
                Log.e(TAG, progressInfo);
            }

            @Override
            public void done(BaseRequest baseRequest, Throwable e) {
                enableViewClickable(buttonTestFirmware, true);
                if (e != null) {
                    e.printStackTrace();
                }
                showLongToast(String.valueOf(e == null ? "Download Success" : "Download Fail"));
                firmwareLocalCheck();
            }
        });
        OnyxDownloadManager.getInstance().startDownload(task);
    }

    private void firmwareLocalCheck() {
        //check based on update.zip/android_info_text-Build.model
        final FirmwareLocalCheckLegalityRequest localCheckRequest = OTAManager.localFirmwareCheckRequest(this);
        OTAManager.sharedInstance().getCloudStore().submitRequest(this, localCheckRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable e) {
                String targetPath = localCheckRequest.getLegalityTargetPath();
                if (e != null) {
                    e.printStackTrace();
                }
                String resultPrefix = "local check ";
                showLocalCheckDialog(resultPrefix + (StringUtils.isNullOrEmpty(targetPath) ? "fail" : "success"));
                if (StringUtils.isNotBlank(targetPath)) {
                    //start update...
                    //OTAManager.sharedInstance().startFirmwareUpdate(SdkDataOTATestActivity.this, targetPath);
                }
            }
        });
    }

    private void showLocalCheckDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("OTA result")
                .create()
                .show();
    }

    @OnClick(R.id.button_test_firmware)
    void testFirmwareClick() {
        enableViewClickable(buttonTestFirmware, false);
        final FirmwareUpdateRequest updateRequest = OTAManager.cloudFirmwareCheckRequest(this);
        OTAManager.sharedInstance().getCloudStore().submitRequest(this, updateRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Throwable e) {
                if (e != null) {
                    e.printStackTrace();
                }
                if (updateRequest.isResultFirmwareValid()) {
                    Firmware resultFirmware = updateRequest.getResultFirmware();
                    String changeLog = resultFirmware.getChangeLog();
                    if (StringUtils.isNullOrEmpty(changeLog)) {
                        changeLog = resultFirmware.buildDisplayId;
                    }
                    showToast(String.valueOf(changeLog));
                    String downloadUrl = resultFirmware.getUrl();
                    if (StringUtils.isNotBlank(downloadUrl)) {
                        downloadFile(downloadUrl, getUpdateZipFile().getAbsolutePath());
                    }
                } else {
                    //download manual
                    testDownloadClick();
                }
            }
        });
    }

    private void enableViewClickable(View view, boolean enable) {
        view.setClickable(enable);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
