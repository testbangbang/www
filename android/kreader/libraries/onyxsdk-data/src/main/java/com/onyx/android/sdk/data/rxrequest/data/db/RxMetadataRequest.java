package com.onyx.android.sdk.data.rxrequest.data.db;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by john on 29/10/2017.
 */

public class RxMetadataRequest extends RxBaseDataRequest {

    private HashSet<String> pathList = new HashSet<>();
    private List<Metadata> list = new ArrayList<>();
    private QueryArgs queryArgs;
    private long count;

    private boolean loadThumbnail = false;
    private Map<String, CloseableReference<Bitmap>> thumbnailBitmap = new HashMap<>();

    public RxMetadataRequest(final DataManager dataManager, QueryArgs queryArgs) {
        super(dataManager);
        this.queryArgs = queryArgs;
    }

    public RxMetadataRequest(final DataManager dataManager, QueryArgs queryArgs, boolean loadThumbnail) {
        super(dataManager);
        this.queryArgs = queryArgs;
        this.loadThumbnail = loadThumbnail;
    }

    @Override
    public RxMetadataRequest call() throws Exception {
        count = getDataManager().getRemoteContentProvider().count(getContext(), queryArgs);
        list.addAll(getDataManager().getRemoteContentProvider().findMetadataByQueryArgs(getContext(), queryArgs));
        loadThumbnails(getContext(), getDataManager());
        loadPathList();
        return this;
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
