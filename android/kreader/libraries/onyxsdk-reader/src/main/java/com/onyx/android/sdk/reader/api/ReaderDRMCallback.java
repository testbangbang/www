package com.onyx.android.sdk.reader.api;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderDRMCallback {

    public void onActivationDone();

    public void onAuthSignInDone() ;

    public void onDownloadDone();

    public void onLoanReturnDone();

    public void onFulfillDone();

    public void onDownloadProgress(double progress);

    public void onActivationFailed(final String message);
    public void onSignInFailed(final String message);
    public void onDownloadFailed(final String message);
    public void onLoanReturnFailed(final String message);
    public void onFulfillFailed(final String message);

    public void reportDRMFulfillContentPath(String fulfillContentPath);
}
