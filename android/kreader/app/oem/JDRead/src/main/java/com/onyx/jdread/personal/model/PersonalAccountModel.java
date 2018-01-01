package com.onyx.jdread.personal.model;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.personal.event.ConsumptionRecordEvent;
import com.onyx.jdread.personal.event.PaidRecordEvent;
import com.onyx.jdread.personal.event.PointsForEvent;
import com.onyx.jdread.personal.event.ReadVipEvent;
import com.onyx.jdread.personal.event.TopUpEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by li on 2017/12/29.
 */

public class PersonalAccountModel {
    private String[] accountTitles = JDReadApplication.getInstance().getResources().getStringArray(R.array.personal_account);
    private Map<String, Object> accountEvents = new HashMap<String, Object>() {
        {
            put(JDReadApplication.getInstance().getResources().getString(R.string.top_up), new TopUpEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.consumption_record), new ConsumptionRecordEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.paid_record), new PaidRecordEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.read_vip), new ReadVipEvent());
            put(JDReadApplication.getInstance().getResources().getString(R.string.points_for), new PointsForEvent());
        }
    };

    public String[] getAccountTitles() {
        return accountTitles;
    }

    public void setAccountTitles(String[] accountTitles) {
        this.accountTitles = accountTitles;
    }

    public Map<String, Object> getAccountEvents() {
        return accountEvents;
    }

    public void setAccountEvents(Map<String, Object> accountEvents) {
        this.accountEvents = accountEvents;
    }
}
