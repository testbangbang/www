package com.onyx.android.sdk.data.request.data.db;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2016/9/2.
 */
public class MetadataRequest extends BaseDBRequest {

    private HashSet<String> pathList = new HashSet<>();
    private List<Metadata> list = new ArrayList<>();
    private QueryArgs queryArgs;
    private long count;

    private boolean loadThumbnail = false;
    private Map<String, CloseableReference<Bitmap>> thumbnailBitmap = new HashMap<>();

    public MetadataRequest(QueryArgs queryArgs) {
        this.queryArgs = queryArgs;
    }

    public MetadataRequest(QueryArgs queryArgs, boolean loadThumbnail) {
        this.queryArgs = queryArgs;
        this.loadThumbnail = loadThumbnail;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        count = dataManager.getRemoteContentProvider().count(getContext(), queryArgs);
        list.addAll(dataManager.getRemoteContentProvider().findMetadataByQueryArgs(getContext(), queryArgs));
        loadThumbnails(getContext(), dataManager);
        loadPathList();
    }

    private void loadThumbnails(Context context, DataManager dataManager) {
        if (loadThumbnail) {
            thumbnailBitmap = DataManagerHelper.loadThumbnailBitmapsWithCache(context, dataManager, list);
        }
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

    public final Map<String, CloseableReference<Bitmap>> getThumbnailBitmap() {
        return thumbnailBitmap;
    }

}
