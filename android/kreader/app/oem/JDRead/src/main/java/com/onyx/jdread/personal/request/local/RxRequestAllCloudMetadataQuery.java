package com.onyx.jdread.personal.request.local;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.LocalDataProvider;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.jdread.personal.cloud.entity.jdbean.PersonalBookBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/1/6.
 */

public class RxRequestAllCloudMetadataQuery extends RxBaseDBRequest {
    private List<PersonalBookBean> datas = new ArrayList<>();

    public RxRequestAllCloudMetadataQuery(DataManager dm) {
        super(dm);
    }

    @Override
    public Object call() throws Exception {
        LocalDataProvider localDataProvider = (LocalDataProvider) getDataProvider();
        List<Metadata> metadatas = localDataProvider.findCloudMetadata();
        for (int i = 0; i < metadatas.size(); i++) {
            Metadata metadata = metadatas.get(i);
            PersonalBookBean bookBean = new PersonalBookBean();
            bookBean.metadata = metadata;
            datas.add(bookBean);
        }
        return this;
    }

    public List<PersonalBookBean> getDatas() {
        return datas;
    }
}
