package com.onyx.jdread.setting.model;

import java.io.Serializable;

/**
 * Created by suicheng on 2018/2/7.
 */

public class PswSettingData implements Serializable {
    public String phone;
    public String password;
    public String mac;
    public String model;

    public static PswSettingData create(String phone, String password, String mac, String model) {
        PswSettingData data = new PswSettingData();
        data.phone = phone;
        data.password = password;
        data.mac = mac;
        data.model = model;
        return data;
    }
}
