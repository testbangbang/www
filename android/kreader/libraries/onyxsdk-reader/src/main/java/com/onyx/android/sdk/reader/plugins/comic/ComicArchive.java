package com.onyx.android.sdk.reader.plugins.comic;

import java.io.InputStream;
import java.util.List;

/**
 * Created by joy on 3/14/16.
 */
public interface ComicArchive {
    /**
     * archive may be encrypted, so when open/extract failed, check this first
     *
     * @return
     */
    boolean isEncrypted();

    void setPassword(String password);

    boolean open(String path, String password);

    List<String> getPageList();

    InputStream getPageInputStream(String page);

    void close();
}
