package com.onyx.jdread.shop.action;

import com.jingdong.app.reader.data.DrmTools;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.cloud.entity.DownLoadWholeBookRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.DownLoadWholeBookResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestDownLoadWholeBook;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackdeng on 2018/1/23.
 */

public class DownLoadWholeBookAction extends BaseAction {
    private long bookId;
    private DownLoadWholeBookResultBean resultBean;
    private int downLoadType;

    public DownLoadWholeBookResultBean getResultBean() {
        return resultBean;
    }

    public DownLoadWholeBookAction(long bookId, int downLoadType) {
        this.bookId = bookId;
        this.downLoadType = downLoadType;
    }

    @Override
    public void execute(ShopDataBundle dataBundle, final RxCallback rxCallback) {
        DownLoadWholeBookRequestBean requestBean = new DownLoadWholeBookRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        Map<String, String> queryArgs = new HashMap<>();
        queryArgs.put(CloudApiContext.BookDownLoad.HAS_CERT, CloudApiContext.BookDownLoad.HAS_CERT_DEFAULT_VALUE);
        queryArgs.put(CloudApiContext.BookDownLoad.TYPE, String.valueOf(downLoadType));
        queryArgs.put(CloudApiContext.BookDownLoad.IS_TOB, String.valueOf(CloudApiContext.BookDownLoad.IS_TOB_DEFAULT_VALUE));
        queryArgs.put(CloudApiContext.BookDownLoad.HARDWARE_ID, DrmTools.getHardwareId(baseInfo.getUuid()));
        baseInfo.addRequestParams(queryArgs);
        baseInfo.removeApp();
        String uri = String.format(CloudApiContext.BookShopURI.DOWN_LOAD_WHOLE_BOOK, String.valueOf(bookId));
        baseInfo.setSign(baseInfo.getSignValue(uri));
        requestBean.setAppBaseInfo(baseInfo);
        requestBean.bookId = bookId;
        requestBean.saltValue = PersonalDataBundle.getInstance().getSalt();
        RxRequestDownLoadWholeBook request = new RxRequestDownLoadWholeBook();
        request.setRequestBean(requestBean);
        request.execute(new RxCallback<RxRequestDownLoadWholeBook>() {

            @Override
            public void onNext(RxRequestDownLoadWholeBook request) {
                resultBean = request.getResultBean();
                if(rxCallback != null){
                    rxCallback.onNext(DownLoadWholeBookAction.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if(rxCallback != null){
                    rxCallback.onError(throwable);
                }
            }
        });
    }
}
