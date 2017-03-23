package com.onyx.android.sdk.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by suicheng on 2016/6/3.
 */
public class ZipUtils {
    private static final int BUFFER = 2048;

    public static boolean compress(File[] files, File zipFile) {
        boolean isSuccess = true;
        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(zipFile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];
            for (int i = 0; i < files.length; i++) {
                Log.i("Zip-Compress", "Adding: " + files[i]);
                FileInputStream fi = new FileInputStream(files[i]);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(files[i].getName());
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            Log.e("zip Compress", "fail", e);
            isSuccess = false;
        }
        return isSuccess;
    }

    public static boolean decompress(String zipFile, String dirLocation) {
        boolean isSuccess = true;
        try {
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze;
            byte data[] = new byte[BUFFER];
            while ((ze = zin.getNextEntry()) != null) {
                Log.i("Decompress", "Unzipping:" + ze.getName());
                if (ze.isDirectory()) {
                    dirChecker(dirLocation, ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(dirLocation + ze.getName());
                    int count;
                    while ((count = zin.read(data, 0, BUFFER)) != -1) {
                        fout.write(data, 0, count);
                    }
                    zin.closeEntry();
                    fout.close();
                }
            }
            zin.close();
        } catch (Exception e) {
            Log.e("zip Decompress", "fail", e);
            isSuccess = false;
        }
        return isSuccess;
    }

    private static void dirChecker(String location, String dirName) {
        File f = new File(location + dirName);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }
}
