package com.onyx.jdread.reader.request;

import android.content.Context;
import android.content.res.AssetManager;

import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.utils.ReaderViewUtil;

/**
 * Created by john on 18/2/2018.
 */

public class PreloadReaderPluginRequest extends ReaderBaseRequest {

    private String preloadBook;
    private static final String PRELOAD_BOOK_NAME = "preload.txt";
    private static final String PRELOAD_BOOK_DIRECTORY = "/sdcard/.preload/";
    private static final String PRELOAD_BOOK_PATH = PRELOAD_BOOK_DIRECTORY + PRELOAD_BOOK_NAME;

    @Override
    public PreloadReaderPluginRequest call() throws Exception {
        final String path = ensureFileExist();

        Reader reader = new Reader(null, getAppContext());
        final DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setBookPath(path);

        final BaseOptions options = new BaseOptions();
        final ReaderPluginOptionsImpl pluginOptions = new ReaderPluginOptionsImpl();
        pluginOptions.setSyncLoading(true);
        reader.getReaderHelper().selectPlugin(getAppContext(), documentInfo, pluginOptions);
        ReaderDocument document  = reader.getReaderHelper().openDocument(path, options, pluginOptions);
        reader.getReaderHelper().saveReaderDocument(document, documentInfo);
        reader.getReaderHelper().updateViewportSize(500, 500);
        reader.getReaderHelper().gotoPosition(String.valueOf(0));
        reader.getReaderHelper().closeDocument();
        cleanup(path);
        return this;
    }

    private String ensureFileExist() {
        if(!FileUtils.fileExist(PRELOAD_BOOK_PATH)){
            if(!FileUtils.fileExist(PRELOAD_BOOK_DIRECTORY)){
                FileUtils.mkdirs(PRELOAD_BOOK_DIRECTORY);
            }
            createPreloadBook(getAppContext());
        }
        preloadBook = PRELOAD_BOOK_PATH;
        return preloadBook;
    }

    public static void createPreloadBook(Context context) {
        String srcFileName = PRELOAD_BOOK_NAME;
        String destPath = PRELOAD_BOOK_PATH;

        AssetManager am = context.getAssets();
        ReaderViewUtil.readAssetsFile(am, srcFileName, destPath);
    }

    private void cleanup(final String path) {
        FileUtils.deleteFile(path);
    }
}