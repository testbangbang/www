package com.onyx.jdread.personal.common;

import com.jd.encryption.newencrypt.decryptionoperation;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.personal.action.GetSaltAction;
import com.onyx.jdread.personal.model.PersonalDataBundle;

import java.io.File;
import java.io.IOException;

/**
 * Created by li on 2018/1/11.
 */

public class EncryptHelper {

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

    public static String getEncryptParams(String key, String signStr) {
        File encryptFile = getEncryptFile();
        FileUtils.appendContentToFile(key, encryptFile);
        String path = encryptFile.getParent() + "/";
        String atencrypting = decryptionoperation.atencrypting(signStr, path, 0);
        return atencrypting;
    }

    public static File getEncryptFile() {
        File externalFilesDir = JDReadApplication.getInstance().getExternalFilesDir(null);
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

    public static void getSaltValue(PersonalDataBundle bundle, final RxCallback callback) {
        GetSaltAction action = new GetSaltAction();
        action.execute(bundle, callback);
    }
}
