package com.neverland.engbook.level1;

import com.neverland.engbook.unicode.AlUnicode;

import java.util.ArrayList;

public class AlFilesZIPRecord extends AlFiles {

    protected final ArrayList<AlOneZIPRecord> recordList = new ArrayList<>();

    private final StringBuilder sb = new StringBuilder();

    public final int  removeFilesFromRecord(int num) {
        if (num >= 0 && num < recordList.size()) {
            int sz = recordList.get(num).size + recordList.get(num).startSize + recordList.get(num).endSize;
            recordList.remove(num);
            size -= sz;
            return sz;
        }
        return 0;
    }


    public final int testFilesToRecord(String fname, int sp) {
        int num = parent.getExternalFileNum(fname);

        if (num != LEVEL1_FILE_NOT_FOUND) {

            AlOneZIPRecord a = new AlOneZIPRecord();
            a.file = parent.getExternalAbsoluteFileName(num);
            a.num = num;
            a.special = sp;

            if (a.special == AlOneZIPRecord.SPECIAL_IMAGE) {
                a.size = 0;

                String tmp = String.format("<image numfiles=\"%d\" idref=\"%d\" src=\"%s\"/>\r\n",
                        a.num, a.special, a.file);
                a.startSize = AlUnicode.string2utf8(tmp, null);
                a.startStr = new byte[a.startSize + 1];
                AlUnicode.string2utf8(tmp, a.startStr);
                a.endSize = 0;
            } else {
                a.size = parent.getExternalFileSize(num);

                String tmp = String.format("<alr:extfile numfiles=\"%d\" idref=\"%d\" id=\"%s\">\r\n",
                        a.num, a.special, a.file);

                a.startSize = AlUnicode.string2utf8(tmp, null);
                a.startStr = new byte[a.startSize + 1];
                AlUnicode.string2utf8(tmp, a.startStr);

                tmp = "\r\n</alr:extfile>\r\n";
                a.endSize = AlUnicode.string2utf8(tmp, null);
                a.endStr = new byte[a.endSize + 1];
                AlUnicode.string2utf8(tmp, a.endStr);
            }

            return a.size + a.startSize + a.endSize;
        }

        return 0;
    }

    public final void addFilesToRecord(String fname, int sp) {
        int num = parent.getExternalFileNum(fname);

        if (num != LEVEL1_FILE_NOT_FOUND) {

            AlOneZIPRecord a = new AlOneZIPRecord();
            a.file = parent.getExternalAbsoluteFileName(num);
            a.num = num;
            a.special = sp;

            if (a.special == AlOneZIPRecord.SPECIAL_IMAGE) {
                a.size = 0;

                String tmp = String.format("<image numfiles=\"%d\" idref=\"%d\" src=\"%s\"/>\r\n",
                        a.num, a.special, a.file);
                a.startSize = AlUnicode.string2utf8(tmp, null);
                a.startStr = new byte[a.startSize + 1];
                AlUnicode.string2utf8(tmp, a.startStr);
                a.endSize = 0;
            } else {
                a.size = parent.getExternalFileSize(num);

                String tmp = String.format("<alr:extfile numfiles=\"%d\" idref=\"%d\" id=\"%s\">\r\n",
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
    protected int getBuffer(int pos, byte[] dst, int cnt) {
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

    @Override
    public boolean fillBufFromExternalFile(int num, int pos, byte[] dst, int dst_pos, int cnt, boolean encrypted) {
        return parent.fillBufFromExternalFile(num, pos, dst, dst_pos, cnt, encrypted);
    }

    @Override
    public void	needUnpackData() {

        if (useUnpack)
            return;
        useUnpack = true;

        try {
            unpack_buffer = new byte[size];
        } catch (Exception e) {
            e.printStackTrace();
            unpack_buffer = null;
        }
        if (unpack_buffer != null) {
            getBuffer(0, unpack_buffer, size);
        } else {
            useUnpack = false;
        }
    }
}
