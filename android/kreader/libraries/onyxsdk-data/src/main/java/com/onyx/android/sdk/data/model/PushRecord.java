package com.onyx.android.sdk.data.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.Constant;

import java.util.Map;

/**
 * Created by suicheng on 2016/10/13.
 */
public class PushRecord extends BaseData {

    public String[] accounts;
    public String[] channels;
    public String[] devices;
    public String[] installations;
    public Map<String, Object> data;
    public String expiration_interval;

    private <T extends BaseData> T parsePushData(final Class<T> clazz) {
        T pushData = null;
        if (data != null) {
            if (data.containsKey(Constant.ARGS_TAG)) {
                JSONObject object = (JSONObject) data.get(Constant.ARGS_TAG);
                pushData = JSON.parseObject(object.toJSONString(), clazz);
            }
        }
        return pushData;
    }

    public PushProduct parsePushProduct() {
        return parsePushData(PushProduct.class);
    }

    public PushBroadcast parsePushBroadcast() {
        return parsePushData(PushBroadcast.class);
    }

    @Override
    public void save() {
        super.save();
    }
}
