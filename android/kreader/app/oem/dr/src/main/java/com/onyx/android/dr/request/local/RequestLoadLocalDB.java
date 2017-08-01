package com.onyx.android.dr.request.local;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by hehai on 17-7-27.
 */

public class RequestLoadLocalDB extends BaseDataRequest {

    @Override
    public void execute(DataManager dataManager) throws Exception {
        File file = new File(getFilesDir(), "AddressDatabase.db");
        if (!file.exists() || file.length() < 0) {
            InputStream is = DRApplication.getInstance().getAssets().open("AddressDatabase.db");
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();
        }
    }

    private String getFilesDir() {
        return Constants.APP_DATABASE_DIRECTORY;
    }
}
