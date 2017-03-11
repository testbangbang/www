package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.Serializable;

/**
 * Created by zhuzeng on 9/14/14.
 */
public class OTAFirmware implements Serializable{

    public String url;
    public String fingerPrint;
    public String changeLogText;
    public String changeLogHtml;

    public OTAFirmware() {
        super();
    }

    static public OTAFirmware otaFirmware(Firmware firmware) {
        if (firmware == null) {
            return null;
        }
        OTAFirmware otaFirmware = new OTAFirmware();
        if (!CollectionUtils.isNullOrEmpty(firmware.downloadUrlList)) {
            otaFirmware.url = firmware.downloadUrlList.get(0);
        }
        otaFirmware.fingerPrint = firmware.fingerprint;
        if (!CollectionUtils.isNullOrEmpty(firmware.changeList)) {
            otaFirmware.changeLogText = StringUtils.join(firmware.changeList, "\n");
        }
        return otaFirmware;
    }
}
