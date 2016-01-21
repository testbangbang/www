package com.onyx.reader.test;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import com.alibaba.fastjson.serializer.ClassSerializer;
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

import java.io.InvalidObjectException;
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
    private int index = 0;
    private String source = "八年来，我研究了我的朋友歇洛克·福尔摩斯的破案方法，记录了七十多个案例。我粗略地翻阅一下这些案例的记录，发现许多案例是悲剧性的，也有一些是喜剧性的，其中很大一部分仅仅是离奇古怪而已，但是却没有一例是平淡无奇的。这是因为，他做工作与其说是为了获得酬金，还不如说是出于对他那门技艺的爱好。除了显得独特或甚至于是近乎荒诞无稽的案情外，他对其它案情从来是不屑一顾，拒不参与任何侦查的。可是，在所有这些变化多端的案例中，我却回忆不起有哪一例会比萨里郡斯托克莫兰的闻名的罗伊洛特家族①那一例更具有异乎寻常的特色了。现在谈论的这件事，发生在我和福尔摩斯交往的早期。那时，我们都是单身汉，在贝克街合住一套寓所。本来我早就可以把这件事记录下来，但是，当时我曾作出严守秘密的保证，直至上月，由于我为之作出过保证的那位女士不幸过早地逝世，方始解除了这种约束。现在，大概是使真相大白于天下的时候了，因为我确实知道，外界对于格里姆斯比·罗伊洛特医生之死众说纷纭，广泛流传着各种谣言。这些谣言使得这桩事情变得比实际情况更加骇人听闻。①英格兰东南部一郡。——译者注事情发生在一八八三年四月初的时候。一天早上，我一觉醒来，发现歇洛克·福尔摩斯穿得整整齐齐在我的床边。一般来说，他是一个爱睡懒觉的人，而壁炉架上的时钟，才刚七点一刻，我有些诧异地朝他眨了眨眼睛，心里还有点不乐意，因为我自己的生活习惯是很有规律的。“对不起，把你叫醒了，华生，\"他说，“但是，你我今天早上都命该如此，先是赫德森太太被敲门声吵醒，接着她报复似地来吵醒我，现在是我来把你叫醒。”“那么，什么事——失火了吗？”“不，是一位委托人。好象是一位年轻的女士来临，她情绪相当激动，坚持非要见我不可。现在她正在起居室里等候。你瞧，如果有些年轻的女士这么一清早就徘徊于这个大都市，甚至把还在梦乡的人从床上吵认为那必定是一件紧急的事情，她们不得不找人商量。假如这件事将是一件有趣的案子，那么，我肯定你一定希望从一开始就能有所了解。我认为无论如何应该把你叫醒，给予你这个机会。”“我的老兄，那我是无论如何也不肯失掉这个机会的。”我最大的乐趣就是观察福尔摩斯进行专业性的调查工作，欣赏他迅速地做出推论，他推论之敏捷，犹如是单凭直觉而做出的，但却总是建立在逻辑的基础之上。他就是依靠这些解决了委托给他的疑难问题。我匆匆地穿上衣服，几分钟后就准备就绪，随同我的朋友来到楼下的起居室。一位女士端坐窗前，她身穿黑色衣服，蒙着厚厚的面。她在我们走进房间时站起身来。“早上好，小姐，\"福尔摩斯愉快地说道，“我的名字是歇洛克·福尔摩斯。这位是我的挚友和伙伴华生医生。在他面前，你可以象在我面前一样地谈话，不必顾虑。哈！赫德森太太想得很周到，我很高兴看到她已经烧旺了壁炉。请凑近炉火坐坐，我叫人给你端一杯热咖啡，我看你在发抖。”“我不是因为觉得冷才发抖的，\"那个女人低声地说，同时，她按照福尔摩斯的请求换了个座位。“那么，是为什么呢？”";
    private String TAG = ReaderTestActivity.class.getSimpleName();

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
                try {
                    testSpan();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        button = (Button)findViewById(R.id.hyphen);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testHyphen();
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

    private void testSpan() throws Exception  {
        TextLayoutJustify textLayoutJustify = new TextLayoutJustify();
        Rect rect = new Rect();
        surfaceView.getDrawingRect(rect);
        List<Element> list = new ArrayList<Element>();
        list.add(LeadingElement.create(randStyle()));
        int count = randInt(100, 600);
        for(int i = 0; i < count; ++i) {
            list.add(randElement());
        }
        int offset = 50;
        RectF rectF = new RectF(rect.left + offset, rect.top + offset, rect.right - offset, rect.bottom - offset);
        final List<LayoutLine> lines = textLayoutJustify.layout(rectF, list);

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

        verifyLayout(lines, rectF);
    }

    private void verifyLayout(final List<LayoutLine> lines, final RectF limitedRect) throws Exception  {
        for(int index = 0; index < lines.size(); ++index) {
            LayoutLine layoutLine = lines.get(index);
            if (layoutLine.getContentWidth() >= limitedRect.width()) {
                throw new InvalidObjectException("width error");
            }
            final List<Element> list = layoutLine.getElementList();
            if (list.isEmpty()) {
                throw new InvalidObjectException("list empty");
            }
            if (!list.get(0).canBeLayoutedAtLineBegin() && index != 0) {
                throw new InvalidObjectException("invalid element");
            }
        }
    }

    private String randString(int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = source.charAt(randInt(0, source.length() - 1));
        }
        return new String(text).trim();
    }

    private Element randElement() {
        Element element = TextElement.create(randString(1), randStyle());
        return element;
    }

    private Style randStyle() {
        Paint paint = new Paint();
        paint.setTextSize(randInt(30, 80));
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        int value = randInt(10, 20);
        if (value > 10 && value < 13) {
            paint.setTypeface(Typeface.create(Typeface.SERIF,Typeface.ITALIC));
        } else if (value > 13 && value < 16) {
            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        paint.setStyle(Paint.Style.STROKE);
        return TextStyle.create(paint);
    }

    private void testHyphen() {
        ALHyphen.reinit_hyph(this, ALHyphen.HYPH_ENGLISH);
        final String source = "representatives";
        char [] data = ALHyphen.getHyph(source);
        for(int i = 0; i < data.length; ++i) {
            if (data[i] == '-') {
                String a = source.substring(0, i);
                String b = source.substring(i);
                Log.d(TAG, "after: " + a + "-" + b);
            } else if (data[i] == '\u0000') {
                break;
            }
        }

    }
}