package com.onyx.jdread.library.fileserver;

import android.util.Log;

import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.tempfiles.ITempFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by hehai on 18-1-30.
 */

public class JDTempFile implements ITempFile {
    private final File file;
    private final OutputStream fstream;

    public JDTempFile(File tempDir, String fileName) throws IOException {
        Log.e("", "JDTempFile: ===============================" + fileName);
        this.file = new File(tempDir, fileName);
        this.fstream = new FileOutputStream(this.file);
    }

    @Override
    public void delete() throws Exception {
        NanoHTTPD.safeClose(this.fstream);
        if (!this.file.delete()) {
            throw new Exception("could not delete temporary file: " + this.file.getAbsolutePath());
        }
    }

    @Override
    public String getName() {
        return this.file.getAbsolutePath();
    }

    @Override
    public OutputStream open() throws Exception {
        return this.fstream;
    }
}
