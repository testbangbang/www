package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.unicode.AlUnicode;

public abstract class AlFilesBypass extends AlFiles {

    @Override
    public int getExternalFileNum(String fname) {
        if (fname == null)
            return LEVEL1_FILE_NOT_FOUND;

        if (mapFile.size() == 0) {
            for (int i = 0; i < fileList.size(); i++) {
                mapFile.put(fileList.get(i).name, i);
            }
        }

        for (int j = 0; j < 2; j++) {
            fname = j == 0 ? getAbsoluteName(fileName, fname) : AlUnicode.URLDecode(fname);

            if (fname != null) {

                Integer i = mapFile.get(fname);
                if (i != null)
                    return i;
                /*for (int i = 0; i < fileList.size(); i++) {
                    if (fileList.get(i).name.contentEquals(fname)) {
                        return i;
                    }
                }*/

                int sz = AlRandomAccessFile.isFileExists(fname);
                if (sz > 0) {
                    AlFileZipEntry of = new AlFileZipEntry();
                    of.compress = 0;
                    of.cSize = sz;
                    of.uSize = sz;
                    of.flag = 0;
                    of.position = 0;
                    of.time = 0;
                    of.name = fname;
                    fileList.add(of);

                    mapFile.put(fname, fileList.size() - 1);

                    return fileList.size() - 1;
                }
            }
        }

        return LEVEL1_FILE_NOT_FOUND;
    }

    @Override
    public boolean fillBufFromExternalFile(int num, int pos, byte[] dst, int dst_pos, int cnt) {
        int res = 0;
        if (num >= 0 && num < fileList.size()) {

            if (fileList.get(num).name.contentEquals(fileName)) {
                res = getByteBuffer(pos, dst, dst_pos, cnt);
            } else {
                AlRandomAccessFile tmp = new AlRandomAccessFile();
                if (tmp.open(fileList.get(num).name, 0) == TAL_RESULT.OK) {
                    tmp.seek(pos);
                    res = tmp.read(dst, dst_pos, cnt);
                    tmp.close();
                }
                tmp = null;
            }

        }
        return res == cnt;
    }

}


