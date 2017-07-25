package com.onyx.einfo.glide;

import android.content.Context;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by suicheng on 2017/4/20.
 */

public class ThumbnailStreamFetcher implements DataFetcher<InputStream> {
    private Context context;
    private DataProviderBase dataProvider;
    private Metadata metadata;

    private boolean cancelled = false;

    public ThumbnailStreamFetcher(Context context, DataProviderBase dataProvider, Metadata metadata) {
        this.context = context;
        this.metadata = metadata;
        this.dataProvider = dataProvider;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        if (metadata == null || StringUtils.isNullOrEmpty(metadata.getAssociationId())) {
            return null;
        }
        Thumbnail thumbnail = dataProvider.getThumbnailEntry(context, metadata.getAssociationId(),
                OnyxThumbnail.ThumbnailKind.Original);
        if (thumbnail == null) {
            return null;
        }
        if (!FileUtils.fileExist(thumbnail.getImageDataPath())) {
            return null;
        }
        if (cancelled) {
            return null;
        }
        return new FileInputStream(thumbnail.getImageDataPath());
    }

    @Override
    public void cleanup() {
    }

    @Override
    public String getId() {
        return String.valueOf(metadata.getId());
    }

    @Override
    public void cancel() {
        cancelled = true;
    }
}
