package com.onyx.jdread.library.fileserver;

import org.nanohttpd.protocols.http.tempfiles.DefaultTempFileManagerFactory;
import org.nanohttpd.protocols.http.tempfiles.ITempFileManager;

/**
 * Created by hehai on 18-1-30.
 */

public class JDTempFileManagerFactory extends DefaultTempFileManagerFactory {

    @Override
    public ITempFileManager create() {
        return new JDTempFileManager();
    }
}
