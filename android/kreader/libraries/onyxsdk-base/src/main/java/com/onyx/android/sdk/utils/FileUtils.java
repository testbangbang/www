package com.onyx.android.sdk.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;

import com.onyx.android.sdk.device.EnvironmentUtil;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by zhuzeng on 10/16/15.
 */
public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public static boolean fileExist(final String path) {
        File file = new File(path);
        return file.exists();
    }

    public static boolean mkdirs(final String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.isDirectory();
        }
        return file.mkdirs();
    }

    public static void purgeDirectory(final File dir) {
        for (File file: dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            }
        }
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

    public static String getFileExtension(File file) {
        return getFileExtension(file.getName());
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
            if (file.isFile()) {
                final String extension = getFileExtension(absolutePath);
                if (extensionFilters == null || extensionFilters.contains(extension)) {
                    fileList.add(absolutePath);
                }
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

    public static String getBaseName(final String path) {
        String fileName = getFileName(path);
        int idx = fileName.lastIndexOf('.');
        if (idx < 0) {
            return fileName;
        }

        return fileName.substring(0, idx);
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

    public static long getLastChangeTime(File file) {
        return file.lastModified();
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

    public static boolean appendContentToFile(String content, File fileForSave) {
        boolean succeed = true;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileForSave, true);
            out.write(content.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            succeed = false;
        } finally {
            closeQuietly(out);
        }
        return succeed;
    }

    public static boolean saveContentToFile(final byte[] data, final File fileForSave) {
        boolean succeed = true;
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(fileForSave);
            output.write(data);
        } catch (Exception e) {
            succeed = false;
        } finally {
            closeQuietly(output);
        }
        return succeed;
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, File fileForSave, Bitmap.CompressFormat format, int quality) {
        boolean succeed = true;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileForSave);
            bitmap.compress(format, quality, out);
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

    public static String computeFullMD5Checksum(File file) throws IOException, NoSuchAlgorithmException {
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[64 * 1024];
            MessageDigest md = MessageDigest.getInstance("MD5");
            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    md.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            return hexToString(md.digest());
        } finally {
            FileUtils.closeQuietly(fis);
        }
    }

    public static String hexToString(byte[] out) {
        final char hex_digits[] = {
                '0', '1', '2', '3',
                '4', '5', '6', '7',
                '8', '9', 'a', 'b',
                'c', 'd', 'e', 'f'};

        char str[] = new char[out.length * 2];
        for (int i = 0; i < out.length; i++) {
            int j = i << 1;
            str[j] = hex_digits[(out[i] >> 4) & 0x0F];
            str[j + 1] = hex_digits[out[i] & 0x0F];
        }
        return String.valueOf(str);
    }

    public static boolean deleteFile(final String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    public static boolean ensureFileExists(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        } else {
            // we will not attempt to create the first directory in the path
            // (for example, do not create /sdcard if the SD card is not mounted)
            int secondSlash = path.indexOf('/', 1);
            if (secondSlash < 1) return false;
            String directoryPath = path.substring(0, secondSlash);
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                return false;
            }

            File parent_folder = file.getParentFile();
            if (!parent_folder.exists() && !parent_folder.mkdirs()) {
                Log.e(TAG, "create folder failed: " + parent_folder.getAbsolutePath());
                return false;
            }
            try {
                return file.createNewFile();
            } catch (IOException ioe) {
                Log.e(TAG, "File creation failed", ioe);
            }
            return false;
        }
    }

    public static void findFileByKey(List<File> fileList, String searchKey) {
        findFileByKey(fileList, EnvironmentUtil.getExternalStorageDirectory(), searchKey);
        findFileByKey(fileList, EnvironmentUtil.getRemovableSDCardDirectory(), searchKey);
    }

    public static void findFileByKey(List<File> fileList, File targetDir, String searchKey) {
        if (!targetDir.canRead()) {
            return;
        }
        for (File temp : targetDir.listFiles()) {
            if (temp.isHidden()) {
                continue;
            }
            if (temp.isDirectory()) {
                if (temp.getName().contains(searchKey)) {
                    fileList.add(temp);
                }
                findFileByKey(fileList, temp, searchKey);
            }
            if (temp.isFile()) {
                if (temp.getName().contains(searchKey)) {
                    fileList.add(temp);
                }
            }
        }
    }

    public static String fixNotAllowFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex == -1) {
            return null;
        }

        String replaceString = fileName;
        String regularExpression = "([.*/^()?|<>\\]\\[])";

        replaceString = replaceString.replaceAll(regularExpression, " ");
        replaceString = replaceString.replace(replaceString.substring(dotIndex), fileName.substring(dotIndex));
        replaceString = replaceString.replace(":", "：");
        int index = 0;
        while (replaceString.indexOf("\"") != -1) {
            if (index == 0) {
                replaceString = replaceString.replaceFirst("\"", "“");
                index = 1;
            } else {
                replaceString = replaceString.replaceFirst("\"", "”");
                index = 0;
            }
        }
        return replaceString;
    }
}
