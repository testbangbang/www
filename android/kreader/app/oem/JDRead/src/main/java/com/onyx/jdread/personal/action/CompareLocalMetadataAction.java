package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.personal.cloud.entity.jdbean.PersonalBookBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.local.RxCompareLocalMetadataRequest;
import com.onyx.jdread.shop.request.db.RxRequestMetadataQuery;

import java.util.List;

/**
 * Created by li on 2018/1/8.
 */

public class CompareLocalMetadataAction extends BaseAction {
    private List<PersonalBookBean> list;
    private List<PersonalBookBean> metadataList;

    public CompareLocalMetadataAction(List<PersonalBookBean> list) {
        this.list = list;
    }

    public List<PersonalBookBean> getMetadataList() {
        return metadataList;
    }

    @Override
    public void execute(PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        final RxCompareLocalMetadataRequest rq = new RxCompareLocalMetadataRequest(dataBundle.getDataManager(), list);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                metadataList = rq.getList();
                RxCallback.invokeNext(rxCallback, CompareLocalMetadataAction.this);
            }
        });
    }
}
