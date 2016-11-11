package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.TAL_RESULT;

import java.util.ArrayList;

public class AlFilesPDBUnk extends AlFiles {

    private static final String str = "Unsupported mobi format!";

    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
        super.initState(file, myParent, fList);

        ident = "pdb unknown";
        size = str.length();
        return TAL_RESULT.OK;
    }

    @Override
    protected int getBuffer(int pos, byte[] dst, int cnt) {
        int res = 0;

        try {
            for (int i = 0; i < cnt && pos + i < size; i++) {
                dst[res] = (byte)str.charAt(pos + i);
                res++;
            }
        } catch (Exception e) {
            res = -1;
        }

        return res;
    }

}
