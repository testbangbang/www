package com.onyx.jdread.shop.action;

import android.content.Context;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.shop.event.DownloadFinishEvent;
import com.onyx.jdread.shop.event.DownloadStartEvent;
import com.onyx.jdread.shop.event.DownloadingEvent;
import com.onyx.jdread.shop.model.ProgressInfoModel;
import com.onyx.jdread.shop.model.ShopDataBundle;

/**
 * Created by jackdeng on 2017/12/21.
 */

public class DownloadAction extends BaseAction<ShopDataBundle> {

    private String TAG = DownloadAction.class.getSimpleName();
    private Context context;
    private String url;
    private String filePath;
    private Object tag;

    public DownloadAction(Context context, String url, String filePath, Object tag) {
        this.context = context;
        this.url = url;
        this.filePath = filePath;
        this.tag = tag;
    }

    @Override
    public void execute(ShopDataBundle dataBundle, RxCallback rxCallback) {
        if (StringUtils.isNullOrEmpty(url)) {
            ToastUtil.showToast(context, R.string.download_link_invalid);
            return;
        }
        if (StringUtils.isNullOrEmpty(filePath)) {
            ToastUtil.showToast(context, R.string.file_path_invalid);
            return;
        }
        if (isTaskDownloading(tag)) {
            return;
        }
        startDownload(dataBundle, rxCallback);
    }

    private boolean isTaskDownloading(Object tag) {
        return getDownLoaderManager().getTask(tag) != null;
    }

    private void startDownload(final ShopDataBundle dataBundle, final RxCallback rxCallback) {
        BaseDownloadTask task = getDownLoaderManager().download(context, url, filePath, tag, new BaseCallback() {

            @Override
            public void start(BaseRequest request) {
                dataBundle.getEventBus().post(new DownloadStartEvent(tag));
            }

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {
                ProgressInfoModel infoModel = new ProgressInfoModel();
                infoModel.soFarBytes = info.soFarBytes;
                infoModel.totalBytes = info.totalBytes;
                if (infoModel.totalBytes > 0) {
                    infoModel.progress = infoModel.soFarBytes / infoModel.totalBytes;
                }
                dataBundle.getEventBus().post(new DownloadingEvent(tag, infoModel));
            }

            @Override
            public void done(BaseRequest request, Throwable e) {
                dataBundle.getEventBus().post(new DownloadFinishEvent(tag));
                if (rxCallback != null) {
                    if (e != null) {
                        ToastUtil.showToast(context, R.string.download_fail);
                        rxCallback.onError(e);
                    } else {
                        ToastUtil.showToast(context, R.string.download_finished);
                        rxCallback.onNext(DownloadAction.this);
                    }
                }
                removeDownloadingTask(tag);
            }

        });
        getDownLoaderManager().addTask(tag, task);
        getDownLoaderManager().startDownload(task);
    }

    private void stopDownloadingTask(Object tag) {
        BaseDownloadTask task = getDownLoaderManager().getTask(tag);
        removeDownloadingTask(tag);
        if (task != null) {
            task.pause();
        }
    }

    private void removeDownloadingTask(Object tag) {
        getDownLoaderManager().removeTask(tag);
    }

    private OnyxDownloadManager getDownLoaderManager() {
        return OnyxDownloadManager.getInstance();
    }
}
