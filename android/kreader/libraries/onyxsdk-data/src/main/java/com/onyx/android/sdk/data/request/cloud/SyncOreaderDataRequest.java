package com.onyx.android.sdk.data.request.cloud;

import android.content.Context;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.compatability.OnyxBookProgress;
import com.onyx.android.sdk.data.compatability.OnyxCmsCenter;
import com.onyx.android.sdk.data.compatability.OnyxHistoryEntry;
import com.onyx.android.sdk.data.compatability.OnyxMetadata;
import com.onyx.android.sdk.data.model.BaseStatisticsModel;
import com.onyx.android.sdk.data.model.OnyxStatisticsModel;
import com.onyx.android.sdk.data.request.data.GetFileMd5Request;
import com.onyx.android.sdk.data.utils.StatisticsUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Created by ming on 2017/2/14.
 */

public class SyncOreaderDataRequest extends BaseCloudRequest {

    private Context context;
    private static final String OREADER_APPLICATION_NAME = "com.neverland.oreader";

    public SyncOreaderDataRequest(final Context context) {
        this.context = context;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        StatisticsUtils.deleteStatisticsListByStatus(context, BaseStatisticsModel.DATA_STATUS_FROM_OREADER);
        List<OnyxHistoryEntry> historyEntries = OnyxCmsCenter.getHistoryByApplication(context, OREADER_APPLICATION_NAME);
        saveHistoryDataByType(historyEntries, OnyxStatisticsModel.DATA_TYPE_OPEN, true);
        saveHistoryDataByType(historyEntries, OnyxStatisticsModel.DATA_TYPE_PAGE_CHANGE, true);
        saveHistoryDataByType(historyEntries, OnyxStatisticsModel.DATA_TYPE_CLOSE, false);
    }

    private void saveHistoryDataByType(List<OnyxHistoryEntry> historyEntries, int dataType, boolean isStartTime) {
        String uuid = "";
        for (OnyxHistoryEntry history : historyEntries) {
            uuid = UUID.randomUUID().toString();
            String md5short = history.getMD5();
            String md5 = StatisticsUtils.getBookMd5(context, md5short);
            OnyxMetadata metadata = OnyxCmsCenter.getMetadataByMD5(context, history.getMD5());
            Date startTime = history.getStartTime();
            Date endTime = history.getEndTime();

            final OnyxStatisticsModel onyxStatisticsModel = OnyxStatisticsModel.create(md5, md5short, uuid, dataType, isStartTime ? startTime : endTime);
            onyxStatisticsModel.setPath(metadata.getNativeAbsolutePath());
            onyxStatisticsModel.setTitle(metadata.getTitle());
            onyxStatisticsModel.setStatus(BaseStatisticsModel.DATA_STATUS_FROM_OREADER);
            onyxStatisticsModel.setName(metadata.getName());
            onyxStatisticsModel.setAuthor(metadata.getAuthors());
            onyxStatisticsModel.setDurationTime(endTime.getTime() - startTime.getTime());

            if (StringUtils.isNullOrEmpty(md5)) {
                DataManager dataManager = new DataManager();
                final GetFileMd5Request md5Request = new GetFileMd5Request(metadata.getNativeAbsolutePath());
                dataManager.submit(context, md5Request, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onyxStatisticsModel.setMd5(md5Request.getMd5());
                    }
                });
            } else {
                onyxStatisticsModel.setMd5(md5);
            }

            OnyxBookProgress progress = history.getProgress();
            if (progress.getTotal() == progress.getCurrent()) {
                onyxStatisticsModel.setType(OnyxStatisticsModel.DATA_TYPE_FINISH);
            }
            onyxStatisticsModel.save();
        }
    }


}
