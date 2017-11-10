package com.neverland.engbook.forpublic;

/**
 * класс-заглушка ддя работы с зашифрованными файлами
 */
public class AlFileDecrypt {
    private static final String str = "test text, simulating a file decryption";

    /*private static final*/protected int size = str.length();
    /*private*/protected int pos = 0;

    public final int getSize() {
        return size;
    }

    public final int open(String fileName, int mode) {
        if (mode != 0)
            return TAL_RESULT.ERROR;
        return realOpen(fileName);
    }

    public final int seek(int newPos) {
        if (pos >= 0 && pos < size)
            pos = newPos;
        return pos;
    }

    //////////////////////////////////////////////////////////

    public int realOpen(String fileName) {
        return TAL_RESULT.OK;
    }

    public void close() {

    }

    public int read(byte[] dst, int start, int cnt) {

        int res = 0;

        try {
            for (int i = 0; i < cnt && pos + i < size; i++) {
                dst[start + res] = (byte)str.charAt(pos + i);
                res++;
            }
        } catch (Exception e) {
            res = -1;
        }

        return res;
    }

    public String getOutFileExt() {
        return ".гтлтщцт";
    }
}
