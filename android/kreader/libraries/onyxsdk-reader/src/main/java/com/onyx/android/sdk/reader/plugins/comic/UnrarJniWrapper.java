package com.onyx.android.sdk.reader.plugins.comic;

/**
 * Created by joy on 3/16/16.
 */
public class UnrarJniWrapper {

    static{
        System.loadLibrary("neo_unrar");
    }

    private static int sPluginId = -1;

    private synchronized static int nextId() {
        sPluginId++;
        return sPluginId;
    }

    private native boolean open(int id, String filePath);
    private native void close(int id);

    private native boolean isEncrypted(int id);
    private native void setPassword(int id, String password);
    private native String[] getEntries(int id);
    private native byte[] extractEntryData(int id, String entryName);

    private int id;

    public UnrarJniWrapper() {
        id = nextId();
    }

    public boolean open(String path) {
        if (open(id, path)) {
            return true;
        }
        return false;
    }

    public void close() {
        close(id);
    }

    public boolean isEncrypted() {
        return isEncrypted(id);
    }

    public void setPassword(String password) {
        setPassword(id, password);
    }

    public String[] getEntries() {
        return getEntries(id);
    }

    public byte[] extractEntryData(String entryName) {
        return extractEntryData(id, entryName);
    }
}
