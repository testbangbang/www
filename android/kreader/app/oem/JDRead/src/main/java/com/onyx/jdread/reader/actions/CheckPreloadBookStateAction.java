package com.onyx.jdread.reader.actions;

import android.content.Context;
import android.content.res.AssetManager;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.utils.ReaderViewUtil;

import java.io.File;

/**
 * Created by huxiaomao on 2018/2/12.
 */

public class CheckPreloadBookStateAction extends BaseReaderAction {
    private String preloadBook;
    private static final String PRELOAD_BOOK_NAME = "preload.txt";

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        String preloadPath = readerDataHolder.getAppContext().getFilesDir().getAbsolutePath() + File.separator;
        String bookPath = preloadPath + PRELOAD_BOOK_NAME;
        if(!FileUtils.fileExist(bookPath)){
            createPreloadBook(readerDataHolder.getAppContext(),bookPath);
        }
        preloadBook = bookPath;
        return;
    }

    public static void createPreloadBook(Context context,String preloadPath) {
        String srcFileName = PRELOAD_BOOK_NAME;

        AssetManager am = context.getAssets();
        ReaderViewUtil.readAssetsFile(am, srcFileName, preloadPath);
    }

    public String getPreloadBook() {
        return preloadBook;
    }
}
