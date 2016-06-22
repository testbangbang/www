package com.onyx.kreader.formats.model;

import android.util.Log;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.formats.encodings.Decoder;
import com.onyx.kreader.formats.filesystem.FileNIO;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;


/**
 * Created by zengzhu on 2/28/16.
 */
public class PlainFileReader {

    private FileNIO file;
    private String encoding;
    private String fallbackEncoding = "UTF-8";
    private Decoder decoder;

    public PlainFileReader(final String path) {
        file = new FileNIO(path);
    }

    public boolean isEncodingDetected() {
        return StringUtils.isNotBlank(encoding);
    }

    public final String getEncoding() {
        return encoding;
    }

    public boolean detectEncoding(final ByteBuffer data) {
        boolean detected = true;
        encoding = Decoder.detectEncoding(data);
        if (StringUtils.isNullOrEmpty(encoding)) {
            encoding = fallbackEncoding;
            detected = false;
        }
        decoder = Decoder.createInstance(encoding);
        return detected;
    }

    public boolean open() {
        return file.open();
    }

    public int read(final ByteBuffer data) {
        data.clear();
        int count = file.read(data);
        data.flip();
        return count;
    }

    public boolean close() {
        decoder = null;
        encoding = null;
        if (file != null) {
            file.close();
        }
        return true;
    }

    public void decodeBuffer(final ByteBuffer inBuffer, final CharBuffer outBuffer, boolean finished) {
        if (decoder != null) {
            decoder.decodeBuffer(inBuffer, outBuffer, finished);
        }
    }

}
