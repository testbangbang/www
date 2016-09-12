package com.onyx.kreader.reflow;

import android.graphics.Bitmap;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.api.ReaderBitmapList;
import com.onyx.kreader.cache.BitmapDiskLruCache;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.utils.ImageUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 3/27/14
 * Time: 10:33 AM
 * cache root
 *   |--- hashcode of settings
 *               |--- index, settings and page index.
 *               |--- page number - sub page.png
 */
public class ImageReflowManager {

    public static final String TAG = ImageReflowManager.class.getSimpleName();

    static private final String INDEX_FILE_NAME = "reflow-index.json";

    /**
     * we must have space big enough to hold at least one page's reflowed bitmaps
     */
    private static final int MAX_DISK_CACHE_SIZE = 20 * 1024 * 1024;

    private Map<String, ReaderBitmapList> pageMap;
    private ConcurrentHashMap<String, Object> mapLock = new ConcurrentHashMap<>();
    private File cacheRoot;
    private BitmapDiskLruCache bitmapCache;
    private ImageReflowSettings settings;

    private ExecutorService reflowExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    });

    public ImageReflowManager(final File root, int dw, int dh) {
        super();
        cacheRoot = root;
        settings = ImageReflowSettings.createSettings();
        settings.dev_width = dw;
        settings.dev_height = dh;

        bitmapCache = BitmapDiskLruCache.create(root, MAX_DISK_CACHE_SIZE);
    }

    public ImageReflowSettings getSettings() {
        return settings;
    }

    public void updateViewportSize(int dw, int dh) {
        ImageReflowSettings copy = ImageReflowSettings.copy(settings);
        copy.dev_width = dw;
        copy.dev_height = dh;
        updateSettings(copy);
    }

    public void updateSettings(final ImageReflowSettings s) {
        settings.update(s);
        loadPageMap();
    }

    public void reflowBitmap(final Bitmap bitmap, final String pageName, final boolean background) {
        synchronized (mapLock) {
            if (!mapLock.containsKey(pageName)) {
                mapLock.put(pageName, new Object());
            }
        }

        if (!background) {
            reflowBitmapImpl(bitmap, pageName);
            return;
        }
        reflowExecutor.submit(new Runnable() {
            @Override
            public void run() {
                reflowBitmapImpl(bitmap, pageName);
            }
        });
    }

    private void reflowBitmapImpl(final Bitmap bitmap, final String pageName) {
        synchronized (mapLock.get(pageName)) {
            if (!isReflowed(pageName)) {
                reflow(bitmap, pageName);
            }
        }
    }

    public void loadPageMap() {
        synchronized (mapLock) {
            if (pageMap != null) {
                pageMap.clear();
            }
            try {
                File file = cacheFilePath(cacheRoot, settings, INDEX_FILE_NAME);
                if (file != null && FileUtils.fileExist(file.getAbsolutePath())) {
                    String json = FileUtils.readContentOfFile(file);
                    pageMap = JSON.parseObject(json, new TypeReference<Map<String, ReaderBitmapList>>() {
                    });
                }
            } catch (Exception e) {
                Debug.w(TAG, e);
            }

            if (pageMap == null) {
                pageMap = new HashMap<>();
            }
        }
    }

    public ReaderBitmapList getSubPageList(final String pageName) {
        synchronized (mapLock) {
            ReaderBitmapList list = pageMap.get(pageName);
            if (list == null) {
                list = new ReaderBitmapList();
                pageMap.put(pageName, list);
            }
            return list;
        }
    }

    public String getSubPageKey(final String pageName, int subPage) {
        return getKeyOfSubPage(settings, pageName, subPage);
    }

    public Bitmap getSubPageBitmap(final String pageName, int subPage) {
        return getBitmapCache(getKeyOfSubPage(settings, pageName, subPage));
    }

    public void clear(final String pageName) {
        getSubPageList(pageName).clear();
    }

    private static final String getKeyOfSubPage(final ImageReflowSettings settings, final String pageName, int index) {
        return String.format("%s-%s-%d", settings.md5(), pageName, index);
    }

    /**
     * will be called from jni
     *
     * @param pageName
     * @param subPage
     * @param bitmap
     */
    @SuppressWarnings("unused")
    public void addBitmap(final String pageName, int subPage, Bitmap bitmap) {
        ReaderBitmapList list = getSubPageList(pageName);
        list.addBitmap(bitmap);
        putBitmapCache(getKeyOfSubPage(settings, pageName, subPage), bitmap);
    }

    public void clearAllCacheFiles() {
        clearBitmapCache();
        clearPageMap();
    }

    private void savePageMap() {
        synchronized (mapLock) {
            try {
                File file = cacheFilePath(cacheRoot, settings, INDEX_FILE_NAME);
                String json = JSON.toJSONString(pageMap);
                FileUtils.saveContentToFile(json, file);
            } catch (Exception e) {
                Debug.w(TAG, e);
            }
        }
    }

    private void clearPageMap() {
        synchronized (mapLock) {
            if (pageMap == null) {
                return;
            }
            pageMap.clear();
            savePageMap();
        }
    }

    private boolean isReflowed(final String pageName) {
        ReaderBitmapList list = getSubPageList(pageName);
        if (list == null || list.isEmpty()) {
            return false;
        }
        Bitmap bitmap = getSubPageBitmap(pageName, list.getCurrent());
        if (bitmap == null) {
            return false;
        }
        bitmap.recycle();
        return true;
    }

    private void reflow(Bitmap bitmap, final String pageName) {
        if (ImageUtils.reflowScannedPage(bitmap, pageName, this)) {
            savePageMap();
        }
    }

    private void putBitmapCache(final String key, Bitmap bitmap) {
        bitmapCache.put(key, bitmap);
    }

    private Bitmap getBitmapCache(final String key) {
        return bitmapCache.get(key);
    }

    private void clearBitmapCache() {
        bitmapCache.clear();
    }

    private static File cacheFilePath(final File root, final ImageReflowSettings settings, final String fileName) {
        if (settings == null || root == null) {
            return null;
        }
        File file = new File(root, settings.md5() + "-" + fileName);
        return file;
    }
}
