package com.onyx.jdread.personal.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.personal.action.SyncReadDataAction;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.reader.data.ReadingData;
import com.onyx.jdread.reader.data.ReadingDataResultBean;
import com.onyx.jdread.reader.menu.common.ReaderConfig;
import com.onyx.jdread.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/3/19.
 */

public class ReadTimeBroadcastReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        long length = intent.getLongExtra(ReaderConfig.BOOK_READING_TIME, 0);
        long startTime = intent.getLongExtra(ReaderConfig.BOOK_READING_START_TIME, 0);
        long cloudId = intent.getLongExtra(ReaderConfig.BOOK_READING_ID, 0);
        ReadingData readingData = new ReadingData();
        readingData.start_time = String.valueOf(startTime);
        readingData.end_time = String.valueOf(System.currentTimeMillis() / 1000);
        readingData.ebook_id = String.valueOf(cloudId);
        readingData.length = String.valueOf(length);
        syncReadingData(readingData);

        long currentReadTime = JDReadApplication.getInstance().getCurrentReadTime();
        long total = length + currentReadTime;
        String s = TimeUtils.getDate(System.currentTimeMillis()) + Constants.DIVIDER + total;
        JDPreferenceManager.setStringValue(ReaderConfig.BOOK_READING_TIME, s);
    }

    private void syncReadingData(final ReadingData readingData) {
        List<ReadingData> list = new ArrayList<>();
        if (readingData != null) {
            list.add(readingData);
        }
        final SyncReadDataAction action = new SyncReadDataAction(list);
        action.execute(PersonalDataBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReadingDataResultBean resultBean = action.getResultBean();
                if (resultBean != null && resultBean.result_code == 0) {
                    PersonalDataBundle.getInstance().deleteReadingData(readingData);
                } else {
                    PersonalDataBundle.getInstance().saveReadingData(readingData);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                PersonalDataBundle.getInstance().saveReadingData(readingData);
            }
        });
    }
}
