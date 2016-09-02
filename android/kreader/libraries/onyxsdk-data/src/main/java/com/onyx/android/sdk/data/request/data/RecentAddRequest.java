package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.OrderBy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 8/31/16.
 */
public class RecentAddRequest extends BaseDataRequest {


    private volatile int limit;
    private List<Metadata> list = new ArrayList<>();

    public RecentAddRequest(int limit) {
        this.limit = limit;
    }

    public void execute(final DataManager dataManager) throws Exception {
        list.addAll(dataManager.getDataProviderManager().getDataProvider().findMetadata(getContext(),
                ConditionGroup.clause().and(Metadata_Table.progress.isNull()),
                OrderBy.fromProperty(Metadata_Table.updatedAt).descending(),
                0,
                limit));
    }

    public final List<Metadata> getList() {
        return list;
    }

}
