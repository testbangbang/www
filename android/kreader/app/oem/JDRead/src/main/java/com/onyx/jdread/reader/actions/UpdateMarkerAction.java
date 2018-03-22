package com.onyx.jdread.reader.actions;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.reader.data.MarkerBean;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.data.UpdateMakerRequestBean;
import com.onyx.jdread.reader.data.UpdateMarkerResultBean;
import com.onyx.jdread.reader.request.RxUpdateMarkerRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by li on 2018/3/21.
 */

public class UpdateMarkerAction extends BaseReaderAction {
    private List<MarkerBean> list;
    private UpdateMarkerResultBean resultBean;

    public UpdateMarkerAction(List<MarkerBean> list) {
        this.list = list;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        UpdateMakerRequestBean requestBean = new UpdateMakerRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.setSign(baseInfo.getSignValue(CloudApiContext.NewBookDetail.BOOK_MARKER));
        String s = JSON.toJSONString(list);
        RequestBody body = RequestBody.create(MediaType.parse(Constants.PARSE_JSON_TYPE), s);
        requestBean.body = body;
        requestBean.baseInfo = baseInfo.getRequestParamsMap();

        final RxUpdateMarkerRequest rq = new RxUpdateMarkerRequest(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                resultBean = rq.getResultBean();
                RxCallback.invokeNext(baseCallback, UpdateMarkerAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                RxCallback.invokeError(baseCallback, throwable);
            }
        });
    }

    public UpdateMarkerResultBean getResultBean() {
        return resultBean;
    }
}
