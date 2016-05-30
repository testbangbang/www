package com.onyx.kreader.reflow;

import android.graphics.Bitmap;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.kreader.api.ReaderBitmapList;
import com.onyx.kreader.cache.BitmapLruCache;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.utils.FileUtils;
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

    /**
     * use standalone class to control synchronize of reflow process
     */
    private static class ReflowImpl {

        private ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>();
        private ImageReflowManager manager;

        private ExecutorService reflowExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setPriority(Thread.MIN_PRIORITY);
                return t;
            }
        });

        public ReflowImpl(ImageReflowManager manager) {
            this.manager = manager;
        }

        public Bitmap getCurrentBitmap(final String pageName) {
            synchronized (lockMap) {
                if (!lockMap.containsKey(pageName)) {
                    lockMap.put(pageName, new Object());
                }
            }
            synchronized (lockMap.get(pageName)) {
                ReaderBitmapList list = manager.getSubPageList(pageName);
                if (list != null && list.getCurrentBitmap() != null) {
                    return list.getCurrentBitmap();
                }
                if (list.isEmpty()) {
                    return null;
                }
                int index = list.getCurrent();
                return manager.getBitmap(getKeyOfSubPage(manager.getSettings(), pageName, index));
            }
        }

        public void reflowBitmap(final Bitmap bitmap, final int pageWidth, final int pageHeight, final String pageName, final boolean precache) {
            synchronized (lockMap) {
                if (!lockMap.containsKey(pageName)) {
                    lockMap.put(pageName, new Object());
                }
            }
            synchronized (lockMap.get(pageName)) {
                if (getCurrentBitmap(pageName) != null) {
                    return;
                }

                manager.settings.page_width = pageWidth;
                manager.settings.page_height = pageHeight;

                manager.clear(pageName);
                if (!precache) {
                    reflow(bitmap, pageName);
                } else {
                    reflowExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (lockMap.get(pageName)) {
                                reflow(bitmap, pageName);
                            }
                        }
                    });
                }
            }
        }

        private void reflow(Bitmap bitmap, final String pageName) {
            Debug.d("reflow settings", JSON.toJSONString(manager.settings));
            if (ImageUtils.reflowScannedPage(bitmap, pageName, manager)) {
                manager.savePageMap();
            }
        }

    }

    static private final String INDEX_FILE_NAME = "reflow-index.json";

    /**
     * we must have space big enough to hold at least one page's reflowed bitmaps
     */
    private static final int MAX_DISK_CACHE_SIZE = 20 * 1024 * 1024;

    private Map<String, ReaderBitmapList> pageMap;
    private File cacheRoot;
    private BitmapLruCache bitmapCache;
    private ImageReflowSettings settings;
    private ReflowImpl impl;

    public ImageReflowManager(final File root, int dw, int dh) {
        super();
        cacheRoot = root;
        settings = ImageReflowSettings.createSettings();
        settings.dev_width = dw;
        settings.dev_height = dh;
        impl = new ReflowImpl(this);

        BitmapLruCache.Builder builder = new BitmapLruCache.Builder();
        builder.setMemoryCacheEnabled(false)
                .setDiskCacheEnabled(true)
                .setDiskCacheLocation(root)
                .setDiskCacheMaxSize(MAX_DISK_CACHE_SIZE);
        bitmapCache = builder.build();
    }

    public ImageReflowSettings getSettings() {
        return settings;
    }

    static public File cacheFilePath(final File root, final ImageReflowSettings settings, final String fileName) {
        if (settings == null || root == null) {
            return null;
        }
        File file = new File(root, settings.md5() + "-" + fileName);
        return file;
    }

    public void updateSettings(final ImageReflowSettings s) {
        settings.update(s);
        pageMap = null;
        loadPageMap();
    }

    public void reflowBitmap(Bitmap bitmap, int pageWidth, int pageHeight, final String pageName, boolean precache) {
        impl.reflowBitmap(bitmap, pageWidth, pageHeight, pageName, precache);
    }

    public void loadPageMap() {
        if (pageMap != null) {
            return;
        }

        try {
            File file = cacheFilePath(cacheRoot, settings, INDEX_FILE_NAME);
            if (file != null && FileUtils.fileExist(file.getAbsolutePath())) {
                String json = FileUtils.readContentOfFile(file);
                pageMap = JSON.parseObject(json, new TypeReference<Map<String, ReaderBitmapList>>(){});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pageMap == null) {
            pageMap = new HashMap<>();
        }
    }

    private void savePageMap() {
        try {
            File file = cacheFilePath(cacheRoot, settings, INDEX_FILE_NAME);
            String json = JSON.toJSONString(pageMap);
            FileUtils.saveContentToFile(json, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putBitmap(final String key, Bitmap bitmap) {
        bitmapCache.put(key, bitmap);
    }

    private Bitmap getBitmap(final String key) {
        return bitmapCache.get(key);
    }

    public ReaderBitmapList getSubPageList(final String pageName) {
        ReaderBitmapList list = pageMap.get(pageName);
        if (list == null) {
            list = new ReaderBitmapList();
            pageMap.put(pageName, list);
        }
        return list;
    }

    public Bitmap getSubPageBitmap(final String pageName, int subPage) {
        return getBitmap(getKeyOfSubPage(settings, pageName, subPage));
    }

    public void clear(final String pageName) {
        getSubPageList(pageName).clear();
    }

    static public final String getKeyOfSubPage(final ImageReflowSettings settings, final String pageName, int index) {
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
        putBitmap(getKeyOfSubPage(settings, pageName, subPage), bitmap);
        bitmap.recycle();
    }

    public void clearAllCacheFiles() {
        bitmapCache.clear();

        if (pageMap != null) {
            for(ReaderBitmapList entry : pageMap.values()) {
                if (entry != null) {
                    entry.clearAllBitmap();
                }
            }
        }
        pageMap.clear();
        savePageMap();
    }

}
