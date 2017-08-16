package com.onyx.android.dr.util;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by zhouzhiming on 2017/8/14.
 */
public class RemainingTimeCalculator {
    public static final int UNKNOWN_LIMIT = 0;
    public static final int FILE_SIZE_LIMIT = 1;
    public static final int DISK_SPACE_LIMIT = 2;
    public static final int TIME_DIVIDEND = 1000;
    public static final int BITRATE_DIVIDEND = 8;
    private int currentLowerLimit = UNKNOWN_LIMIT;
    private File sdCardDirectory;
    private File recordingFile;
    private long maxBytes;
    private int bytesPerSecond;
    private long blocksChangedTime;
    private long lastBlocks;
    private long fileSizeChangedTime;
    private long lastFileSize;

    public RemainingTimeCalculator() {
        sdCardDirectory = Environment.getExternalStorageDirectory();
    }

    /**
     * If called, the calculator will return the minimum of two estimates:
     * how long until we run out of disk space and how long until the file
     * reaches the specified size.
     *
     * @param file     the file to watch
     * @param maxBytes the limit
     */
    public void setFileSizeLimit(File file, long maxBytes) {
        recordingFile = file;
        this.maxBytes = maxBytes;
    }

    /**
     * Resets the interpolation.
     */
    public void reset() {
        currentLowerLimit = UNKNOWN_LIMIT;
        blocksChangedTime = -1;
        fileSizeChangedTime = -1;
    }

    /**
     * Returns how long (in seconds) we can continue recording.
     */
    public long timeRemaining() {
        // Calculate how long we can record based on free disk space
        StatFs fs = new StatFs(sdCardDirectory.getAbsolutePath());
        long blocks = fs.getAvailableBlocks();
        long blockSize = fs.getBlockSize();
        long now = System.currentTimeMillis();
        if (blocksChangedTime == -1 || blocks != lastBlocks) {
            blocksChangedTime = now;
            lastBlocks = blocks;
        }
        // at blocksChangedTime we had this much time
        long result = lastBlocks * blockSize / bytesPerSecond;
        // so now we have this much time
        result -= (now - blocksChangedTime) / TIME_DIVIDEND;
        if (recordingFile == null) {
            currentLowerLimit = DISK_SPACE_LIMIT;
            return result;
        }
        // If we have a recording file set, we calculate a second estimate
        // based on how long it will take us to reach maxBytes.
        recordingFile = new File(recordingFile.getAbsolutePath());
        long fileSize = recordingFile.length();
        if (fileSizeChangedTime == -1 || fileSize != lastFileSize) {
            fileSizeChangedTime = now;
            lastFileSize = fileSize;
        }
        // just for safety
        long result2 = (maxBytes - fileSize) / bytesPerSecond;
        result2 -= (now - fileSizeChangedTime) / TIME_DIVIDEND;
        result2 -= 1;
        currentLowerLimit = result < result2
                ? DISK_SPACE_LIMIT : FILE_SIZE_LIMIT;
        return Math.min(result, result2);
    }

    /**
     * Indicates which limit we will hit (or have hit) first, by returning one
     * of FILE_SIZE_LIMIT or DISK_SPACE_LIMIT or UNKNOWN_LIMIT. We need this to
     * display the correct message to the user when we hit one of the limits.
     */
    public int currentLowerLimit() {
        return currentLowerLimit;
    }

    /**
     * Is there any point of trying to start recording?
     */
    public boolean diskSpaceAvailable() {
        StatFs fs = new StatFs(sdCardDirectory.getAbsolutePath());
        // keep one free block
        return fs.getAvailableBlocks() > 1;
    }

    /**
     * Sets the bit rate used in the interpolation.
     *
     * @param bitRate the bit rate to set in bits/sect.
     */
    public void setBitRate(int bitRate) {
        bytesPerSecond = bitRate / BITRATE_DIVIDEND;
    }
}