package com.neverland.engbook.level1;

import com.neverland.engbook.bookobj.AlBookEng;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.onyx.android.sdk.reader.utils.OnyxDrmUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class AlFilesEPUB extends AlFilesZIPRecord {

    private static HashSet<String> metaFileSet = new HashSet<>(Arrays.asList(new String[] {
        "container.xml", "content.opf", "toc.ncx"
    }));

    private boolean isEncrypted = false;
    private String drmManifest;
    private String drmAdditionalData;

    @Override
    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
	    /*addon_support = true;*/
        super.initState(LEVEL1_ZIP_FIRSTNAME_EPUB, myParent, fList);

        fileName = null;

        ident = "epub";

        recordList.clear();

        size = 0;
        addFilesToRecord(LEVEL1_ZIP_FIRSTNAME_EPUB, AlOneZIPRecord.SPECIAL_FIRST);

        AlFileZipEntry booxEntry = null;
        AlFileZipEntry oadEntry = null;
        for (AlFileZipEntry entry : fList) {
            if (entry.name.compareTo("/boox") == 0) {
                booxEntry = entry;
            } else if (entry.name.compareTo("/oad") == 0) {
                oadEntry = entry;
            }
            if (booxEntry != null && oadEntry != null) {
                break;
            }
        }

        if (booxEntry != null) {
            if (oadEntry != null) { // additional data is not necessary
                int oadNum = parent.getExternalFileNum(oadEntry.name);
                byte[] oad = new byte[oadEntry.uSize];
                if (fillBufFromExternalFile(oadNum, 0, oad, 0, oad.length)) {
                    drmAdditionalData = new String(oad);
                }
            }

            int booxNum = parent.getExternalFileNum(booxEntry.name);
            byte[] boox = new byte[booxEntry.uSize];
            if (fillBufFromExternalFile(booxNum, 0, boox, 0, boox.length) ) {
                drmManifest = new String(boox);
                if (!OnyxDrmUtils.setup(AlBookEng.drmDeviceId, AlBookEng.drmCertificate,
                        drmManifest, drmAdditionalData)) {
                    return TAL_RESULT.ERROR;
                }
                isEncrypted = true;
            }
        }

        return TAL_RESULT.OK;
    }

    @Override
    protected final int getBuffer(int pos, byte[] dst, int cnt) {
        int out_num = 0, ps = 0, j;// , out_max = cnt;
        AlOneZIPRecord oc;
        int	recLength = recordList.size();

        for (int i = 0; i < recLength; i++) {

            oc = recordList.get(i);

            boolean encrypted = isEncrypted && !metaFileSet.contains(getFileName(fileList.get(oc.num).name));

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
                    if (fillBufFromExternalFile(oc.num, src_start, dst, dst_start, c, encrypted))
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

    private String getFileName(final String path) {
        File file = new File(path);
        return file.getName();
    }

}
