package com.onyx.kreader.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static String getFileName(final String path) {
        File file = new File(path);
        return file.getName();
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

    public static String getRealFilePathFromUri(Context context, Uri uri) {
        String filePath = null;
        if (uri != null) {
            if ("content".equals(uri.getScheme())) {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{
                        android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                cursor.moveToFirst();
                filePath = cursor.getString(0);
                cursor.close();
            } else {
                filePath = uri.getPath();
            }
        }
        return filePath;
    }


    public static String computeMD5(File file) throws IOException, NoSuchAlgorithmException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        if (!file.isFile()) {
            throw new IllegalArgumentException();
        }

        byte[] digest_buffer = getDigestBuffer(file);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(digest_buffer);
        byte[] out = md.digest();

        final char hex_digits[] = {
                '0', '1', '2', '3',
                '4', '5', '6', '7',
                '8', '9', 'a', 'b',
                'c', 'd', 'e', 'f' };

        char str[] = new char[out.length * 2];
        for (int i = 0; i < out.length; i++) {
            int j = i << 1;
            str[j] = hex_digits[(out[i] >> 4) & 0x0F];
            str[j + 1] = hex_digits[out[i] & 0x0F];
        }

        return String.valueOf(str);
    }

    public static byte[] getDigestBuffer(File file) throws IOException {
        final int digestBlockLength = 512;
        byte[] digestBuffer = null;
        RandomAccessFile rf = null;

        try {
            rf = new RandomAccessFile(file, "r");

            long fileSize = rf.length();

            // TODO: what about an empty file?
            if (fileSize <= (digestBlockLength * 3)) {
                digestBuffer = new byte[(int)fileSize];
                rf.read(digestBuffer);
            } else {
                // 3 digest blocks, head, mid, end
                digestBuffer = new byte[3 * digestBlockLength];
                rf.seek(0);
                rf.read(digestBuffer, 0, digestBlockLength);
                rf.seek((fileSize / 2) - (digestBlockLength / 2));
                rf.read(digestBuffer, digestBlockLength, digestBlockLength);
                rf.seek(fileSize - digestBlockLength);
                rf.read(digestBuffer, 2 * digestBlockLength, digestBlockLength);
            }
        }
        finally {
            if (rf != null) {
                rf.close();
            }
        }
        return digestBuffer;
    }

}
