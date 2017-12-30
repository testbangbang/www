package com.neverland.engbook.level1;

import com.neverland.engbook.forpublic.TAL_RESULT;

import java.security.MessageDigest;
import java.util.ArrayList;

public class AlFilesBypassNative extends AlFilesBypass {
    public static final int CHECK_MD5_LEN = 32;
    static char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private AlRandomAccessFile	raf = null;

    @Override
    public int initState(String file, AlFiles myParent, ArrayList<AlFileZipEntry> fList) {
        super.initState(file, myParent, fList);

        ident = "bypass";

        raf = new AlRandomAccessFile();

        if (raf.open(file, 0) == TAL_RESULT.OK) {
            size = raf.getSize();
            return TAL_RESULT.OK;
        } else {
            size = 0;
            return TAL_RESULT.ERROR;
        }
    }

    @Override
    public void finalize() throws Throwable {
        if (raf != null)
            raf.close();
        super.finalize();
    }

    @Override
    public int getBuffer(int pos, byte[] dst, int cnt) {
        if (raf.seek(pos) == pos) {
            int r = raf.read(dst, 0, cnt);
            if (r >= 0)
                return r;
        }
        for (int i = 0; i < cnt; i++)
            dst[i] = 0;
        return cnt;
    }

    @Override
    public String getFileMD5() {
        String result = null;
        int fileSize = raf.getSize();
        int checkLen = 32;

        if(fileSize < CHECK_MD5_LEN * 3){
            checkLen = fileSize / 3;
        }

        int begin = 0;
        int middle = fileSize / 2 - checkLen;
        int end = fileSize - checkLen;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] checkData = new byte[checkLen];

            raf.seek(begin);
            raf.read(checkData,0,checkLen);
            byte[] md5Byte = md5.digest(checkData);

            raf.seek(middle);
            raf.read(checkData,0,checkLen);
            md5.update(checkData);

            raf.seek(end);
            raf.read(checkData,0,checkLen);
            md5.update(checkData);

            md5.update(fileName.getBytes());

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < md5Byte.length; i++) {
                sb.append(HEX[(md5Byte[i] & 0xff) / 16]);
                sb.append(HEX[(md5Byte[i] & 0xff) % 16]);
            }
            result = sb.toString();
        }catch (Exception e){

        }
        return result;
    }

    public static String getMd5(byte[] data){
        String result = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] md5Byte = md5.digest(data);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < md5Byte.length; i++) {
                sb.append(HEX[(md5Byte[i] & 0xff) / 16]);
                sb.append(HEX[(md5Byte[i] & 0xff) % 16]);
            }
            result = sb.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
