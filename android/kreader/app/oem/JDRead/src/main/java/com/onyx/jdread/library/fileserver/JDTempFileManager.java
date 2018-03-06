package com.onyx.jdread.library.fileserver;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.main.common.Constants;

import org.nanohttpd.protocols.http.tempfiles.DefaultTempFile;
import org.nanohttpd.protocols.http.tempfiles.DefaultTempFileManager;
import org.nanohttpd.protocols.http.tempfiles.ITempFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 18-1-30.
 */

public class JDTempFileManager extends DefaultTempFileManager {
    private final File tmpdir = new File(Constants.WIFI_PASS_BOOK_DIR);
    private final List<ITempFile> tempFiles;

    public JDTempFileManager() {
        if (!this.tmpdir.exists()) {
            this.tmpdir.mkdirs();
        }

        this.tempFiles = new ArrayList();
    }

    @Override
    public void clear() {
        this.tempFiles.clear();
    }

    @Override
    public ITempFile createTempFile(String filename_hint) throws Exception {
        ITempFile tempFile = null;
        if (StringUtils.isNotBlank(filename_hint)) {
            tempFile = new JDTempFile(this.tmpdir, filename_hint);
            this.tempFiles.add(tempFile);
        } else {
            tempFile = new DefaultTempFile(new File(System.getProperty("java.io.tmpdir")));
            this.tempFiles.add(tempFile);
        }
        return tempFile;
    }
}
