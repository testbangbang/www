package com.onyx.kreader.utils;

import android.database.Cursor;

import java.io.*;
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

    public static void collectFiles(final String parentPath, final Set<String> extensionFilters, boolean recursive, final List<String> fileList) {
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
            } else if (file.isDirectory() && recursive) {
                collectFiles(absolutePath, extensionFilters, recursive, fileList);
            }
        }
    }

    public static String getParent(final String path) {
        File file = new File(path);
        return file.getParent();
    }


    public static void closeQuietly(Cursor cursor) {
        try {
            if (cursor != null)
                cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String canonicalPath(final String ref, final String path) {
        String result = path;
        int index = ref.lastIndexOf('/');
        if (index > 0 && path.indexOf('/') < 0) {
            result = ref.substring(0, index + 1) + path;
        }
        return result;
    }

    public static boolean isImageFile(String fileName) {
        fileName = fileName.toLowerCase(Locale.getDefault());
        return fileName.endsWith(".bmp") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") || fileName.endsWith(".gif");
    }

    public static boolean isZipFile(String fileName) {
        fileName = fileName.toLowerCase(Locale.getDefault());
        return fileName.endsWith(".zip") || fileName.endsWith(".cbz");
    }

    public static boolean isRarFile(String fileName) {
        fileName = fileName.toLowerCase(Locale.getDefault());
        return fileName.endsWith(".rar") || fileName.endsWith(".cbr");
    }

    public static String readContentOfFile(File fileForRead) {
        FileInputStream in = null;
        InputStreamReader reader = null;
        BufferedReader breader = null;
        try {
            in = new FileInputStream(fileForRead);
            reader = new InputStreamReader(in, "utf-8");
            breader = new BufferedReader(reader);

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = breader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(breader);
            closeQuietly(reader);
            closeQuietly(in);
        }
        return null;
    }

    public static boolean saveContentToFile(String content, File fileForSave) {
        boolean succeed = true;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileForSave);
            out.write(content.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            succeed = false;
        } finally {
            closeQuietly(out);
        }
        return succeed;
    }

}
