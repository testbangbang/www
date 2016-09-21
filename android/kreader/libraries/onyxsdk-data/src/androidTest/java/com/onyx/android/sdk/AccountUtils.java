package com.onyx.android.sdk;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.TestUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.Format;
import java.util.UUID;

/**
 * Created by zhuzeng on 9/4/16.
 */
public class AccountUtils {

    private static OnyxAccount currentAccount;

    public static OnyxAccount getCurrentAccount() {
        if (currentAccount == null) {
            currentAccount = new OnyxAccount(UUID.randomUUID().toString(),
                    TestUtils.randString(), TestUtils.randomEmail());
        }
        return currentAccount;
    }

    public static OnyxAccount generateRandomAccount() {
        return new OnyxAccount(UUID.randomUUID().toString(),
                TestUtils.randString(), TestUtils.randomEmail());
    }

    public static File getAvatarFile() {
        Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(0x88FF00FF);//桃粉色
        File rootDir = EnvironmentUtil.getExternalStorageAndroidDataDir();
        File avatarFile = new File(rootDir, UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8) + ".png");
        try {
            if (!avatarFile.exists()) {
                avatarFile.createNewFile();
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, new FileOutputStream(avatarFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return avatarFile;
    }
}
