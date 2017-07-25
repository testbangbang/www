package com.onyx.einfo.glide;

import android.content.Context;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
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
