package com.onyx.android.sdk.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogUtils {
    public static final SimpleDateFormat DATE_FORMAT_YYYY_MM_DD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());

    public static final String COMMAND_LOGCAT = "/system/bin/logcat -b system -b main ";
    public static final String COMMAND_SU = "su ";
    public static final String COMMAND_CAT = "/system/bin/cat ";
    public static final String COMMAND_LS = "/system/bin/ls ";
    public static final String COMMAND_ECHO = "/system/bin/echo ";
    public static final String COMMAND_BIN_SH = "/system/bin/sh ";

    public static final String LOGCAT_GET_NOTE = "-d ";
    public static final String LOGCAT_CLEAR_NOTE = "-c ";
    public static final String LOGCAT_GET_SIZE_NOTE = "-g ";
    public static final String LOGCAT_FORMAT_SET = "-v ";
    public static final String LOGCAT_OUTPUT_TO_FILE = "-f ";
    public static final String LOGCAT_FILTER_BY = "-s ";

    public static final String TAG_FORMAT_BRIEF = "brief ";
    public static final String TAG_FORMAT_PROCESS = "process ";
    public static final String TAG_FORMAT_TAG = "tag ";
    public static final String TAg_FORMAT_THREAD = "thread ";
    public static final String TAG_FORMAT_RAW = "raw ";
    public static final String TAG_FORMAT_TIME = "time ";
    public static final String TAG_FORMAT_THREADTIME = "threadtime ";
    public static final String TAG_FORMAT_LONG = " long";

    //tag:W Log.w(tag, xxxx); W/System.err
    public static final String TAG_FILTER_SYSTEM_ERR = "System.err:* ";
    public static final String TAG_FILTER_AVOS_CLOUD = "===AVOS*:* ";

    public static final String DIR_SDCARD = "/mnt/sdcard";
    public static final String DIR_PROC = "/proc/";
    public static final String PROC_VERSION = "version";

    public static final String SEPARATOR_NEW_LINE = "\r\n";
    public static final String SEPARATOR_BLANK = " ";
    public static final String SEPARATOR_STAR = "\r\n***********************************\r\n\r\n";

    public static final String LOGCAT_FILTER_ARGS = TAG_FILTER_SYSTEM_ERR + TAG_FILTER_AVOS_CLOUD;

    public static final String PROGRAM_EXEC_LOGCAT_GET_NOTE = COMMAND_LOGCAT + LOGCAT_GET_NOTE + LOGCAT_FORMAT_SET + TAG_FORMAT_TAG;
    public static final String PROGRAM_EXEC_CAT_LINUX_VERSION = COMMAND_CAT + DIR_PROC + PROC_VERSION;
    public static final String PROGRAM_EXEC_LS_SDCARD = COMMAND_LS + DIR_SDCARD;
    public static final String PROGRAM_EXEC_LOGCAT_CLEAR_NOTE = COMMAND_LOGCAT + LOGCAT_CLEAR_NOTE;

    public static final String ENCODING_TYPE = "utf-8";
    public static final String OUTPUT_FILE_ZIP_PREFIX = "feedback_";
    public static final String OUTPUT_FILE_LOGCAT_PREFIX = "logcat_";
    public static final String OUTPUT_FILE_KERNEL_PREFIX = "kernel_";
    public static final String OUTPUT_FILE_TXT_EXTENSION = ".txt";
    public static final String OUTPUT_FILE_ZIP_EXTENSION = ".zip";

    public static final String[] COMMAND_LOGCAT_SET = new String[]{
            PROGRAM_EXEC_LOGCAT_GET_NOTE,
            PROGRAM_EXEC_CAT_LINUX_VERSION,
    };

    public static final String[] COMMAND_KERNEL_SET = new String[]{
            PROGRAM_EXEC_CAT_LINUX_VERSION,
    };

    public static final String[] COMMAND_CLEAR_NOTE_SET = new String[]{
            PROGRAM_EXEC_LOGCAT_CLEAR_NOTE,
    };

    private static String getFileNameBasedOnDate(String prefix, String suffix) {
        return prefix + DATE_FORMAT_YYYY_MM_DD_HHMMSS.format(new Date()) + suffix;
    }

    public static File generateExceptionFile(Context context, String[] commandSet, String filePrefix) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (String command : commandSet) {
            ShellUtils.CommandResult commandResult = ShellUtils.execCommand(command, false, true);
            if (commandResult.successMsg != null) {
                stringBuilder.append(commandResult.successMsg);
            }
            stringBuilder.append(SEPARATOR_STAR);
        }
        File logcatFile = new File(context.getFilesDir(), getFileNameBasedOnDate(filePrefix, OUTPUT_FILE_TXT_EXTENSION));
        FileOutputStream out = new FileOutputStream(logcatFile);
        out.write(stringBuilder.toString().getBytes(ENCODING_TYPE));
        out.flush();
        out.close();
        Log.i(filePrefix + "File success:" + logcatFile.getName(), "" + logcatFile.length());
        return logcatFile;
    }

    // must in work workThread
    public static File generateFeedBackFile(final Context context) {
        File zipFile = null;
        File[] wantCompressFiles = null;
        try {
            File logcatFile = generateExceptionFile(context, COMMAND_LOGCAT_SET, OUTPUT_FILE_LOGCAT_PREFIX);
            File kernelFile = generateExceptionFile(context, COMMAND_KERNEL_SET, OUTPUT_FILE_KERNEL_PREFIX);
            wantCompressFiles = new File[]{logcatFile, kernelFile};
            zipFile = new File(context.getFilesDir(), getFileNameBasedOnDate(OUTPUT_FILE_ZIP_PREFIX, OUTPUT_FILE_ZIP_EXTENSION));
            boolean isSuccess = ZipUtils.compress(wantCompressFiles, zipFile);
            if (!isSuccess) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            deleteFiles(wantCompressFiles);
        }
        return zipFile;
    }

    public static void deleteFiles(File[] files) {
        if (files == null || files.length <= 0) {
            return;
        }
        for (File file : files) {
            if (file != null) {
                file.delete();
            }
        }
    }
}
