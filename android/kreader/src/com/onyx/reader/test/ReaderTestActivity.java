package com.onyx.reader.test;

import android.app.Activity;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import com.onyx.reader.R;
import com.onyx.reader.api.ReaderSelection;
import com.onyx.reader.api.ReaderViewOptions;
import com.onyx.reader.common.BaseCallback;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.impl.ReaderViewOptionsImpl;
import com.onyx.reader.host.layout.ReaderLayoutManager;
import com.onyx.reader.host.math.EntryInfo;
import com.onyx.reader.host.math.EntryManager;
import com.onyx.reader.host.navigation.SubScreenListProvider;
import com.onyx.reader.host.math.EntryUtils;
import com.onyx.reader.host.request.*;
import com.onyx.reader.host.wrapper.Reader;
import com.onyx.reader.host.wrapper.ReaderManager;
import com.onyx.reader.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class ReaderTestActivity extends Activity {
    private Reader reader;
    final String path = "file:///mnt/sdcard/Books/ZerotoOne.pdf";
    int pn = 0;
    int next = 0;

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
        testMath3();
        testMath4();
        testMath5();
        testReaderOpen();
    }

    public ReaderViewOptions getViewOptions() {
        return new ReaderViewOptionsImpl(768, 1024);
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

    public void testChangeLayout() {
        BaseRequest request = new ChangeLayoutRequest(ReaderLayoutManager.CONTINUOUS_PAGE);
        reader.submitRequest(this, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                testReaderGoto();
            }
        });
    }

    public void testReaderGoto() {
        BaseRequest gotoPosition = new GotoLocationRequest(pn++);
        reader.submitRequest(this, gotoPosition, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                testScaleToPage();
            }
        });
    }

    public void testScaleToPage() {
        final ScaleToPageRequest renderRequest = new ScaleToPageRequest();
        reader.submitRequest(this, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                BitmapUtils.saveBitmap(renderRequest.getRenderBitmap().getBitmap(), "/mnt/sdcard/Books/scaleToPage.png");
                testScaleToWidth();
            }
        });
    }

    public void testScaleToWidth() {
        final ScaleToWidthRequest renderRequest = new ScaleToWidthRequest();
        reader.submitRequest(this, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                BitmapUtils.saveBitmap(renderRequest.getRenderBitmap().getBitmap(), "/mnt/sdcard/Books/scaleToWidth.png");
                testActualScale();
            }
        });
    }

    public void testActualScale() {
        final ScaleRequest renderRequest = new ScaleRequest(0.5f, 0, 0);
        reader.submitRequest(this, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                BitmapUtils.saveBitmap(renderRequest.getRenderBitmap().getBitmap(), "/mnt/sdcard/Books/scale.png");
                testScaleByRect();
            }
        });
    }

    public void testScaleByRect() {
        final ScaleByRectRequest renderRequest = new ScaleByRectRequest(new RectF(100, 100, 200, 200));
        reader.submitRequest(this, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                BitmapUtils.saveBitmap(renderRequest.getRenderBitmap().getBitmap(), "/mnt/sdcard/Books/scaleByRect.png");
                testSubscreenNavigation();
            }
        });
    }

    public void testSubscreenNavigation() {
        RectF child = new RectF(0, 0, 1, 1);
        int rows = 3, cols = 3;
        List<RectF> list = new ArrayList<RectF>();
        for(int i = 0; i < rows; ++i) {
            for(int j = 0; j < cols; ++j) {
                float left = child.left + child.width() / cols * j;
                float right = left + child.width() / cols;
                float top = child.top + child.height() / rows * i;
                float bottom = top + child.height() / rows;
                RectF sub = new RectF(left, top, right, bottom);
                list.add(sub);
            }
        }

        final SubScreenNavigationRequest request = new SubScreenNavigationRequest(1.0f, list);
        reader.submitRequest(this, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                BitmapUtils.saveBitmap(request.getRenderBitmap().getBitmap(), "/mnt/sdcard/Books/subScreen.png");
                testNextScreen();
            }
        });

    }


    public void testNextScreen() {
        final NextScreenRequest renderRequest = new NextScreenRequest();
        reader.submitRequest(this, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                if (e == null) {
                    BitmapUtils.saveBitmap(renderRequest.getRenderBitmap().getBitmap(), "/mnt/sdcard/Books/next" + next + ".png");
                    ++next;
                    testNextScreen();
                } else {
                    testHitTestWithoutRendering();
                }
            }
        });
    }

    public void testHitTestWithoutRendering() {
        final SelectionRequest request = new SelectionRequest(new PointF(200, 200), new PointF(250, 300));
        reader.submitRequest(this, request, new BaseCallback() {
            @Override
            public void done(BaseRequest baseRequest, Exception e) {
                assert(e == null);
                ReaderSelection selection = request.getSelection();
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
            entryManager.add(String.valueOf(i), entryInfo);
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
        entryManager.add(String.valueOf(0), new EntryInfo(randInt(100, 2000), randInt(100, 2000)));
        entryManager.setScale(1.0f);
        entryManager.setViewportRect(0, 0, 2000, 2500);
        entryManager.scaleToPage();
        assert(Float.compare(entryManager.getViewportRect().centerX(), entryManager.getHostRect().centerX()) == 0);
        assert(Float.compare(entryManager.getViewportRect().centerY(), entryManager.getHostRect().centerY()) == 0);

        assert(entryManager.nextViewport() == false);
        entryManager.nextViewport();
        assert(entryManager.prevViewport() == false);
        entryManager.prevViewport();
    }

    public void testMath3() {
        RectF child = new RectF(100, 100, 200, 200);
        RectF parent = new RectF(0, 0, 300, 300);
        float left = child.left;
        float top = child.top;
        float width = parent.width();
        float height = parent.height();

        float delta = EntryUtils.scaleByRect(child, parent);
        assert(delta > 0);
        assert(Float.compare(delta  * left, child.left) == 0);
        assert(Float.compare(delta  * top, child.top) == 0);


        float centerX = parent.centerX();
        float centerY = parent.centerY();
        float newCenterX = child.centerX();
        float newCenterY = child.centerY();
        assert(Float.compare(centerX, newCenterX) == 0);
        assert(Float.compare(centerY, newCenterY) == 0);
        assert(Float.compare(parent.width(), width) == 0);
        assert(Float.compare(parent.height(), height) == 0);
    }


    public void testMath4() {
        RectF child = new RectF(randInt(200, 500), randInt(200, 500), randInt(800, 1200), randInt(800, 1200));
        RectF parent = new RectF(randInt(0, 100), randInt(0, 100), randInt(1300, 2000), randInt(1300, 2000));
        float left = child.left;
        float top = child.top;
        float width = parent.width();
        float height = parent.height();

        float delta = EntryUtils.scaleByRect(child, parent);
        assert(delta > 0);
        assert(Float.compare(delta  * left, child.left) == 0);
        assert(Float.compare(delta  * top, child.top) == 0);


        float centerX = parent.centerX();
        float centerY = parent.centerY();
        float newCenterX = child.centerX();
        float newCenterY = child.centerY();
        assert(Float.compare(centerX, newCenterX) == 0);
        assert(Float.compare(centerY, newCenterY) == 0);
        assert(Float.compare(parent.width(), width) == 0);
        assert(Float.compare(parent.height(), height) == 0);
    }

    public void testMath5() {
        RectF child = new RectF(0, 0, 1, 1);
        RectF entry = new RectF(0, 0, 500, 500);
        RectF parent = new RectF(0, 0, 1024, 768);
        float scale = EntryUtils.scaleToPage(child, parent);
        child.set(0, 0, child.width() * scale, child.height() * scale);
        int rows = 3, cols = 3;
        List<RectF> list = new ArrayList<RectF>();
        for(int i = 0; i < rows; ++i) {
            for(int j = 0; j < cols; ++j) {
                float left = child.left + child.width() / cols * j;
                float right = left + child.width() / cols;
                float top = child.top + child.height() / rows * i;
                float bottom = top + child.height() / rows;
                RectF sub = new RectF(left, top, right, bottom);
                list.add(sub);
            }
        }

        SubScreenListProvider navigator = new SubScreenListProvider();
        navigator.addAll(list);
        navigator.setActualScale(scale);

        while (navigator.next()) {
            RectF ratio = new RectF(navigator.getCurrent());
            float newScale = scale * EntryUtils.scaleByRatio(ratio, entry, parent);
            scale = newScale;
        }
    }

}