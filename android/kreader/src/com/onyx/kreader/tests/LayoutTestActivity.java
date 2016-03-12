package com.onyx.kreader.tests;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import com.onyx.kreader.R;
import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.text.TextPosition;
import com.onyx.kreader.formats.txt.TxtReader;
import com.onyx.kreader.text.*;
import com.onyx.kreader.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 3/3/16.
 */
public class LayoutTestActivity extends Activity {

    private Button openButton;
    private Button controllerButton;
    private Button runButton;
    private SurfaceHolder holder;
    private SurfaceView surfaceView;
    private static final String TAG = LayoutTestActivity.class.getSimpleName();

    final String path = "/mnt/sdcard/Books/test.txt";
    private TxtReader bookReader = new TxtReader();
    private BookModel bookModel = new BookModel(path);
    private TextPosition position = new TextPosition(bookReader, bookModel);
    private List<LayoutRun> runlist = new ArrayList<LayoutRun>();
    private Style textStyle = randStyle();
    private int runIndex = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_activity);

        openButton = (Button)findViewById(R.id.test_layout);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLayout();
            }
        });

        controllerButton = (Button)findViewById(R.id.test_layout_controller);
        controllerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testController();
            }
        });

        runButton = (Button)findViewById(R.id.test_layout_run);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLayoutRun();
            }
        });

        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        holder = surfaceView.getHolder();
        bookReader.open(bookModel);
    }


    private void testLayout() {

    }

    private RectF layoutRect() {
        int margin = 100;
        RectF rect = new RectF(margin, margin, surfaceView.getMeasuredWidth() - margin, surfaceView.getMeasuredHeight() - margin);
        return rect;
    }

    private void testController() {
    }

    private void testLayoutRun() {

        final RectF lineRect = new RectF(layoutRect());
        if (runIndex <= 0) {
            LayoutRunSplitter.fetchMore(runlist, position, textStyle, 10 * 1000);
        }

        List<LayoutRunLine> lineList = new ArrayList<LayoutRunLine>();
        LayoutRunLine layoutLine = new LayoutRunLine(lineRect);
        lineList.add(layoutLine);

        long start = System.currentTimeMillis();
        float lineSpacing;
        boolean stop = false;
        int index = runIndex;
        while (!stop && index < runlist.size()) {
            final LayoutRun layoutRun = runlist.get(index);
            LayoutRunLine.LayoutResult result = layoutLine.layoutRun(layoutRun);
            switch (result) {
                case LAYOUT_ADDED:
                    ++index;
                    break;
                case LAYOUT_FINISHED:
                    lineSpacing = Math.max(layoutLine.getContentHeight(), textStyle.measureHeight("A"));
                    if (!layoutRun.isParagraphEnd()) {
                        lineSpacing = 10;
                    }
                    if (!layoutLine.nextLine(layoutRect(), lineRect, lineSpacing)) {
                        stop = true;
                        break;
                    }
                    layoutLine = new LayoutRunLine(lineRect);
                    lineList.add(layoutLine);
                    ++index;
                    break;
                case LAYOUT_FAIL:
                    lineSpacing = Math.max(layoutLine.getContentHeight(), textStyle.measureHeight("A"));
                    if (!layoutRun.isParagraphEnd()) {
                        lineSpacing = 10;
                    }

                    if (!layoutLine.nextLine(layoutRect(), lineRect, lineSpacing)) {
                        stop = true;
                        break;
                    }
                    layoutLine = new LayoutRunLine(lineRect);
                    lineList.add(layoutLine);
                    break;
                case LAYOUT_BREAK:
                    final LayoutRun another = breakRunByWidth(layoutRun, layoutLine.getAvailableWidth(), textStyle);
                    runlist.add(index + 1, another);
                    break;
            }
        }

        runIndex = index;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        for(LayoutRunLine lineManager: lineList) {
            for (LayoutRun layoutRun : lineManager.getRunList()) {
                if (layoutRun.isWord()) {
                    final Paint.FontMetrics fontMetrics = textStyle.getPaint().getFontMetrics();
//                    canvas.drawRect(layoutRun.getPositionRect(), textStyle.getPaint());
                    canvas.drawText(layoutRun.getText(),
                            layoutRun.getStart(),
                            layoutRun.getEnd(),
                            layoutRun.getPositionRect().left, layoutRun.getPositionRect().top - fontMetrics.top - fontMetrics.bottom, textStyle.getPaint());
                }
            }
        }
        canvas.drawRect(layoutRect(), paint);
        holder.unlockCanvasAndPost(canvas);

        long end = System.currentTimeMillis();
        Log.d(TAG, "layout and rendering takes: " + (end - start));
    }

    private final LayoutRun breakRunByWidth(final LayoutRun layoutRun, final float width, final Style textStyle) {
        final float characterWidth = layoutRun.singleCharacterWidth();
        int count = (int)(width /characterWidth);
        float newWidth = textStyle.getPaint().measureText(layoutRun.getText(), layoutRun.getStart(), layoutRun.getStart() + count);
        return layoutRun.breakRun(count, newWidth);
    }

    private Style randStyle() {
        Paint paint = new Paint();
        paint.setTextSize(35);//TestUtils.randInt(10, 80));
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        int value = TestUtils.randInt(10, 20);
        if (value > 10 && value < 13) {
            paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.ITALIC));
        } else if (value > 13 && value < 16) {
            paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        paint.setStyle(Paint.Style.STROKE);
        return TextStyle.create(paint);
    }





}