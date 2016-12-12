package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.TAL_RESULT;

import java.util.ArrayList;

public class AlFilesDocx extends AlFilesZIPRecord {
    public static final String FILE_RELS = "/word/_rels/document.xml.rels";
    public static final String FILE_STYLES0 =  "/styles.xml";
    public static final String FILE_STYLES1 =  "/word/styles.xml";
    public static final String FILE_FOOTNOTES = "/word/footnotes.xml";
    public static final String FILE_ENDNOTES = "/word/endnotes.xml";

    @Override
    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
	    /*addon_support = true;*/
        super.initState(LEVEL1_ZIP_FIRSTNAME_EPUB, myParent, fList);

        fileName = null;

        ident = "docx";

        recordList.clear();

        size = 0;
        addFilesToRecord(FILE_RELS, AlOneZIPRecord.SPECIAL_FIRST);
        addFilesToRecord(FILE_STYLES0, AlOneZIPRecord.SPECIAL_STYLE);
        addFilesToRecord(FILE_STYLES1, AlOneZIPRecord.SPECIAL_STYLE);
        addFilesToRecord(LEVEL1_ZIP_FIRSTNAME_DOCX, AlOneZIPRecord.SPECIAL_NONE);
        addFilesToRecord(FILE_FOOTNOTES, AlOneZIPRecord.SPECIAL_FOOTNOTE);
        addFilesToRecord(FILE_ENDNOTES, AlOneZIPRecord.SPECIAL_ENDNOTE);

        return TAL_RESULT.OK;
    }
}
