package com.onyx.android.sdk.data;

/**
 * Created by ming on 2017/4/24.
 */

public class DatabaseInfo {

    private String name;

    private int version;

    private String dbPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public DatabaseInfo(String name, int version, String dbPath) {
        this.name = name;
        this.version = version;
        this.dbPath = dbPath;
    }

    public DatabaseInfo(String dbPath) {
        this.dbPath = dbPath;
    }

    public static DatabaseInfo create(String name, int version, String dbPath) {
        return new DatabaseInfo(name, version, dbPath);
    }

    public static DatabaseInfo create(String dbPath) {
        return new DatabaseInfo(dbPath);
    }
}
