package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.db.PushDatabase;
import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.Table;

import java.io.File;

/**
 * Created by suicheng on 2017/8/2.
 */
@Table(database = PushDatabase.class, allFields = true)
public class PushFileEvent extends BaseData {
    public String url;
    public String name;
    public String type;
    public String filePath;
    public String md5;
    @ColumnIgnore
    public boolean open;

    public String getFileName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getFileNameWithExtension() {
        return getFileName() + "." + getType();
    }

    public static boolean checkOpenAfterLoaded(PushFileEvent pushFile) {
        if (!checkFileExist(pushFile)) {
            return false;
        }
        return pushFile.open;
    }

    public static boolean checkFileExist(PushFileEvent pushFile) {
        if (pushFile == null || StringUtils.isNullOrEmpty(pushFile.filePath)) {
            return false;
        }
        File file = new File(pushFile.filePath);
        return file.exists();
    }
}
