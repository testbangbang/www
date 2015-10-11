package com.onyx.reader.test;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import com.onyx.reader.R;
import com.onyx.reader.api.ReaderViewOptions;
import com.onyx.reader.common.BaseCallback;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.common.Utils;
import com.onyx.reader.host.impl.ReaderViewOptionsImpl;
import com.onyx.reader.host.math.EntryInfo;
import com.onyx.reader.host.math.EntryManager;
import com.onyx.reader.host.request.*;
import com.onyx.reader.host.wrapper.Reader;
import com.onyx.reader.host.wrapper.ReaderManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class ReaderTestActivity extends Activity {
    private Reader reader;
    final String path = "file:///mnt/sdcard/Books/ZerotoOne.pdf";

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        reader = ReaderManager.createReader(this, path, null, getViewOptions());
        testMath();
        testMath2();
        testReaderOpen();
    }

    public ReaderViewOptions getViewOptions() {
        return new ReaderViewOptionsImpl(500, 500);
    }

    public void testReaderOpen() {
        BaseRequest open = new OpenRequest(path, null, null);
        reader.submitRequest(this, open, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                testReaderGoto();
            }
        });
    }

    public void testReaderGoto() {
        BaseRequest gotoPosition = new GotoLocationRequest(2);
        reader.submitRequest(this, gotoPosition, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                testReaderRender();
            }
        });
    }

    public void testReaderRender() {
        final ScaleToPageRequest renderRequest = new ScaleToPageRequest();
        reader.submitRequest(this, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                Utils.saveBitmap(renderRequest.getRenderBitmap().getBitmap(), "/mnt/sdcard/Books/temp.png");
                testReaderClose();
            }
        });
    }


    public void testReaderClose() {
        final CloseRequest closeRequest = new CloseRequest();
        reader.submitRequest(this, closeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                testReaderOpen();
            }
        });
    }

    private void compareList(final List<EntryInfo> a, final List<EntryInfo> b) {
        assert(a.size() == b.size());
        for(EntryInfo entryInfo : a) {
            assert(b.contains(entryInfo));
        }
        for(EntryInfo entryInfo : b) {
            assert(a.contains(entryInfo));
        }
    }

    public void testMath() {
        RectF a = new RectF(0, 0, 100, 100);
        RectF b = new RectF(50, 50, 60, 60);
        RectF.intersects(a, b);


        EntryManager entryManager = new EntryManager();
        for (int i = 0; i < 5000; ++i) {
            EntryInfo entryInfo = new EntryInfo(randInt(100, 2000), randInt(100, 2000));
            entryManager.add(entryInfo);
        }
        entryManager.setViewportRect(0, 0, 1024, 2000);
        long start = System.currentTimeMillis();
        entryManager.setScale(1.0f);
        long end = System.currentTimeMillis();
        Log.i("TEST", "update takes: " + (end - start));
        long end2 = System.currentTimeMillis();
        List<EntryInfo> visiblePages = entryManager.updateVisiblePages();
        Log.i("TEST", "update takes: " + (end2 - end));

        List<EntryInfo> verify = new ArrayList<EntryInfo>();
        List<EntryInfo> all = entryManager.getEntryInfoList();
        for (EntryInfo entryInfo : all) {
            if (RectF.intersects(entryManager.getViewportRect(), entryInfo.getDisplayRect())) {
                verify.add(entryInfo);
            }
        }
        compareList(verify, visiblePages);
    }

    public void testMath2() {
        EntryManager entryManager = new EntryManager();
        entryManager.clear();
        entryManager.add(new EntryInfo(randInt(100, 2000), randInt(100, 2000)));
        entryManager.setScale(1.0f);
        entryManager.setViewportRect(0, 0, 2000, 2500);
        entryManager.scaleToPage();
        EntryInfo entryInfo = entryManager.getEntryInfoList().get(0);
        assert(Float.compare(entryManager.getViewportRect().centerX(), entryManager.getHostRect().centerX()) == 0);
        assert(Float.compare(entryManager.getViewportRect().centerY(), entryManager.getHostRect().centerY()) == 0);

    }

}