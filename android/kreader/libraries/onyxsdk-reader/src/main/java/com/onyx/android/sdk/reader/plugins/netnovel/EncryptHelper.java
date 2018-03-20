package com.onyx.android.sdk.reader.plugins.netnovel;

import android.content.Context;

import com.jd.encryption.newencrypt.decryptionoperation;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by li on 2018/1/11.
 */

public class EncryptHelper {

    public static class Constants {
        public static final String ENCRYPTION_DIR = "drdrop";
        public static final String ENCRYPTION_NAME = "dataread.jdr";
        public static final String NET_BOOK_DECRYPT_SALT = "1513304880000";
        public static final int GET_DECRYPT_KEY_POINT = 6;
    }

    private static class JDAppBaseInfo {
        public static final String APP_DEFAULT_VALUE = "eink";
    }

    private static String path;

    public static String getEncryptKey(String salt) {
        char[] chars = salt.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            sb.append(chars[i]);
            if (i == 6) {
                sb.append("eink");
            }
        }
        return sb.toString();
    }

    public static String getNetBookDecryptKey(String part) {
        char[] chars = Constants.NET_BOOK_DECRYPT_SALT.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            sb.append(chars[i]);
            if (i == Constants.GET_DECRYPT_KEY_POINT) {
                sb.append(JDAppBaseInfo.APP_DEFAULT_VALUE);
                sb.append(part);
            }
        }
        return sb.toString();
    }

    public static void setNetBookDecryptKeyPath(Context context, String key) {
        File encryptFile = getEncryptFile(context);
        FileUtils.appendContentToFile(key, encryptFile);
        path = encryptFile.getParent() + File.separator;
    }

    public static String getDecryptContent(String encryptContent) {
        String atdecryption = decryptionoperation.atdecryption(encryptContent, path, 0);
        return atdecryption;
    }

    public static File getEncryptFile(Context context) {
        File externalFilesDir = context.getExternalFilesDir(null);
        File dir = new File(externalFilesDir + File.separator + Constants.ENCRYPTION_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, Constants.ENCRYPTION_NAME);
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
