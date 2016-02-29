package com.onyx.kreader.formats.encodings;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * Created by zengzhu on 2/29/16.
 */
public class Decoder {

    public static String decode(final ByteBuffer data) {
        Charset charset = Charset.forName("GBK");
        CharsetDecoder decoder = charset.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
        final ByteBuffer inBuffer = data;
        final CharBuffer outBuffer = CharBuffer.allocate(data.capacity());
        CoderResult result = decoder.decode(inBuffer, outBuffer, false);

        // check in buffer.
        int pos = outBuffer.remaining();
        pos = inBuffer.remaining();
        data.compact();


        // read from char buffer.
        outBuffer.flip();
        StringBuilder builder = new StringBuilder();
        builder.append(outBuffer);

        return builder.toString();

    }
}
