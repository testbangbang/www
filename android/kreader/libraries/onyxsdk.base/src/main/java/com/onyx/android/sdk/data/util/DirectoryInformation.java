package com.onyx.android.sdk.data.util;

/**
 * Created by solskjaer49 on 14/10/28 12:34.
 */
public class DirectoryInformation {


    int mFileItemCount, mDirectoryCount;

    public int getDirectoryCount() {
        return mDirectoryCount;
    }

    public void setDirectoryCount(int c) {
        mDirectoryCount = c;
    }

    public int getFileItemCount() {
        return mFileItemCount;
    }

    public void setFileItemCount(int c) {
        mFileItemCount = c;
    }

    public DirectoryInformation() {
        mFileItemCount =0;
        mDirectoryCount=0;
    }

    public DirectoryInformation(int fileItemCount, int directoryCount) {
        mFileItemCount = fileItemCount;
        mDirectoryCount = directoryCount;
    }
}
