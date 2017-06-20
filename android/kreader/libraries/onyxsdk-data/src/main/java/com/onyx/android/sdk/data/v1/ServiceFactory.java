package com.onyx.android.sdk.data.v1;

import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Retrofit;

/**
 * Created by zhuzeng on 8/10/16.
 */
public class ServiceFactory {
    private static ConcurrentHashMap<String, Retrofit> retrofitMap = new ConcurrentHashMap<>();

    private static Retrofit getRetrofit(String baseUrl) {
        if (!retrofitMap.containsKey(baseUrl)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .build();
            retrofitMap.put(baseUrl, retrofit);
        }
        return retrofitMap.get(baseUrl);
    }

    public static final OnyxAccountService getAccountService(final String baseUrl) {
        return getSpecifyService(OnyxAccountService.class, baseUrl);
    }

    public static final OnyxBookStoreService getBookStoreService(final String baseUrl) {
        return getSpecifyService(OnyxBookStoreService.class, baseUrl);
    }

    public static final OnyxDictionaryService getDictionaryService(final String baseUrl) {
        return getSpecifyService(OnyxDictionaryService.class, baseUrl);
    }

    public static final OnyxHardwareService getHardwareService(final String baseUrl) {
        return getSpecifyService(OnyxHardwareService.class, baseUrl);
    }

    public static final OnyxOTAService getOTAService(final String baseUrl) {
        return getSpecifyService(OnyxOTAService.class, baseUrl);
    }

    public static final OnyxFileDownloadService getFileDownloadService(final String baseUrl) {
        return getSpecifyService(OnyxFileDownloadService.class, baseUrl);
    }

    public static final OnyxConsumerService getConsumerService(final String baseUrl) {
        return getSpecifyService(OnyxConsumerService.class, baseUrl);
    }

    public static final OnyxGroupService getGroupService(final String baseUrl) {
        return getSpecifyService(OnyxGroupService.class, baseUrl);
    }

    public static final OnyxPushService getPushService(final String baseUrl) {
        return getSpecifyService(OnyxPushService.class, baseUrl);
    }

    public static final OnyxStatisticsService getStatisticsService(final String baseUrl) {
        return getSpecifyService(OnyxStatisticsService.class, baseUrl);
    }

    public static final OnyxLogService getLogService(final String baseUrl) {
        return getSpecifyService(OnyxLogService.class, baseUrl);
    }

    public static final <T> T getSpecifyService(final Class<T> service, final String baseUrl) {
        return getRetrofit(baseUrl).create(service);
    }
}
