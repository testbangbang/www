package com.onyx.reader.test;

import android.app.Activity;
import android.os.Bundle;
import com.onyx.reader.R;
import com.onyx.reader.common.BaseCallback;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.request.OpenRequest;
import com.onyx.reader.host.wrapper.Reader;
import com.onyx.reader.host.wrapper.ReaderManager;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class ReaderTestActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        testReader();
    }


    public void testReader() {
        final String path = "file:///mnt/sdcard/Books/ZerotoOne.pdf";
        Reader reader = ReaderManager.createReader(path);
        BaseRequest open = new OpenRequest(path, null, null);
        reader.submitRequest(null, open, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
            }
        });
    }
}