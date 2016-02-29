package com.onyx.kreader.formats.txt;

import android.util.Log;
import com.onyx.kreader.formats.encodings.Decoder;
import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.BookReader;
import com.onyx.kreader.formats.model.BookReaderContext;
import com.onyx.kreader.utils.StringUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * Created by zengzhu on 2/28/16.
 */
public class TxtReader implements BookReader {

    private static String TAG = TxtReader.class.getSimpleName();
    private BookReaderContext myContext;
    private final int limit = 2048;
    private ByteBuffer data = ByteBuffer.allocate(limit);
    private CharBuffer result = CharBuffer.allocate(limit);
    private StringBuilder stringBuilder = new StringBuilder();

    static public final char CR = '\r';
    static public final char NL = '\n';
    static public final char TAB = '\t';
    static public final char SPACE = ' ';


    public boolean processNext(final BookModel bookModel, final BookReaderContext context) {
        if (!ensureOpen(context)) {
            return false;
        }

        if (readNext() <= 0) {
            return false;
        }

        if (myContext.encoding == null) {
            detectEncoding();
        }
        return processNextImpl();
    }

    private boolean ensureOpen(final BookReaderContext context) {
        if (myContext == null) {
            myContext = context;
            if (!myContext.bookStream.open()) {
                return false;
            }
        }
        return true;
    }

    private int readNext() {
        int count = myContext.bookStream.read(data);
        data.flip();
        return count;
    }

    private String detectEncoding() {
        myContext.encoding = Decoder.detectEncoding(data);
        if (StringUtils.isNullOrEmpty(myContext.encoding)) {
            myContext.encoding = "UTF-8";
        }
        myContext.decoder = Decoder.createInstance(myContext.encoding);
        data.rewind();
        return myContext.encoding;
    }

    private boolean processNextImpl() {
        result.clear();
        myContext.decoder.decode(data, result, false);
        result.flip();
        int start = 0;
        int end = result.length();
        int ptr = start;
        while (ptr < end) {
            char value = result.get(ptr);
            if (value == NL || value == CR) {
                boolean skipNewLine = false;
                if (value == CR && ptr + 1 < end && result.charAt(ptr + 1) == NL) {
                    skipNewLine = true;
                    result.put(ptr, NL);
                }
                if (start != ptr) {
                    processData(result, start, ptr + 1);
                }
                if (skipNewLine) {
                    ++ptr;
                }
                start = ptr + 1;
                processNewLine();
            } else if ((value & 0x80) == 0 && Character.isSpaceChar(value)) {
                if (value != TAB) {
                    result.put(ptr, SPACE);
                }
            } else {
            }
            ++ptr;
        }
        if (start != end) {
            processData(result, start, end);
        }

        data.clear();
        return true;
    }

    private void processNewLine() {
        Log.i(TAG, "on new line");
    }

    private int processData(final CharBuffer buffer, int start, int end) {
        String string = stringBuilder.append(buffer.subSequence(start, end)).toString();
        Log.i(TAG, "get string:" + string);
        stringBuilder.setLength(0);
        return 0;
    }






}
