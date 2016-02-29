package com.onyx.kreader.formats.encodings;

import org.mozilla.universalchardet.UniversalDetector;

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

    private CharsetDecoder decoderProvider = null;

    public static String detectEncoding(final ByteBuffer buffer) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(buffer.array(), 0, buffer.array().length);
        detector.dataEnd();
        return detector.getDetectedCharset();
    }

    public boolean decode(final ByteBuffer inBuffer, final CharBuffer outBuffer, boolean finished) {
        if (decoderProvider != null) {
            decoderProvider.decode(inBuffer, outBuffer, finished);
            return true;
        }
        return false;
    }

    public static Decoder createInstance(final String encoding) {
        Decoder decoder = new Decoder();
        Charset charset = Charset.forName(encoding);
        decoder.decoderProvider = charset.newDecoder();
        decoder.decoderProvider.onMalformedInput(CodingErrorAction.IGNORE).onUnmappableCharacter(CodingErrorAction.IGNORE);
        return decoder;
    }
}
