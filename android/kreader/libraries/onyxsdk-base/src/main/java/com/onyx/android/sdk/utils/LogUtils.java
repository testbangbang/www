package com.onyx.android.sdk.utils;

import android.content.Context;
import android.util.Log;

import com.onyx.android.sdk.device.EnvironmentUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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
    public static final String COMMAND_DUMPSYS = "/system/bin/dumpsys ";
    public static final String COMMAND_DMSG = "/system/bin/dmesg ";

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

    public static final String DIR_SDCARD = EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath();
    public static final String DIR_PROC = "/proc/";
    public static final String VERSION = "version";
    public static final String LAST_KMSG = "last_kmsg";

    public static final String POWER = "power";
    public static final String BATTERY = "battery";
    public static final String BATTERY_STATS = "batterystats";
    public static final String ALARM = "alarm";
    public static final String ANR_TRACES_PATH = "/data/anr/traces.txt";

    public static final String SEPARATOR_NEW_LINE = "\r\n";
    public static final String SEPARATOR_BLANK = " ";
    public static final String SEPARATOR_STAR = "\r\n***********************************\r\n\r\n";

    public static final String LOGCAT_FILTER_ARGS = TAG_FILTER_SYSTEM_ERR + TAG_FILTER_AVOS_CLOUD;

    public static final String PROGRAM_EXEC_LOGCAT_GET_NOTE = COMMAND_LOGCAT + LOGCAT_GET_NOTE
            + LOGCAT_FORMAT_SET + TAG_FORMAT_TAG
            + LOGCAT_FORMAT_SET + TAG_FORMAT_TIME;
    public static final String PROGRAM_EXEC_CAT_LINUX_VERSION = COMMAND_CAT + DIR_PROC + VERSION;
    public static final String PROGRAM_EXEC_CAT_LINUX_LAST_KMSG = COMMAND_CAT + DIR_PROC + LAST_KMSG;
    public static final String PROGRAM_EXEC_CAT_LINUX_DMSG = COMMAND_DMSG;
    public static final String PROGRAM_EXEC_LS_SDCARD = COMMAND_LS + DIR_SDCARD;
    public static final String PROGRAM_EXEC_LOGCAT_CLEAR_NOTE = COMMAND_LOGCAT + LOGCAT_CLEAR_NOTE;

    public static final String PROGRAM_EXEC_DUMPSYS_ALL = COMMAND_DUMPSYS;
    public static final String PROGRAM_EXEC_DUMPSYS_POWER = COMMAND_DUMPSYS + POWER;
    public static final String PROGRAM_EXEC_DUMPSYS_BATTERY = COMMAND_DUMPSYS + BATTERY;
    public static final String PROGRAM_EXEC_DUMPSYS_BATTERY_STATS = COMMAND_DUMPSYS + BATTERY_STATS;
    public static final String PROGRAM_EXEC_DUMPSYS_ALARM = COMMAND_DUMPSYS + ALARM;

    public static final String PROGRAM_EXEC_WAKEUP_REASON_SUSPEND_HISTORY = COMMAND_CAT + "/sys/kernel/wakeup_reasons/suspend_history";
    public static final String PROGRAM_EXEC_CAT_ANR = COMMAND_CAT + ANR_TRACES_PATH;


    public static final String ENCODING_TYPE = "utf-8";
    public static final String OUTPUT_FILE_DIR_PREFIX = "feedback_";
    public static final String OUTPUT_FILE_ZIP_PREFIX = OUTPUT_FILE_DIR_PREFIX;
    public static final String OUTPUT_FILE_LOGCAT_PREFIX = "logcat_";
    public static final String OUTPUT_FILE_KERNEL_PREFIX = "kernel_";
    public static final String OUTPUT_FILE_DUMPSYS_PREFIX = "dumpsys_";
    public static final String OUTPUT_FILE_DUMPSYS_SPECIFY_PREFIX = "dumpsys_specify_";
    public static final String OUTPUT_FILE_WAKEUP_REASONS_PREFIX = "wakeup_reasons_";
    public static final String OUTPUT_FILE_KMSG_PREFIX = "kmsg_";
    public static final String OUTPUT_FILE_ANR_PREFIX = "anr_";
    public static final String OUTPUT_FILE_DESC_PREFIX = "desc_";
    public static final String OUTPUT_FILE_TXT_EXTENSION = ".txt";
    public static final String OUTPUT_FILE_ZIP_EXTENSION = ".zip";

    public static final String[] COMMAND_LOGCAT_SET = new String[]{
            PROGRAM_EXEC_LOGCAT_GET_NOTE,
            PROGRAM_EXEC_CAT_LINUX_VERSION,
    };

    public static final String[] COMMAND_KERNEL_SET = new String[]{
            PROGRAM_EXEC_CAT_LINUX_VERSION,
            PROGRAM_EXEC_CAT_LINUX_DMSG
    };

    public static final String[] COMMAND_CLEAR_NOTE_SET = new String[]{
            PROGRAM_EXEC_LOGCAT_CLEAR_NOTE,
    };

    public static final String[] COMMAND_DUMPSYS_SET = new String[]{
            PROGRAM_EXEC_DUMPSYS_ALL
    };

    public static final String[] COMMAND_DUMPSYS_SPECIFY_SET = new String[]{
            PROGRAM_EXEC_DUMPSYS_POWER,
            PROGRAM_EXEC_DUMPSYS_BATTERY,
            PROGRAM_EXEC_DUMPSYS_BATTERY_STATS,
            PROGRAM_EXEC_DUMPSYS_ALARM
    };

    public static final String[] COMMAND_WAKEUP_REASON_SET = new String[]{
            PROGRAM_EXEC_WAKEUP_REASON_SUSPEND_HISTORY
    };

    public static final String[] COMMAND_KMSG_SET = new String[]{
            PROGRAM_EXEC_CAT_LINUX_LAST_KMSG
    };

    public static final String[] COMMAND_ANR_SET = new String[]{
            PROGRAM_EXEC_CAT_ANR
    };

    private static String getFileNameBasedOnDate(String prefix, String suffix) {
        return prefix + DATE_FORMAT_YYYY_MM_DD_HHMMSS.format(new Date()) + suffix;
    }

    public static File generateExceptionFile(Context context, String[] commandSet, String fileDirPath, String filePrefix) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (String command : commandSet) {
            ShellUtils.CommandResult commandResult = ShellUtils.execCommand(command, false, true);
            if (commandResult.successMsg != null) {
                stringBuilder.append(commandResult.successMsg);
            }
            stringBuilder.append(SEPARATOR_STAR);
        }
        File logcatFile = new File(fileDirPath, getFileNameBasedOnDate(filePrefix, OUTPUT_FILE_TXT_EXTENSION));
        FileOutputStream out = new FileOutputStream(logcatFile);
        out.write(stringBuilder.toString().getBytes(ENCODING_TYPE));
        out.flush();
        out.close();
        Log.i(filePrefix + "File success:" + logcatFile.getName(), "" + logcatFile.length());
        return logcatFile;
    }

    private static File getFeedbackDir(Context context) {
        File file = new File(context.getFilesDir(), getFileNameBasedOnDate(OUTPUT_FILE_ZIP_PREFIX, ""));
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    // must in work workThread
    public static File generateFeedBackFile(final Context context) {
        File zipFile = null;
        File[] wantCompressFiles = null;
        try {
            String dirPath = getFeedbackDir(context).getAbsolutePath();
            File logcatFile = generateExceptionFile(context, COMMAND_LOGCAT_SET, dirPath, OUTPUT_FILE_LOGCAT_PREFIX);
            File kernelFile = generateExceptionFile(context, COMMAND_KERNEL_SET, dirPath, OUTPUT_FILE_KERNEL_PREFIX);
            File dumpsysFile = generateExceptionFile(context, COMMAND_DUMPSYS_SET, dirPath, OUTPUT_FILE_DUMPSYS_PREFIX);
            File dumpsysSpecifyFile = generateExceptionFile(context, COMMAND_DUMPSYS_SPECIFY_SET, dirPath, OUTPUT_FILE_DUMPSYS_SPECIFY_PREFIX);
            File wakeupReasonsFile = generateExceptionFile(context, COMMAND_WAKEUP_REASON_SET, dirPath, OUTPUT_FILE_WAKEUP_REASONS_PREFIX);
            File kmsgFile = generateExceptionFile(context, COMMAND_KMSG_SET, dirPath, OUTPUT_FILE_KMSG_PREFIX);
            File anrFile = generateExceptionFile(context, COMMAND_ANR_SET, dirPath, OUTPUT_FILE_ANR_PREFIX);
            wantCompressFiles = new File[]{logcatFile, kernelFile, dumpsysFile, dumpsysSpecifyFile, wakeupReasonsFile, kmsgFile, anrFile};
            zipFile = new File(dirPath, getFileNameBasedOnDate(OUTPUT_FILE_ZIP_PREFIX, OUTPUT_FILE_ZIP_EXTENSION));
            boolean isSuccess = ZipUtils.compress(wantCompressFiles, zipFile);
            if (!isSuccess) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            deleteFiles(wantCompressFiles);
        }
        return zipFile;
    }

    public static File generateFeedBackFile(final Context context, String desc) {
        File zipFile = generateFeedBackFile(context);
        if (zipFile != null && zipFile.exists()) {
            generateDescFile(zipFile.getParent(), desc);
        }
        return zipFile;
    }

    private static File generateDescFile(String dirPath, String desc) {
        File descFile = generateDescFile(dirPath);
        try {
            FileUtils.writeStringToFile(descFile, String.valueOf(desc), Charset.defaultCharset());
        } catch (Exception ignored) {
        }
        return descFile;
    }

    private static File generateDescFile(String dirPath) {
        return new File(dirPath, getFileNameBasedOnDate(OUTPUT_FILE_DESC_PREFIX, OUTPUT_FILE_TXT_EXTENSION));
    }

    public static File getDescFile(final String dirPath) {
        File file = new File(dirPath);
        File[] fileSet = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return isLogDir(dir.getAbsolutePath()) && isLogDescFile(name);
            }
        });
        if (fileSet != null && fileSet.length > 0) {
            return fileSet[0];
        }
        return null;
    }

    public static Collection<File> getLogFiles(final String dirPath) {
        Collection<File> files = FileUtils.listFilesAndDirs(new File(dirPath), new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return isLogDir(file.getAbsolutePath()) && isLogFile(file.getName());
            }

            @Override
            public boolean accept(File dir, String name) {
                return isLogDir(dir.getAbsolutePath());
            }
        }, new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return isLogDir(file.getAbsolutePath());
            }

            @Override
            public boolean accept(File dir, String name) {
                return isLogDir(name);
            }
        });
        Collection<File> fileSet = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                fileSet.add(file);
            }
        }
        return fileSet;
    }

    public static void deleteFiles(File[] files) {
        if (files == null || files.length <= 0) {
            return;
        }
        for (File file : files) {
            if (file != null && file.exists()) {
                FileUtils.deleteQuietly(file);
            }
        }
    }

    public static boolean isLogDir(String path) {
        return path.contains(LogUtils.OUTPUT_FILE_DIR_PREFIX);
    }

    public static boolean isLogFile(String name) {
        return name.startsWith(LogUtils.OUTPUT_FILE_ZIP_PREFIX);
    }

    public static boolean isLogDescFile(String name) {
        return name.startsWith(LogUtils.OUTPUT_FILE_DESC_PREFIX);
    }
}
