package com.onyx.jdread.reader.actions;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.reader.data.NotesDetailBean;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.data.SyncNotesAndLineationRequestBean;
import com.onyx.jdread.reader.data.SyncNotesResultBean;
import com.onyx.jdread.reader.request.RxSyncNotesAndLineationRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by li on 2018/3/20.
 */

public class SyncNotesAndLineationAction extends BaseReaderAction {
    private long bookId;
    private List<NotesDetailBean> detailBeanList;
    private SyncNotesResultBean resultBean;

    public SyncNotesAndLineationAction(long bookId, List<NotesDetailBean> detailBeanList) {
        this.bookId = bookId;
        this.detailBeanList = detailBeanList;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        SyncNotesAndLineationRequestBean requestBean = new SyncNotesAndLineationRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String format = String.format(CloudApiContext.NewBookDetail.SYNC_NOTES, String.valueOf(bookId));
        baseInfo.setSign(baseInfo.getSignValue(format));
        String s = JSON.toJSONString(detailBeanList);
        RequestBody body = RequestBody.create(MediaType.parse(Constants.PARSE_JSON_TYPE), s);

        requestBean.baseInfoMap = baseInfo.getRequestParamsMap();
        requestBean.bookId = bookId;
        requestBean.body = body;
        final RxSyncNotesAndLineationRequest rq = new RxSyncNotesAndLineationRequest(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                resultBean = rq.getResultBean();
                RxCallback.invokeNext(baseCallback, SyncNotesAndLineationAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                RxCallback.invokeError(baseCallback, throwable);
            }
        });
    }

    public SyncNotesResultBean getResultBean() {
        return resultBean;
    }
}
