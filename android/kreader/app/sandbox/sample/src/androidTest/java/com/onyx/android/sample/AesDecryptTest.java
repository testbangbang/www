package com.onyx.android.sample;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.onyx.android.sample.utils.AesDecryptUtil;
import com.onyx.android.sdk.device.EnvironmentUtil;

/**
 * Created by wangxu on 31/10/2017.
 */

public class AesDecryptTest extends InstrumentationTestCase {

    private final static String TAG = AesDecryptTest.class.getSimpleName();

    /*
     * 1. Execute the following command on linux pc
     * openssl enc -aes-128-cbc -in update.zip -out encrypt.zip -K 50E1723DC328D98F133E321FC2908B78 -iv 1528E9AD498FF118AB7ECB3025AD0DC6
     * 2. Copy the encrypt.zip to device
     */
    public void test() {
        boolean success = AesDecryptUtil.decrypt("50E1723DC328D98F133E321FC2908B78", "1528E9AD498FF118AB7ECB3025AD0DC6",
                EnvironmentUtil.getExternalStorageDirectory() + "/encrypt.zip",
                EnvironmentUtil.getExternalStorageDirectory() + "/update.zip");
        Log.d(TAG, "Decryption test was " + (success ? "successful" : "failed") + "!");
    }
}
