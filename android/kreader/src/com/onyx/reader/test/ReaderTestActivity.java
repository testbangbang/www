package com.onyx.reader.test;

import android.app.Activity;
import android.os.Bundle;
import com.onyx.reader.R;
import com.onyx.reader.api.ReaderViewOptions;
import com.onyx.reader.common.BaseCallback;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.common.Utils;
import com.onyx.reader.host.impl.ReaderViewOptionsImpl;
import com.onyx.reader.host.request.GotoLocationRequest;
import com.onyx.reader.host.request.OpenRequest;
import com.onyx.reader.host.request.RenderRequest;
import com.onyx.reader.host.wrapper.Reader;
import com.onyx.reader.host.wrapper.ReaderManager;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class ReaderTestActivity extends Activity {
    private Reader reader;
    final String path = "file:///mnt/sdcard/Books/ZerotoOne.pdf";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        reader = ReaderManager.createReader(path, null, getViewOptions());
        testReaderOpen();
    }

    public ReaderViewOptions getViewOptions() {
        return new ReaderViewOptionsImpl(500, 500);
    }

    public void testReaderOpen() {
        BaseRequest open = new OpenRequest(path, null, null);
        reader.submitRequest(null, open, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                testReaderGoto();
            }
        });
    }

    public void testReaderGoto() {
        BaseRequest gotoPosition = new GotoLocationRequest(1);
        reader.submitRequest(null, gotoPosition, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                testReaderRender();
            }
        });
    }

    public void testReaderRender() {
        final RenderRequest  renderRequest = new RenderRequest();
        reader.submitRequest(null, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                Utils.saveBitmap(renderRequest.getReaderBitmap().getBitmap(), "/mnt/sdcard/Books/temp.png");
            }
        });
    }



}