package com.onyx.android.sdk.reader.api;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderDrmManager {

    /**
     * Check if drm manager accept the file or not.
     * @param path
     * @return
     */
    public boolean acceptDRMFile(final String path);

    public boolean registerDRMCallback(final ReaderDRMCallback callback);
    public boolean activateDeviceDRM(String user, String password);
    public boolean deactivateDeviceDRM();
    public String getDeviceDRMAccount();
    public boolean fulfillDRMFile(String path);


}
