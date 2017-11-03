package com.onyx.android.sample;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.onyx.android.sample.utils.AesDecryptUtil;
import com.onyx.android.sample.utils.StringUtils;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.TestUtils;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

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

    public void testOpenssl() {
        final String[][] files = {
                {"/mnt/sdcard/test.txt", "/mnt/sdcard/test_encrypted.txt", "/mnt/sdcard/test_decrypted.txt"},
                {"/mnt/sdcard/update.zip", "/mnt/sdcard/update_encrypted.zip", "/mnt/sdcard/update_decrypted.zip"},
                {"/mnt/sdcard/user-manual.pdf", "/mnt/sdcard/user-manual-encrypted.pdf", "/mnt/sdcard/user-manual-decrypted.pdf"}
        };
        try {
            for (int i = 0; i < 30; i++) {
                final int index = TestUtils.randInt(0, files.length - 1);
                String srcFileMd5 = FileUtils.computeMD5(new File(files[index][0]));
                Log.d(TAG, "srcFileMd5 is: " + srcFileMd5);
                assertEquals(true, StringUtils.isNotBlank(srcFileMd5));
                final String key = AesDecryptUtil.generateRandomString(32);
                final String iv = AesDecryptUtil.generateRandomString(32);

                String encryptedFileMd5 = AesDecryptUtil.encryptFile(files[index][0], files[index][1], key, iv);
                Log.d(TAG, "encryptedFileMd5 is: " + encryptedFileMd5);
                assertEquals(false, srcFileMd5.equals(encryptedFileMd5));

                String decryptedFileMd5 = AesDecryptUtil.decryptFile(files[index][1], files[index][2], key, iv);
                Log.d(TAG, "decryptedFileMd5 is: " + decryptedFileMd5);
                assertEquals(true, srcFileMd5.equals(decryptedFileMd5));

                FileUtils.deleteFile(files[index][1]);
                FileUtils.deleteFile(files[index][2]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
