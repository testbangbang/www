package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.TAL_RESULT;
import com.onyx.zip.ZipDecryption;

import java.util.ArrayList;

public class AlTagusFilesBypassNative extends AlFilesBypass {

    private ZipDecryption zipDecryption = null;

    public AlTagusFilesBypassNative(final String password) {
        zipDecryption = new ZipDecryption(password);
    }

    @Override
    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
        super.initState(file, myParent, fList);

        ident = "bypass";

        zipDecryption.init(file);

        if (zipDecryption.openFile() == TAL_RESULT.OK) {
            size = zipDecryption.getSize();
            return TAL_RESULT.OK;
        } else {
            size = 0;
            return TAL_RESULT.ERROR;
        }
    }

    @Override
    public void finalize() throws Throwable {
        if (zipDecryption != null)
            zipDecryption.close();
        super.finalize();
    }

    @Override
    public int getBuffer(int pos, byte[] dst, int cnt) {
        if (zipDecryption.seek(pos) == pos) {
            int r = zipDecryption.read(dst, 0, cnt);
            if (r >= 0)
                return r;
        }
        for (int i = 0; i < cnt; i++)
            dst[i] = 0;
        return cnt;
    }

}
