package com.onyx.kreader.ui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.onyx.android.sdk.reader.ISetDocumentTitleService;
import com.onyx.android.sdk.reader.utils.PdfWriterUtils;

public class ReaderSetDocumentTitleService extends Service {

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
        PdfWriterUtils.setDocumentTitle(path, titleToSet);
        return true;
    }

}
