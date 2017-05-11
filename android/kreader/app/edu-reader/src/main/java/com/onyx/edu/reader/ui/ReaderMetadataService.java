package com.onyx.edu.reader.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.data.RefValue;
import com.onyx.android.sdk.reader.IMetadataService;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.dataprovider.ContentSdKDataUtils;
import com.onyx.android.sdk.reader.dataprovider.LegacySdkDataUtils;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.request.CreateViewRequest;
import com.onyx.android.sdk.reader.host.request.OpenRequest;
import com.onyx.android.sdk.reader.host.request.ReadDocumentMetadataRequest;
import com.onyx.android.sdk.reader.host.request.ReaderDocumentCoverRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.host.wrapper.ReaderManager;
import com.onyx.edu.reader.ui.data.DrmCertificateFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 4/30/14
 * Time: 11:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderMetadataService extends Service {

    private final static String TAG = ReaderMetadataService.class.getSimpleName();

    public  IMetadataService.Stub mBinder = new IMetadataService.Stub() {

        @Override
        public boolean extractMetadataAndThumbnail(String filePath, int timeout) {
            return extract(ReaderMetadataService.this, filePath);
        }

        public boolean extractMetadata(List<String>  fileList, int timeout) {
            return false;
        }

        @Override
        public void interrupt() throws RemoteException {
            close();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent)  {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private String documentPath;
    Reader reader;

    private boolean extract(final ReaderMetadataService service, final String documentPath) {
        this.documentPath = documentPath;
        reader = ReaderManager.getReader(documentPath);

        final RefValue<Boolean> result = new RefValue<>(false);
        extractMetadataAndThumbnail(service, documentPath, result);
        close();

        return result.getValue();
    }

    private void close() {
        if (reader != null) {
            reader.getDocument().close();
            ReaderManager.releaseReader(documentPath);
            reader = null;
            documentPath = null;
        }
    }

    private void extractMetadataAndThumbnail(final ReaderMetadataService service, final String documentPath, final RefValue<Boolean> result) {
        final DrmCertificateFactory factory = new DrmCertificateFactory();
        OpenRequest openRequest = new OpenRequest(documentPath, new BaseOptions(), factory, false);
        openRequest.setRunInBackground(false);
        reader.submitRequest(service, openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    Log.w(TAG, "open document failed: " + documentPath);
                    return;
                }
                onFileOpenSucceed(service, documentPath, result);
            }
        });
    }

    private void onFileOpenSucceed(final ReaderMetadataService service, final String documentPath, final RefValue<Boolean> result) {
        WindowManager window = (WindowManager)service.getSystemService(Context.WINDOW_SERVICE);
        if (window == null) {
            Log.w(TAG, "getById display metrics failed: " + documentPath);
            return;
        }
        DisplayMetrics display = new DisplayMetrics();
        window.getDefaultDisplay().getMetrics(display);
        final BaseReaderRequest config = new CreateViewRequest(display.widthPixels, display.heightPixels);
        config.setRunInBackground(false);
        reader.submitRequest(service, config, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    Log.w(TAG, "update viewport failed: " + documentPath);
                    return;
                }
                result.setValue(saveDocumentMetadata(service, documentPath) &&
                        saveDocumentThumbnail(service, documentPath));
            }
        });
    }

    private boolean saveDocumentMetadata(final ReaderMetadataService service, final String documentPath) {
        final RefValue<Boolean> result = new RefValue<>(false);
        final ReadDocumentMetadataRequest metadataRequest = new ReadDocumentMetadataRequest();
        metadataRequest.setRunInBackground(false);
        reader.submitRequest(service, metadataRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    Log.w(TAG, "read document metadata failed: " + documentPath);
                    return;
                }
                boolean saveSuccess = LegacySdkDataUtils.saveMetadata(service, documentPath,
                        metadataRequest.getMetadata());
                saveSuccess &= ContentSdKDataUtils.saveMetadata(service, documentPath,
                        metadataRequest.getMetadata());
                result.setValue(saveSuccess);
            }
        });
        return result.getValue();
    }

    private boolean saveDocumentThumbnail(final ReaderMetadataService service, final String documentPath) {
        final RefValue<Boolean> result = new RefValue<>(false);
        final ReaderBitmapImpl bitmap = ReaderBitmapImpl.create(reader.getViewOptions().getViewWidth(),
                reader.getViewOptions().getViewHeight(), Bitmap.Config.ARGB_8888);
        final ReaderDocumentCoverRequest coverRequest = new ReaderDocumentCoverRequest(bitmap);
        coverRequest.setRunInBackground(false);
        reader.submitRequest(service, coverRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    Log.w(TAG, "read document cover failed: " + documentPath);
                    return;
                }
                boolean saveSuccess = LegacySdkDataUtils.saveThumbnail(service, documentPath,
                        bitmap.getBitmap());
                saveSuccess &= ContentSdKDataUtils.saveThumbnail(service, documentPath,
                        bitmap.getBitmap());
                result.setValue(saveSuccess);
                bitmap.recycleBitmap();
            }
        });
        return result.getValue();
    }

}
