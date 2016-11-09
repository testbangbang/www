package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.TAL_RESULT;

import java.util.ArrayList;

public class AlFilesPDB extends AlFiles {

    private static boolean isPDBSygnature(String str, int tp, int cr) {
        return (((((byte)str.charAt(0))) | (((byte)str.charAt(1)) << 8) | (((byte)str.charAt(2)) << 16) | ((byte)str.charAt(3)) << 24) == tp) &&
               (((((byte)str.charAt(4))) | (((byte)str.charAt(5)) << 8) | (((byte)str.charAt(6)) << 16) | ((byte)str.charAt(7)) << 24) == cr);
    }

    public static EngBookMyType.TAL_FILE_TYPE isPDBFile(String fName, AlFiles a, ArrayList<AlFileZipEntry> fList, String ext) {

        EngBookMyType.TAL_FILE_TYPE res = EngBookMyType.TAL_FILE_TYPE.TXT;

        if (ext != null && !
                (ext.equalsIgnoreCase(".pdb")
                || ext.equalsIgnoreCase(".prc")
                || ext.equalsIgnoreCase(".mobi")
                || ext.equalsIgnoreCase(".azw")
                || ext.equalsIgnoreCase(".azw3")))
            return res;

        if (a.getSize() < 256)
            return res;

        a.read_pos = 0x3c;
        int docType = (int) a.getDWord();
        a.read_pos = 0x40;
        int docCreator = (int) a.getDWord();

        if (isPDBSygnature("TEXtREAd", docType, docCreator)) {
            res = EngBookMyType.TAL_FILE_TYPE.PDB;
        } else
        if (isPDBSygnature("BOOKMOBI", docType, docCreator)) {
            res = EngBookMyType.TAL_FILE_TYPE.MOBI;
        } else
        if (isPDBSygnature(".pdfADBE", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("BVokBDIC", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("DB99DBOS", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("PNRdPPrs", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("vIMGView", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("PmDBPmDB", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("InfoINDB", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("ToGoToGo", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("SDocSilX", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("JbDbJBas", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("JfDbJFil", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("DATALSdb", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("Mdb1Mdb1", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("DataPlkr", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("DataSprd", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("SM01SMem", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("TEXtTlDc", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("InfoTlIf", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("DataTlMl", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("DataTlPt", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("dataTDBP", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("TdatTide", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("ToRaTRPW", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("zTXTGPlm", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;} else
        if (isPDBSygnature("BDOCWrdS", docType, docCreator)) { return EngBookMyType.TAL_FILE_TYPE.PDBUnk;}

        a.read_pos = 0x4c;
        docType = a.getRevWord();
        if (docType < 2)
            return EngBookMyType.TAL_FILE_TYPE.TXT;

        a.read_pos = 76;
        int CountRecords = a.getRevWord();
        int FRec = (int) a.getRevDWord();

        if (FRec < 80 || /*FRec > CountRecords * 8 + 128 || */a.size < FRec + 16) {
            res = EngBookMyType.TAL_FILE_TYPE.TXT;
        } else {
            a.read_pos = FRec;
            int ver = a.getRevWord();

            if (res == EngBookMyType.TAL_FILE_TYPE.MOBI) {
                if (ver != 1 && ver != 2 && ver != 17480)
                    res = EngBookMyType.TAL_FILE_TYPE.PDBUnk;

                a.read_pos = FRec + 0x0c;
                ver = a.getRevWord();
                if (ver != 0)
                    res = EngBookMyType.TAL_FILE_TYPE.PDBUnk;
            } else if (res == EngBookMyType.TAL_FILE_TYPE.PDB) {
                if (ver == 258 || ver == 257)
                    ver -= 256;
                if (ver != 1 && ver != 2)
                    res = EngBookMyType.TAL_FILE_TYPE.PDBUnk;
            }
        }


        return res;
    }

    protected int       numRec = 0;

    protected int	    rec0_ver;
    protected int	    rec0_res1;
    protected int	    rec0_usize;
    protected int	    rec0_nrec;
    protected int	    rec0_rsize;
    protected int	    rec0_res2;



    protected final ArrayList<AlOnePDBRecord>	recordList = new ArrayList<>();

    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
        super.initState(file, myParent, fList);

        ident = "pdb";

        int i;
        StringBuilder docName = new StringBuilder(32);
        docName.append('/');

        char ch;
        for (i = 0; i < 32; i++) {
            ch = (char)parent.getByte(i);
            if (ch >= 0x80)
                ch = '_';
            if (ch >= 0x20)
                docName.append(ch);
        }

        fileName = docName.toString();

        parent.read_pos = 0x4c;
        numRec = parent.getRevWord();

        recordList.clear();

        int maxBuffSize = 0;

        AlOnePDBRecord oc, ocPrev = null;
        for (i = 0; i < numRec; i++) {
            oc = new AlOnePDBRecord();
            oc.start = (int) parent.getRevDWord();
            parent.getRevDWord();

            if (i > 0) {
                ocPrev.len1 = oc.start - ocPrev.start;
                if (ocPrev.len1 > maxBuffSize)
                    maxBuffSize = ocPrev.len1;
            }

            ocPrev = oc;
            recordList.add(oc);
        }

        ocPrev.len1 = parent.size - ocPrev.start;
        if (ocPrev.len1 > maxBuffSize)
            maxBuffSize = ocPrev.len1;

        parent.read_pos = recordList.get(0).start;
        rec0_ver = parent.getRevWord();

        if (rec0_ver >= 257 && rec0_ver <= 258)
            rec0_ver -= 256;
        if (rec0_ver == 2)
            ident = "pdbcomp";

        rec0_res1 = parent.getRevWord();
        rec0_usize = (int) parent.getRevDWord();
        rec0_nrec = parent.getRevWord();
        rec0_rsize = parent.getRevWord();
        rec0_res2 = (int) parent.getRevDWord();

        if (rec0_rsize > maxBuffSize)
            maxBuffSize = rec0_rsize;
        /*if (maxBuffSize > (rec0_rsize << 1))
            maxBuffSize = (rec0_rsize << 1);*/

        in_buff = new byte[maxBuffSize];
        out_buff = new byte[rec0_rsize];

        if (parent.read_pos > recordList.get(1).start) {
            size = 0;
            return TAL_RESULT.ERROR;
        }

        boolean flagRealLength = true;
        if (parent.read_pos + rec0_nrec * 2 == recordList.get(1).start && rec0_ver == 2) {
            for (i = 1; i < rec0_nrec + 1; i++) {
                recordList.get(i).len2 = parent.getRevWord();
                if (recordList.get(i).len2 < 1 || recordList.get(i).len2 > rec0_rsize) {
                    flagRealLength = false;
                    break;
                }
            }
            numRec = rec0_nrec + 1;
        } else flagRealLength = false;

        size = 0;
        for (i = 1; i < numRec; i++) {
            oc = recordList.get(i);

            if (rec0_ver == 1) {
                oc.len2 = oc.len1;
                if (oc.len2 > rec0_rsize)
                    oc.len2 = rec0_rsize;
            } else
            if (!flagRealLength) {
                parent.getByteBuffer(oc.start, in_buff, oc.len1);
                oc.len2 = calcsize_decompressPDB(in_buff, oc.len1, rec0_rsize);
            }

            oc.pos = size;
            size += oc.len2;

            if (size == rec0_usize) {
                numRec = i + 1;
                break;
            }
        }

        if (size > rec0_usize)
            size = rec0_usize - 1;

        return TAL_RESULT.OK;
    }

    private byte[] in_buff = null;
    private byte[] out_buff = null;

    @Override
    protected int getBuffer(int pos, byte[] dst, int cnt) {
        //if (rec0_ver == 1)
        //    return parent.getByteBuffer(pos + data_start, dst, cnt);

        int i, j, dst_start = 0, dst_max = dst.length, len = recordList.size();
        AlOnePDBRecord oc;

        for (i = 1; i < len; i++) {
            if (dst_start >= dst_max)
                break;

            if (i == len - 1)
                i = len - 1;
            oc = recordList.get(i);

            if (pos >= oc.pos && pos < oc.pos + oc.len2) {
                if (rec0_ver == 1) {
                    parent.getByteBuffer(oc.start, out_buff, oc.len2);
                } else {
                    parent.getByteBuffer(oc.start, in_buff, oc.len1);
                    decompressPDB(in_buff, out_buff, oc.len1);
                }

                j = oc.pos + oc.len2 - pos;
                if (pos + j >= size)
                    j = size - pos;
                if (dst_start + j >= dst_max)
                    j = dst_max - dst_start;
                System.arraycopy(out_buff, pos - oc.pos, dst, dst_start, j);

                pos += j;
                dst_start += j;
            }
        }

        return dst.length;
    }

    protected static final int calcsize_decompressPDB(byte[] src, int len, int maxsize) {
        int src_num = 0;
        int dst_num = 0;

        int c, k;
        while (src_num < len && dst_num < maxsize) {
            c = (src[src_num++] & 0xff);
            if (c >= 1 && c <= 8) {
                while (c > 0 && src_num < len && dst_num < maxsize) {
                    dst_num++;
                    src_num++;
                    c--;
                }
            } else
            if (c < 0x7f) {
                dst_num++;
            } else
            if (c >= 0xc0) {
                dst_num++;
                if (dst_num < maxsize)
                    dst_num++;
            } else
            if (src_num < len) {
                c = ((c << 8) | (src[src_num++] & 0xff));
                k = ((c & 0x3fff) >> 3);
                c = (3 + (c & 0x07));
                if (dst_num - k < 0 || dst_num + c > maxsize)
                    break;
                while (c > 0 && dst_num < maxsize) {
                    dst_num++;
                    c--;
                }
            }
        }

        return dst_num;
    }

    protected static final int decompressPDB(byte[] src, byte[] dst, int len) {
        int src_num = 0;
        int dst_num = 0;
        int dst_max = dst.length;

        int c, k;
        while (src_num < len && dst_num < dst_max) {
            c = (src[src_num++] & 0xff);
            if (c >= 1 && c <= 8) {
                while (c > 0 && src_num < len && dst_num < dst_max) {
                    dst[dst_num++] = (byte) (src[src_num++] & 0xff);
                    c--;
                }
            } else
            if (c < 0x7f) {
                dst[dst_num++] = (byte) c;
            } else
            if (c >= 0xc0) {
                dst[dst_num++] = 0x20;
                if (dst_num < dst_max)
                    dst[dst_num++] = (byte) (c & 0x7f);
            } else
            if (src_num < len) {
                c = ((c << 8) | (src[src_num++] & 0xff));
                k = ((c & 0x3fff) >> 3);
                c = (3 + (c & 0x07));
                if (dst_num - k < 0 || dst_num + c > dst_max)
                    break;
                while (c > 0 && dst_num < dst_max) {
                    dst[dst_num] = dst[dst_num - k];
                    dst_num++;
                    c--;
                }
            }
        }

        return dst_num;
    }

}
