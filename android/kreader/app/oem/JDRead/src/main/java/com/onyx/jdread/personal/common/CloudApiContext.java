package com.onyx.jdread.personal.common;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.ClientUtils;
import com.onyx.jdread.main.common.Constants;
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
    public static final String JD_BOOK_SHOP_URL = "https://gw-e.jd.com/";
    public static final String JD_BASE_URL = "https://gw-e.jd.com/";

    public static class NewBookDetail {
        public static final String FUNCTION_ID = "functionId";
        public static final String USER_BASIC_INFO = "userBasicInfo";
        public static final String SYNC_LOGIN_INFO = "SyncLoginInfo";
        public static final String GET_TOKEN = "genToken";
    }

    public static class GotoOrder {
        public static final String ORDER_ORDERSTEP1_ACTION = "order_orderStep1.action?";
        public static final String TOKENKEY = "tokenKey=";
        public static final String NUM = "num";
        public static final String PURCHASE_QUANTITY = "1";
        public static final String ID = "Id";
        public static final String THESKUS = "TheSkus";
        public static final String SINGLE_UNION_ID = "singleUnionId";
        public static final String SINGLE_SUB_UNION_ID = "singleSubUnionId";
        public static final String IS_SUPPORT_JS = "isSupportJs";
        public static final String BOOLEAN = "true";
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
