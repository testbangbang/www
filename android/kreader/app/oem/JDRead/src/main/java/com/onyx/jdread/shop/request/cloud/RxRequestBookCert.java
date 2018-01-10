package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.action.DownloadAction;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CertBean;
import com.onyx.jdread.shop.model.ShopDataBundle;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by 12 on 2017/4/8.
 */

public class RxRequestBookCert extends RxBaseCloudRequest {
    private BaseRequestBean requestBean;
    private CertBean certBean;
    private BookDetailResultBean.Detail bookDetailBean;
    private ShopDataBundle dataBundle;

    public void setRequestBean(BaseRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public void setBookDetailEntity(BookDetailResultBean.Detail bookDetailBean) {
        this.bookDetailBean = bookDetailBean;
    }

    public void setDataBundle(ShopDataBundle dataBundle) {
        this.dataBundle = dataBundle;
    }

    public CertBean getCertBean() {
        return certBean;
    }

    @Override
    public Object call() throws Exception {
        if (CommonUtils.isNetworkConnected(JDReadApplication.getInstance())) {
            executeCloudRequest();
            downloadBook();
        }
        return this;
    }

    private void downloadBook() {
        String bookName = bookDetailBean.getDownLoadUrl().substring(bookDetailBean.getDownLoadUrl().lastIndexOf("/") + 1);
        String localPath = CommonUtils.getJDBooksPath() + File.separator + bookName;
        DownloadAction downloadAction = new DownloadAction(getAppContext(), bookDetailBean.getDownLoadUrl(), localPath, bookName);
        downloadAction.setBookDetailBean(bookDetailBean);
        downloadAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }

    private void executeCloudRequest() throws IOException {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_VERIFY_URL);
        Call<CertBean> call = getCall(service);
        Response<CertBean> response = call.execute();
        if (response != null) {
            certBean = response.body();
        }
    }

    private Call<CertBean> getCall(ReadContentService service) {
        return service.getBookCert(CloudApiContext.Cert.GET_CERT, requestBean.getAppBaseInfo().getRequestParamsMap(), requestBean.getBody());
    }
}
