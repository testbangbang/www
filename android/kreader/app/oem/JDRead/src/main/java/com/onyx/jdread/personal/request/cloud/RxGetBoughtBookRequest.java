package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.api.GetBoughtService;
import com.onyx.jdread.personal.cloud.entity.ReadUnlimitedRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.BoughtBookResultBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.JDBook;
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

public class RxGetBoughtBookRequest extends RxBaseCloudRequest {
    private ReadUnlimitedRequestBean requestBean;
    private List<Metadata> boughtBooks = new ArrayList<>();

    public RxGetBoughtBookRequest(ReadUnlimitedRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public List<Metadata> getBoughtBooks() {
        return boughtBooks;
    }

    @Override
    public Object call() throws Exception {
        GetBoughtService service = init(CloudApiContext.JD_BASE_URL);
        Call<BoughtBookResultBean> call = getCall(service);
        Response<BoughtBookResultBean> response = call.execute();
        if (response.isSuccessful()) {
            BoughtBookResultBean resultBean = response.body();
            convertToMetadata(resultBean);
        }
        return this;
    }

    private void convertToMetadata(BoughtBookResultBean resultBean) {
        if (boughtBooks.size() > 0) {
            boughtBooks.clear();
        }
        if (resultBean != null && resultBean.code == 0) {
            List<JDBook> resultList = resultBean.resultList;
            for (JDBook book :resultList) {
                Metadata metadata = new Metadata();
                metadata.setName(book.name);
                metadata.setAuthors(book.author);
                metadata.setCloudId(String.valueOf(book.bookId));
                metadata.setCoverUrl(book.imgUrl);
                metadata.setSize((long) (book.size * 1000 * 1000));
                metadata.setIdString(String.valueOf(book.bookId));
                metadata.setLocation(book.fileUrl);
                boughtBooks.add(metadata);
            }
        }
    }

    private Call<BoughtBookResultBean> getCall(GetBoughtService service) {
        return service.getBoughtBook(CloudApiContext.NewBookDetail.NEW_BOUGHT_BOOK_ORDER,
                requestBean.getBody(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }

    private GetBoughtService init(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(CloudApiContext.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(GetBoughtService.class);
    }
}
