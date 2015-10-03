package com.onyx.reader.plugin;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderDrmManager {

    public boolean acceptDRMFile(final String path);

    public boolean registerDRMCallback(final ReaderDRMCallback callback);
    public boolean activateDeviceDRM(String user, String password);
    public boolean deactivateDeviceDRM();
    public String getDeviceDRMAccount();
    public boolean fulfillDRMFile(String path);


}
