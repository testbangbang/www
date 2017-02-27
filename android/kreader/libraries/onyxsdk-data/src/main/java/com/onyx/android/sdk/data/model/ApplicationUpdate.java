package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.converter.MapListStringConverter;
import com.onyx.android.sdk.data.db.OnyxCloudDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2016/9/26.
 */
@Table(database = OnyxCloudDatabase.class)
public class ApplicationUpdate extends BaseData {

    @Column
    public String channel;
    @Column
    public String packageName;
    @Column
    public String model;

    public String[] downloadUrlList;
    @Column(typeConverter = MapListStringConverter.class)
    public Map<String, List<String>> changeLogs;
    @Column
    public String type;
    @Column
    public String versionName;
    @Column
    public int versionCode;
    @Column
    public int size;
    @Column
    public String macAddress;
    @Column
    public String platform;
}
