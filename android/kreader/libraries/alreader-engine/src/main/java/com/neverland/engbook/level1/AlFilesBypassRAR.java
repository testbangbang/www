package com.neverland.engbook.level1;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.neverland.engbook.forpublic.EngBookMyType;
import com.neverland.engbook.forpublic.TAL_RESULT;
import com.neverland.engbook.unicode.AlUnicode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AlFilesBypassRAR extends AlFiles {

    private Archive rar = null;
    public Archive getBypassRAR() {
        return rar;
    }

    protected class MyByteArrayOutputStream extends ByteArrayOutputStream {
        public byte[] getBuff() {
            return buf;
        }
    }

    static public EngBookMyType.TAL_FILE_TYPE isBypassRARFile(String fName) {
        EngBookMyType.TAL_FILE_TYPE res = EngBookMyType.TAL_FILE_TYPE.TXT;

        Archive a = null;

        File f = new File(fName);

        if (!f.exists() || !f.isFile() || f.length() == 0)
            return EngBookMyType.TAL_FILE_TYPE.RARUnk;

        try {
            a = new Archive(f);
        } catch (RarException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (a != null) {
            res = EngBookMyType.TAL_FILE_TYPE.RARUnk;



            int cnt_files = 0;
            if (!a.isEncrypted() && !a.getMainHeader().isEncrypted() && !a.getMainHeader().isMultiVolume()) {
                for (FileHeader fileHeader : a.getFileHeaders()) {
                    if (!fileHeader.isDirectory() &&
                            fileHeader.getHighUnpackSize() == 0 &&
                            fileHeader.getUnpSize() != 0 && fileHeader.getUnpSize() <  0x1fffffff &&
                            fileHeader.getHighPackSize() == 0 &&
                            fileHeader.getPackSize() > 0 && fileHeader.getPackSize() < 0x1fffffff) {
                        cnt_files++;
                        break;
                    }
                }

                if (cnt_files > 0)
                    res = EngBookMyType.TAL_FILE_TYPE.RAR;
            }

            try {
                a.close();
            } catch (IOException e) {
                e.printStackTrace();
                res = EngBookMyType.TAL_FILE_TYPE.RARUnk;
            }
        }

        return res;
    }

    @Override
    public void finalize() throws Throwable {
        if (rar != null)
            rar.close();
        super.finalize();
    }

    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
        super.initState(file, myParent, fList);

        ident = "bypassrar";
        size = 0;
        File f = new File(file);

        try {
            rar = new Archive(f);
        } catch (RarException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (rar != null) {
            if (!rar.isEncrypted()) {
                int cnt = 0;

                for (FileHeader fileHeader : rar.getFileHeaders()) {
                    if (!fileHeader.isDirectory() &&
                            fileHeader.getHighUnpackSize() == 0 &&
                            fileHeader.getUnpSize() != 0 && fileHeader.getUnpSize()  < 0x1fffffff &&
                            fileHeader.getHighPackSize() == 0 &&
                            fileHeader.getPackSize() > 0 && fileHeader.getPackSize() < 0x1fffffff) {

                        AlFileZipEntry of = new AlFileZipEntry();
                        of.compress = 0;
                        of.cSize = fileHeader.getPackSize();
                        of.uSize = (int) fileHeader.getUnpSize();
                        of.flag = 0;
                        of.position = cnt++;
                        of.time = 0;
                        of.name = fileHeader.getFileNameW();
                        if (of.name.length() == 0) of.name = fileHeader.getFileNameString();
                        of.name = of.name.replace(EngBookMyType.AL_ROOT_WRONGPATH, EngBookMyType.AL_ROOT_RIGHTPATH);
                        of.obj = fileHeader;

                        if (of.name.length() > 0) {
                            if (of.name.charAt(0) != EngBookMyType.AL_ROOT_RIGHTPATH)
                                of.name = "" + EngBookMyType.AL_ROOT_RIGHTPATH + of.name;
                            fileList.add(of);
                        }
                    }
                }

                if (fileList.size() > 0)
                    size = (int) f.length();
            }
        }

        return TAL_RESULT.OK;
    }

    @Override
    protected int getBuffer(int pos, byte[] dst, int cnt) {
        return 0;
    }

    @Override
    public int getExternalFileNum(String fname) {
        if (fname == null)
            return LEVEL1_FILE_NOT_FOUND;

        for (int j = 0; j < 2; j++) {
            fname = j == 0 ? getAbsoluteName(fileName, fname) : AlUnicode.URLDecode(fname);

            if (fname != null) {
                for (int i = 0; i < fileList.size(); i++) {
                    if (fileList.get(i).name.contentEquals(fname)) {
                        return i;
                    }
                }
            }
        }

        return LEVEL1_FILE_NOT_FOUND;
    }

    @Override
    public boolean fillBufFromExternalFile(int num, int pos, byte[] dst, int dst_pos, int cnt) {
        int res = 0;
        if (num >= 0 && num < fileList.size()) {

            RARByteArrayOutputStream baos = new RARByteArrayOutputStream();
            try {
                rar.extractFile((FileHeader)(fileList.get(num).obj), baos);
            } catch (RarException e) {
                e.printStackTrace();
                baos = null;
            }

            if (baos != null) {
                byte[] data = baos.getBuffer();
                System.arraycopy(data, pos, dst, dst_pos, cnt);
                res = cnt;

                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return res == cnt;
    }
}
