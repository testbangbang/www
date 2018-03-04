package com.onyx.jdread.personal.request.local;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.LocalDataProvider;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.personal.cloud.entity.jdbean.PersonalBookBean;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by li on 2018/1/9.
 */

public class RxCompareLocalMetadataRequest extends RxBaseDBRequest {
    private List<PersonalBookBean> list;

    public RxCompareLocalMetadataRequest(DataManager dm, List<PersonalBookBean> list) {
        super(dm);
        this.list = list;
    }

    public List<PersonalBookBean> getList() {
        return list;
    }

    @Override
    public Object call() throws Exception {
        ListIterator<PersonalBookBean> iterator = list.listIterator();
        while (iterator.hasNext()) {
            PersonalBookBean data = iterator.next();
            Metadata metadata = getDataProvider().findMetadataByCloudId(data.metadata.getCloudId());
            if (metadata != null && StringUtils.isNotBlank(metadata.getCloudId())) {
                data.metadata = metadata;
            }
        }
        return this;
    }
}
