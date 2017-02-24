package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.AlFileDecrypt;
import com.neverland.engbook.forpublic.TAL_RESULT;

import java.util.ArrayList;

public class AlFilesBypassDecrypt extends AlFilesBypass {

    private AlFileDecrypt 		decrypt = null;

    public AlFilesBypassDecrypt(AlFileDecrypt decryptObj) {
        decrypt = decryptObj;
    }

    @Override
    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
        super.initState(file, myParent, fList);

        ident = "decrypt";

        if (decrypt.open(file, 0) == TAL_RESULT.OK) {
            size = decrypt.getSize();
            return TAL_RESULT.OK;
        } else {
            size = 0;
            return TAL_RESULT.ERROR;
        }
    }

    @Override
    public void finalize() throws Throwable {
        decrypt.close();
        super.finalize();
    }

    @Override
    public int getBuffer(int pos, byte[] dst, int cnt) {
        if (decrypt.seek(pos) == pos) {
            int r = decrypt.read(dst, 0, cnt);
            if (r >= 0)
                return r;
        }
        for (int i = 0; i < cnt; i++)
            dst[i] = 0;
        return cnt;
    }

    public String getDecriptFileExt() {
        return decrypt.getOutFileExt();
    }
}
