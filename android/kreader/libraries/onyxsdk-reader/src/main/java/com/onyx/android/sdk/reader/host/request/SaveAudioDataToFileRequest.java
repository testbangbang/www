package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by lxm on 2018/1/10.
 */

public class SaveAudioDataToFileRequest extends BaseReaderRequest {

    private byte[] data;
    private FileDescriptor fileDescriptor;

    public SaveAudioDataToFileRequest(byte[] data) {
        this.data = data;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        File tempMp3 = File.createTempFile("temp", "mp3");
        tempMp3.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempMp3);
        fos.write(data);
        fos.close();
        FileInputStream fis = new FileInputStream(tempMp3);
        fileDescriptor = fis.getFD();
    }

    public FileDescriptor getFileDescriptor() {
        return fileDescriptor;
    }
}
