package com.onyx.jdread.shop.action;

import android.content.Context;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.common.ToastUtil;
import com.onyx.jdread.shop.event.DownloadingEvent;
import com.onyx.jdread.shop.model.ShopDataBundle;

import org.greenrobot.eventbus.EventBus;

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
        startDownload(rxCallback);
    }

    private boolean isTaskDownloading(Object tag) {
        return getDownLoaderManager().getTask(tag) != null;
    }

    private void startDownload(final RxCallback rxCallback) {
        BaseDownloadTask task = getDownLoaderManager().download(context, url, filePath, tag, new BaseCallback() {

            @Override
            public void start(BaseRequest request) {
                EventBus.getDefault().post(new DownloadingEvent());
            }

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {
                EventBus.getDefault().post(new DownloadingEvent(tag, info));
            }

            @Override
            public void done(BaseRequest request, Throwable e) {
                EventBus.getDefault().post(new DownloadingEvent());
                removeDownloadingTask(tag);
                if (rxCallback != null) {
                    if (e != null) {
                        ToastUtil.showToast(context, R.string.download_fail);
                        rxCallback.onError(e);
                    } else {
                        rxCallback.onNext(DownloadAction.this);
                    }
                }
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
