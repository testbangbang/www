package com.onyx.cloud.model;

import com.onyx.cloud.OnyxCloseDatabase;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by zhuzeng on 11/27/15.
 */
@Table(database = OnyxCloseDatabase.class, name = "DownloadsHistory", allFields = true)
public class DownloadLink extends BaseObject {

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
