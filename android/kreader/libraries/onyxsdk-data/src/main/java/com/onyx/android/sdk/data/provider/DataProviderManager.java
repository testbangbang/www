package com.onyx.android.sdk.data.provider;

/**
 * Created by zhuzeng on 5/27/16.
 * serves as proxy between request and provider. it may forward request to real impl provider like
 * onyx android sdk or new sdk, local data provider or remote data provider.
 */
public class DataProviderManager {

    public static final String TAG = DataProviderManager.class.getSimpleName();
    private static DataProviderBase localDataProvider;
    private static DataProviderBase remoteDataProvider;

    public static DataProviderBase getDataProvider() {
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
}
