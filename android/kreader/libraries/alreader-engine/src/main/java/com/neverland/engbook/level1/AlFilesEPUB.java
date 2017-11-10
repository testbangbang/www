package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.TAL_RESULT;

import java.util.ArrayList;

public class AlFilesEPUB extends AlFilesZIPRecord {

    //private String savedFileName;

    @Override
    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
	    /*addon_support = true;*/
        super.initState(LEVEL1_ZIP_FIRSTNAME_EPUB, myParent, fList);

        //savedFileName = fileName;
        fileName = null;

        ident = "epub";

        recordList.clear();

        size = 0;
        addFilesToRecord(LEVEL1_ZIP_FIRSTNAME_EPUB, AlOneZIPRecord.SPECIAL_FIRST);

        return TAL_RESULT.OK;
    }

    /*@Override
    public int getExternalFileNum(String fname) {
        return parent.getExternalFileNum(fname);
    }*/
}
