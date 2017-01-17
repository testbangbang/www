package com.neverland.engbook.level1;

import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.neverland.engbook.forpublic.TAL_RESULT;

import java.util.ArrayList;

public class AlFilesRAR extends AlFiles {

    private int rar_position = -1;
    private RARByteArrayOutputStream baos = null;

    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
        super.initState(file, myParent, fList);

        ident = "rar";
        fileList = myParent.getFileList();

        if (file.length() > 0) {
            for (int i = 0; i < fileList.size(); i++) {
                if (fileList.get(i).name.contentEquals(file)) {
                    fileName = file;
                    size = fileList.get(i).uSize;
                    rar_position = fileList.get(i).position;
                    break;
                }
            }
        }

        if (fileName.length() == 0) {
            for (int i = 0; i < fileList.size(); i++) {
                if (AlFiles.isValidExt(fileList.get(i).name)) {
                    fileName = fileList.get(i).name;
                    size = fileList.get(i).uSize;
                    rar_position = fileList.get(i).position;
                    break;
                }
            }
        }

        if (fileName.length() == 0) {
            fileName = fileList.get(0).name;
            size = fileList.get(0).uSize;
            rar_position = fileList.get(0).position;
        }

        return TAL_RESULT.OK;
    }

    @Override
    public void finalize() throws Throwable {
        baos = null;
        super.finalize();
    }

    @Override
    protected int getBuffer(int pos, byte[] dst, int cnt) {
        if (baos == null) {
            try {
                baos = new RARByteArrayOutputStream();
                ((AlFilesBypassRAR)parent).getBypassRAR().extractFile((FileHeader)(fileList.get(rar_position).obj), baos);
            } catch (RarException e) {
                e.printStackTrace();
                baos = null;
            }
        }

        if (baos != null) {
            int r = Math.min(cnt, baos.size() - pos);

            byte[] data = baos.getBuffer();
            System.arraycopy(data, pos, dst, 0, r);
            for (int i = r; i < cnt; i++)
                dst[i] = 0x00;

            return cnt;
        }

        return 0;
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
