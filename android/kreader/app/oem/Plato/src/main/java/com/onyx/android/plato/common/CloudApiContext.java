package com.onyx.android.plato.common;

import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.cloud.service.FastJsonConverterFactory;

import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Retrofit;

/**
 * Created by li on 2017/10/10.
 */

public class CloudApiContext {
    public static final String BASE_URL = "http://120.55.45.184:9001/";
    private static ConcurrentHashMap<String, Retrofit> retrofitMap = new ConcurrentHashMap<>();

    public static final Retrofit getRetrofit(final String baseUrl) {
        if(!retrofitMap.containsKey(baseUrl)) {
            Retrofit retrofit = getBaseRetrofitBuilder(baseUrl).build();
            retrofitMap.put(baseUrl, retrofit);
        }
        return retrofitMap.get(baseUrl);
    }

    public static final Retrofit.Builder getBaseRetrofitBuilder(final String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(FastJsonConverterFactory.create());
    }

    public static final ContentService getService(final String baseUrl) {
        return getRetrofit(baseUrl).create(ContentService.class);
    }

    public static class Practices{
        public static final String STATUS = "status";
        public static final String STUDENTID = "studentId";
        public static final String PAGE = "page";
        public static final String SIZE = "size";
        public static final String COURSE = "course";
        public static final String TYPE = "type";
        public static final String STARTTIME = "starttime";
        public static final String ENDTIME = "endtime";
        public static final String ID = "id";
        public static final String PID = "pid";
        public static final String UNFINISHED_STATE = "tbd";
        public static final String FINISHED_STATE = "completed";
        public static final String REPORT_STATE = "report";
        public static final String COURSE_ID = "courseId";
    }

    public static class Message{
        public static final String STUDENTID = "studentId";
        public static final String PAGE = "page";
        public static final String SIZE = "size";
        public static final String ID = "id";
    }

    public static class SubjectAbility {
        public static final String ID = "id";
        public static final String COURSE = "course";
        public static final String TERM = "term";
    }

    public static class UserInfo {
        public static final String ACCOUNT = "account";
        public static final String PASSWORD = "password";
    }

    public static class HttpReusltCode {
        public static final int RESULT_CODE_SUCCESS = 0;
        public static final String RESULT_MESSAGE_SUCCESS = "ok";
        public static final int RESULT_CODE_FAILED = 1;
    }

    public static class ChangePassword {
        public static final String ACCOUNT = "account";
        public static final String OLD_PASSWORD = "oldPassword";
        public static final String NEW_PASSWORD = "newPassword";
    }
}
