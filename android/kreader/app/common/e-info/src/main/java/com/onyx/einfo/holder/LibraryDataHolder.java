package com.onyx.einfo.holder;

import android.content.Context;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.CloudQueryBuilder;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;

/**
 * Created by suicheng on 2017/4/15.
 */

public class LibraryDataHolder extends BaseDataHolder {

    private DataManager dataManager = new DataManager();
    private CloudManager cloudManager = new CloudManager();
    private LibraryViewInfo libraryViewInfo = new LibraryViewInfo();
    private LibraryViewInfo cloudViewInfo = new LibraryViewInfo() {
        @Override
        public QueryArgs generateQueryArgs(QueryArgs queryArgs) {
            return CloudQueryBuilder.generateQueryArgs(queryArgs);
        }

        @Override
        public QueryArgs generateMetadataInQueryArgs(QueryArgs queryArgs) {
            return CloudQueryBuilder.generateMetadataInQueryArgs(queryArgs);
        }
    };

    public LibraryDataHolder(Context context) {
        super(context);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public LibraryViewInfo getLibraryViewInfo() {
        return libraryViewInfo;
    }

    public void setLibraryViewInfo(LibraryViewInfo libraryViewInfo) {
        this.libraryViewInfo = libraryViewInfo;
    }

    public CloudManager getCloudManager() {
        return cloudManager;
    }

    public void setCloudManager(CloudManager cloudManager) {
        this.cloudManager = cloudManager;
    }

    public LibraryViewInfo getCloudViewInfo() {
        return cloudViewInfo;
    }

    public void setCloudViewInfo(LibraryViewInfo cloudViewInfo) {
        this.cloudViewInfo = cloudViewInfo;
    }
}
