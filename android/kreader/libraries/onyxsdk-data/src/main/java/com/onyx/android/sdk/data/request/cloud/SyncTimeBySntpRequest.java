package com.onyx.android.sdk.data.request.cloud;

import android.app.AlarmManager;
import android.net.Uri;
import android.os.SystemClock;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.utils.SntpClient;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhuzeng on 01/06/2017.
 */

public class SyncTimeBySntpRequest extends BaseCloudRequest {

    private int count = 3;
    boolean isSyncTime = false;

    public SyncTimeBySntpRequest(int retry) {
        count = retry;
    }


    @Override
    public void execute(CloudManager parent) throws Exception {
        final SntpClient client = new SntpClient();
        for (int i = 0; i < count; ++i) {
            isSyncTime = client.requestTime("1.cn.pool.ntp.org", 10 * 1000);
            if (isSyncTime) {
                break;
            }
        }
        if (!isSyncTime) {
            return;
        }

        try {
            long now = client.getNtpTime() + SystemClock.elapsedRealtime() - client.getNtpTimeReference();
            Date current = new Date(now);
            Calendar c = Calendar.getInstance();
            c.set(current.getYear(), current.getMonth(), current.getDay(), current.getHours(), current.getMinutes(), current.getSeconds());
            AlarmManager am = (AlarmManager) getContext().getSystemService(getContext().ALARM_SERVICE);
            am.setTime(c.getTimeInMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

