package com.onyx.kreader.formats.txt;

import android.util.Log;
import com.onyx.kreader.formats.encodings.Decoder;
import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.BookReader;
import com.onyx.kreader.formats.model.BookReaderContext;

import java.nio.ByteBuffer;

/**
 * Created by zengzhu on 2/28/16.
 */
public class TxtReader implements BookReader {

    private static String TAG = TxtReader.class.getSimpleName();
    private BookReaderContext myContext;
    private final int limit = 5;
    private ByteBuffer data = ByteBuffer.allocate(limit);

    static public final byte CR = '\r';
    static public final byte NL = '\n';
    static public final byte TAB = '\t';
    static public final byte SPACE = ' ';


    public boolean readNext(final BookModel bookModel, final BookReaderContext context) {
        if (!ensureOpen(context)) {
            return false;
        }
        return readNextImpl();
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

    private boolean readNextImpl() {
        int read = myContext.bookStream.read(data);
        if (read <= 0) {
            return false;
        }
        data.flip();

        int start = 0;
        int end = read;
        int ptr = start;
        while (ptr < end) {
            byte value = data.get(ptr);
            if (value == NL || value == CR) {
                boolean skipNewLine = false;
                if (value == CR && ptr + 1 < end && data.get(ptr + 1) == NL) {
                    skipNewLine = true;
                    data.put(ptr, NL);
                }
                if (start != ptr) {
                    processData(data, start, ptr + 1);
                }
                if (skipNewLine) {
                    ++ptr;
                }
                start = ptr + 1;
                processNewLine();
            } else if ((value & 0x80) == 0 && Character.isSpaceChar(value)) {
                if (value != TAB) {
                    data.put(ptr, SPACE);
                }
            } else {
            }
            ++ptr;
        }
        if (start != end) {
            processData(data, start, end);
        }
        return true;
    }

    private void processNewLine() {
        Log.i(TAG, "on new line");
    }

    private int processData(final ByteBuffer data, final int start, final int end) {
        String string = null;
        try {
            string = Decoder.decode(data);
            Log.i(TAG, string + " : " );
            return end - string.length() * 2;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }






}
