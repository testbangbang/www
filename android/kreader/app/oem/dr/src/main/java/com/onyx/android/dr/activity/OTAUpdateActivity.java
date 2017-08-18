package com.onyx.android.dr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.dialog.CustomEditDialog;
import com.onyx.android.dr.util.ApkUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.download.onyxdownloadservice.DownloadCallback;
import com.onyx.download.onyxdownloadservice.DownloadRequest;
import com.onyx.download.onyxdownloadservice.DownloadTaskManager;
import com.onyx.download.onyxdownloadservice.ReportDownloadProcessEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class OTAUpdateActivity extends Activity {

    private int reference;
    private CustomEditDialog.Builder builder;
    private ProgressBar progressBar;
    private CustomEditDialog dialog;
    private LinearLayout contentView;
    private TextView progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otaupdate);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra(Constants.UPDATE_URL);
            if (StringUtils.isNotBlank(url)) {
                downloadUpdateZip(url);
            }
        }
    }

    private void downloadUpdateZip(String url) {
        DownloadRequest downloadRequest = DownloadTaskManager.getInstance().createDownloadRequest(url, ApkUtils.getUpdateZipFile().getAbsolutePath(), Constants.UPDATE_ZIP);
        reference = DownloadTaskManager.getInstance().addDownloadCallback(downloadRequest, new DownloadCallback() {
            @Override
            public void progressChanged(int reference, String title, String remoteUri, String localUri, int state, long finished, long total, long percentage) {

            }
        });
        DRApplication.getInstance().setApkDownloadReference(reference);
        builder = new CustomEditDialog.Builder(this);
        contentView = (LinearLayout) View.inflate(this, R.layout.progressbar_layout, null);
        progressBar = (ProgressBar) contentView.findViewById(R.id.progressBar);
        progress = (TextView) contentView.findViewById(R.id.progress);
        dialog = builder.setTitle(R.string.downloading).setContentView(contentView).create();
        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReportDownloadProcessEvent(ReportDownloadProcessEvent event) {
        if (reference == event.getReference()) {
            if (DownloadTaskManager.getInstance().isDownloaded(event.getState())) {
                ApkUtils.firmwareLocal();
                dialog.dismiss();
            } else {
                progressBar.setProgress((int) event.getPercentage());
                progress.setText(event.getPercentage() + "%");
                contentView.invalidate();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
}
