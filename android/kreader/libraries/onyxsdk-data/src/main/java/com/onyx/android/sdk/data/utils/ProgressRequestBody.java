package com.onyx.android.sdk.data.utils;

import android.os.Handler;
import android.os.Looper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by suicheng on 2016/9/20.
 */
public class ProgressRequestBody extends RequestBody {
    private File file;
    private String mediaType;
    private UploadCallbacks listener;

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);
        void onError();
        void onFinish();
    }

    public ProgressRequestBody(final File file, final String mediaType, final UploadCallbacks listener) {
        this.file = file;
        this.listener = listener;
        this.mediaType = mediaType;
    }

    @Override
    public MediaType contentType() {
        //image/*
        return MediaType.parse(mediaType);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = file.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(file);
        Handler handler = new Handler(Looper.getMainLooper());
        long uploaded = 0;

        try {
            int read;
            while ((read = in.read(buffer)) != -1) {
                // update progress on UI thread
                if (listener != null) {
                    handler.post(new ProgressUpdater(uploaded, fileLength));
                }
                uploaded += read;
                sink.write(buffer, 0, read);
            }
        } finally {
            if (listener != null) {
                if (uploaded == 0 || uploaded < fileLength) {
                    handler.post(new DoneUpdater(false));
                } else {
                    handler.post(new DoneUpdater(true));
                }
            }
            in.close();
        }
    }

    private class DoneUpdater implements Runnable {
        private boolean isFinish;

        public DoneUpdater(boolean finish) {
            this.isFinish = finish;
        }

        @Override
        public void run() {
            if (isFinish) {
                listener.onFinish();
            } else {
                listener.onError();
            }
        }
    }

    private class ProgressUpdater implements Runnable {
        private long uploaded;
        private long total;

        public ProgressUpdater(long uploaded, long total) {
            this.uploaded = uploaded;
            this.total = total;
        }

        @Override
        public void run() {
            listener.onProgressUpdate((int) (100 * uploaded / total));
        }
    }
}
