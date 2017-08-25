/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onyx.android.dr.devicesetting.data.util;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StorageSizeUtil {

    private static final String TAG = StorageSizeUtil.class.getSimpleName();

    /**
     * @return total storage amount in bytes
     */
    public static long getTotalStorageAmount() {
        List<String> pathList = new ArrayList<String>();
        pathList.add("/system");
        pathList.add("/data");
        pathList.add("/cache");
        pathList.add("/mnt/sdcard");
        int len = pathList.size();
        long total = 0;
        for (int i = 0; i < len; i++) {
            Log.i(TAG, "path," + i + ": " + pathList.get(i));
            StatFs stat = new StatFs(pathList.get(i));
            long bytesAvailable;
            if (CommonUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR2)) {
                bytesAvailable = stat.getBlockSizeLong() * stat.getBlockCountLong();
            } else {
                bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
            }
            total += bytesAvailable;
        }
        return total;
    }

    public static long getStorageAmountForPartitions(List<String> pathList) {
        int len = pathList.size();
        long total = 0;
        try {
            for (int i = 0; i < len; i++) {
                StatFs stat = new StatFs(pathList.get(i));
                long bytesAvailable;
                if (CommonUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR2)) {
                    bytesAvailable = stat.getBlockSizeLong() * stat.getBlockCountLong();
                } else {
                    bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
                }
                total += bytesAvailable;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public static List<String> getExtSDCardPaths() {
        List<String> paths = new ArrayList<String>();
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        if (extFileStatus.endsWith(Environment.MEDIA_MOUNTED)
                && extFile.exists() && extFile.isDirectory()
                && extFile.canWrite()) {
            paths.add(extFile.getAbsolutePath());
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null) {
                if ((!line.contains("fat") && !line.contains("fuse") && !line
                        .contains("storage"))
                        || line.contains("secure")
                        || line.contains("asec")
                        || line.contains("firmware")
                        || line.contains("shell")
                        || line.contains("obb")
                        || line.contains("legacy") || line.contains("data")) {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length) {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data")
                        || mountPath.contains("Data")) {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory()
                        || !mountRoot.canWrite()) {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(extFile
                        .getAbsolutePath());
                if (equalsToPrimarySD) {
                    continue;
                }
                paths.add(mountPath);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return paths;
    }


    public static long getInternalStorageAmount() {
        List<String> list = new ArrayList<String>(1);
        list.add("/mnt/sdcard");
        return getStorageAmountForPartitions(list);
    }

    public static long getExtsdStorageAmount() {
        List<String> list = new ArrayList<String>(1);
        list.add("/mnt/external_sd");
        List<String> extSDCardPaths = getExtSDCardPaths();
        Log.d(TAG, "getExtsdStorageAmount: " + extSDCardPaths);
        return getStorageAmountForPartitions(list);
    }

    public static long convertBytesToMB(long bytes) {
        return bytes / (1024 * 1024);
    }

    /**
     * @return total storage amount in mega bytes (base on 1024)
     */
    public static long getTotalStorageAmountInMB() {
        return convertBytesToMB(getTotalStorageAmount());
    }

    /**
     * @return the amount with GB only for display that targeting user (base on 1000)
     */
    public static long getDisplayGBForUser(double bytes) {
        return Math.round(bytes / 1000 / 1000 / 1000);
    }

    /**
     * @return total storage amount in giga bytes with correction (3.5 -> 4, 7.6 -> 8)
     */
    public static long getTotalStorageAmountInGB() {
        return getDisplayGBForUser(getTotalStorageAmount());
    }

    /**
     * @return total available storage amount in bytes
     */
    public static long getTotalFreeBytes() {
        long availableAmount = 0;
        String[] paths = {"/system", "/data", "/cache", "/mnt/sdcard"};
        int size = paths.length;
        for (String p : paths) {
            StatFs path = new StatFs(p);
            if (CommonUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR2)) {
                availableAmount += path.getFreeBlocksLong() * path.getBlockSizeLong();
            } else {
                availableAmount += (long) path.getFreeBlocks() * (long) path.getBlockSize();
            }
        }
        return availableAmount;
    }

    public static long getSDCardFreeBytes() {
        return getFreeBytes("/mnt/sdcard");
    }

    public static long getFreeBytes(String path) {
        long amount = 0;
        if (!new File(path).exists()) {
            return amount;
        }
        StatFs sdPath = new StatFs(path);

        Log.i(TAG, "blocks: " + sdPath.getFreeBlocks());
        Log.i(TAG, "block size: " + sdPath.getBlockSize());
        if (CommonUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR2)) {
            amount = sdPath.getFreeBlocksLong() * sdPath.getBlockSizeLong();
        } else {
            amount = (long) sdPath.getFreeBlocks() * (long) sdPath.getBlockSize();
        }
        Log.i(TAG, "amount: " + amount / 1024 / 1024 + "MB");
        return amount;
    }

    public static long getExtsdFreeBytes() {
        return getFreeBytes("/mnt/extsd");
    }

    /**
     * @return get the ratio of free storage amount
     */
    public static int getFreeInternalStorageRatio() {
        return calculateRatio(getInternalStorageAmount(), getSDCardFreeBytes());
    }

    public static int calculateRatio(long total, long free) {
        Log.i(TAG, "total: " + total / 1024 / 1024);
        Log.i(TAG, "free: " + free / 1024 / 1024);
        if (total > 0) {
            return (int) (100 * free / total);
        }
        return 100;
    }

    public static int getFreeExternalStorageRatio() {
        return calculateRatio(getExtsdStorageAmount(), getExtsdFreeBytes());
    }

    public static BigDecimal getFreeStorageInGB() {
        BigDecimal d = new BigDecimal((double) StorageSizeUtil.getSDCardFreeBytes() / (double) (1024 * 1024 * 1024));
        d = d.setScale(2, BigDecimal.ROUND_HALF_UP);
        return d;
    }

    public static long getTotalExtsdStorageAmountInGB() {
        return getDisplayGBForUser(getExtsdStorageAmount());
    }

    public static long getExtsdStorageBytes() {
        return getFreeBytes("/mnt/external_sd");
    }

    public static BigDecimal getFreeExtsdStorageInGB() {
        BigDecimal d = new BigDecimal((double) StorageSizeUtil.getExtsdStorageBytes() / (double) (1024 * 1024 * 1024));
        d = d.setScale(2, BigDecimal.ROUND_HALF_UP);
        return d;
    }
}
