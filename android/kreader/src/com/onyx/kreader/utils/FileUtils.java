package com.onyx.kreader.utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by zhuzeng on 10/16/15.
 */
public class FileUtils {

    static public boolean fileExist(final String path) {
        File file = new File(path);
        return file.exists();
    }

    public static String getFileExtension(String fileName) {
        if (StringUtils.isNullOrEmpty(fileName)) {
            return "";
        }
        int dotPosition = fileName.lastIndexOf('.');
        if (dotPosition >= 0) {
            return fileName.substring(dotPosition + 1).toLowerCase(Locale.getDefault());
        }

        return "";
    }

    public static void collectFiles(final String parentPath, final Set<String> extensionFilters, final List<String> fileList) {
        File parent = new File(parentPath);
        File[] files = parent.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isHidden()) {
                continue;
            }
            final String absolutePath = file.getAbsolutePath();
            final String extension = getFileExtension(absolutePath);
            if (file.isFile() && extensionFilters.contains(extension)) {
                fileList.add(absolutePath);
            } else if (file.isDirectory()) {
                collectFiles(absolutePath, extensionFilters, fileList);
            }
        }
    }

    public static String getParent(final String path) {
        File file = new File(path);
        return file.getParent();
    }
}
