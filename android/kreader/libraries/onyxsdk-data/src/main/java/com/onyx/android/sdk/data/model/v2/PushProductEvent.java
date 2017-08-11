package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.converter.CloudLibraryConverter;
import com.onyx.android.sdk.data.converter.CloudMetadataConverter;
import com.onyx.android.sdk.data.db.PushDatabase;
import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.Table;

import java.io.File;


/**
 * Created by suicheng on 2017/8/3.
 */
@Table(database = PushDatabase.class, allFields = true)
public class PushProductEvent extends BaseData {
    @Column(typeConverter = CloudLibraryConverter.class)
    public CloudLibrary library;
    @Column(typeConverter = CloudMetadataConverter.class)
    public CloudMetadata metadata;
    @ColumnIgnore
    public boolean open;

    public String getFileName() {
        return metadata == null ? null : metadata.getName();
    }

    public String getType() {
        return metadata == null ? null : metadata.getType();
    }

    public String getFileNameWithExtension() {
        if (StringUtils.isNullOrEmpty(getFileName()) || StringUtils.isNullOrEmpty(getType())) {
            return null;
        }
        return getFileName() + "." + getType();
    }

    public static boolean checkOpenAfterLoaded(PushProductEvent pushFile) {
        if (!checkFileExist(pushFile)) {
            return false;
        }
        return pushFile.open;
    }

    public static boolean checkFileExist(PushProductEvent pushProduct) {
        if (pushProduct == null || pushProduct.metadata == null ||
                StringUtils.isNullOrEmpty(pushProduct.metadata.getNativeAbsolutePath())) {
            return false;
        }
        File file = new File(pushProduct.metadata.getNativeAbsolutePath());
        return file.exists();
    }
}
