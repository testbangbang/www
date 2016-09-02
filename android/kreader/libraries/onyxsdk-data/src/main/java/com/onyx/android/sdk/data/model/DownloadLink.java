package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.db.OnyxCloudDatabase;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by zhuzeng on 11/27/15.
 */
@Table(database = OnyxCloudDatabase.class, name = "DownloadsHistory", allFields = true)
public class DownloadLink extends BaseData {

    public String ext;
    public String provider;
    public String url;
    public String md5;
    public long expires;

    public DownloadLink() {
    }

    public DownloadLink(String ext, String url, String provider) {
        this.ext = ext;
        this.url = url;
        this.provider = provider;
    }

}
