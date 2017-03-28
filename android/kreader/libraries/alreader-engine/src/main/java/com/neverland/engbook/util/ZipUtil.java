package com.neverland.engbook.util;

import android.util.Log;


import com.jingdong.app.reader.epub.paging.JDDecryptUtil;
import com.neverland.engbook.level1.AlFileZipEntry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by huxiaomao on 17/3/23.
 */

public class ZipUtil {
    private static final String TAG = ZipUtil.class.getCanonicalName();

    public static Map<String,Integer> unzipFile(String dataZip, String dstFolder, final String key,
                                 final String deviceUUID, final String random) {
        Map<String,Integer> maps = new HashMap<>();

        ZipInputStream zis = null;
        InputStream is = null;

        JDDecryptUtil.key = key;
        JDDecryptUtil.deviceUUID = deviceUUID;
        JDDecryptUtil.random = random;

        try {
            File zipFile = new File(dataZip);
            is = new FileInputStream(zipFile);
            zis = new ZipInputStream(is);
            ZipEntry entry;
            //Log.d(TAG, "start unzip:" + dataZip + "...");
            while ((entry = zis.getNextEntry()) != null) {
                String zipPath = entry.getName();
                int fileSize = 0;
                try {
                    if (entry.isDirectory() || zipPath.contains("images")) {

                    } else {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        byte[] buf = new byte[64 * 1024];
                        int n;
                        int length = 0;
                        while ((n = zis.read(buf, 0, buf.length)) > 0) {
                            os.write(buf, 0, n);
                            length += n;
                        }
                        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
                        fileSize = JDDecryptUtil.getDecryptFileSize(inputStream,length);
                        inputStream.close();
                        os.close();
                        maps.put(File.separator + zipPath,fileSize);
                        //Log.d(TAG, "unzip zipPath:" + zipPath + ",fileSize:" + fileSize);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                    continue;
                }
            }
            //Log.d(TAG, "unzip over");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(zis != null){
                    zis.close();
                }
                if(is != null){
                    is.close();
                }
            }catch (Exception e){

            }
        }
        return maps;
    }
}
