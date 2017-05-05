package com.onyx.android.eschool.glide;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.db.table.OnyxThumbnailProvider;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.model.Thumbnail_Table;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by suicheng on 2017/4/19.
 */

public class ThumbnailLoader implements StreamModelLoader<Metadata> {
    private Context context;
    private DataProviderBase dataProvider;

    public ThumbnailLoader(Context context, DataProviderBase dataProvider) {
        this.context = context;
        this.dataProvider = dataProvider;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(Metadata model, int width, int height) {
        return new ThumbnailStreamFetcher(context, dataProvider, model);
    }
}
