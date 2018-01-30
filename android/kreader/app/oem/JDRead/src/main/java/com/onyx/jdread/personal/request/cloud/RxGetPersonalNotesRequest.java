package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.personal.cloud.entity.jdbean.PersonalNoteBean;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/30.
 */

public class RxGetPersonalNotesRequest extends RxBaseCloudRequest {
    private JDAppBaseInfo baseInfo;
    private PersonalNoteBean personalNoteBean;

    public PersonalNoteBean getPersonalNoteBean() {
        return personalNoteBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<PersonalNoteBean> call = getCall(service);
        Response<PersonalNoteBean> response = call.execute();
        if (response.isSuccessful()) {
            personalNoteBean = response.body();
            checkResult();
        }
        return this;
    }

    private void checkResult() {
        if (personalNoteBean != null && !Constants.RESULT_CODE_SUCCESS.equals(personalNoteBean.result_code)) {
            PersonalDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(personalNoteBean.message));
        }
    }

    public Call<PersonalNoteBean> getCall(ReadContentService service) {
        return service.getPersonalNotes(baseInfo.getRequestParamsMap());
    }

    public void setBaseInfo(JDAppBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
}
