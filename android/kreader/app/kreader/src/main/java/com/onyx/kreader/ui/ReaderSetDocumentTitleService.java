package com.onyx.kreader.ui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.onyx.android.sdk.reader.ISetDocumentTitleService;
import com.onyx.android.sdk.reader.utils.PdfWriterUtils;

public class ReaderSetDocumentTitleService extends Service {

    private final static String TAG = ReaderSetDocumentTitleService.class.getSimpleName();

    ISetDocumentTitleService.Stub mBinder = new ISetDocumentTitleService.Stub() {
        @Override
        public boolean setTitle(String path, String titleToSet) throws RemoteException {
            return copy(path, titleToSet);
        }
    };

    public ReaderSetDocumentTitleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private boolean copy(String path, String titleToSet) {
        Log.d(TAG, "#### copy document, path: " + path + ", title to set: " + titleToSet);
        PdfWriterUtils.setDocumentTitle(path, titleToSet);
        return true;
    }

}
