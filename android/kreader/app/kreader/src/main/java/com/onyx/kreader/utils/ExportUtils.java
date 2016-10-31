package com.onyx.kreader.utils;

import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by ming on 2016/10/31.
 */

public class ExportUtils {

    private static String getExportFolderPath(String documentPath) throws IOException{
        String parent = FileUtils.getParent(documentPath);
        String baseName = FileUtils.getBaseName(documentPath);
        String path = parent + "/" + baseName;
        if (!FileUtils.mkdirs(path)) {
            throw new IOException();
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
}
