package com.onyx.android.sdk.data.model.v2;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by hehai on 17-9-7.
 */

public class PayBean {
    public String appId;
    public String timeStamp;
    public String nonceStr;
    public String signType;
    @JSONField(name = "package")
    public String packageX;
    public String paySign;
    public String code_url;
    public String total;
}
