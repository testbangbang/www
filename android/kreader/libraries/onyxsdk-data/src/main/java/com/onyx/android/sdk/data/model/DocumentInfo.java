package com.onyx.android.sdk.data.model;

import java.util.List;
import java.util.Set;

/**
 * Created by ming on 2017/2/8.
 */

public class DocumentInfo {

    private String path;
    private String md5;
    private String title;
    private String name;
    private List<String> authors;

    public DocumentInfo(List<String> authors, String md5, String name, String path, String title) {
        this.authors = authors;
        this.md5 = md5;
        this.name = name;
        this.path = path;
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static DocumentInfo create(List<String> authors, String md5, String name, String path, String title) {
        return new DocumentInfo(authors, md5, name, path, title);
    }
}
