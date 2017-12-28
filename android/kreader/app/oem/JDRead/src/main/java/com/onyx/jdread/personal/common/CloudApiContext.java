package com.onyx.jdread.personal.common;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.common.ClientUtils;
import com.onyx.jdread.common.Constants;
import com.onyx.jdread.shop.request.JavaNetCookieJar;
import com.onyx.jdread.shop.request.PersistentCookieStore;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;

import okhttp3.OkHttpClient;

/**
 * Created by huxiaomao on 2016/12/2.
 */

public class CloudApiContext {
    public static final String JD_BOOK_SHOP_URL = "http://tob-gw.jd.com/";
    public static final String JD_BASE_URL = "https://tob-gw.jd.com/";

    public static class NewBookDetail {
        public static final String FUNCTION_ID = "functionId";
        public static final String USER_BASIC_INFO = "userBasicInfo";
        public static final String SYNC_LOGIN_INFO = "SyncLoginInfo";
    }

    public static String getJDBooxBaseUrl() {
        return JD_BOOK_SHOP_URL;
    }

    public static String getJdBaseUrl() {
        return JD_BASE_URL;
    }

    private static CookieHandler addCookie() {
        String a2 = ClientUtils.getWJLoginHelper().getA2();
        if (!StringUtils.isNullOrEmpty(a2)) {
            PersistentCookieStore persistentCookieStore = new PersistentCookieStore(JDReadApplication.getInstance());
            HttpCookie newCookie = new HttpCookie(Constants.COOKIE_KEY, a2);
            newCookie.setDomain(Constants.COOKIE_DOMAIN);
            newCookie.setPath("/");
            newCookie.setVersion(0);
            persistentCookieStore.removeAll();
            persistentCookieStore.add(null, newCookie);
            CookieHandler cookieHandler = new CookieManager(persistentCookieStore, CookiePolicy.ACCEPT_ALL);
            return cookieHandler;
        }
        return null;
    }

    public static OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(addCookie()))
                .build();
        return client;
    }
}