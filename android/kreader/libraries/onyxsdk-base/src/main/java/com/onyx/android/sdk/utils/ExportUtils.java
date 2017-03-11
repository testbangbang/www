package com.onyx.android.sdk.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * Created by ming on 2016/10/31.
 */

public class ExportUtils {

    public static String NOTE_EXPORT_LOCATION = "/mnt/sdcard/note/";

    private static String getExportFolderPath(String documentPath) throws IOException{
        String parent = FileUtils.getParent(documentPath);
        String baseName = FileUtils.getBaseName(documentPath);
        baseName = baseName.trim();
        String path = parent + "/" + baseName;
        if (!FileUtils.mkdirs(path)) {
            throw new IOException(path);
        }
        return path;
    }

    public static String getExportPdfPath(String documentPath) throws IOException{
        String baseName = FileUtils.getBaseName(documentPath);
        String ext = FileUtils.getFileExtension(documentPath);
        return new File(getExportFolderPath(documentPath), baseName + "-Exported." + ext).getAbsolutePath();
    }

    public static String getExportAnnotationPath(String documentPath) throws IOException{
        String baseName = FileUtils.getBaseName(documentPath);
        return new File(getExportFolderPath(documentPath), baseName + "-annotation.txt").getAbsolutePath();
    }

    public static String getExportScribblePath(String documentPath, String page) throws IOException{
        String baseName = FileUtils.getBaseName(documentPath);
        return new File(getExportFolderPath(documentPath), baseName + "-scribble-" + page + ".png").getAbsolutePath();
    }

    public static String getExportNotePath(String document, String page) throws IOException{
        String documentPath = NOTE_EXPORT_LOCATION + document;
        return new File(getExportFolderPath(documentPath), page + ".png").getAbsolutePath();
    }

    public static String getExportPicPath(String document) throws IOException {
        String documentPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).getAbsolutePath() + File.separator +"Screenshots";
        return new File(documentPath, document + ".png").getAbsolutePath();
    }
}
