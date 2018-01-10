package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.local.RxRequestAllCloudMetadataQuery;

import java.util.List;

/**
 * Created by li on 2018/1/6.
 */

public class QueryAllCloudMetadataAction extends BaseAction {
    private List<Metadata> metadatas;

    @Override
    public void execute(PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        final RxRequestAllCloudMetadataQuery rq = new RxRequestAllCloudMetadataQuery(dataBundle.getDataManager());
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                if (rxCallback != null) {
                    metadatas = rq.getMetadatas();
                    if (metadatas != null && metadatas.size() > 0) {
                        rxCallback.onNext(QueryAllCloudMetadataAction.class);
                    }
                }
            }
        });
    }

    public List<Metadata> getMetadatas() {
        return metadatas;
    }
}
