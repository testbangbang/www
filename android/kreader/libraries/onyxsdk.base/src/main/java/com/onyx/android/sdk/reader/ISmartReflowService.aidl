package com.onyx.android.sdk.reader;

import android.os.ParcelFileDescriptor;

/**
 * @author joy
 *
 */
 interface ISmartReflowService
 {
     ParcelFileDescriptor reflowPage(in ParcelFileDescriptor bmp);
     void interrupt();
 }