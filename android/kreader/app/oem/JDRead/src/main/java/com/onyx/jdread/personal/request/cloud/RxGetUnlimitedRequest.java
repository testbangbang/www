package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.jdread.personal.cloud.api.GetReadUnlimitedService;
import com.onyx.jdread.personal.cloud.entity.ReadUnlimitedRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.JDOnlineBook;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadUnlimitedResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookExtraInfoBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by li on 2018/1/8.
 */

public class RxGetUnlimitedRequest extends RxBaseCloudRequest {
    private ReadUnlimitedRequestBean requestBean;
    private ReadUnlimitedResultBean resultBean;
    private List<Metadata> unlimitedBooks = new ArrayList<>();

    public RxGetUnlimitedRequest(ReadUnlimitedRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public List<Metadata> getUnlimitedBooks() {
        return unlimitedBooks;
    }

    @Override
    public Object call() throws Exception {
        GetReadUnlimitedService service = init(CloudApiContext.JD_SMOOTH_READ_URL);
        Call<ReadUnlimitedResultBean> call = getCall(service);
        Response<ReadUnlimitedResultBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
            convertToMetadata(resultBean);
        }
        return this;
    }

    private void convertToMetadata(ReadUnlimitedResultBean resultBean) {
        if (unlimitedBooks.size() > 0) {
            unlimitedBooks.clear();
        }
        if (resultBean != null && resultBean.code == 0) {
            List<JDOnlineBook> resultList = resultBean.resultList;
            if (resultList != null && resultList.size() > 0) {
                for (int i = 0; i < resultList.size(); i++) {
                    JDOnlineBook onlineBook = resultList.get(i);
                    Metadata metadata = new Metadata();
                    metadata.setName(onlineBook.ebookName);
                    metadata.setAuthors(onlineBook.author);
                    metadata.setCloudId(String.valueOf(onlineBook.itemId));
                    metadata.setCoverUrl(onlineBook.imgUrl);
                    metadata.setSize((long) (onlineBook.size * 1000 * 1000));
                    metadata.setIdString(String.valueOf(onlineBook.itemId));
                    unlimitedBooks.add(metadata);
                }
            }
        }
    }

    private Call<ReadUnlimitedResultBean> getCall(GetReadUnlimitedService service) {
        return service.getUnlimited(CloudApiContext.AddToSmooth.SMOOTH_READ_BOOK_LIST,
                requestBean.getBody(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }

    private GetReadUnlimitedService init(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(CloudApiContext.getClient())
                .build();
        return retrofit.create(GetReadUnlimitedService.class);
    }
}
