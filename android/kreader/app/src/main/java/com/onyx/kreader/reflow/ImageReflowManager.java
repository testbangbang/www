package com.onyx.kreader.reflow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.kreader.api.ReaderBitmapList;
import com.onyx.kreader.utils.FileUtils;
import com.onyx.kreader.utils.ImageUtils;

import java.io.File;
import java.io.FileOutputStream;
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
                return manager.getBitmap(subpageCacheKey(pageName, index));
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
            Log.i("reflow settings", JSON.toJSONString(manager.settings));
            if (ImageUtils.reflowScannedPage(bitmap, pageName, manager)) {
                manager.savePageMap();
            }
        }

    }

    static private final String INDEX_FILE_NAME = "reflow-index.json";
    static private final String IMG_EXTENSION = ".png";

    private Map<String, ReaderBitmapList> pageMap;
    private File cacheRoot;
    private ImageReflowSettings settings;
    private ReflowImpl impl;

    public ImageReflowManager(final File root, int dw, int dh) {
        super();
        cacheRoot = root;
        settings = ImageReflowSettings.createSettings();
        settings.dev_width = dw;
        settings.dev_height = dh;
        impl = new ReflowImpl(this);
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
            pageMap = null;
        }

        if (pageMap == null) {
            pageMap = new HashMap<String, ReaderBitmapList>();
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

    private Bitmap loadBitmapFromFile(final String key) {
        File file = cacheFilePath(cacheRoot, settings, key);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        final String path = file.getAbsolutePath() + IMG_EXTENSION;
        if (!FileUtils.fileExist(path)) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    private boolean saveBitmapToFile(final String key, Bitmap bitmap) {
        final String path = cacheFilePath(cacheRoot, settings, key).getAbsolutePath() + IMG_EXTENSION;
        return saveBitmap(bitmap, path);
    }

    static public boolean saveBitmap(Bitmap bitmap, final String path) {
        try {
            FileOutputStream out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean putBitmap(final String key, final Bitmap bitmap) {
        return saveBitmapToFile(key, bitmap);
    }

    public Bitmap getBitmap(final String key) {
        final Bitmap bitmap = loadBitmapFromFile(key);
        return bitmap;
    }

    private ReaderBitmapList getSubPageList(final String pageName) {
        ReaderBitmapList list = pageMap.get(pageName);
        if (list == null) {
            list = new ReaderBitmapList();
            pageMap.put(pageName, list);
        }
        return list;
    }

    public boolean atBegin(final String pageName) {
        return getSubPageList(pageName).atBegin();
    }

    public boolean atEnd(final String pageName) {
        return getSubPageList(pageName).atEnd();
    }

    public void moveToEnd(final String pageName) {
        getSubPageList(pageName).moveToEnd();
    }

    public void moveToBegin(final String pageName) {
        getSubPageList(pageName).moveToBegin();
    }

    public int getCurrentScreenIndex(final String pageName) {
        return getSubPageList(pageName).getCurrent();
    }

    public void moveToScreen(final String pageName, final int screenIndex) {
        getSubPageList(pageName).moveToScreen(screenIndex);
    }

    public boolean next(final String pageName) {
        return getSubPageList(pageName).next();
    }

    public boolean prev(final String pageName) {
        return getSubPageList(pageName).prev();
    }

    public void clear(final String pageName) {
        getSubPageList(pageName).clear();
    }

    static public final String subpageCacheKey(final String pageKey, int index) {
        return String.format("%s-%d", pageKey, index);
    }

    static public final String pageKey(int page) {
        return String.valueOf(page);
    }

    public Bitmap getCurrentBitmap(final String pageName) {
        return impl.getCurrentBitmap(pageName);
    }

    public void addBitmap(final String pageName, int subPage, Bitmap bitmap) {
        ReaderBitmapList list = getSubPageList(pageName);
        list.addBitmap(bitmap);
        putBitmap(subpageCacheKey(pageName, subPage), bitmap);
        bitmap.recycle();
    }

    public void clearAllCacheFiles() {
        File[] list = cacheRoot.listFiles();
        if (list == null) {
            return;
        }

        for (File f : list) {
            f.delete();
        }
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
