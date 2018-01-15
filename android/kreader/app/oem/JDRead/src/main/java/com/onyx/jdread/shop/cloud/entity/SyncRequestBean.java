package com.onyx.jdread.shop.cloud.entity;

import com.jingdong.app.reader.data.DrmTools;
import com.onyx.jdread.JDReadApplication;

/**
 * Created by li on 2018/1/9.
 */

public class SyncRequestBean {
    public String app = "eink";
    public String uuid = DrmTools.hashDevicesInfo(JDReadApplication.getInstance());
    public String ip = "192.168.0.80";
    public long tm = System.currentTimeMillis();
    public String tid;
    public String client = "android";
}
