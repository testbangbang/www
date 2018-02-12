package com.onyx.jdread.reader.actions;

import android.content.Context;
import android.content.res.AssetManager;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.utils.ReaderViewUtil;

/**
 * Created by huxiaomao on 2018/2/12.
 */

public class CheckPreloadBookStateAction extends BaseReaderAction {
    private String preloadBook;
    private static final String PRELOAD_BOOK_NAME = "preload.txt";
    private static final String PRELOAD_BOOK_DIRECTORY = "/sdcard/.preload/";
    private static final String PRELOAD_BOOK_PATH = PRELOAD_BOOK_DIRECTORY + PRELOAD_BOOK_NAME;
    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        if(!FileUtils.fileExist(PRELOAD_BOOK_PATH)){
            if(!FileUtils.fileExist(PRELOAD_BOOK_DIRECTORY)){
                FileUtils.mkdirs(PRELOAD_BOOK_DIRECTORY);
            }
            createPreloadBook(readerDataHolder.getAppContext());
        }
        preloadBook = PRELOAD_BOOK_PATH;
        return;
    }

    public static void createPreloadBook(Context context) {
        String srcFileName = PRELOAD_BOOK_NAME;
        String destPath = PRELOAD_BOOK_PATH;

        AssetManager am = context.getAssets();
        ReaderViewUtil.readAssetsFile(am, srcFileName, destPath);
    }

    public String getPreloadBook() {
        return preloadBook;
    }
}
