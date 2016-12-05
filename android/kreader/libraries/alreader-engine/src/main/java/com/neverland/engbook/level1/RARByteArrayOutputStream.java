package com.neverland.engbook.level1;

import java.io.ByteArrayOutputStream;

public class RARByteArrayOutputStream extends ByteArrayOutputStream {
    public byte[] getBuffer() {
        return buf;
    }
}
