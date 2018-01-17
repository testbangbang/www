package com.onyx.android.sdk.reader.host.request;

import android.util.Base64;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by lxm on 2018/1/10.
 */

public class Base64ByteArrayRequest extends BaseReaderRequest {

    private byte[] data;
    private String base64;

    public Base64ByteArrayRequest(byte[] data) {
        this.data = data;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        base64 = Base64.encodeToString(data, Base64.DEFAULT);
    }

    public String getBase64() {
        return base64;
    }
}
