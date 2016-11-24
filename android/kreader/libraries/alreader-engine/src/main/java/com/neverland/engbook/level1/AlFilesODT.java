package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.TAL_RESULT;

import java.util.ArrayList;

public class AlFilesODT extends AlFilesZIPRecord{
    private static final String FILE_STYLES =  "/styles.xml";

    @Override
    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
	    /*addon_support = true;*/
        super.initState(LEVEL1_ZIP_FIRSTNAME_ODT, myParent, fList);

        fileName = null;

        ident = "odt";

        recordList.clear();

        size = 0;
        addFilesToRecord(FILE_STYLES, AlOneZIPRecord.SPECIAL_STYLE);
        addFilesToRecord(LEVEL1_ZIP_FIRSTNAME_ODT, AlOneZIPRecord.SPECIAL_NONE);

        return TAL_RESULT.OK;
    }
}
