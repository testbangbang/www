package com.onyx.jdread.shop.common;

import com.onyx.jdread.main.common.AppBaseInfo;
import com.onyx.jdread.personal.cloud.entity.jdbean.BoughtAndUnlimitedBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.CheckGiftBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ConsumeRecordBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetOrderStatusBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetOrderUrlResultBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetReadPreferenceBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetRechargeStatusBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GiftBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.PersonalNoteBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadForVoucherBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.RecommendUserBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.SaltResultBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.SetReadPreferenceBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.SignForVoucherBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.SyncLoginInfoBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.VerifySignBean;
import com.onyx.jdread.reader.data.ReadingDataResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookCommentsResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CartDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetVipGoodsListResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.RecommendListResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.SearchHotWord;
import com.onyx.jdread.shop.cloud.entity.jdbean.UpdateCartBean;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by li on 2018/1/9.
 */

public interface ReadContentService {

    @GET("search")
    Call<BookModelBooksResultBean> getSearchBooks(@QueryMap Map<String, String> baseInfoMap);

    @GET("category")
    Call<CategoryListResultBean> getCategoryList(@QueryMap Map<String, String> baseInfoMap);

    @POST("client.action")
    Call<GetOrderUrlResultBean> getOrderUrl(@Query(CloudApiContext.NewBookDetail.FUNCTION_ID) String functionID,
                                            @Query(AppBaseInfo.BODY_KEY) String body,
                                            @QueryMap Map<String, String> map);

    @GET(CloudApiContext.User.SYNC_INFO)
    Call<SyncLoginInfoBean> getSyncLoginInfo(@QueryMap Map<String, String> map);


    @GET(CloudApiContext.User.GET_USER_INFO)
    Call<String> getUserInfo(@QueryMap Map<String, String> map);

    @GET("now")
    Call<SaltResultBean> getSalt(@QueryMap Map<String, String> map);

    @GET("module/{f_type}/{module_id}")
    Call<BookModelBooksResultBean> getBookShopModule(@Path("f_type") int fType,
                                                     @Path("module_id") int moduleId,
                                                     @QueryMap Map<String, String> baseInfoMap);

    @GET("channel/{cid}")
    Call<BookModelConfigResultBean> getShopMainConfig(@Path("cid") int cid,
                                                      @QueryMap Map<String, String> baseInfoMap);

    @GET("rank/modules")
    Call<BookModelConfigResultBean> getBookRank(@QueryMap Map<String, String> baseInfoMap);

    @GET("ebook/{bookId}")
    Call<BookDetailResultBean> getBookDetail(@Path("bookId") long bookId,
                                             @QueryMap Map<String, String> baseInfoMap);

    @GET("search/key_word")
    Call<SearchHotWord> getSearchHot(@QueryMap Map<String, String> baseInfoMap);

    @GET("ebook/{bookId}/comment")
    Call<BookCommentsResultBean> getBookCommentsList(@Path("bookId") long bookId,
                                                     @QueryMap Map<String, String> baseInfoMap);

    @GET("ebook/{bookId}/recommend")
    Call<RecommendListResultBean> getRecommendList(@Path("bookId") long bookId,
                                                   @QueryMap Map<String, String> baseInfoMap);

    @GET("rank/{module_type}/{type}")
    Call<RecommendListResultBean> getBookRankList(@Path("module_type") int moduleType,
                                                  @Path("type") String type,
                                                  @QueryMap Map<String, String> baseInfoMap);

    @GET(CloudApiContext.BookShopURI.GET_VIP_GOOD_LIST)
    Call<GetVipGoodsListResultBean> getVipGoodList(@QueryMap Map<String, String> baseInfoMap);

    @POST(CloudApiContext.GotoOrder.CART)
    Call<UpdateCartBean> updateCart(@QueryMap Map<String, String> map,
                                    @Body RequestBody body);

    @POST(CloudApiContext.GotoOrder.CART_DETAIL)
    Call<CartDetailResultBean> getCartDetail(@QueryMap Map<String, String> map,
                                             @Body RequestBody body);

    @POST(CloudApiContext.User.READ_PREFERENCE)
    Call<SetReadPreferenceBean> setReadPreference(@QueryMap Map<String, String> map,
                                                  @Body RequestBody body);

    @POST(CloudApiContext.GotoOrder.ORDER_STEPONE)
    Call<String> getOrderInfo(@QueryMap Map<String, String> map,
                                              @Body RequestBody body);

    @GET(CloudApiContext.User.READ_PREFERENCE)
    Call<GetReadPreferenceBean> getReadPreference(@QueryMap Map<String, String> map);

    @POST(CloudApiContext.User.SIGN_CHECK)
    Call<VerifySignBean> verifySign(@QueryMap Map<String, String> map);

    @POST(CloudApiContext.User.SIGN)
    Call<SignForVoucherBean> signForVoucher(@QueryMap Map<String, String> map);

    @GET(CloudApiContext.User.READING_VOUCHER)
    Call<ReadForVoucherBean> readForVoucher(@QueryMap Map<String, String> map);

    @GET(CloudApiContext.User.USER_GIFT)
    Call<GiftBean> getGiftInfo(@QueryMap Map<String, String> map);

    @GET(CloudApiContext.User.CHECK_GIFT)
    Call<CheckGiftBean> checkGift(@QueryMap Map<String, String> map);

    @GET(CloudApiContext.User.RECOMMEND_USER)
    Call<RecommendUserBean> recommendUser(@QueryMap Map<String, String> map);

    @GET(CloudApiContext.ReadBean.RECHARGE_PACKAGE)
    Call<String> getRechargePackage(@QueryMap Map<String, String> map);

    @GET(CloudApiContext.ReadBean.RECHARGE)
    Call<String> getPayQRCode(@QueryMap Map<String, String> map);

    @GET(CloudApiContext.ReadBean.RECHARGE_STATUS)
    Call<GetRechargeStatusBean> getRechargeStatus(@QueryMap Map<String, String> map);

    @GET(CloudApiContext.ReadBean.CONSUME_RECORD)
    Call<ConsumeRecordBean> getConsumeRecord(@QueryMap Map<String, String> map);

    @GET(CloudApiContext.ReadBean.READ_BEAN_RECORD)
    Call<ConsumeRecordBean> getReadBeanRecord(@QueryMap Map<String, String> map);

    @GET("{bookId}/download")
    Call<String> getDownLoadBookInfo(@Path("bookId") long bookId,
                                     @QueryMap Map<String, String> baseInfoMap);

    @GET(CloudApiContext.User.BOUGHT_UNLIMITED_BOOKS)
    Call<BoughtAndUnlimitedBean> getBoughtAndUnlimitedBooks(@QueryMap Map<String, String> map);

    @GET(CloudApiContext.User.PERSONAL_NOTES)
    Call<PersonalNoteBean> getPersonalNotes(@QueryMap Map<String, String> map);

    @POST(CloudApiContext.ReadBean.PAY_BY_READ_BEAN)
    Call<String> payByReadBean(@QueryMap Map<String, String> baseInfoMap);

    @GET(CloudApiContext.GotoOrder.ORDER_STATUS)
    Call<GetOrderStatusBean<Boolean>> payByCash(@QueryMap Map<String, String> baseInfoMap);

    @POST(CloudApiContext.User.READING_DATA)
    Call<ReadingDataResultBean> syncReadingData(@QueryMap Map<String, String> map,
                                                @Body RequestBody body);

    @POST(CloudApiContext.User.EXPORT_NOTE)
    Call<String> exportNote(@QueryMap Map<String, String> map,
                            @Body RequestBody body);
}
