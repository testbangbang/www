package com.neverland.engbook.forpublic;

import com.neverland.engbook.level1.AlFilesBypassNative;

/**
 * класс-заглушка ддя работы с зашифрованными файлами
 */
public class AlFileDecrypt {
    private static final String str = "test text, simulating a file decryption";

    /*private static final*/private int size = str.length();
    /*private*/private int pos = 0;

    public int getSize() {
        return size;
    }

    public int open(String fileName, int mode) {
        if (mode != 0)
            return TAL_RESULT.ERROR;
        return realOpen(fileName);
    }

    public int seek(int newPos) {
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

    public String getFileMD5() {
        return AlFilesBypassNative.getMd5(str.getBytes());
    }
}
