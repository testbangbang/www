package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.TAL_RESULT;

import java.util.ArrayList;

public class JEBFilesEPUB extends AlFilesZIPRecord {

    @Override
    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
	    /*addon_support = true;*/
        super.initState(LEVEL1_ZIP_FIRSTNAME_EPUB, myParent, fList);

        fileName = null;

        ident = "jeb";

        recordList.clear();

        size = 0;
        addFilesToRecord(LEVEL1_ZIP_FIRSTNAME_EPUB, AlOneZIPRecord.SPECIAL_FIRST);

        return TAL_RESULT.OK;
    }

}
