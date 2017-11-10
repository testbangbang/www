package com.neverland.engbook.level1;


import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.EngBookMyType.TAL_FILE_TYPE;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.unicode.AlUnicode;

import java.util.ArrayList;

public class AlFilesCHM extends AlFiles {

    private static RealCHM nchm = new RealCHM();

    private static int handleCHM = 0;
    public static int chmCodePage = 0;

    private static final int MODE_NONE = 0;
    private static final int MODE_HHC = 1;
    private static final int MODE_INDEX = 2;

    private static int getCodePage(AlFiles a) {
        a.read_pos = 0x00;
        long signature = a.getDWord();
        if (signature == 0x46535449) {
            a.read_pos = 0x14;
            long codepage = a.getDWord();
            return AlUnicode.getCodePageFromCodeLang((int)codepage);
        }

        return 0xffffffff;
    }

    public static TAL_FILE_TYPE isCHMFile(String fName, AlFiles a, ArrayList<AlFileZipEntry> fList, String ext) {
        TAL_FILE_TYPE res = EngBookMyType.TAL_FILE_TYPE.TXT;

        int codePage = getCodePage(a);
        if (codePage == 0xffffffff)
            return res;

        nchm.attachFList(fList);

        if (handleCHM != 0)
            nchm.closeRealFile(handleCHM);
        handleCHM = nchm.openRealFile(a.fileName);

        int isHTMLPresent = getStartFile(fList, ".htm", FIND_END);
        if (isHTMLPresent == LEVEL1_FILE_NOT_FOUND)
            isHTMLPresent = getStartFile(fList, ".html", FIND_END);

        if (isHTMLPresent == LEVEL1_FILE_NOT_FOUND) {
            nchm.closeRealFile(handleCHM);
            handleCHM = 0;
        }

        return handleCHM != 0 && fList.size() > 0 ? TAL_FILE_TYPE.CHM : res;
    }

    private static final int FIND_FULL = 0;
    private static final int FIND_END = 1;
    private static int/*String*/ getStartFile(ArrayList<AlFileZipEntry> fList, String endFile, int mode) {

        if (mode == FIND_END) {
            for (int i = 0; i < fList.size(); i++) {
                String fn = fList.get(i).name.toLowerCase();
                if (fn.endsWith(endFile))
                    //return fList.get(i).name;
                    return i;
            }
        } else {
            for (int i = 0; i < fList.size(); i++) {
                String fn = fList.get(i).name.toLowerCase();
                if (fn.contentEquals(endFile))
                    //return fList.get(i).name;
                    return i;
            }
        }

        //return null;
        return LEVEL1_FILE_NOT_FOUND;
    }

    @Override
    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
	    /*addon_support = true;*/
        super.initState(null, myParent, fList);

        fileName = null;

        ident = "chm";

        recordList.clear();

        chmCodePage = getCodePage(parent);
        size = 0;

        int hhcFile = getStartFile(fList, ".hhc", FIND_END);
        if (hhcFile != LEVEL1_FILE_NOT_FOUND) {
            addFilesToRecord(hhcFile, AlOneZIPRecord.SPECIAL_CHM_HHC);
        } else {
            hhcFile = getStartFile(fList, "/index.htm", FIND_FULL);
            if (hhcFile == LEVEL1_FILE_NOT_FOUND)
                hhcFile = getStartFile(fList, "/index.html", FIND_FULL);
            if (hhcFile == LEVEL1_FILE_NOT_FOUND)
                hhcFile = getStartFile(fList, "/start.htm", FIND_FULL);
            if (hhcFile == LEVEL1_FILE_NOT_FOUND)
                hhcFile = getStartFile(fList, "/start.html", FIND_FULL);

            if (hhcFile == LEVEL1_FILE_NOT_FOUND)
                hhcFile = getStartFile(fList, "/index.htm", FIND_END);
            if (hhcFile == LEVEL1_FILE_NOT_FOUND)
                hhcFile = getStartFile(fList, "/index.html", FIND_END);
            if (hhcFile == LEVEL1_FILE_NOT_FOUND)
                hhcFile = getStartFile(fList, "/start.htm", FIND_END);
            if (hhcFile == LEVEL1_FILE_NOT_FOUND)
                hhcFile = getStartFile(fList, "/start.html", FIND_END);

            if (hhcFile == LEVEL1_FILE_NOT_FOUND)
                hhcFile = getStartFile(fList, "index.htm", FIND_END);
            if (hhcFile == LEVEL1_FILE_NOT_FOUND)
                hhcFile = getStartFile(fList, "index.html", FIND_END);

            if (hhcFile != LEVEL1_FILE_NOT_FOUND)
                addFilesToRecord(hhcFile, AlOneZIPRecord.SPECIAL_NONE);
        }

        return TAL_RESULT.OK;
    }

    protected final ArrayList<AlOneZIPRecord> recordList = new ArrayList<>();

    private final StringBuilder sb = new StringBuilder();

    public final void addFilesToRecord1(String fname, int sp) {
        int num = getExternalFileNum(fname);
        addFilesToRecord(num, sp);
    }

    public final void addFilesToRecord(int num, int sp) {
        if (num != LEVEL1_FILE_NOT_FOUND && fileList.get(num).position < 0) {

            AlOneZIPRecord a = new AlOneZIPRecord();
            a.file = getExternalAbsoluteFileName(num);
            a.num = num;
            a.special = sp;
            //
            fileList.get(num).position = recordList.size();
            //

            if (a.special == AlOneZIPRecord.SPECIAL_IMAGE) {
                a.size = 0;

                String tmp = String.format("\r\n<image numfiles=\"%d\" idref=\"%d\" src=\"%s\">\r\n",
                        a.num, a.special, a.file);
                a.startSize = AlUnicode.string2utf8(tmp, null);
                a.startStr = new byte[a.startSize + 1];
                AlUnicode.string2utf8(tmp, a.startStr);
                a.endSize = 0;
            } else {
                a.size = getExternalFileSize(num);

                String tmp = String.format("\r\n<alr:extfile numfiles=\"%d\" idref=\"%d\" id=\"%s\">\r\n",
                        a.num, a.special, a.file);

                a.startSize = AlUnicode.string2utf8(tmp, null);
                a.startStr = new byte[a.startSize + 1];
                AlUnicode.string2utf8(tmp, a.startStr);

                tmp = "\r\n</alr:extfile>\r\n";
                a.endSize = AlUnicode.string2utf8(tmp, null);
                a.endStr = new byte[a.endSize + 1];
                AlUnicode.string2utf8(tmp, a.endStr);
            }

            recordList.add(a);

            size += a.size + a.startSize + a.endSize;
        }
    }

    @Override
    protected final int getBuffer(int pos, byte[] dst, int cnt) {
        int out_num = 0, ps = 0, j;// , out_max = cnt;
        AlOneZIPRecord oc;
        int	recLength = recordList.size();

        for (int i = 0; i < recLength; i++) {

            oc = recordList.get(i);

            if (oc.startSize != 0) {
                if ((pos + out_num >= ps) && (pos + out_num < ps + oc.startSize)) {
                    for (j = pos + out_num - ps; j < oc.startSize; j++) {
                        dst[out_num++] = (byte) oc.startStr[j];
                        if (out_num >= cnt)
                            return out_num;
                    }
                }
                ps += oc.startSize;
            }

            if (oc.size != 0) {
                if ((pos + out_num >= ps) && (pos + out_num < ps + oc.size)) {
                    int src_start = pos + out_num - ps;
                    int dst_start = out_num;
                    int c = Math.min(cnt - out_num, ps + oc.size - pos - out_num);
                    if (fillBufFromExternalFile(oc.num, src_start, dst, dst_start, c))
                        out_num += c;
                    if (out_num >= cnt)
                        return out_num;
                }
                ps += oc.size;
            }

            if (oc.endSize != 0) {
                if ((pos + out_num >= ps) && (pos + out_num < ps + oc.endSize)) {
                    for (j = pos + out_num - ps; j < oc.endSize; j++) {
                        dst[out_num++] = (byte) oc.endStr[j];
                        if (out_num >= cnt)
                            return out_num;
                    }
                }
                ps += oc.endSize;
            }
        }

        return out_num;
    }


    @Override
    public boolean fillBufFromExternalFile(int num, int pos, byte[] dst, int dst_pos, int cnt) {
        return nchm.getPointBuffer(handleCHM, fileList.get(num).name, fileList.get(num).uSize, pos, dst, dst_pos, cnt) == cnt;
    }
}
