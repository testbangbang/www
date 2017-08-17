package com.onyx.phone.reader.utils;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by suicheng on 2017/8/17.
 */

public class IntentUtils {

    public static String getBundleInfo(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder("Bundle{\r\n");
        for (String key : bundle.keySet()) {
            sb.append(key).append(" => ").append(bundle.get(key)).append(";\r\n");
        }
        sb.append(" }Bundle");
        return sb.toString();
    }
}
