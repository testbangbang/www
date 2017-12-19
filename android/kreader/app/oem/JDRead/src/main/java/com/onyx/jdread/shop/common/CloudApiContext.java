package com.onyx.jdread.shop.common;

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
    public static final String JD_BOOK_SHOP_URL = "http://gw.e.jd.com/";
    public static final String JD_BASE_URL = "https://gw-e.jd.com/";

    public static class NewBookDetail {
        public static final String BOOK_SPECIAL_PRICE_TYPE = "specialPrice";
        public static final String FUNCTION_ID = "functionId";
        public static final String API_NEW_BOOK_DETAIL = "newBookDetail";
        public static final String DETAIL = "detail";
        public static final String TYPE = "type";
        public static final String BOOK_LIST = "bookList";
        public static final String BOOK_ID = "bookId";
        public static final String NEW_BOOK_REVIEW = "newBookReview";
        public static final String ADD_BOOK_COMMENT = "addBookComment";
    }

    public static class BookShopModuleList {
        public static final String SYS_ID = "sysId";
        public static final String RETURN_MESSAGE = "returnMessage";
        public static final String API_GET_MAIN_THEME_INFO = "getMainThemeInfo";
    }

    public static class BookShopModule {
        public static final String ID = "id";
        public static final String MODULE_TYPE = "moduleType";
        public static final String RETURN_MESSAGE = "returnMessage";
        public static final String MODULE_CHILD_INFO = "getModuleChildInfo";
        public static final int TODAY_SPECIAL_ID = 226;
        public static final int TODAY_SPECIAL_MODULE_TYPE = 10;
        public static final int NEW_BOOK_DELIVERY_ID = 68;
        public static final int NEW_BOOK_DELIVERY_MODULE_TYPE = 5;
        public static final int FREE_JOURNALS_ID = 181;
        public static final int FREE_JOURNALS_MODULE_TYPE = 6;
    }

    public static class CategoryList {
        public static final String CLIENT_PLATFORM = "clientPlatform";
        public static final int CLIENT_PLATFORM_VALUE = 1;
        public static final String CATEGORY_LIST = "CategoryList";
    }

    public static class RecommendList {
        public static final String BOOK_ID = "bookId";
        public static final String BOOK_DETAIL_RECOMMEND_LIST_V2 = "bookDetailRecommendListV2";
        public static final String BOOK_TYPE = "ebook";
        public static final String BOOK_TYPE_ID = "eBookId";
        public static final String PAGE_BOOK_ID = "paperBookId";
    }

    public static class SearchBook {
        public static final String SORT_TYPE = "sortType";
        public static final String SORT_SALE_DESC = "sort_sale_desc";
        public static final String PAGE_SIZE = "pageSize";
        public static final int PAGE_SIZE_COUNT = 20;
        public static final String BOOK_TYPE = "bookType";
        public static final String CURRENT_PAGE = "currentPage";
        public static final String KEY_WORD = "keyword";
        public static final String SEARCH_PAPER_BOOK = "searchPaperBook";
        public static final String SEARCH_BOOK_V2 = "searchBookV2";
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