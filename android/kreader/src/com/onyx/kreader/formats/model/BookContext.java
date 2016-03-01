package com.onyx.kreader.formats.model;

import com.onyx.kreader.formats.encodings.Decoder;
import com.onyx.kreader.formats.filesystem.FileNIO;
import com.onyx.kreader.utils.StringUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;


/**
 * Created by zengzhu on 2/28/16.
 */
public class BookContext {

    public FileNIO file;
    public String encoding;
    public String fallbackEncoding = "UTF-8";
    public Decoder decoder;

    public BookContext(final String path) {
        file = new FileNIO(path);
    }

    public boolean isEncodingDetected() {
        return StringUtils.isNotBlank(encoding);
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

    public int read(final ByteBuffer data) {
        data.clear();
        int count = file.read(data);
        data.flip();
        return count;
    }

    public void decode(final ByteBuffer inBuffer, final CharBuffer outBuffer, boolean finished) {
        if (decoder != null) {
            decoder.decode(inBuffer, outBuffer, finished);
        }
    }

}
