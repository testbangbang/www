package com.neverland.engbook.level1;


import com.neverland.engbook.forpublic.TAL_RESULT;

import java.util.ArrayList;

public class AlFilesFB3 extends AlFilesZIPRecord {

    @Override
    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
        super.initState(LEVEL1_ZIP_FIRSTNAME_FB3, myParent, fList);

        fileName = null;

        ident = "fb3";

        recordList.clear();

        size = 0;
        addFilesToRecord(LEVEL1_FB3_FILE_FORCOVER, AlOneZIPRecord.SPECIAL_CONTENT);
        addFilesToRecord(LEVEL1_FB3_FILE_CONTENTTYPES, AlOneZIPRecord.SPECIAL_CONTENT);
        addFilesToRecord(LEVEL1_FB3_FILE_RELS, AlOneZIPRecord.SPECIAL_FIRST);
        addFilesToRecord(LEVEL1_ZIP_DESCRIPTION_FB3, AlOneZIPRecord.SPECIAL_CONTENT);
        addFilesToRecord(LEVEL1_ZIP_FIRSTNAME_FB3, AlOneZIPRecord.SPECIAL_NONE);

        return TAL_RESULT.OK;
    }
}
