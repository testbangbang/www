package com.onyx.android.libsetting.util;

import android.os.Build;

import com.onyx.android.sdk.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by solskjaer49 on 2017/2/11 16:12.
 */

public class OTAUtil {
    private final static String INFO_TXT = "android-info.txt";

    static public boolean checkLocalUpdateZipLegality(final String file) {
        FileInputStream fis = null;
        ZipInputStream zin = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(file);
            zin = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                if (entry.getName().equals(INFO_TXT)) {
                    baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = zin.read(buffer)) != -1) {
                        baos.write(buffer, 0, count);
                    }
                    String[] arr = baos.toString().split("=");
                    return arr[1] != null && arr[1].trim().equals(Build.DEVICE);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(baos);
            FileUtils.closeQuietly(fis);
            FileUtils.closeQuietly(zin);
        }
        return false;
    }
}
