package com.onyx.jdread.reader.request;

import android.content.Context;
import android.content.res.AssetManager;

import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.utils.ReaderViewUtil;

import java.io.File;

/**
 * Created by huxiaomao on 2018/3/9.
 */

public class CheckPreloadBookStateRequest extends ReaderBaseRequest {
    private static final String PRELOAD_BOOK_NAME = "preload.txt";
    private String preloadBook;
    private AssetManager am;
    private String preloadPath;

    public CheckPreloadBookStateRequest(Reader reader, AssetManager am, String preloadPath) {
        super(reader);
        this.am = am;
        this.preloadPath = preloadPath;
    }

    @Override
    public CheckPreloadBookStateRequest call() throws Exception {
        String bookPath = preloadPath + PRELOAD_BOOK_NAME;
        if (!FileUtils.fileExist(bookPath)) {
            createPreloadBook(bookPath);
        }
        preloadBook = bookPath;
        return this;
    }

    public void createPreloadBook(String preloadPath) {
        String srcFileName = PRELOAD_BOOK_NAME;

        ReaderViewUtil.readAssetsFile(am, srcFileName, preloadPath);
    }

    public String getPreloadBook() {
        return preloadBook;
    }
}
