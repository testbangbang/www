package com.onyx.android.sdk.data.provider;

import com.onyx.android.sdk.data.utils.CloudConf;


/**
 * Created by zhuzeng on 5/27/16.
 * serves as proxy between request and provider. it may forward request to real impl provider like
 * onyx android sdk or new sdk, local data provider or remote data provider.
 */
public class DataProviderManager {

    public static final String TAG = DataProviderManager.class.getSimpleName();
    private static DataProviderBase localDataProvider;
    private static DataProviderBase remoteDataProvider;
    private static CloudDataProvider cloudDataProvider;

    public static DataProviderBase getLocalDataProvider() {
        if (localDataProvider == null) {
            localDataProvider = new LocalDataProvider();
        }
        return localDataProvider;
    }

    public static DataProviderBase getRemoteDataProvider() {
        if (remoteDataProvider == null) {
            remoteDataProvider = new RemoteDataProvider();
        }
        return remoteDataProvider;
    }

    public static DataProviderBase getCloudDataProvider(CloudConf cloudConf) {
        if (remoteDataProvider == null) {
            remoteDataProvider = new CloudDataProvider(cloudConf);
        }
        return remoteDataProvider;
    }
}
