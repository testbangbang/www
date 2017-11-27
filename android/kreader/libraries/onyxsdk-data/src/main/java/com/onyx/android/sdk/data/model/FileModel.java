package com.onyx.android.sdk.data.model;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by suicheng on 2017/9/9.
 */
public class FileModel {

    private File file;
    private Bitmap thumbnail;
    private ModelType type;
    private String name;

    public static FileModel create(File file, Bitmap thumbnail) {
        FileModel model = new FileModel();
        model.file = file;
        model.thumbnail = thumbnail;
        model.type = file.isDirectory() ? ModelType.TYPE_DIRECTORY : ModelType.TYPE_FILE;
        return model;
    }

    public static FileModel createGoUpModel(File file, String name) {
        FileModel model = new FileModel();
        model.file = file;
        model.type = ModelType.TYPE_GO_UP;
        model.name = name;
        return model;
    }

    public static FileModel createShortcutModel(File file) {
        FileModel model = create(file, null);
        model.type = ModelType.TYPE_SHORT_CUT;
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

    public ModelType getType() {
        return type;
    }

    public void setType(ModelType type) {
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
        return getType() == ModelType.TYPE_GO_UP;
    }

    public boolean isFileType() {
        return getType() == ModelType.TYPE_FILE;
    }
}