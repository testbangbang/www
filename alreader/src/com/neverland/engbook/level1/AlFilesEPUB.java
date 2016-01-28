package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.unicode.AlUnicode;

import java.util.ArrayList;

public class AlFilesEPUB extends AlFiles {
    public static final int	SPECIAL_NONE = 0;
    public static final int	SPECIAL_FIRST = 1;
    public static final int	SPECIAL_CONTENT = 2;
    public static final int	SPECIAL_TOC = 3;
    public static final int	SPECIAL_IMAGE = 4;
    public static final int	SPECIAL_NOTE = 5;

    ArrayList<AlOneEPUBRecord>	recordList = new ArrayList<AlOneEPUBRecord>();

    class AlOneEPUBRecord {
        String		    file = null;
        int			    num = -1;
        int			    size = 0;
        int			    startSize = 0;
        int 		    endSize = 0;
        int			    special = 0;
        byte[]			startStr = null;
        byte[]			endStr = null;
    };

    @Override
    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
	/*addon_support = true;*/
        super.initState(LEVEL1_ZIP_FIRSTNAME_EPUB, myParent, fList);

        ident = "epub";

        recordList.clear();

        size = 0;
        addFilesToRecord(LEVEL1_ZIP_FIRSTNAME_EPUB, SPECIAL_FIRST);

        return TAL_RESULT.OK;
    }

    public void addFilesToRecord(String fname, int sp) {
        int num = parent.getExternalFileNum(fname);
        if (num != LEVEL1_FILE_NOT_FOUND) {

            AlOneEPUBRecord a = new AlOneEPUBRecord();
            a.file = parent.getExternalAbsoluteFileName(num);
            a.num = num;
            a.special = sp;

            if (a.special == SPECIAL_IMAGE) {
                a.size = 0;

                String tmp = String.format("\r\n<image numfiles = \"%d\" id=\"%s\" src=\"%s\"\r\n",
                        a.special, a.file, a.file);
                a.startSize = AlUnicode.string2utf8(tmp, null);
                a.startStr = new byte[a.startSize + 1];
                AlUnicode.string2utf8(tmp, a.startStr);
                a.endSize = 0;
            } else {
                a.size = parent.getExternalFileSize(num);

                String tmp = String.format("\r\n<alr:extfile numfiles=\"%d\" idref=\"%d\" id=\"%s\">\r\n",
                        a.num, a.special, a.file);
                a.startSize = AlUnicode.string2utf8(tmp, null);
                a.startStr = new byte[a.startSize + 1];
                AlUnicode.string2utf8(tmp, a.startStr);

                tmp = String.format("\r\n</alr:extfile>\r\n");
                a.endSize = AlUnicode.string2utf8(tmp, null);
                a.endStr = new byte[a.endSize + 1];
                AlUnicode.string2utf8(tmp, a.endStr);
            }

            recordList.add(a);

            size += a.size + a.startSize + a.endSize;
        }
    }

    @Override
    protected int getBuffer(int pos, byte[] dst, int cnt) {
        int out_num = 0, ps = 0, j;// , out_max = cnt;
        AlOneEPUBRecord oc;
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
                    if (parent.fillBufFromExternalFile(oc.num, src_start, dst, dst_start, c))
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
    public int getExternalFileNum(String fname) {
        return parent.getExternalFileNum(fname);
    }

    @Override
    public String getExternalAbsoluteFileName(int num) {
        return parent.getExternalAbsoluteFileName(num);
    }

    @Override
    public int getExternalFileSize(int num) {
        return parent.getExternalFileSize(num);
    }

    @Override
    public boolean fillBufFromExternalFile(int num, int pos, byte[] dst, int dst_pos, int cnt) {
        return parent.fillBufFromExternalFile(num, pos, dst, dst_pos, cnt);
    }
}
