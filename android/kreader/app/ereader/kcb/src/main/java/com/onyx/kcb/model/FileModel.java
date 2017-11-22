package com.onyx.kcb.model;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by suicheng on 2017/9/9.
 */
public class FileModel {
    public static final int TYPE_DIRECTORY = 0;
    public static final int TYPE_FILE = 1;
    public static final int TYPE_GO_UP = 2;
    public static final int TYPE_SHORT_CUT = 3;

    private File file;
    private Bitmap thumbnail;
    private int type;
    private String name;

    public static FileModel create(File file, Bitmap thumbnail) {
        FileModel model = new FileModel();
        model.file = file;
        model.thumbnail = thumbnail;
        model.type = file.isDirectory() ? TYPE_DIRECTORY : TYPE_FILE;
        return model;
    }

    public static FileModel createGoUpModel(File file, String name) {
        FileModel model = new FileModel();
        model.file = file;
        model.type = TYPE_GO_UP;
        model.name = name;
        return model;
    }

    public static FileModel createShortcutModel(File file) {
        FileModel model = create(file, null);
        model.type = TYPE_SHORT_CUT;
        return model;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        if (name == null) {
            return getFileName();
        }
        return name;
    }

    private String getFileName() {
        if (file == null) {
            return null;
        }
        return file.getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGoUpType() {
        return getType() == TYPE_GO_UP;
    }

    public boolean isFileType() {
        return getType() == TYPE_FILE;
    }
}