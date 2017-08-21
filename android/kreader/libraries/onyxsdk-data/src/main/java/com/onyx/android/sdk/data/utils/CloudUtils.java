package com.onyx.android.sdk.data.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.model.*;
import com.onyx.android.sdk.dataprovider.BuildConfig;
import com.onyx.android.sdk.dataprovider.R;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.Map;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/8/12.
 */
public class CloudUtils {
    static private String CLOUD_BASE_DIR;

    static public final String cloudBaseDir() {
        if (CLOUD_BASE_DIR != null) {
            return CLOUD_BASE_DIR;
        }
        File dir = EnvironmentUtil.getExternalStorageDirectory();
        if (dir == null) {
            CLOUD_BASE_DIR = "/mnt/sdcard/cloud";
        } else {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            CLOUD_BASE_DIR = new File(dir, "cloud").getAbsolutePath();
        }
        return CLOUD_BASE_DIR;
    }

    static public File baseDataDir(final Context context, final String id) {
        File file = new File(cloudBaseDir(), id);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    static public File imageCacheDirectory(final Context context, final String id) {
        return baseDataDir(context, id);
    }

    static public File dataCacheDirectory(final Context context, final String id) {
        return baseDataDir(context, id);
    }

    static public File imageCachePath(final Context context, final String idString) {
        if (StringUtils.isNullOrEmpty(idString)) {
            return null;
        }
        return new File(imageCacheDirectory(context, idString), "cover.png");
    }

    static public File dataCachePath(final Context context, final String idString, final String title, final String ext) {
        if (StringUtils.isNullOrEmpty(idString)) {
            return null;
        }
        String name;
        if (!ext.startsWith(".")) {
            name = title + "." + ext;
        } else {
            name = title + ext;
        }
        File file = new File(dataCacheDirectory(context, idString), name);
        return file;
    }

    static public Bitmap localCoverCache(final Context context, final String idString) {
        File file = imageCachePath(context, idString);
        Bitmap bitmap = null;
        if (file.exists()) {
            //bitmap = BitmapUtil.loadBitmap(file.getAbsolutePath());
        }
        return bitmap;
    }

    static public Bitmap localCoverBitmap(final Context context, final String idString) {
        File file = imageCachePath(context, idString);
        Bitmap bitmap = null;
        if (file.exists()) {
            //bitmap = BitmapUtil.loadBitmap(file.getAbsolutePath());
        }
        return bitmap;
    }

    static public boolean updateFilePath(final Context context, final Product product, final GObject object) {
        final Map<String, Map<String, Link>> storage = product.storage;
        if (CollectionUtils.isNullOrEmpty(storage)) {
            return false;
        }
        for (Map.Entry<String, Map<String, Link>> entry : storage.entrySet()) {
            File file = dataCachePath(context, product.getIdString(), product.title, entry.getKey());
            if (file != null && file.exists()) {
                object.putString(GAdapterUtil.FILE_PATH, file.getAbsolutePath());
                return true;
            }
        }
        return false;
    }

    /**
     * 暂时不可用
     */
    static public boolean updateThumbnail(final Context context, final Product product, final GObject object) {
        File cover = imageCachePath(context, product.getIdString());
        if (cover != null && cover.exists()) {
            Bitmap bitmap = null;// = BitmapUtil.loadBitmap(cover.getAbsolutePath());
            if (bitmap != null) {
                object.putObject(GAdapterUtil.TAG_THUMBNAIL, bitmap);
            }
            return true;
        }
        return false;
    }

    static public final String getCoverUrl(final Product product, final CloudConf cloudConf) {
        final Map<String, Link> map = product.covers.get(Constant.COVER_TYPE_GRAY);
        if (CollectionUtils.isNullOrEmpty(map)) {
            if (product.coverUrl != null) {
                String coverUrl = product.coverUrl;
                if(!coverUrl.startsWith(cloudConf.getHostBase())){
                    coverUrl = cloudConf.getHostBase() + coverUrl;
                }
                return coverUrl;
            }
            return "";
        }

        final Link link = map.get(cloudConf.getCloudStorage());
        if (link == null) {
            if (product.coverUrl != null) {
                String coverUrl = product.coverUrl;
                if(!coverUrl.startsWith(cloudConf.getHostBase())){
                    coverUrl = cloudConf.getHostBase() + coverUrl;
                }
                return coverUrl;
            }
            return "";
        }
        return link.url;
    }

    static public GObject objectFromProduct(final Context context, final Product product, boolean loadThumbnail,
                                            final CloudConf cloudConf) {
        if (product == null) {
            return null;
        }

        GObject object = new GObject();
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, product.getIdString());
        object.putString(GAdapterUtil.TAG_TITLE_STRING, product.title);
        //object.putString(GAdapterUtil.TAG_AUTHOR_STRING, product.getAuthorString());
        //object.putInt(GAdapterUtil.TAG_READING_PROGRESS, 0);
        //object.putString(GAdapterUtil.TAG_CLOUD_REFERENCE, product.idString);
        object.putObject(GAdapterUtil.TAG_THUMBNAIL, R.drawable.cloud_file);
        object.putString(GAdapterUtil.TAG_COVER_URL, getCoverUrl(product, cloudConf));
        object.putObject(GAdapterUtil.TAG_ORIGIN_OBJ, product);
        if (loadThumbnail) {
            updateThumbnail(context, product, object);
        }
        updateFilePath(context, product, object);
        return object;
    }

    static public GObject objectFromDictionary(final Context context, final Dictionary dictionary,
                                               boolean loadThumbnail, final CloudConf cloudConf) {
        if (dictionary == null) {
            return null;
        }

        GObject object = new GObject();
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, dictionary.getIdString());
        object.putString(GAdapterUtil.TAG_TITLE_STRING, dictionary.title);
        //object.putString(GAdapterUtil.TAG_AUTHOR_STRING, dictionary.getAuthorString());
        //object.putString(GAdapterUtil.TAG_CLOUD_REFERENCE, dictionary.idString);
        object.putObject(GAdapterUtil.TAG_THUMBNAIL, R.drawable.cloud_file);
        object.putString(GAdapterUtil.TAG_COVER_URL, getCoverUrl(dictionary, cloudConf));
        object.putObject(GAdapterUtil.TAG_ORIGIN_OBJ, dictionary);
        if (loadThumbnail) {
            updateThumbnail(context, dictionary, object);
        }
        updateFilePath(context, dictionary, object);
        return object;
    }

    static public GObject objectFromContainer(final Category container) {
        if (container == null) {
            return null;
        }

        GObject object = new GObject();
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, container.getIdString());
        object.putString(GAdapterUtil.TAG_TITLE_STRING, container.name);
        //object.putString(GAdapterUtil.TAG_CLOUD_REFERENCE, container.idString);
        object.putObject(GAdapterUtil.TAG_THUMBNAIL, R.drawable.cloud_file);
        object.putObject(GAdapterUtil.TAG_ORIGIN_OBJ, container);
        //object.putBoolean(GAdapterUtil.TAG_CONTAINER, true);
        return object;
    }

    static public GAdapter adapterFromContainerResult(final ProductResult<Category> result) {
        GAdapter adapter = new GAdapter();
        if (result == null || result.list == null) {
            return adapter;
        }
        for (Category container : result.list) {
            adapter.addObject(objectFromContainer(container));
        }
        return adapter;
    }

    static public GAdapter adapterFromProductResult(final Context context, final ProductResult<Product> result,
                                                    int limit, final CloudConf cloudConf) {
        GAdapter adapter = new GAdapter();
        if (result == null || result.list == null) {
            return adapter;
        }
        int count = 0;
        for (Product product : result.list) {
            adapter.addObject(objectFromProduct(context, product, count++ < limit, cloudConf));
        }
        return adapter;
    }

    static public GAdapter adapterFromDictionaryResult(final Context context, final ProductResult<Dictionary> result,
                                                       int limit, final CloudConf cloudConf) {
        GAdapter adapter = new GAdapter();
        if (result == null || result.list == null) {
            return adapter;
        }
        int count = 0;
        for (Dictionary product : result.list) {
            adapter.addObject(objectFromDictionary(context, product, count++ < limit, cloudConf));
        }
        return adapter;
    }

    /*static public OnyxMetadata metadataFromProduct(final Product product, OnyxMetadata metadata) {
        if (metadata == null) {
            metadata = new OnyxMetadata();
        }
        metadata.setTitle(product.title);
        metadata.setName(product.name);
        metadata.setDescription(product.description);
        metadata.setCloudReference(product.idString);
        return metadata;
    }*/

    static public Product originProduct(final GObject object) {
        Object originObject = GAdapterUtil.getOriginObject(object);
        Product product = null;
        if (originObject instanceof Product) {
            product = (Product) originObject;
        }
        return product;
    }

    /*static public boolean saveProductToLibrary(final Context context, final Product product) {
        OnyxMetadata reference = OnyxCmsCenter.getMetadataByCloudReference(context, product.idString);
        OnyxMetadata result = metadataFromProduct(product, reference);
        if (reference == null) {
            OnyxCmsCenter.insertMetadata(context, result);
        } else {
            OnyxCmsCenter.updateMetadata(context, result);
        }
        final Bitmap bitmap = localCoverCache(context, product.idString);
        if (bitmap != null) {
            OnyxCmsCenter.insertThumbnail(context, result, bitmap);
        }
        return true;
    }*/

    static public <T extends BaseData> boolean isEmpty(final ProductResult<T> productResult) {
        if (productResult == null || productResult.list == null || productResult.list.size() <= 0) {
            return true;
        }
        return false;
    }

    static public void dumpResponseMessage(String tag, Response<ProductResult<Product>> response, boolean debug) {
        if (BuildConfig.DEBUG && debug) {
            Log.d(tag + ",statusCode:" + response.code(), String.valueOf(response.message()));
        }
    }
}
