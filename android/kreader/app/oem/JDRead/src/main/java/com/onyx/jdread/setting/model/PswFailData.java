package com.onyx.jdread.setting.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by suicheng on 2018/2/11.
 */
public class PswFailData implements Serializable {

    public int times = 5;
    public Date createdAt;

    public static PswFailData create(int times, Date createdAt) {
        PswFailData data = new PswFailData();
        data.times = times;
        data.createdAt = createdAt;
        return data;
    }

    public boolean isTimesInvalid() {
        return times <= 0;
    }

    public boolean isCreatedTimeExpired(int expired) {
        if (createdAt == null) {
            return true;
        }
        return createdAt.getTime() + expired < System.currentTimeMillis();
    }
}
