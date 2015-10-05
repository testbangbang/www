package com.onyx.reader.test;

import com.onyx.reader.api.ReaderPlugin;
import com.onyx.reader.common.BaseCallback;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.request.OpenRequest;
import com.onyx.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class ReaderTest {

    public void testReader() {
        Reader reader = new Reader();
        BaseRequest open = new OpenRequest("");
        reader.submitRequest(null, open, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {

            }
        });
    }
}
