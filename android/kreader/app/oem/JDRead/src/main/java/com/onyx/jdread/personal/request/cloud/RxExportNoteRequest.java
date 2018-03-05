package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.entity.jdbean.ExportNoteBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ExportNoteResultBean;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by li on 2018/2/28.
 */

public class RxExportNoteRequest extends RxBaseCloudRequest {
    private ExportNoteBean noteBean;
    private JDAppBaseInfo baseInfo;
    private Map<String, RequestBody> params;
    private ExportNoteResultBean resultBean;

    public RxExportNoteRequest(ExportNoteBean noteBean, JDAppBaseInfo baseInfo) {
        this.noteBean = noteBean;
        this.baseInfo = baseInfo;
    }

    @Override
    public Object call() throws Exception {
        getRequestParams();
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<ExportNoteResultBean> call = getCall(service);
        Response<ExportNoteResultBean> response = call.execute();
        if (response.isSuccessful()) {
            resultBean = response.body();
            checkResult();
        }
        return this;
    }

    private void checkResult() {
        if (resultBean != null && resultBean.result_code != 0) {
            PersonalDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
        }
    }

    private void getRequestParams() {
        params = new HashMap<>();
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody requestBody = RequestBody.create(mediaType, noteBean.file);
        params.put("jdread_note\"; filename=\"" + noteBean.file.getName(), requestBody);
        params.put("fileSize", RequestBody.create(mediaType, noteBean.fileSize));
        params.put("sendEmail", RequestBody.create(mediaType, noteBean.sendEmail));
        params.put("fileName", RequestBody.create(mediaType, noteBean.fileName));
        params.put("fileCount", RequestBody.create(mediaType, noteBean.fileCount));
    }

    private Call<ExportNoteResultBean> getCall(ReadContentService service) {
        return service.exportNote(baseInfo.getRequestParamsMap(), params);
    }

    public ExportNoteResultBean getResultBean() {
        return resultBean;
    }
}
