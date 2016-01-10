package com.onyx.reader.test;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import com.onyx.reader.R;
import com.onyx.reader.api.ReaderSelection;
import com.onyx.reader.api.ReaderViewOptions;
import com.onyx.reader.common.BaseCallback;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.impl.ReaderViewOptionsImpl;
import com.onyx.reader.host.layout.ReaderLayoutManager;
import com.onyx.reader.host.math.EntryInfo;
import com.onyx.reader.host.math.EntryManager;
import com.onyx.reader.host.navigation.NavigationList;
import com.onyx.reader.host.math.EntryUtils;
import com.onyx.reader.host.navigation.NavigationArgs;
import com.onyx.reader.host.request.*;
import com.onyx.reader.host.wrapper.Reader;
import com.onyx.reader.host.wrapper.ReaderManager;
import com.onyx.reader.text.*;
import com.onyx.reader.utils.BitmapUtils;

import java.util.*;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class ReaderTestActivity extends Activity {
    private Reader reader;
    final String path = "file:///mnt/sdcard/Books/ZerotoOne.pdf";
    private int pn = 0;
    private int next = 0;
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private SurfaceHolder.Callback surfaceHolderCallback;
    private Button button;

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initSurfaceView();
        reader = ReaderManager.createReader(this, path, null, getViewOptions());
        testMath();
        testMath2();
        testMath3();
        testMath4();
        testMath5();
        testReaderOpen();
    }

    private void initSurfaceView() {
        button = (Button)findViewById(R.id.update);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testSpan();
            }
        });

        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceHolderCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Canvas canvas = holder.lockCanvas();
                canvas.drawColor(Color.WHITE);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Canvas canvas = holder.lockCanvas();
                canvas.drawColor(Color.WHITE);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                holder.removeCallback(surfaceHolderCallback);
            }
        };

        surfaceView.getHolder().addCallback(surfaceHolderCallback);
        holder = surfaceView.getHolder();
        surfaceView.setFocusable(true);
        surfaceView.setFocusableInTouchMode(true);
        surfaceView.requestFocusFromTouch();

        surfaceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                surfaceView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
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
                testChangeLayout();
            }
        });
    }

    public void testChangeLayout() {
        NavigationArgs navigationArgs = NavigationArgs.rowsLeftToRight(NavigationArgs.Type.ALL, 3, 3, null);
        BaseRequest request = new ChangeLayoutRequest(ReaderLayoutManager.CONTINUOUS_PAGE, navigationArgs);
        reader.submitRequest(this, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                BitmapUtils.saveBitmap(request.getRenderBitmap().getBitmap(), "/mnt/sdcard/Books/listLayout.png");
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
                testOriginScale();
            }
        });
    }

    public void testOriginScale() {
        final ScaleRequest renderRequest = new ScaleRequest(0.3f, 0, 0);
        reader.submitRequest(this, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                assert(e == null);
                BitmapUtils.saveBitmap(renderRequest.getRenderBitmap().getBitmap(), "/mnt/sdcard/Books/originScale.png");
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
                    BitmapUtils.saveBitmap(renderRequest.getRenderBitmap().getBitmap(), "/mnt/sdcard/Books/next.png");
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
        RectF entry = new RectF(0, 0, 500, 500);
        RectF parent = new RectF(0, 0, 1024, 768);
        int rows = 3, cols = 3;
        NavigationList navigator = NavigationList.rowsLeftToRight(3, 3, null);
        float actualScale;

        int index = 0;
        while (navigator.hasNext()) {
            RectF ratio = navigator.next();
            float left = 1.0f / cols * (index % cols);
            float top = 1.0f / rows * (index / cols);
            assert(Float.compare(ratio.left, left) == 0);
            assert(Float.compare(ratio.top, top) == 0);
            ++index;
            actualScale = EntryUtils.scaleByRatio(ratio, entry, parent);
        }
    }

    // use span list to draw string
    // split into word list
    // get each word size
    // adjust the spacing if necessary
    // adjust the character finally

    private void testSpan() {
        TextLayout textLayout = new TextLayout();
        Rect rect = new Rect();
        surfaceView.getDrawingRect(rect);
        List<Element> list = new ArrayList<Element>();
        int count = randInt(500, 1000);
        for(int i = 0; i < count; ++i) {
            list.add(randElement());
        }
        int offset = 100;
        RectF rectF = new RectF(rect.left + offset, rect.top + offset, rect.right - offset, rect.bottom - offset);
        textLayout.layoutElementsAdjusted(rectF, list);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setStyle(Paint.Style.STROKE);

        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        for(Element element : list) {
            element.draw(canvas);
        }
        canvas.drawRect(rectF, paint);
        holder.unlockCanvasAndPost(canvas);
    }

    private String randString(int length) {
        String source = "【数据造假成常态，互联网行业自律出路在何处？】我是一名96年出生的创业者，虽然我今年才19岁，但其实我已经在互联网行业折腾近5年了。接触的越久，知道的越多。看到满大街的创业新闻，我忍不住吐槽一句“中国创业只有一个索尼展示360度4K视频，用了12部Xperia Z5 Compact完成】在今年的CES2016上索尼并没有展出新手机，所以在推广自家Xperia Z5系列手机的任务依旧艰难。最近索尼分享了一段用12部Xperia Z5 Compact拍摄的360度4K视频行业：互联网”。那么就接着这个话题继续吐槽吧";
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = source.charAt(randInt(0, source.length() - 1));
        }
        return new String(text);
    }

    private Element randElement() {
        Element element = TextElement.create(randString(1), randStyle());
        return element;
    }

    private Style randStyle() {
        Paint paint = new Paint();
        paint.setTextSize(randInt(30, 60));
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        return TextStyle.create(paint);
    }
}