package com.onyx.jdread.shop.action;

import android.content.Context;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookExtraInfoBean;
import com.onyx.jdread.shop.event.DownloadErrorEvent;
import com.onyx.jdread.shop.event.DownloadFinishEvent;
import com.onyx.jdread.shop.event.DownloadStartEvent;
import com.onyx.jdread.shop.event.DownloadingEvent;
import com.onyx.jdread.shop.model.ProgressInfoModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.utils.DownLoadHelper;

import java.io.IOException;
import java.net.ConnectException;

/**
 * Created by jackdeng on 2017/12/21.
 */

public class DownloadAction extends BaseAction<ShopDataBundle> {

    private String TAG = DownloadAction.class.getSimpleName();
    private Context context;
    private String url;
    private String filePath;
    private Object tag;
    private BookDetailResultBean.DetailBean bookDetailBean;

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
                if (downLoadCallback != null) {
                    downLoadCallback.start(tag);
                }
            }

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {
                if (bookDetailBean != null) {
                    dataBundle.setBookDetail(bookDetailBean);
                }
                ProgressInfoModel infoModel = new ProgressInfoModel();
                infoModel.soFarBytes = info.soFarBytes;
                infoModel.totalBytes = info.totalBytes;
                if (infoModel.totalBytes > 0) {
                    infoModel.progress = infoModel.soFarBytes / infoModel.totalBytes;
                }
                dataBundle.getEventBus().post(new DownloadingEvent(tag, infoModel));
                BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(tag);
                if (DownLoadHelper.isPause(task.getStatus())) {
                    updateDownloadInfo(task);
                }
                if (downLoadCallback != null) {
                    downLoadCallback.progress(tag, infoModel);
                }
            }

            @Override
            public void done(BaseRequest request, Throwable e) {
                DownloadFinishEvent downloadFinishEvent = new DownloadFinishEvent(tag);
                downloadFinishEvent.setThrowable(e);
                dataBundle.getEventBus().post(downloadFinishEvent);
                BaseDownloadTask task = OnyxDownloadManager.getInstance().getTask(tag);
                if (task != null) {
                    updateDownloadInfo(task);
                }
                if (downLoadCallback != null) {
                    downLoadCallback.done(tag);
                }
                if (rxCallback != null) {
                    if (e != null) {
                        if (e instanceof ConnectException || e instanceof IOException) {
                            ToastUtil.showToast(ResManager.getString(R.string.network_exception));
                        } else {
                            ToastUtil.showToast(R.string.download_fail);
                        }
                        rxCallback.onError(e);
                    } else {
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

    public void setBookDetailBean(BookDetailResultBean.DetailBean bookDetailBean) {
        this.bookDetailBean = bookDetailBean;
    }

    public interface DownLoadCallback {
        void start(Object tag);

        void progress(Object tag, ProgressInfoModel progressInfoModel);

        void done(Object tag);
    }

    private DownLoadCallback downLoadCallback;

    public void setDownLoadCallback(DownLoadCallback downLoadCallback) {
        this.downLoadCallback = downLoadCallback;
    }

    public void removeDownLoadCallback(DownLoadCallback downLoadCallback) {
        this.downLoadCallback = downLoadCallback;
    }

    private void updateDownloadInfo(BaseDownloadTask task) {
        JDReadApplication.getInstance().setNotifyLibraryData(true);
        BookExtraInfoBean extraInfoBean = new BookExtraInfoBean();
        extraInfoBean.downLoadState = task.getStatus();
        extraInfoBean.downloadUrl = task.getUrl();
        extraInfoBean.percentage = OnyxDownloadManager.getInstance().getTaskProgress(task.getId());
        extraInfoBean.progress = task.getSmallFileSoFarBytes();
        extraInfoBean.totalSize = task.getSmallFileTotalBytes();
        UpdateDownloadInfoAction action = new UpdateDownloadInfoAction(extraInfoBean, task.getPath());
        action.execute(ShopDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }
}
