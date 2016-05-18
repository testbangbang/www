package com.onyx.android.sdk.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.onyx.android.sdk.data.util.FileUtil;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 5/25/14
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtils {
    public static String TAG_FILENAME="fileName";
    public static String TAG_FILEPATH="filePath";
    public static String TAG_FILESIZE="fileSize";

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

    public static List<String> readStringListOfFile(File fileForRead) {
        FileInputStream in = null;
        InputStreamReader reader = null;
        BufferedReader breader = null;
        List<String> list = new ArrayList<String>();
        try {
            in = new FileInputStream(fileForRead);
            reader = new InputStreamReader(in, "utf-8");
            breader = new BufferedReader(reader);

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = breader.readLine()) != null) {
                list.add(line);
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(breader);
            closeQuietly(reader);
            closeQuietly(in);
        }
        return null;
    }

    public static List<String> readStringListOfFileWithEncoding(File fileForRead, final String encoding) {
        FileInputStream in = null;
        InputStreamReader reader = null;
        BufferedReader breader = null;
        List<String> list = new ArrayList<String>();
        try {
            in = new FileInputStream(fileForRead);
            reader = new InputStreamReader(in, encoding);
            breader = new BufferedReader(reader);

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = breader.readLine()) != null) {
                list.add(line);
            }
            return list;
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
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            succeed = false;
        } finally {
            closeQuietly(out);
        }
        return succeed;
    }

    public static boolean deleteFile(final String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static long getFileSize(File file) {
        if (file.isDirectory()) {
            long size = 0;
            try {
                File[] fileList = file.listFiles();
                for (File temp : fileList) {
                    if (temp.isDirectory()) {
                        size = size + getFileSize(temp);

                    } else {
                        size = size + temp.length();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return size;
        } else if (file.exists() && file.isFile()) {
            return file.length();
        }
        return -1;
    }

    //Use NioMethod(FileChannel) to Copy File To Specific Directory
    public static void copyToDestinationDirectory(File sourceFile, String destinationFolder, String resultFileName) throws IOException {
        File dir = new File(destinationFolder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File targetFile = new File(dir.getAbsolutePath() + resultFileName);
        if(!targetFile.exists()){
            targetFile.createNewFile();
        }
        String folderCommand = "chmod 777 " + dir.getAbsolutePath();
        String fileCommand="chmod 777 " + targetFile.getAbsolutePath();
        Runtime runtime = Runtime.getRuntime();
        java.lang.Process proc = runtime.exec(folderCommand);
        proc=runtime.exec(fileCommand);
        FileChannel in;
        FileChannel out;
        FileInputStream inStream;
        FileOutputStream outStream;
        inStream = new FileInputStream(sourceFile);
        outStream = new FileOutputStream(targetFile);
        in = inStream.getChannel();
        out = outStream.getChannel();
        in.transferTo(0, in.size(), out);
        inStream.close();
        in.close();
        outStream.close();
        out.close();
    }

    /*
    *  searchFile with keyWord
    *  @String keyword
    *  @File path
    */
    public static void searchFile(String keyword, File path,
                                  ArrayList<HashMap<String, String>> resultList,
                                  boolean checkExtension, String extensionKey){
        File[] files = path.listFiles();
        if (files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (file.canRead()) {
                        searchFile(keyword, file, resultList, checkExtension, extensionKey);
                    }
                } else {
                    if (!file.getPath().contains("/.") &&
                            (file.getName().contains(keyword) ||
                                    file.getName().contains(keyword.toUpperCase()))) {
                        HashMap<String, String> resultItem = new HashMap<String, String>();
                        if (checkExtension) {
                            if (FileUtil.getFileExtension(file).equalsIgnoreCase(extensionKey)) {
                                resultItem.put(TAG_FILENAME, file.getName());
                                resultItem.put(TAG_FILEPATH, file.getPath());
                                resultItem.put(TAG_FILESIZE, Long.toString(file.length()));
                            }
                        }else {
                            resultItem.put(TAG_FILENAME, file.getName());
                            resultItem.put(TAG_FILEPATH, file.getPath());
                            resultItem.put(TAG_FILEPATH, Long.toString(file.length()));
                        }
                        resultList.add(resultItem);
                    }
                }
            }
        }
    }

    public static boolean deleteFile(File file) {
        return file.exists() && file.isFile() && file.delete();
    }

    public static boolean deleteAllFilesOfDir(File path) {
        if (!path.exists())
            return false;
        if (path.isFile()) {
            return path.delete();
        }
        File[] files = path.listFiles();
        for (File file : files) {
            deleteAllFilesOfDir(file);
        }
        return path.delete();
    }


    public static boolean exists(final String path) {
        File file = new File(path);
        return file.exists();
    }

    public static String getRealFilePathFromUri(Context context,Uri uri) {
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

}
