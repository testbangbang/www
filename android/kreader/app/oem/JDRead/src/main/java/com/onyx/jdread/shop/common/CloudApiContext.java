package com.onyx.jdread.shop.common;

import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.ClientUtils;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.setting.service.OnyxService;
import com.onyx.jdread.shop.cloud.cache.EnhancedCacheInterceptor;
import com.onyx.jdread.shop.request.JavaNetCookieJar;
import com.onyx.jdread.shop.request.PersistentCookieStore;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by huxiaomao on 2016/12/2.
 */

public class CloudApiContext {
    public static final String JD_BOOK_SHOP_URL = "https://eink-api.jd.com/eink/api/";
    public static final String JD_BASE_URL = "https://gw-e.jd.com/";
    public static final String JD_BOOK_ORDER_URL = "https://order-e.jd.com/";
    public static final String JD_BOOK_BASE_URI = "/eink/api/";

    public static final String ONYX_EINK_HOST = "http://oa.o-in.me:9066/";
    public static final String ONYX_EINK_API = ONYX_EINK_HOST + "api/";

    public static final String DEFAULT_COVER_PRE_FIX = "https://img10.360buyimg.com/n12/s350x350_";

    public static class User {
        public static final String SYNC_INFO = "user/sync";
        public static final String GET_USER_INFO = "user";
        public static final String READ_PREFERENCE = "user/features";
        public static final String SIGN_CHECK = "sign/check";
        public static final String SIGN = "sign";
        public static final String READING_VOUCHER = "reading/voucher";
        public static final String USER_GIFT = "gift";
        public static final String CHECK_GIFT = "check_gift";
        public static final String RECOMMEND_USER = "recommend/user";
        public static final String BOUGHT_UNLIMITED_BOOKS = "order/my_ebooks";
        public static final String PERSONAL_NOTES = "my_notes";
        public static final String READING_DATA = "reading_data";
        public static final String EXPORT_NOTE = "export_note";
    }

    public static class ReadBean {
        public static final String RECHARGE_PACKAGE = "recharge/package";
        public static final String RECHARGE = "recharge";
        public static final String RECHARGE_STATUS = "recharge/status";
        public static final String CONSUME_RECORD = "yuedou/consum";
        public static final String READ_BEAN_RECORD = "yuedou/recharge";
        public static final String PAY_BY_READ_BEAN = "order/yuedou/done";
        public static final String PAY_BY_CASH = "order/pay";
        public static final String PAY_TOKEN = "token";
    }

    public static class NewBookDetail {
        public static final String FUNCTION_ID = "functionId";
        public static final String TYPE = "type";
        public static final String BOOK_LIST = "bookList";
        public static final String GET_TOKEN = "genToken";
    }

    public static class BookShopURI {
        public static final String SHOP_MAIN_CONFIG_URI = "channel/%s";
        public static final String CATEGORY_URI = "category";
        public static final String SEARCH_URI = "search";
        public static final String HOT_SEARCH_URI = "search/key_word";
        public static final String BOOK_MODULE_URI = "module/%1s/%2s";
        public static final String BOOK_DETAIL_URI = "ebook/%s";
        public static final String BOOK_RANK_URI = "rank/modules";
        public static final String BOOK_COMMENT_LIST_URI = "ebook/%s/comment";
        public static final String BOOK_RECOMMEND_LIST_URI = "ebook/%s/recommend";
        public static final String BOOK_RANK_LIST_URI = "rank/%1s/%2s";
        public static final String DOWN_LOAD_WHOLE_BOOK = "%1s/download";
        public static final String GET_VIP_GOOD_LIST = "vip";
        public static final String GET_CHAPTER_GROUP_INFO = "net/%s/order_commit";
    }

    public static class CategoryLevel2BookList {
        public static final String PAGE_SIZE_DEFAULT_VALUES = "40";
        public static final int SORT_KEY_DEFAULT_VALUES = SearchBook.SORT_KEY_SALES;
        public static final int SORT_TYPE_DEFAULT_VALUES = SearchBook.SORT_TYPE_DESC;
    }

    public static class BookRankList {
        public static final String RANK_LIST_TIME_TYPE = "week";
    }

    public static class SearchBook {
        public static final String SEARCH_TYPE = "search_type";
        public static final String CATE_ID = "cid";
        public static final String FILTER = "filter";
        public static final String SORT = "sort";
        public static final String KEY_WORD = "key_word";
        public static final int SORT_TYPE_DESC = 1;
        public static final int SORT_TYPE_ASC = 2;
        public static final int SORT_KEY_SALES = 1;
        public static final int SORT_KEY_PRICE = 2;
        public static final int SORT_KEY_PRAISE = 3;
        public static final int SORT_KEY_TIME = 4;
        public static final String SEARCH_TYPE_BOOK_SHOP = "1";
        public static final String SEARCH_TYPE_BOOK_COMMUNITY = "2";
        public static final String PAGE_SIZE = "page_size";
        public static final String CURRENT_PAGE = "page";
        public static final int PAGE_SIZE_COUNT = 20;
        public static final int FILTER_DEFAULT = 0;
        public static final int FILTER_VIP = 1;
        public static final int FILTER_SALE = 2;
        public static final int FILTER_FREE = 3;
    }

    public static class BookDownLoad {
        public static final String HAS_CERT = "has_cert";
        public static final String BOOK_ID = "bookId";
        public static final String START_CHAPTER = "start_chapter";
        public static final String TYPE = "type";
        public static final String HARDWARE_ID = "hardware_id";
        public static final String IS_TOB = "is_tob"; //is Enterprise Edition
        public static final String HAS_CERT_DEFAULT_VALUE = "0";
        public static final int TYPE_SMOOTH_READ = 1;
        public static final int TYPE_ORDER = 2;
        public static final boolean IS_TOB_DEFAULT_VALUE = false;
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
        public static final String CART = "cart";
        public static final String CART_DETAIL = "cart/detail";
        public static final String ORDER_STEPONE = "order/stepone";
        public static final String ORDER_STATUS = "order/status";
    }

    public static String getJDBooxBaseUrl() {
        return JD_BOOK_SHOP_URL;
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

    public static ReadContentService getService(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(CloudApiContext.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ReadContentService.class);
    }

    public static ReadContentService getServiceForString(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(CloudApiContext.getClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(ReadContentService.class);
    }

    public static ReadContentService getServiceForNoLogin(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ReadContentService.class);
    }

    public static OkHttpClient getClientNoCookie() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new EnhancedCacheInterceptor())
                .build();
        return client;
    }

    public static ReadContentService getServiceNoCookie(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(CloudApiContext.getClientNoCookie())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ReadContentService.class);
    }

    public static OnyxService getOnyxService(String baseUrl) {
        return ServiceFactory.getSpecifyService(OnyxService.class, baseUrl);
    }
}
