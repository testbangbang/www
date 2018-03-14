package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.personal.cloud.entity.jdbean.BoughtAndUnlimitedBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.BoughtAndUnlimitedItemBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.PersonalBookBean;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/29.
 */

public class RxGetBoughtAndUnlimitedRequest extends RxBaseCloudRequest {
    private JDAppBaseInfo baseInfo;
    private BoughtAndUnlimitedBean resultBean;
    private List<PersonalBookBean> boughtBooks = new ArrayList<>();

    public BoughtAndUnlimitedBean getResultBean() {
        return resultBean;
    }

    public List<PersonalBookBean> getBooks() {
        return boughtBooks;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<BoughtAndUnlimitedBean> call = getCall(service);
        Response<BoughtAndUnlimitedBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
            convertToMetadata(resultBean);
        }
        return this;
    }

    private Call<BoughtAndUnlimitedBean> getCall(ReadContentService service) {
        return service.getBoughtAndUnlimitedBooks(baseInfo.getRequestParamsMap());
    }

    public void setBaseInfo(JDAppBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    private void convertToMetadata(BoughtAndUnlimitedBean resultBean) {
        if (resultBean != null && resultBean.result_code != 0) {
            PersonalDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
            return;
        }
        if (boughtBooks.size() > 0) {
            boughtBooks.clear();
        }
        if (resultBean != null && resultBean.result_code == 0) {
            BoughtAndUnlimitedBean.DataBean data = resultBean.data;
            if (data != null) {
                List<BoughtAndUnlimitedItemBean> items = data.items;
                for (BoughtAndUnlimitedItemBean bean : items) {
                    PersonalBookBean bookBean = new PersonalBookBean();
                    Metadata metadata = new Metadata();
                    String type = baseInfo.getRequestParamsMap().get(Constants.SEARCH_TYPE);
                    if (StringUtils.isNotBlank(type)) {
                        metadata.setOrdinal(Constants.TYPE_BOUGHT.equals(type) ? 0 : 1);
                    }
                    metadata.setName(bean.name);
                    metadata.setAuthors(bean.author);
                    metadata.setCloudId(String.valueOf(bean.ebook_id));
                    metadata.setCoverUrl(bean.large_image_url);
                    metadata.setSize((long) (bean.file_size * 1000 * 1000));
                    metadata.setIdString(String.valueOf(bean.ebook_id));
                    metadata.setType(bean.format);
                    bookBean.metadata = metadata;
                    bookBean.total = data.total;
                    bookBean.total_page = data.total_page;
                    boughtBooks.add(bookBean);
                }
            }
        }
    }
}
