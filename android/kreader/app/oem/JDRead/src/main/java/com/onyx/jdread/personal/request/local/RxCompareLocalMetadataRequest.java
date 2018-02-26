package com.onyx.jdread.personal.request.local;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.LocalDataProvider;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by li on 2018/1/9.
 */

public class RxCompareLocalMetadataRequest extends RxBaseDBRequest {
    private List<Metadata> list;

    public RxCompareLocalMetadataRequest(DataManager dm, List<Metadata> list) {
        super(dm);
        this.list = list;
    }

    public List<Metadata> getList() {
        return list;
    }

    @Override
    public Object call() throws Exception {
        ListIterator<Metadata> iterator = list.listIterator();
        while (iterator.hasNext()) {
            Metadata data = iterator.next();
            Metadata metadata = getDataProvider().findMetadataByCloudId(data.getCloudId());
            if (metadata != null && StringUtils.isNotBlank(metadata.getCloudId())) {
                iterator.set(metadata);
            }
        }
        return this;
    }
}
