package com.onyx.jdread.shop.common;

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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huxiaomao on 2016/12/2.
 */

public class CloudApiContext {
    public static final String JD_BOOK_SHOP_URL = "http://tob-gw.jd.com/";
    public static final String JD_BASE_URL = "https://gw-e.jd.com/";
    public static final String JD_BOOK_VERIFY_URL = "http://rights.e.jd.com/";
    public static final String JD_SMOOTH_READ_URL = "https://cread.jd.com/";
    public static final String JD_BOOK_ORDER_URL = "https://order-e.jd.com/";
    public static final String JD_BOOK_STATISTIC_URL = "https://sns-e.jd.com/";
    public static final String JD_NEW_BASE_URL = "https://eink-api.jd.com/eink/api/";


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
        public static final String ADD_BOOK_TO_SMOOTH_CARD = "addNewReadInfo";
        public static final String ADD_BOOKS_TO_SMOOTH_CARD = "addNewReadInfoBatch";
        public static final String SHOPPING_CART = "shoppingCart";
        public static final String USER_BASIC_INFO = "userBasicInfo";
        public static final String SYNC_LOGIN_INFO = "SyncLoginInfo";
        public static final String GET_TOKEN = "genToken";
        public static final String READ_TOTAL_BOOK = "userReadEBookScale";
        public static final String NEW_BOUGHT_BOOK_ORDER = "newBuyedEbookOrderList";
    }

    public static class AddToSmooth {
        public static final String EBOOK_ID = "ebook_id";
        public static final String CURRENT_PAGE = "currentPage";
        public static final String PAGE_SIZE = "pageSize";
        public static final String SMOOTH_READ_BOOK_LIST = "myNewCardReadBook";
    }

    public static class BookShopModuleList {
        public static final String SYS_ID = "sysId";
        public static final String RETURN_MESSAGE = "returnMessage";
        public static final String API_GET_MAIN_THEME_INFO = "getMainThemeInfo";
    }

    public static class BookShopModule {
        public static final String ID = "id";
        public static final String MODULE_TYPE = "moduleType";
        public static final String MODULE_ID = "moduleId";
        public static final String RETURN_MESSAGE = "returnMessage";
        public static final String MODULE_CHILD_INFO = "getReversionModuleData";
        public static final int TODAY_SPECIAL_ID = 11;
        public static final int TODAY_SPECIAL_MODULE_TYPE = 10;
        public static final int NEW_BOOK_DELIVERY_ID = 18;
        public static final int NEW_BOOK_DELIVERY_MODULE_TYPE = 6;
        public static final int FREE_JOURNALS_ID = 181;
        public static final int FREE_JOURNALS_MODULE_TYPE = 6;
        public static final int IMPORTANT_RECOMMEND_ID = 91;
        public static final int IMPORTANT_RECOMMEND_MODULE_TYPE = 6;
    }

    public static class CategoryList {
        public static final String CLIENT_PLATFORM = "clientPlatform";
        public static final int CLIENT_PLATFORM_VALUE = 1;
        public static final String CATEGORY_LIST = "CategoryList";
    }

    public static class CategoryLevel2BookList {
        public static final String SORT_TYPE = "sortType";
        public static final String PAGE_SIZE = "pageSize";
        public static final String CAT_ID = "catId";
        public static final String CURRENT_PAGE = "currentPage";
        public static final String SORT_KEY = "sortKey";
        public static final String CLIENT_PLATFORM = "clientPlatform";
        public static final String ROOT_ID = "rootId";
        public static final String CATEGORY_LEVEL2_BOOK_LIST = "categoryBookListV2";
        public static final int PAGE_SIZE_DEFAULT_VALUES = 40;
        public static final int SORT_KEY_DEFAULT_VALUES = 1;
        public static final int CLIENT_PLATFORM_DEFAULT_VALUES = 1;
        public static final int ROOT_ID_DEFAULT_VALUES = 2;
        public static final int SORT_TYPE_HOT = 1;
        public static final int SORT_TYPE_SALES = 2;
        public static final int SORT_TYPE_NEWEST = 3;
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

    public static class BookDownloadUrl {
        public static final String GET_CONTENT = "getContent";
        public static final String ORDER_ID = "orderId";
        public static final String UUID = "uuid";
        public static final String EBOOK_ID = "ebookId";
        public static final String USER_ID = "userId";
    }

    public static class Cert {
        public static final String GET_CERT = "getCert";
        public static final String ORDER_ID = "orderId";
        public static final String ORDER_TYPE = "orderType";
        public static final String DEVICE_TYPE = "deviceType";
        public static final String HAS_RANDOM = "hasRandom";
        public static final String DEVICE_MODEL = "deviceModel";
        public static final String IS_BORROW_BUY = "isBorrowBuy";
        public static final String HAS_CERT = "hasCert";
        public static final String UUID = "uuid";
        public static final String EBOOK_ID = "ebookId";
        public static final String USER_ID = "userId";
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
    }

    public static String getJDBooxBaseUrl() {
        return JD_BOOK_SHOP_URL;
    }

    public static String getJdBaseUrl() {
        return JD_BASE_URL;
    }

    public static String getJdSmoothReadUrl() {
        return JD_SMOOTH_READ_URL;
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
}
