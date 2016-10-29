package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.unicode.AlUnicode;

import java.util.ArrayList;

public class AlFilesBypassNative extends AlFilesBypass {

    private AlRandomAccessFile	raf = null;

    @Override
    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
        super.initState(file, myParent, fList);

        ident = "bypass";

        raf = new AlRandomAccessFile();

        if (raf.open(file, 0) == TAL_RESULT.OK) {
            size = raf.getSize();
            return TAL_RESULT.OK;
        } else {
            size = 0;
            return TAL_RESULT.ERROR;
        }
    }

    @Override
    public void finalize() throws Throwable {
        raf.close();
        super.finalize();
    }

    @Override
    public int getBuffer(int pos, byte[] dst, int cnt) {
        if (raf.seek(pos) == pos) {
            int r = raf.read(dst, 0, cnt);
            if (r >= 0)
                return r;
        }
        for (int i = 0; i < cnt; i++)
            dst[i] = 0;
        return cnt;
    }

}
