package com.onyx.android.sdk.data.rxrequest.data.db;

import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.DataModelUtil;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.utils.CollectionUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-11-27.
 */

public class RxRecentAddRequest extends RxBaseDBRequest {
    private EventBus eventBus;
    private List<DataModel> recentAddList;
    private Map<String, CloseableReference<Bitmap>> thumbnailMap;
    private List<Metadata> list;

    public RxRecentAddRequest(DataManager dm, EventBus eventBus, List<DataModel> recentAddList) {
        super(dm);
        this.eventBus = eventBus;
        this.recentAddList = recentAddList;
    }

    @Override
    public RxRecentAddRequest call() throws Exception {
        QueryArgs queryArg = QueryBuilder.recentAddQuery();
        list = DataManagerHelper.loadMetadataListWithCache(getAppContext(), getDataManager(), queryArg, false);
        if (!CollectionUtils.isNullOrEmpty(list)) {
            loadBitmap(list);
        }
        DataModelUtil.metadataToDataModel(eventBus, recentAddList, list, thumbnailMap);
        return this;
    }

    private void loadBitmap(List<Metadata> list) {
        thumbnailMap = DataManagerHelper.loadThumbnailBitmapsWithCache(getAppContext(), getDataManager(), list);
    }

    public List<Metadata> getList() {
        return list;
    }
}
