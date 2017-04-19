package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by suicheng on 2016/9/2.
 */
public class MetadataRequest extends BaseDBRequest {

    private HashSet<String> pathList = new HashSet<>();
    private List<Metadata> list = new ArrayList<>();
    private QueryArgs queryArgs;
    private long count;

    public MetadataRequest(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        count = dataManager.getRemoteContentProvider().count(getContext(), queryArgs);
        list.addAll(dataManager.getRemoteContentProvider().findMetadataByQueryArgs(getContext(), queryArgs));
        loadPathList();
    }

    private void loadPathList() {
        if (CollectionUtils.isNullOrEmpty(queryArgs.propertyList)) {
            return;
        }
        Iterator<IProperty> iterator = queryArgs.propertyList.iterator();
        while (iterator.hasNext()) {
            IProperty iProperty = iterator.next();
            if (iProperty.getNameAlias().name().equals(Metadata_Table.nativeAbsolutePath.getNameAlias().name())) {
                for (Metadata metadata : list) {
                    pathList.add(metadata.getNativeAbsolutePath());
                }
            }
        }
    }

    public final List<Metadata> getList() {
        return list;
    }

    public long getCount() {
        return count;
    }

    public final HashSet<String> getPathList() {
        return pathList;
    }

}
